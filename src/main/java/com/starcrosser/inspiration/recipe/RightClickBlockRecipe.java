package com.starcrosser.inspiration.recipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
public record RightClickBlockRecipe(Block inputBlock, Block outputBlock, ItemStack inputItem,ItemStack outputItem)
        implements Recipe<RightClickBlockRecipe.RightClickBlockRecipeInput> {

    @Override
    public boolean matches(RightClickBlockRecipeInput input, Level level) {
        return input.block() == this.inputBlock &&
                ItemStack.isSameItemSameComponents(input.itemStack(), this.inputItem) &&
                input.itemStack().getCount() >= this.inputItem.getCount();
    }

    @Override
    public ItemStack assemble(RightClickBlockRecipeInput input, HolderLookup.Provider registries) {
        // 返回输出方块的物品形式（通常你不需要这个，但为了满足接口）
        return outputBlock.asItem().getDefaultInstance();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputBlock.asItem().getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RIGHT_CLICK_BLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RIGHT_CLICK_BLOCK_TYPE.get();
    }
    public record RightClickBlockRecipeInput(Block block, ItemStack itemStack) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public int size() {
            return 1; // 只有一个物品槽
        }

        @Override
        public ItemStack getItem(int index) {
            if (index == 0) {
                return itemStack;
            }
            return ItemStack.EMPTY;
        }

        // 可选：为了性能，也可以重写 isEmpty()
        @Override
        public boolean isEmpty() {
            return itemStack.isEmpty();
        }
    }
    // 为了方便使用，定义输入对象
}