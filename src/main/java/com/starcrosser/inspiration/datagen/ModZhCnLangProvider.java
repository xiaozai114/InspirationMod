package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {

    public ModZhCnLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.INSPIRATION.get(), "灵感");
        add(ModItems.COOKED_INSPIRATION.get(), "烤灵感");
        add(ModItems.LIGNITE.get(), "褐煤");

        add(ModBlocks.LIGNITE_BLOCK.get(), "褐煤块");

        add("itemGroup.inspiration", "灵感");
    }
}
