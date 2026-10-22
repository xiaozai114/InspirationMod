package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.alkali.AlkalinityDataMaps;
import com.starcrosser.inspiration.material.InspirationMaterial;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.radiation.RadiationDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

/**
 * 碱性值与辐射值两张 data_map 的生成器。
 * <p>
 * 以前这两个文件是手写的（{@code src/main/resources/data/inspiration/data_maps/}），
 * 现在交给 datagen：材料自带的条目由 {@link InspirationMaterial#addDataMaps} 出，
 * 不属于锭类材料的东西（粉、盐、矿）在这里登记。
 * 生成物与手写物同为 {@code data/inspiration/data_maps/...}，两边都留会撞同名文件，
 * 所以手写那三份已经删掉。
 */
public class ModDataMapsProvider extends DataMapProvider {

    public ModDataMapsProvider(PackOutput packOutput,
                               CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path);
    }

    @Override
    protected void gather(HolderLookup.Provider lookupProvider) {
        Builder<Integer, Item> alkalinity = builder(AlkalinityDataMaps.ITEM_ALKALINITY);
        Builder<Integer, Item> itemRadiation = builder(RadiationDataMaps.ITEM_RADIATION);
        Builder<Integer, Block> blockRadiation = builder(RadiationDataMaps.BLOCK_RADIATION);

        /* ---- 碱性：只属于物品（放进碱性发电机每刻产电「100 × 值」FE） ----
         * 按物品 ID 字母序登记，与原先手写文件的排列一致。 */
        alkalinity.add(id("calcium_dust"), 1, false);
        alkalinity.add(id("cesium_dust"), 4, false);
        for (InspirationMaterial material : ModMaterials.ALL) {
            material.addDataMaps(alkalinity, null, null);
        }
        alkalinity.add(id("francium_dust"), 6, false);
        // 钫锭等钫材料迁进 ModMaterials 后，这一行由材料自己出
        alkalinity.add(id("francium_ingot"), 6, false);

        /* ---- 辐射：按「钫的处理流程」排列（粗盐 → 盐 → 粉 → 锭 → 矿 → 盐块 → 块） ---- */
        itemRadiation.add(id("raw_francium_salt"), 1, false);
        itemRadiation.add(id("francium_salt"), 1, false);
        itemRadiation.add(id("francium_dust"), 3, false);
        // 钫锭：迁进 ModMaterials 后由材料自己出
        itemRadiation.add(id("francium_ingot"), 3, false);
        itemRadiation.add(id("francium_salt_ore"), 1, false);
        itemRadiation.add(id("francium_salt_block"), 9, false);
        // 钫块：同上
        itemRadiation.add(id("francium_block"), 27, false);

        blockRadiation.add(id("francium_salt_ore"), 1, false);
        blockRadiation.add(id("francium_salt_block"), 9, false);
        blockRadiation.add(id("francium_block"), 27, false);
    }
}
