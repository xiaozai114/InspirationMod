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

/**
 * 把草方块压成土径时额外掉落草皮与经验的铲。
 * <p>
 * 行为与 {@link ExperienceHoe} 对称：锄头锄草方块、铲子压土径，两者都吐草皮。
 * 只有把 <b>草方块</b> 压成土径才掉草皮（泥土、菌丝体等其它可压方块不掉），
 * 且必须是原版逻辑真的压成功了才算。
 */
public class ExperienceShovel extends ShovelItem implements ExperienceTool {

    private final int experiencePerAction;

    public ExperienceShovel(Tier tier, float attackDamage, float attackSpeed,
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
        if (state.getBlock() == Blocks.GRASS_BLOCK && result.consumesAction()) {
            dropSod(level, pos);
        }
        return result;
    }

    /** 土径压好后，在方块上方吐出草皮并把经验撒在原地。 */
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
        tooltip.add(Component.translatable("tooltip.inspiration.exp_shovel.description", experiencePerAction));
    }
}
