package com.starcrosser.inspiration.block.custom;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 浓缩灵感结晶：思维流体<b>源</b>遇到水时生成。
 * <p>
 * 与 {@link DilutedInspirationCrystalBlock} 同构，区别只在数据生成阶段：
 * 贴图几乎不透明、硬度更高、掉落的是完整的「灵感」而非碎片。
 */
public class ConcentratedInspirationCrystalBlock extends HalfTransparentBlock {

    public ConcentratedInspirationCrystalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
