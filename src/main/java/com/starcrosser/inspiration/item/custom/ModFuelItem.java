package com.starcrosser.inspiration.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

public class ModFuelItem extends Item {
    private int burnTime=0;
    public ModFuelItem(Item.Properties properties,int burnTime) {
        super(properties);
        this.burnTime=burnTime;
    }
    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return burnTime;
    }
}
