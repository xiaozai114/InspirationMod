package com.starcrosser.inspiration.datagen;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.block.custom.EmpoweredTNTBlock;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.injection.block.InjectionGunBlock;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import com.starcrosser.inspiration.multiblock.block.HatchBlock;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.material.InspirationMaterial;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockStatesProvider extends BlockStateProvider {

    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Inspiration.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 褐煤块
        simpleBlock(ModBlocks.LIGNITE_BLOCK.get(), cubeAll(ModBlocks.LIGNITE_BLOCK.get()));
        // 红石锭块
        // 青金石锭块
        // 无恒矿石
        simpleBlock(ModBlocks.EPHEMERAL_ORE.get(), cubeAll(ModBlocks.EPHEMERAL_ORE.get()));
        simpleBlock(ModBlocks.CESIUM_SALT_ORE.get(), cubeAll(ModBlocks.CESIUM_SALT_ORE.get()));
        simpleBlock(ModBlocks.MIND_FLUID_BLOCK.get(), cubeAll(ModBlocks.MIND_FLUID_BLOCK.get()));
        simpleBlock(ModBlocks.CESIUM_SALT_BLOCK.get(), cubeAll(ModBlocks.CESIUM_SALT_BLOCK.get()));
        // 材料块：六面同贴图，遍历清单
        for (InspirationMaterial material : ModMaterials.ALL) {
            if (material.hasBlock()) {
                simpleBlock(material.block().get(), cubeAll(material.block().get()));
            }
        }
        // 钫盐系列：矿石与两种块，形状同铯盐系列
        simpleBlock(ModBlocks.FRANCIUM_SALT_ORE.get(), cubeAll(ModBlocks.FRANCIUM_SALT_ORE.get()));
        simpleBlock(ModBlocks.FRANCIUM_SALT_BLOCK.get(), cubeAll(ModBlocks.FRANCIUM_SALT_BLOCK.get()));
        // 思维流体反应生成的方块
        translucentBlock(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get());
        translucentBlock(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get());
        translucentBlock(ModBlocks.INSPIRATION_GEL.get());
        simpleBlock(ModBlocks.MIND_CRYSTAL_ROCK.get(), cubeAll(ModBlocks.MIND_CRYSTAL_ROCK.get()));

        // 高能机器外壳：结构模拟机的机体材料
        simpleBlock(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get(), cubeAll(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get()));
        simpleBlock(ModVoidEnergy.CRACKED_BEDROCK.get(), cubeAll(ModVoidEnergy.CRACKED_BEDROCK.get()));
        simpleBlock(ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get(), cubeAll(ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get()));
        simpleBlock(ModVoidEnergy.VOID_ENERGY_RELAY.get(), cubeAll(ModVoidEnergy.VOID_ENERGY_RELAY.get()));
        // 结构模拟机的五种仓室：没有朝向，六面同图，靠面板上的标记区分
        hatchBlock(ModMultiblocks.INPUT_HATCH);
        hatchBlock(ModMultiblocks.OUTPUT_HATCH);
        hatchBlock(ModMultiblocks.ENERGY_PORT);
        hatchBlock(ModMultiblocks.INSPIRATION_HATCH);
        hatchBlock(ModMultiblocks.DATA_HATCH);

        // 玄武岩合金块：注入结构的 1 级底座

        IntensifiedTNTBlock();
        EmpoweredTNTBlock();
        // 11 台机器：遍历 MachineRegistry，加机器不用再往这里补一行
        for (MachineRegistry.MachineEntry machine : MachineRegistry.machines()) {
            machineBlock(machine.machine());
        }
        // 结构模拟机的主方块：与单方块机器一样按 subfacing 转四个朝向
        machineBlock(ModMultiblocks.STRUCTURE_SIMULATOR);

        // 注入结构：两个不完整方块
        matterBinder();
        injectionGun();
    }

    /**
     * 物质束缚器：一座矮台座（底盘 + 束腰立柱 + 顶面托盘），顶面是束缚环。
     * <p>
     * 元素坐标与 {@link com.starcrosser.inspiration.injection.block.MatterBinderBlock} 的
     * 包围盒严格一致，否则会出现「看得见摸不着」。
     */
    private void matterBinder() {
        Block block = ModInjection.MATTER_BINDER.getBlock();
        BlockModelBuilder model = blockModel("matter_binder", "matter_binder_side")
                .texture("top", modLoc("block/matter_binder_top"));

        element(model, 2, 0, 2, 14, 2, 14, "#side", null);
        element(model, 5, 2, 5, 11, 7, 11, "#side", null);
        element(model, 3, 7, 3, 13, 10, 13, "#side", "#top");

        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    /**
     * 注入枪：底部座 + 朝 {@link Direction#NORTH} 伸出的炮管，绑定后换用亮起的枪口贴图。
     * <p>
     * 六个方向都能摆，靠方块状态里的 {@code rotationX / rotationY} 把「朝北」的模型转过去；
     * UP 对应 {@code x=270}、DOWN 对应 {@code x=90}，与 {@code minecraft:block/barrel} 的约定相同。
     */
    private void injectionGun() {
        Block block = ModInjection.INJECTION_GUN.getBlock();
        ModelFile idle = gunModel("injection_gun", "injection_gun_front");
        ModelFile linked = gunModel("injection_gun_linked", "injection_gun_front_linked");

        getVariantBuilder(block).forAllStates(state -> {
            Direction direction = state.getValue(RotatableBlock.FACING_ALL);
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder()
                    .modelFile(state.getValue(InjectionGunBlock.LINKED) ? linked : idle);
            return switch (direction) {
                case UP -> builder.rotationX(270).build();
                case DOWN -> builder.rotationX(90).build();
                default -> builder.rotationY(((int) direction.toYRot() + 180) % 360).build();
            };
        });
        simpleBlockItem(block, idle);
    }

    private ModelFile gunModel(String name, String muzzle) {
        BlockModelBuilder model = blockModel(name, "injection_gun_side")
                .texture("front", modLoc("block/" + muzzle))
                .texture("top", modLoc("block/injection_gun_top"));
        // 底部座
        element(model, 3, 0, 3, 13, 7, 15, "#side", null);
        // 炮管：前端面是枪口，顶面是带准星的导轨
        model.element()
                .from(5, 5, 0)
                .to(11, 11, 12)
                .allFaces((direction, face) -> face.texture("#side").uvs(0, 0, 16, 16))
                .face(Direction.NORTH).texture("#front").uvs(0, 0, 16, 16).end()
                .face(Direction.UP).texture("#top").uvs(0, 0, 16, 16).end()
                .end();
        return model;
    }

    /**
     * 起一个以 {@code minecraft:block/block} 为父模型的方块模型。
     * 父模型带物品栏的展示变换，物品模型直接继承它就能正常立起来。
     */
    private BlockModelBuilder blockModel(String name, String sideTexture) {
        return models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/" + sideTexture))
                .texture("side", modLoc("block/" + sideTexture));
    }

    /**
     * 加一个长方体元素：所有面默认铺满同一张贴图（{@code uvs(0,0,16,16)}），
     * {@code top} 非空时顶面单独换贴图。
     */
    private BlockModelBuilder element(BlockModelBuilder model, int x0, int y0, int z0,
                                      int x1, int y1, int z1, String side, @Nullable String top) {
        return model.element()
                .from(x0, y0, z0)
                .to(x1, y1, z1)
                .allFaces((direction, face) -> face
                        .texture(direction == Direction.UP && top != null ? top : side)
                        .uvs(0, 0, 16, 16))
                .end();
    }

    /**
     * 半透明方块：在方块模型里写明 {@code render_type: translucent}，
     * 否则贴图的 alpha 会被忽略、看上去是实心的。
     * <p>
     * 这是 1.19 之后 NeoForge 推荐的写法，优于在客户端调
     * {@code ItemBlockRenderTypes#setRenderLayer}（该方法已废弃）。
     */
    private void translucentBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile model = models().cubeAll(name, modLoc("block/" + name)).renderType("translucent");
        simpleBlock(block, model);
    }

    /**
     * 机器方块：正面带工作面板，其余面为机身。
     * <p>
     * 这里不用 {@code horizontalBlock}，因为 Titanium 的朝向属性名是 {@code subfacing}，
     * 与 {@code BlockStateProperties.HORIZONTAL_FACING} 不通用，需要手动构建变体。
     * 纹理按 {@code <机器名>_front / _side / _top} 约定命名。
     */
    private void machineBlock(BlockWithTile machine) {
        Block block = machine.getBlock();
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile model = models().orientable(name,
                modLoc("block/" + name + "_side"),
                modLoc("block/" + name + "_front"),
                modLoc("block/" + name + "_top"));
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            // 与 BlockStateProvider 的 DEFAULT_ANGLE_OFFSET 一致：正面朝向该方向需要额外旋转 180°
            getVariantBuilder(block)
                    .partialState()
                    .with(RotatableBlock.FACING_HORIZONTAL, direction)
                    .modelForState()
                    .modelFile(model)
                    .rotationY(((int) direction.toYRot() + 180) % 360)
                    .addModel();
        }
        simpleBlockItem(block, model);
    }

    /**
     * 仓室方块：{@link HatchBlock} 没有朝向属性，所以六面共用一张图 ——
     * 面板中央那枚标记就是它的身份。
     */
    private void hatchBlock(BlockWithTile hatch) {
        Block block = hatch.getBlock();
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile model = models().cubeAll(name, modLoc("block/" + name));
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void IntensifiedTNTBlock() {
        // 1. 定义纹理：假设你已经把纹理图片放在src/main/resources/assets/your_modid/textures/block/目录下
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.TOP, modLoc("block/intensified_tnt_block_top_bottom")) // 顶部纹理
                .put(TextureSlot.BOTTOM, modLoc("block/intensified_tnt_block_top_bottom")) // 底部纹理，此处与顶部相同
                .put(TextureSlot.SIDE, modLoc("block/intensified_tnt_block_block_side"));       // 侧面纹理

        // 2. 使用cubeBottomTop模型框架生成模型文件
        ModelFile modelFile = models().cubeBottomTop(
                "intensified_tnt",                       // 模型名称
                textures.get(TextureSlot.SIDE),          // 侧面纹理
                textures.get(TextureSlot.BOTTOM),        // 底部纹理
                textures.get(TextureSlot.TOP)            // 顶部纹理
        );

        // 3. 生成方块状态文件和物品模型
        simpleBlock(ModBlocks.INTENSIFIED_TNT.get(), modelFile);
        simpleBlockItem(ModBlocks.INTENSIFIED_TNT.get(), modelFile);
    }



    private void EmpoweredTNTBlock() {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.TOP, modLoc("block/empowered_tnt_block_top_bottom"))
                .put(TextureSlot.BOTTOM, modLoc("block/empowered_tnt_block_top_bottom"))
                .put(TextureSlot.SIDE, modLoc("block/empowered_tnt_block_side"));

        ModelFile modelFile = models().cubeBottomTop(
                "empowered_tnt",
                textures.get(TextureSlot.SIDE),
                textures.get(TextureSlot.BOTTOM),
                textures.get(TextureSlot.TOP)
        );

        simpleBlock(ModBlocks.EMPOWERED_TNT.get(), modelFile);
        simpleBlockItem(ModBlocks.EMPOWERED_TNT.get(), modelFile);
    }
}