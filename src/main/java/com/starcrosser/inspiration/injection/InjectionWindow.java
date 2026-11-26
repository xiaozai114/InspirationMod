package com.starcrosser.inspiration.injection;

import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 一条注入配方在**当前世界**里实际生效的能量窗口。
 * <p>
 * 固定配方直接取 JSON 里写的数；种子随机配方（{@code seeded_energy = true}）
 * 按存档种子现算，同一存档恒定、换存档就变。
 */
public record InjectionWindow(int min, int max) {

    /**
     * 解析配方窗口。
     *
     * @return 种子随机配方在拿不到世界种子时（例如客户端）返回 {@code null}
     */
    @Nullable
    public static InjectionWindow of(@Nullable Level level, RecipeHolder<InjectionRecipe> holder) {
        InjectionRecipe recipe = holder.value();
        if (!recipe.seeded()) {
            return new InjectionWindow(recipe.minEnergy(), recipe.maxEnergy());
        }
        SeededEnergyWindow window = level == null ? null : SeededEnergyWindow.of(level, recipe.windowSource().orElse(holder.id()));
        return window == null ? null : new InjectionWindow(window.minEnergy(), window.maxEnergy());
    }

    /** 形如 {@code 13,168 ~ 26,337 FE}。 */
    public String describe() {
        return String.format("%,d ~ %,d FE", min, max);
    }
}
