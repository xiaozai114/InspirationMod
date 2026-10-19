package com.starcrosser.inspiration.block.custom;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 稀释灵感结晶：流动的思维流体遇到水时生成。
 * <p>
 * 用 {@link HalfTransparentBlock} 作父类，得到「相邻同类方块之间不渲染内侧面」的行为，
 * 配合方块模型里的 {@code render_type: translucent} 呈现半透明效果。
 * 掉落物与挖掘要求由数据生成决定：需要镐，掉落灵感碎片并受时运影响。
 */
public class DilutedInspirationCrystalBlock extends HalfTransparentBlock {

    public DilutedInspirationCrystalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
