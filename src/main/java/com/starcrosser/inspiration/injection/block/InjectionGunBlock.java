package com.starcrosser.inspiration.injection.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.injection.blockentity.InjectionGunTile;
import com.starcrosser.inspiration.injection.item.BindingWandItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 注入枪：注入结构的执行端，红石脉冲只作用在它身上。
 * <p>
 * 做成一门小炮：底部座 + 朝向前方伸出的炮管，不是满方块，所以能一眼看出枪口指向哪。
 * 六个方向都能摆（{@link RotationType#SIX_WAY}），绑定物质束缚器后由方块实体自动转向目标。
 * {@link #LINKED} 表示它是否已经绑定，绑定后换用亮起的枪口贴图。
 */
public class InjectionGunBlock extends RotatableBlock<InjectionGunTile> {

    /** 是否已绑定物质束缚器。由方块实体在绑定 / 解绑时改写。 */
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    /**
     * 炮身两段包围盒的两个角点，单位 1/16，枪口朝 {@link Direction#NORTH}。
     * 与 {@code assets/.../models/block/injection_gun.json} 的两个元素一一对应。
     */
    private static final double[][] BASE_BOX = {{3.0, 0.0, 3.0}, {13.0, 7.0, 15.0}};
    private static final double[][] BARREL_BOX = {{5.0, 5.0, 0.0}, {11.0, 11.0, 12.0}};

    /** 六个朝向各自的合并形状，构造一次即可。 */
    private static final Map<Direction, VoxelShape> SHAPES = buildShapes();

    public InjectionGunBlock() {
        super("injection_gun",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                InjectionGunTile.class);
        // BooleanProperty 的取值顺序是 true、false，any() 会拿到 true；
        // 朝向同理会落到 DOWN。这里显式声明默认状态，否则刚放下的枪就亮着「已绑定」的枪口。
        registerDefaultState(defaultBlockState()
                .setValue(LINKED, false)
                .setValue(RotatableBlock.FACING_ALL, Direction.NORTH));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<InjectionGunTile> getTileEntityFactory() {
        return InjectionGunTile::new;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LINKED);
    }

    /** 手持绑定器时让出这次交互，详见 {@link MatterBinderBlock} 的同名方法。 */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BindingWandItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /* ============================ 不完整方块 ============================ */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(facingOf(state), SHAPES.get(Direction.NORTH));
    }

    /** Titanium 的碰撞形状取自这里，返回空列表会让方块彻底没有碰撞体。 */
    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, BlockGetter level, BlockPos pos) {
        return List.of(getShape(state, level, pos, CollisionContext.empty()));
    }

    @Override
    public boolean hasCustomBoxes(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    /**
     * 从枪指向目标的方向：取差值最大的那个轴。
     * 目标可能不在同一水平面（比如正上方），所以这里必须考虑 Y 轴。
     */
    public static Direction facingTowards(BlockPos from, BlockPos to) {
        return Direction.getNearest(
                to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }

    private static Direction facingOf(BlockState state) {
        return state.hasProperty(RotatableBlock.FACING_ALL)
                ? state.getValue(RotatableBlock.FACING_ALL)
                : Direction.NORTH;
    }

    /* ============================ 形状的旋转 ============================ */

    private static Map<Direction, VoxelShape> buildShapes() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction facing : Direction.values()) {
            shapes.put(facing, Shapes.or(boxFor(BASE_BOX, facing), boxFor(BARREL_BOX, facing)));
        }
        return shapes;
    }

    /**
     * 把「朝北」的包围盒旋转到指定朝向。
     * <p>
     * 变换与方块状态里的 {@code rotationX / rotationY} 保持一致：
     * 水平四个方向只转 Y（EAST 是 {@code y=90}），UP 与 DOWN 分别对应 {@code x=270} 与 {@code x=90}。
     * 这样模型与碰撞盒永远指向同一边。
     */
    private static VoxelShape boxFor(double[][] box, Direction facing) {
        double[] a = rotate(box[0], facing);
        double[] b = rotate(box[1], facing);
        return Block.box(
                Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.min(a[2], b[2]),
                Math.max(a[0], b[0]), Math.max(a[1], b[1]), Math.max(a[2], b[2]));
    }

    private static double[] rotate(double[] point, Direction facing) {
        double x = point[0];
        double y = point[1];
        double z = point[2];
        return switch (facing) {
            case NORTH -> new double[]{x, y, z};
            case SOUTH -> new double[]{16.0 - x, y, 16.0 - z};
            case EAST -> new double[]{16.0 - z, y, x};
            case WEST -> new double[]{z, y, 16.0 - x};
            case UP -> new double[]{x, 16.0 - z, y};
            case DOWN -> new double[]{x, z, 16.0 - y};
        };
    }
}
