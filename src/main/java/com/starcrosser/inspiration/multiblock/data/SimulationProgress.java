package com.starcrosser.inspiration.multiblock.data;

import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** Associates saved work with a card type, including saves made before multiple cards existed. */
public final class SimulationProgress {
    private static final String CARD_KEY = "SimulationCard";
    private final ResourceLocation legacyCard;
    private ResourceLocation card;

    public SimulationProgress(String legacyPath) {
        this.legacyCard = ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, legacyPath);
        this.card = legacyCard;
    }

    public boolean bind(Item next, int duration, ProgressBarComponent<?> bar) {
        var id = BuiltInRegistries.ITEM.getKey(next);
        if (!id.equals(card)) {
            card = id;
            bar.setProgress(0);
            bar.setMaxProgress(duration);
            return true;
        }
        int previous = bar.getMaxProgress();
        if (duration == previous) {
            return false;
        }
        // Retuning a recipe preserves the fraction completed in an existing save.
        int scaled = previous > 0 ? (int) ((long) bar.getProgress() * duration / previous) : 0;
        bar.setProgress(Math.clamp(scaled, 0, duration - 1));
        bar.setMaxProgress(duration);
        return true;
    }

    public void save(CompoundTag tag) {
        tag.putString(CARD_KEY, card.toString());
    }

    public void load(CompoundTag tag) {
        var saved = tag.contains(CARD_KEY) ? ResourceLocation.tryParse(tag.getString(CARD_KEY)) : null;
        card = saved != null ? saved : legacyCard;
    }
}
