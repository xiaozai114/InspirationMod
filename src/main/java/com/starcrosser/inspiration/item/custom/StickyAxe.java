//todo: 写一个给铜除锈生成铜粒的逻辑

package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.tool.ExperienceDrops;
import com.starcrosser.inspiration.item.tool.ExperienceTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 给原木去皮时额外掉落木棍与经验的斧。 */
public class StickyAxe extends AxeItem implements ExperienceTool {

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
    }

    private final int experiencePerAction;

    public StickyAxe(Tier tier, float attackDamage, float attackSpeed,
                     int experiencePerAction, Properties properties) {
        super(tier, properties.attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed)));
        this.experiencePerAction = experiencePerAction;
    }

    @Override
    public int experiencePerAction() {
        return experiencePerAction;
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
            dropStick(level, pos);
            ExperienceDrops.spawnExperience(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    experiencePerAction);
        }

        return result;
    }

    private void dropStick(Level level, BlockPos pos) {
        ItemEntity stick = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5 + 1, pos.getZ() + 0.5,
                new ItemStack(Items.STICK, 1));
        level.addFreshEntity(stick);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.inspiration.sticky_axe.description"));
        tooltip.add(Component.translatable("tooltip.inspiration.sticky_axe.experience", experiencePerAction));
    }
}
