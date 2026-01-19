package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ExperienceHoe extends HoeItem {
    public ExperienceHoe(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);


        InteractionResult result = super.useOn(context);
        if (state.getBlock() == Blocks.GRASS_BLOCK) {
            ExperienceOrb orb = new ExperienceOrb(level ,pos.getX()+0.5,pos.getY()+1, pos.getZ()+0.5,1);
            level.addFreshEntity(orb);
            ItemEntity stickEntity = new ItemEntity(
                    level,
                    pos.getX()+0.5,
                    pos.getY()+0.5+1,
                    pos.getZ()+0.5,
                    new ItemStack(ModItems.SOD.asItem(), 1)
            );
            // 添加到世界中
            level.addFreshEntity(stickEntity);
        }



        return result;
    }
}
