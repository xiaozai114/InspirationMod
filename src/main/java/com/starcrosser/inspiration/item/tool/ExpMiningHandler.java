package com.starcrosser.inspiration.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/** 挖掘方块的经验产出：手持实现了 {@link MiningExperienceTool} 的工具破坏方块时撒出经验。 */
@EventBusSubscriber(modid = "inspiration")
public class ExpMiningHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof MiningExperienceTool tool)) {
            return;
        }

        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        Level level = player.level();
        ExperienceDrops.spawnExperience(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                tool.experiencePerAction());
    }
}
