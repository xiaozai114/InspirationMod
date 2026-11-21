package com.starcrosser.inspiration.voidenergy.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 开裂的基岩：幽匿合金钻头挖到基岩时的产物。
 * <p>
 * 与基岩一样不可破坏（硬度 -1），它只是「虚空能量提取器」的合法底座，
 * 掉落表为空 —— 不存在被刷成可携带方块的可能。
 */
public class CrackedBedrockBlock extends Block {

    public CrackedBedrockBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .strength(-1.0F, 3_600_000.0F)
                .sound(SoundType.DEEPSLATE)
                .requiresCorrectToolForDrops());
    }
}
