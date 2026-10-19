package com.starcrosser.inspiration.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 灵感凝胶：流动的思维流体遇到岩浆时生成。
 * <p>
 * 直接继承原版的 {@link SlimeBlock}，弹跳、缓冲坠落伤害、踩上去减速这些行为
 * 全部来自父类，本类只需提供自己的外观与属性。目前没有额外特性。
 * <p>
 * 注意：{@code codec()} 的返回类型必须与父类保持一致（{@code MapCodec<SlimeBlock>}），
 * 泛型不支持向下收窄，只能让编解码器构造出本类的实例。
 */
public class InspirationGelBlock extends SlimeBlock {

    public InspirationGelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SlimeBlock> codec() {
        return simpleCodec(InspirationGelBlock::new);
    }
}
