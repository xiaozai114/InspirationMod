package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

/**
 * 结构模拟机五种仓室的公共基类。
 * <p>
 * 仓室自身不跑任何逻辑：它们只是**给控制器提供能力**的方块（物品槽 / 能量缓冲 /
 * 灵感充能罐），控制器成型后按类型把它们收集起来统一调度。仓室被拆掉或结构失效时，
 * 控制器会在下一轮检测里发现它不在了，结构随即失效。
 * <p>
 * 继承 {@link ActiveTile} 而不是 {@code BasicTile}：只有它带 {@code openGui}，
 * 物品类仓室要靠它打开自己的小界面。
 */
public abstract class HatchTile<T extends HatchTile<T>> extends ActiveTile<T> {

    protected HatchTile(BasicTileBlock<T> block, BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(block, type, pos, state);
    }

    /** 打开本仓室的界面。只有带槽位的仓室会覆写 {@link #onActivated} 调到这里。 */
    protected ItemInteractionResult openHatchGui(Player player) {
        openGui(player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                             double hitX, double hitY, double hitZ) {
        return openHatchGui(player);
    }
}
