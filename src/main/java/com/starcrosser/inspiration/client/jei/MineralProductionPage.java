package com.starcrosser.inspiration.client.jei;

import java.util.ArrayList;
import java.util.List;

import com.starcrosser.inspiration.multiblock.data.DimensionDataItem;
import com.starcrosser.inspiration.multiblock.data.MineralTable;

public record MineralProductionPage(DimensionDataItem card, int index, List<MineralTable.Ore> outputs) {
    private static final int PAGE_SIZE = 8;

    public static List<MineralProductionPage> forCard(DimensionDataItem card) {
        var ores = card.table().ores();
        var pages = new ArrayList<MineralProductionPage>();
        for (int i = 0; i < ores.size(); i += PAGE_SIZE) {
            pages.add(new MineralProductionPage(card, i / PAGE_SIZE, ores.subList(i, Math.min(ores.size(), i + PAGE_SIZE))));
        }
        return pages;
    }
}
