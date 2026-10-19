package com.starcrosser.inspiration.recipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.util.BlockItemLookup;
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

    /**
     * 判断机器输入是否满足本配方。
     * <p>
     * 与 {@link #matches} 语义一致，区别在于方块用其物品形态（见
     * {@link com.starcrosser.inspiration.util.BlockItemLookup}）比对，供机器与 JEI 复用。
     *
     * @param blockStack 第一个槽位：待转化的方块（物品形态）
     * @param toolStack  第二个槽位：手持物品
     */
    public boolean matchesStacks(ItemStack blockStack, ItemStack toolStack) {
        if (blockStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }
        ItemStack blockIcon = BlockItemLookup.iconOf(inputBlock);
        if (blockIcon.isEmpty() || !ItemStack.isSameItem(blockStack, blockIcon)) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(toolStack, inputItem)
                && toolStack.getCount() >= inputItem.getCount();
    }

    /**
     * 配方的展示用产物。
     * <p>
     * 结果有两种表达方式：替换成的方块，或直接掉落的物品；掉落物优先，两者都为空时返回空物品。
     */
    public ItemStack displayResult() {
        if (!outputItem.isEmpty()) {
            return outputItem;
        }
        return BlockItemLookup.iconOf(outputBlock);
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