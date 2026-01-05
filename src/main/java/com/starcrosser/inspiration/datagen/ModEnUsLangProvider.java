package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {

    public ModEnUsLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.INSPIRATION.get(), "Inspiration");
        add(ModItems.COOKED_INSPIRATION.get(), "Cooked Inspiration");
        add(ModItems.LIGNITE.get(), "Lignite");

        add(ModBlocks.LIGNITE_BLOCK.get(), "Lignite Block");

        add("itemGroup.inspiration", "Inspiration");
    }
}
