package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 「材料声明生成熔融金属」的唯一入口。
 * <p>
 * 一条熔融金属 = 流体类型 + 源 + 流动 + 桶，四样一起登记，命名全部由 {@code molten_<base>} 派生：
 * 流体 {@code molten_<base>}、流动 {@code flowing_molten_<base>}、桶 {@code molten_<base>_bucket}。
 * <p>
 * 贴图不在这里管：客户端扩展按流体 id 找 {@code block/molten_<base>_still} 与 {@code _flowing}，
 * 缺图时用 `tools/textures/gen_molten_metals.py` 补（它只补缺失的文件，不会覆盖已有的）。
 */
public final class MoltenMetals {

    /** 全部已登记的熔融金属，客户端扩展与配方生成都遍历它。 */
    public static final List<Entry> ALL = new ArrayList<>();

    /**
     * @param fluidId 流体 id（一般就是 {@code molten_<base>}）
     * @param tint    ARGB 染色；{@code 0xFFFFFF} 表示不染色（贴图自带颜色）
     */
    public record Entry(String fluidId, int tint,
                        DeferredHolder<FluidType, ? extends FluidType> type,
                        DeferredHolder<net.minecraft.world.level.material.Fluid, ? extends net.minecraft.world.level.material.Fluid> source,
                        DeferredHolder<net.minecraft.world.level.material.Fluid, ? extends net.minecraft.world.level.material.Fluid> flowing,
                        DeferredItem<Item> bucket) {
    }

    private MoltenMetals() {
    }

    public static Entry register(String fluidId, int tint) {
        // Properties 需要三个 holder，而 holder 又要靠 Properties 构建流体 —— 先建空壳再回填
        Props props = new Props();
        DeferredHolder<FluidType, MoltenMetalFluidType> type =
                ModFluids.FluidsTypes.FLUID_TYPES.register(fluidId, () -> new MoltenMetalFluidType(tint));
        props.type = type;

        DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Source> source =
                ModFluids.Fluids.FLUIDS.register(fluidId, () -> new BaseFlowingFluid.Source(props.build()));
        DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Flowing> flowing =
                ModFluids.Fluids.FLUIDS.register("flowing_" + fluidId,
                        () -> new BaseFlowingFluid.Flowing(props.build()));
        props.source = source;
        props.flowing = flowing;

        DeferredItem<Item> bucket = ModItems.ITEMS.register(fluidId + "_bucket",
                () -> new net.minecraft.world.item.BucketItem(source.get(),
                        new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
        props.bucket = bucket;

        Entry entry = new Entry(fluidId, tint, type, source, flowing, bucket);
        ALL.add(entry);
        return entry;
    }

    /** 可回填的 Properties 壳，避开「holder 依赖 Properties、Properties 又依赖 holder」的环。 */
    private static final class Props {
        private DeferredHolder<FluidType, ? extends FluidType> type;
        private DeferredHolder<net.minecraft.world.level.material.Fluid, ? extends net.minecraft.world.level.material.Fluid> source;
        private DeferredHolder<net.minecraft.world.level.material.Fluid, ? extends net.minecraft.world.level.material.Fluid> flowing;
        private DeferredItem<Item> bucket;

        private BaseFlowingFluid.Properties build() {
            return new BaseFlowingFluid.Properties(type, source, flowing).bucket(bucket);
        }
    }
}
