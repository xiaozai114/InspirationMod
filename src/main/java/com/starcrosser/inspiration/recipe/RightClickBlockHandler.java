package com.starcrosser.inspiration.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.control.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
@EventBusSubscriber(modid = Inspiration.MODID)
public class RightClickBlockHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) return;

        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block clickedBlock = state.getBlock();
        ItemStack heldItem = event.getItemStack();

        // 调试日志：打印点击的方块和手持物品
        Inspiration.LOGGER.info("Clicked block: {}, Held item: {}",
                BuiltInRegistries.BLOCK.getKey(clickedBlock),
                heldItem.getItem());

        RightClickBlockRecipe.RightClickBlockRecipeInput input = new RightClickBlockRecipe.RightClickBlockRecipeInput(clickedBlock, heldItem);
        Optional<RecipeHolder<RightClickBlockRecipe>> optionalHolder = level.getRecipeManager()
                .getRecipeFor(ModRecipes.RIGHT_CLICK_BLOCK_TYPE.get(), input, level);

        if (optionalHolder.isPresent()) {
            RecipeHolder<RightClickBlockRecipe> holder = optionalHolder.get();
            RightClickBlockRecipe recipe = holder.value();

            // 二次确认：再次手动检查输入条件（防御性编程）
            if (recipe.matches(input, level)) {
                Inspiration.LOGGER.info("Recipe matched! Converting block.");
                // 执行转换
                level.setBlock(pos, recipe.outputBlock().defaultBlockState(), Block.UPDATE_ALL);
                ItemStack outputItem = recipe.outputItem();
                if (!outputItem.isEmpty()) {
                    Block.popResource(level, pos.above(), outputItem.copy());
                }
                for (int i = 0; i < 40; i++) {
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .125f);
                    level.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), motion.x, motion.y, motion.z);
                    Vec3 circle = motion.multiply(1, 0, 1)
                            .normalize()
                            .scale(.5f);
                    level.addParticle(ParticleTypes.SMOKE, circle.x, pos.getY(), circle.z, 0, -0.125, 0);
                }
                if (!player.isCreative()) {
                    heldItem.shrink(recipe.inputItem().getCount());
                }
            } else {
                // 理论上不应该进入这里，但如果有 bug 则记录
                Inspiration.LOGGER.warn("Recipe holder present but matches() returned false!");
            }
        } else {
            // 没有匹配的配方：绝对不取消事件
            Inspiration.LOGGER.info("No recipe matched. Event not cancelled.");
        }
        // 注意：这里没有额外的 setCanceled}
    }
}