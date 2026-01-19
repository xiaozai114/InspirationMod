package com.starcrosser.inspiration.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.minecraft.world.level.block.SculkSensorBlock.COOLDOWN_TICKS;

public class BedrockPickaxe extends PickaxeItem {
    public BedrockPickaxe(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        // 允许挖掘基岩
        if (state.is(Blocks.BEDROCK)) {
            return true;
        }
        return super.canAttackBlock(state, level, pos, player);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        InteractionResult result = super.useOn(context);
        ItemEntity bedrockItem = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    new ItemStack(state.getBlock().asItem(), 1)
        );
            level.addFreshEntity(bedrockItem);
            // 移除基岩方块
            level.removeBlock(pos, false);
        return result;


    }
//
//    @Override
//    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
//        if (state.getBlock() == Blocks.BEDROCK) {
//                ItemEntity bedrockItem = new ItemEntity(
//                    level,
//                    pos.getX() + 0.5,
//                    pos.getY() + 0.5,
//                    pos.getZ() + 0.5,
//                    new ItemStack(Blocks.BEDROCK.asItem(), 1)
//                );
//                level.addFreshEntity(bedrockItem);
//                // 移除基岩方块
//                level.removeBlock(pos, false);
//                return true;
//            }
//        return super.mineBlock(stack, level, state, pos, entityLiving);
//    }
//    @Override
//    public float getDestroySpeed(ItemStack stack, BlockState state) {
//        if (state.is(Blocks.BEDROCK)) {
//            // 给基岩一个正常的挖掘速度（原版是-1，无法挖掘）
//            return -1.0F; // 非常快的挖掘速度
//        }
//        return super.getDestroySpeed(stack, state);
//    }
//    @Override
//    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
//        if (state.is(Blocks.BEDROCK)) {
//            return true; // 允许掉落基岩
//        }
//        return super.isCorrectToolForDrops(stack, state);
//    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 添加描述文本
        tooltip.add(Component.literal("§8??????????"));
    }
}
