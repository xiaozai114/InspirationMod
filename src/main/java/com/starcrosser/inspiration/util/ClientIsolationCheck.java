package com.starcrosser.inspiration.util;

import com.starcrosser.inspiration.Inspiration;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 客户端隔离体检：扫一遍自己编译出来的 class，找「服务端路径引用了客户端类」的地方。
 * <p>
 * 这类问题编译期发现不了（客户端类在本地一定存在），只在**专用服务器**上才炸，
 * 而本地开发永远碰不到，所以加一道自动检查：读 class 字节码，看每个方法引用了哪些类型，
 * 凡是引用了客户端类、但方法没标 {@code @OnlyIn(Dist.CLIENT)} 的，都列出来。
 * <p>
 * 只在非生产环境跑（IDE / runServer / runData 都属于非生产），几十毫秒，不进正式包的行为路径。
 *
 * <h3>判定口径</h3>
 * <ul>
 *     <li>「客户端类」= 内部名带 {@code /client/} 段的（含我们自己 {@code client/} 包下的所有类），
 *         外加 {@code com/mojang/blaze3d/} 与 {@code mezz/jei/} 这两个整包都是客户端的包。</li>
 *     <li>类本身在 {@code client/} 包下 → 不检查（它本来就是客户端的）。</li>
 *     <li>类或方法标了 {@code @OnlyIn(CLIENT)} → 允许。</li>
 *     <li>lambda / 合成方法没有自己的注解，跟着「所在类有没有客户端方法」走。</li>
 *     <li>{@code datagen/} 整包跳过：它只在 {@code runData} 时存在，而 NeoForge 的模型生成 API
 *         （{@code neoforge.client.model.generators}）本来就在 client 包下面，属于误报。</li>
 * </ul>
 */
public final class ClientIsolationCheck {

    private static final String OUR_PACKAGE = "com/starcrosser/inspiration/";
    private static final String DATAGEN_PACKAGE = OUR_PACKAGE + "datagen/";
    private static final String ONLY_IN_DESC = "Lnet/neoforged/api/distmarker/OnlyIn;";
    private static final Pattern SIGNATURE_TYPE = Pattern.compile("L([^;<]+);");

    private ClientIsolationCheck() {
    }

    /** 扫一遍并把结果写进日志。生产环境直接返回。 */
    public static void run() {
        if (FMLLoader.isProduction()) {
            return;
        }
        long start = System.nanoTime();
        List<String> violations = new ArrayList<>();
        int scanned;
        try {
            List<byte[]> classes = loadOwnClasses();
            scanned = classes.size();
            for (byte[] bytes : classes) {
                scanClass(bytes, violations);
            }
        } catch (IOException e) {
            Inspiration.LOGGER.warn("[客户端隔离] 读不到自己的 class，跳过这次体检：{}", e);
            return;
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (violations.isEmpty()) {
            Inspiration.LOGGER.info("[客户端隔离] 体检通过：{} 个类，没发现服务端引用客户端类（{} ms）", scanned, ms);
            return;
        }
        Inspiration.LOGGER.error("[客户端隔离] 发现 {} 处服务端路径引用了客户端类（{} ms）：", violations.size(), ms);
        for (String violation : violations) {
            Inspiration.LOGGER.error("  - {}", violation);
        }
        Inspiration.LOGGER.error("[客户端隔离] 修法：给引用客户端类的方法标 @OnlyIn(Dist.CLIENT)，"
                + "或者把这段代码挪进 client/ 包。标了注解的方法在专用服务器上不会被加载。");
    }

    /** 把自己 jar（开发时是 classes 目录）里的 .class 全读出来。 */
    private static List<byte[]> loadOwnClasses() throws IOException {
        Path root;
        try {
            root = Path.of(ClientIsolationCheck.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        List<byte[]> out = new ArrayList<>();
        if (Files.isDirectory(root)) {
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path path : walk.filter(ClientIsolationCheck::isOwnClass).toList()) {
                    out.add(Files.readAllBytes(path));
                }
            }
            return out;
        }
        try (FileSystem zip = FileSystems.newFileSystem(root, Map.of()); Stream<Path> walk = Files.walk(zip.getPath("/"))) {
            for (Path path : walk.filter(ClientIsolationCheck::isOwnClass).toList()) {
                out.add(Files.readAllBytes(path));
            }
        }
        return out;
    }

    private static boolean isOwnClass(Path path) {
        String name = path.toString().replace('\\', '/');
        return name.endsWith(".class") && name.contains(OUR_PACKAGE);
    }

    /** 读一个 class：把「引用了客户端类但没标 @OnlyIn」的地方记下来。 */
    private static void scanClass(byte[] bytes, List<String> violations) {
        ClassReader reader = new ClassReader(bytes);
        String className = reader.getClassName();
        if (!className.startsWith(OUR_PACKAGE) || isClientClass(className)) {
            return;
        }
        if (className.startsWith(DATAGEN_PACKAGE)) {
            // 数据生成只在 runData 里跑，而它的 API 本身就长在 neoforge 的 client 包下
            return;
        }
        List<String> classLevelRefs = new ArrayList<>();
        List<String> methodViolations = new ArrayList<>();
        boolean[] classIsClient = {false};
        boolean[] hasClientMethod = {false};

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                addType(superName, classLevelRefs);
                addSignature(signature, classLevelRefs);
                if (interfaces != null) {
                    for (String itf : interfaces) {
                        addType(itf, classLevelRefs);
                    }
                }
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (ONLY_IN_DESC.equals(descriptor)) {
                    return clientDistMarker(classIsClient);
                }
                return null;
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                addDescriptor(descriptor, classLevelRefs);
                addSignature(signature, classLevelRefs);
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                Set<String> refs = new LinkedHashSet<>();
                addDescriptor(descriptor, refs);
                addSignature(signature, refs);
                boolean[] methodIsClient = {false};
                // lambda / 合成方法是编译器从别的方法体里抽出来的，自己不带注解，
                // 只能看整个类有没有客户端方法可用作豁免依据。
                boolean synthetic = (access & Opcodes.ACC_SYNTHETIC) != 0 || name.startsWith("lambda$");

                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        if (ONLY_IN_DESC.equals(desc)) {
                            return clientDistMarker(methodIsClient);
                        }
                        return null;
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        addType(type, refs);
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name1, String descriptor) {
                        addType(owner, refs);
                        addDescriptor(descriptor, refs);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name1, String descriptor, boolean isInterface) {
                        addType(owner, refs);
                        addDescriptor(descriptor, refs);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name1, String descriptor, Handle bootstrapMethodHandle,
                                                      Object... bootstrapMethodArguments) {
                        addDescriptor(descriptor, refs);
                        addType(bootstrapMethodHandle.getOwner(), refs);
                        for (Object arg : bootstrapMethodArguments) {
                            if (arg instanceof Type type) {
                                addType(type, refs);
                            } else if (arg instanceof Handle handle) {
                                addType(handle.getOwner(), refs);
                            }
                        }
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        if (value instanceof Type type) {
                            addType(type, refs);
                        }
                    }

                    @Override
                    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                        addDescriptor(descriptor, refs);
                    }

                    @Override
                    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                        addType(type, refs);
                    }

                    @Override
                    public void visitEnd() {
                        if (methodIsClient[0]) {
                            hasClientMethod[0] = true;
                            return;
                        }
                        boolean exempt = classIsClient[0];
                        String where = className + "#" + name;
                        if (synthetic) {
                            // 合成方法的豁免留到类扫描结束时再定（见下面的回填）
                            SyntheticHits.add(where, refs);
                            return;
                        }
                        if (exempt) {
                            return;
                        }
                        for (String ref : refs) {
                            if (isClientClass(ref)) {
                                methodViolations.add(where + " -> " + readable(ref));
                            }
                        }
                    }
                };
            }
        };
        reader.accept(visitor, 0);

        // 回填：合成/lambda 方法只有在「整个类都没有客户端方法」时才算违规
        for (SyntheticHits.Hit hit : SyntheticHits.take()) {
            if (!classIsClient[0] && !hasClientMethod[0]) {
                for (String ref : hit.refs()) {
                    if (isClientClass(ref)) {
                        methodViolations.add(hit.where() + "（合成方法） -> " + readable(ref));
                    }
                }
            }
        }
        if (!classIsClient[0]) {
            for (String ref : classLevelRefs) {
                if (isClientClass(ref)) {
                    violations.add(className + "（类签名/字段） -> " + readable(ref));
                }
            }
        }
        violations.addAll(methodViolations);
    }

    private static AnnotationVisitor clientDistMarker(boolean[] flag) {
        return new AnnotationVisitor(Opcodes.ASM9) {
            @Override
            public void visitEnum(String name, String descriptor, String value) {
                if ("CLIENT".equals(value)) {
                    flag[0] = true;
                }
            }
        };
    }

    private static boolean isClientClass(String internalName) {
        if (internalName == null || internalName.isEmpty()) {
            return false;
        }
        String name = internalName.replace('\\', '/');
        return name.contains("/client/")
                || name.startsWith("com/mojang/blaze3d/")
                || name.startsWith("mezz/jei/");
    }

    private static String readable(String internalName) {
        return internalName.replace('/', '.');
    }

    private static void addType(String internalName, java.util.Collection<String> out) {
        if (internalName != null && !internalName.isEmpty()) {
            out.add(internalName);
        }
    }

    private static void addType(Type type, java.util.Collection<String> out) {
        if (type == null) {
            return;
        }
        if (type.getSort() == Type.ARRAY) {
            addType(type.getElementType(), out);
        } else if (type.getSort() == Type.OBJECT) {
            out.add(type.getInternalName());
        }
    }

    /** 字段描述符或方法描述符里出现的所有类型。 */
    private static void addDescriptor(String descriptor, java.util.Collection<String> out) {
        if (descriptor == null || descriptor.isEmpty()) {
            return;
        }
        if (descriptor.startsWith("(")) {
            Type method = Type.getMethodType(descriptor);
            for (Type argument : method.getArgumentTypes()) {
                addType(argument, out);
            }
            addType(method.getReturnType(), out);
            return;
        }
        addType(Type.getType(descriptor), out);
    }

    /** 泛型签名里的类型（Signature 属性，可能为 null）。 */
    private static void addSignature(String signature, java.util.Collection<String> out) {
        if (signature == null) {
            return;
        }
        Matcher matcher = SIGNATURE_TYPE.matcher(signature);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
    }

    /** 合成方法的命中记录：类扫完才知道能不能豁免，所以先攒着。 */
    private static final class SyntheticHits {
        private static final List<Hit> HITS = new ArrayList<>();

        private SyntheticHits() {
        }

        static void add(String where, Set<String> refs) {
            HITS.add(new Hit(where, refs));
        }

        static List<Hit> take() {
            List<Hit> copy = List.copyOf(HITS);
            HITS.clear();
            return copy;
        }

        record Hit(String where, Set<String> refs) {
        }
    }
}
