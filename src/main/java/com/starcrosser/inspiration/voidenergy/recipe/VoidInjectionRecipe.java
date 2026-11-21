package com.starcrosser.inspiration.voidenergy.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 虚空注入配方：只认虚空能量（ve），不涉及 FE。
 * <p>
 * 判定用的是「团簇当前能量的 10%」而不是能量本身 —— 枪那边算好 {@code cost} 再和
 * {@link #minVe()} / {@link #maxVe()} 比：高于上限过载爆炸、低于下限不生效。
 * {@code inject} 可空：弱化核心、虚空等离子锭这类「只往底材里灌能量」的配方没有第二件材料。
 */
public class VoidInjectionRecipe implements Recipe<VoidInjectionRecipeInput> {

    public static final MapCodec<VoidInjectionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(VoidInjectionRecipe::base),
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("inject")
                    .forGetter(recipe -> Optional.ofNullable(recipe.inject())),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("tier", 1).forGetter(VoidInjectionRecipe::tier),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_ve").forGetter(VoidInjectionRecipe::minVe),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_ve").forGetter(VoidInjectionRecipe::maxVe),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(VoidInjectionRecipe::result),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", 100).forGetter(VoidInjectionRecipe::duration)
    ).apply(instance, VoidInjectionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VoidInjectionRecipe> STREAM_CODEC =
            StreamCodec.of(VoidInjectionRecipe::encode, VoidInjectionRecipe::decode);

    private final Ingredient base;
    @Nullable
    private final Ingredient inject;
    private final int tier;
    private final int minVe;
    private final int maxVe;
    private final ItemStack result;
    private final int duration;

    public VoidInjectionRecipe(Ingredient base, @Nullable Ingredient inject, int tier,
                               int minVe, int maxVe, ItemStack result, int duration) {
        this.base = base;
        this.inject = inject;
        this.tier = tier;
        this.minVe = minVe;
        this.maxVe = maxVe;
        this.result = result;
        this.duration = duration;
    }

    private VoidInjectionRecipe(Ingredient base, Optional<Ingredient> inject, int tier,
                                int minVe, int maxVe, ItemStack result, int duration) {
        this(base, inject.orElse(null), tier, minVe, maxVe, result, duration);
    }

    public Ingredient base() {
        return base;
    }

    @Nullable
    public Ingredient inject() {
        return inject;
    }

    public int tier() {
        return tier;
    }

    public int minVe() {
        return minVe;
    }

    public int maxVe() {
        return maxVe;
    }

    public ItemStack result() {
        return result;
    }

    public int duration() {
        return duration;
    }

    @Override
    public boolean matches(VoidInjectionRecipeInput input, Level level) {
        if (!base.test(input.base())) {
            return false;
        }
        return inject == null || inject.test(input.inject());
    }

    @Override
    public ItemStack assemble(VoidInjectionRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.VOID_INJECTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.VOID_INJECTION_TYPE.get();
    }

    private static void encode(RegistryFriendlyByteBuf buffer, VoidInjectionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
        buffer.writeBoolean(recipe.inject != null);
        if (recipe.inject != null) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.inject);
        }
        buffer.writeVarInt(recipe.tier);
        buffer.writeVarInt(recipe.minVe);
        buffer.writeVarInt(recipe.maxVe);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        ByteBufCodecs.VAR_INT.encode(buffer, recipe.duration);
    }

    private static VoidInjectionRecipe decode(RegistryFriendlyByteBuf buffer) {
        Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient inject = buffer.readBoolean() ? Ingredient.CONTENTS_STREAM_CODEC.decode(buffer) : null;
        int tier = buffer.readVarInt();
        int minVe = buffer.readVarInt();
        int maxVe = buffer.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        int duration = ByteBufCodecs.VAR_INT.decode(buffer);
        return new VoidInjectionRecipe(base, inject, tier, minVe, maxVe, result, duration);
    }
}
