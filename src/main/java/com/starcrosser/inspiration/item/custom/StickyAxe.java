//todo: 写一个给铜除锈生成铜粒的逻辑

package com.starcrosser.inspiration.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StickyAxe extends AxeItem {
    // 定义可以被剥皮的原木方块
    private static final Set<Block> STRIPPABLE_LOGS = new HashSet<>();

    static {
        // 添加所有可剥皮的原木
        STRIPPABLE_LOGS.add(Blocks.OAK_LOG);
        STRIPPABLE_LOGS.add(Blocks.SPRUCE_LOG);
        STRIPPABLE_LOGS.add(Blocks.BIRCH_LOG);
        STRIPPABLE_LOGS.add(Blocks.JUNGLE_LOG);
        STRIPPABLE_LOGS.add(Blocks.ACACIA_LOG);
        STRIPPABLE_LOGS.add(Blocks.DARK_OAK_LOG);
        STRIPPABLE_LOGS.add(Blocks.MANGROVE_LOG);
        STRIPPABLE_LOGS.add(Blocks.CHERRY_LOG);

        // 1.20+ 的新原木
        STRIPPABLE_LOGS.add(Blocks.CRIMSON_STEM);
        STRIPPABLE_LOGS.add(Blocks.WARPED_STEM);


//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_OAK_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_SPRUCE_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_BIRCH_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_JUNGLE_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_ACACIA_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_DARK_OAK_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_MANGROVE_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_CHERRY_LOG);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_CRIMSON_STEM);
//        STRIPPABLE_LOGS.add(Blocks.STRIPPED_WARPED_STEM);
    }

    public StickyAxe(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }
    private boolean isStrippableLog(BlockState state) {
        return STRIPPABLE_LOGS.contains(state.getBlock());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        // 先让原版斧子处理剥皮逻辑
        InteractionResult result = super.useOn(context);
        if (isStrippableLog(state)) {
            dropStick(context, pos);
            ExperienceOrb orb = new ExperienceOrb(level ,pos.getX()+0.5,pos.getY()+1, pos.getZ()+0.5,1);
            level.addFreshEntity(orb);
        }



        return result;
    }

    public void dropStick(UseOnContext context, BlockPos pos) {
        Level level = context.getLevel();
        ItemEntity stickEntity = new ItemEntity(
                level,
                pos.getX()+0.5,
                pos.getY()+0.5+1,
                pos.getZ()+0.5,
                new ItemStack(Items.STICK, 1)
        );
        // 添加到世界中
        level.addFreshEntity(stickEntity);
    }
    /**
     * 添加工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 添加描述文本
        tooltip.add(Component.translatable("tooltip.inspiration.sticky_axe.description"));
    }
}
