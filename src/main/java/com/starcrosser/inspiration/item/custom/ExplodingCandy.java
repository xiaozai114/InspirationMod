package com.starcrosser.inspiration.item.custom;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExplodingCandy extends Item {
    public ExplodingCandy(Item.Properties properties) {
        super(properties);
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        livingEntity.level().explode(
                livingEntity,  // 爆炸源实体
                livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                1.0F,  // 使用自定义的爆炸威力
                Level.ExplosionInteraction.TNT  // 模拟 TNT 的交互行为
        );
        return super.finishUsingItem(stack, level, livingEntity);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 添加描述文本
        tooltip.add(Component.translatable("tooltip.inspiration.exploding_candy.description"));
    }
}
