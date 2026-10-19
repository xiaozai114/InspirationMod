package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.item.tool.ExperienceDrops;
import com.starcrosser.inspiration.item.tool.ExperienceTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** 锄草方块时同时产出草皮与经验的锄。 */
public class ExperienceHoe extends HoeItem implements ExperienceTool {

    private final int experiencePerAction;

    public ExperienceHoe(Tier tier, float attackDamage, float attackSpeed,
                         int experiencePerAction, Properties properties) {
        super(tier, properties.attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed)));
        this.experiencePerAction = experiencePerAction;
    }

    @Override
    public int experiencePerAction() {
        return experiencePerAction;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        InteractionResult result = super.useOn(context);
        if (state.getBlock() == Blocks.GRASS_BLOCK) {
            dropSod(level, pos);
        }
        return result;
    }

    /** 草被锄掉后，在方块上方吐出草皮并把经验撒在原地。 */
    private void dropSod(Level level, BlockPos pos) {
        ExperienceDrops.spawnExperience(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                experiencePerAction);
        ItemEntity sod = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5 + 1, pos.getZ() + 0.5,
                new ItemStack(ModItems.SOD.asItem(), 1));
        level.addFreshEntity(sod);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.inspiration.exp_hoe.description", experiencePerAction));
    }
}
