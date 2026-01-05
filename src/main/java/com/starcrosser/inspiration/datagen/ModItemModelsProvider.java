package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {

    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, Inspiration.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.INSPIRATION.get());
        basicItem(ModItems.COOKED_INSPIRATION.get());
        basicItem(ModItems.LIGNITE.get());
    }
}
