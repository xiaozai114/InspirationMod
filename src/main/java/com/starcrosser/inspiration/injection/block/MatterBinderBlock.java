package com.starcrosser.inspiration.injection.block;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.injection.blockentity.MatterBinderTile;
import com.starcrosser.inspiration.injection.item.BindingWandItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * 物质束缚器：注入结构的中枢。
 * <p>
 * 做成一座矮台座（底盘 + 束腰立柱 + 顶面托盘），不是满方块；被束缚的那一份物质就摆在顶面上。
 * 不需要朝向，也不耗电，因此直接用最朴素的 {@link BasicTileBlock}。
 * 手持绑定器右键时交给绑定器处理，其余情况沿用 Titanium 的「开 GUI」行为。
 */
public class MatterBinderBlock extends BasicTileBlock<MatterBinderTile> {

    /**
     * 台座的三段包围盒（单位 1/16）：底盘、束腰立柱、顶面托盘。
     * 与 {@code assets/.../models/block/matter_binder.json} 里的三个元素一一对应。
     */
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.box(5.0, 2.0, 5.0, 11.0, 7.0, 11.0),
            Block.box(3.0, 7.0, 3.0, 13.0, 10.0, 13.0));

    public MatterBinderBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                MatterBinderTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MatterBinderTile> getTileEntityFactory() {
        return MatterBinderTile::new;
    }

    /**
     * 右键交互分三种情况，按优先级判定：
     * <ol>
     *     <li>手持绑定器：让给绑定器处理。必须返回 {@code SKIP_DEFAULT_BLOCK_INTERACTION}
     *         而不是 {@code PASS}，后者会继续走 {@code useWithoutItem}，物品就没机会处理了。</li>
     *     <li>潜行右键：打开 GUI（走 Titanium 的默认行为）。</li>
     *     <li>普通右键：槽空则放入手持的一份，槽里有东西则取回。</li>
     * </ol>
     * 这样束缚器像置物台一样用右键就能取放，不需要每次都开 GUI。
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BindingWandItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        // 失败时返回 CONSUME 而不是 PASS：PASS 会继续走 useWithoutItem，把取放逻辑再跑一遍
        return interact(level, pos, player, stack, hand)
                ? ItemInteractionResult.SUCCESS
                : ItemInteractionResult.CONSUME;
    }

    /** 空手右键：把承放的物质取回手上。 */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
        return interact(level, pos, player, ItemStack.EMPTY, InteractionHand.MAIN_HAND)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    /**
     * 取放逻辑：槽空就放入，槽里有东西就取回。
     * <p>
     * 只做「放入」会成功消耗手上的物品，取回只从主手/背包入手，因此都只需要服务端执行。
     */
    private static boolean interact(Level level, BlockPos pos, Player player, ItemStack held, InteractionHand hand) {
        if (!(level.getBlockEntity(pos) instanceof MatterBinderTile binder)) {
            return false;
        }
        boolean serverSide = !level.isClientSide();
        if (binder.getBase().isEmpty()) {
            if (held.isEmpty() || !serverSide) {
                return false;
            }
            int inserted = binder.insert(held);
            if (inserted <= 0) {
                return false;
            }
            held.shrink(inserted);
            player.setItemInHand(hand, held.isEmpty() ? ItemStack.EMPTY : held);
            return true;
        }
        if (!serverSide) {
            // 客户端也要返回 true，否则会继续尝试放置方块
            return true;
        }
        return binder.extractToPlayer(player);
    }

    /* ============================ 不完整方块 ============================ */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** Titanium 的碰撞形状取自这里，返回空列表会让方块彻底没有碰撞体。 */
    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, BlockGetter level, BlockPos pos) {
        return List.of(SHAPE);
    }

    @Override
    public boolean hasCustomBoxes(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
