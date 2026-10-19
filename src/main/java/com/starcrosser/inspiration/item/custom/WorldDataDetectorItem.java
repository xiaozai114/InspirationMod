package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.injection.InjectionWindow;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 世界数据探测器：只给创造模式用的量具。
 * <p>
 * 有些注入配方的能量窗口由**存档种子**随机决定，玩家在 JEI 里只会看到 {@code ???}。
 * 这件工具就是唯一的读数入口：右键把本存档所有「种子随机」注入配方的实际窗口打在聊天框里。
 * 没有合成配方，也不会出现在任何战利品表里，只能从创造物品栏拿。
 */
public class WorldDataDetectorItem extends Item {

    public WorldDataDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            report(serverLevel, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** 把每条种子随机配方的窗口报给玩家。窗口必须由服务端算，客户端拿不到世界种子。 */
    private static void report(ServerLevel level, Player player) {
        player.displayClientMessage(Component.translatable("item.inspiration.world_data_detector.header",
                Long.toString(level.getSeed())).withStyle(ChatFormatting.AQUA), false);

        int found = 0;
        for (RecipeHolder<InjectionRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.INJECTION_TYPE.get())) {
            if (!holder.value().seeded()) {
                continue;
            }
            InjectionWindow window = InjectionWindow.of(level, holder);
            if (window == null) {
                continue;
            }
            found++;
            player.displayClientMessage(Component.translatable("item.inspiration.world_data_detector.line",
                    holder.value().result().getHoverName().copy().withStyle(ChatFormatting.LIGHT_PURPLE),
                    Component.literal(window.describe()).withStyle(ChatFormatting.GOLD)), false);
        }
        if (found == 0) {
            player.displayClientMessage(
                    Component.translatable("item.inspiration.world_data_detector.empty")
                            .withStyle(ChatFormatting.GRAY), false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.inspiration.world_data_detector.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
