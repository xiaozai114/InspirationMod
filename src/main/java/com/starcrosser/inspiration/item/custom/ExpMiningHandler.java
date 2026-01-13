package com.starcrosser.inspiration.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid="inspiration")
public class ExpMiningHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        ItemStack heldItem = player.getMainHandItem();
        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        Level level=player.level();

        // 检查是否使用经验镐
        if (heldItem.getItem() instanceof ExperiencePickaxe) {

                ExperienceOrb orb = new ExperienceOrb(level ,pos.getX()+0.5,pos.getY()+0.5, pos.getZ()+0.5,1);
                level.addFreshEntity(orb);
            }
        }
    }