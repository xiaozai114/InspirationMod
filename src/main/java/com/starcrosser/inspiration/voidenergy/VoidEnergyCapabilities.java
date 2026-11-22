package com.starcrosser.inspiration.voidenergy;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.voidenergy.blockentity.VoidEnergyExtractorTile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * 虚空能量体系的能量接口登记。
 * <p>
 * 提取器不是 Titanium 机器，没有现成的能量组件，所以这里把它的 {@code IEnergyStorage}
 * 挂到 NeoForge 的能力系统上 —— 任何能推电的方块（发电机、电容、其它模组的线缆）都能给它充电。
 */
@EventBusSubscriber(modid = Inspiration.MODID)
public final class VoidEnergyCapabilities {

    private VoidEnergyCapabilities() {
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModVoidEnergy.VOID_ENERGY_EXTRACTOR_TYPE.get(),
                (tile, side) -> ((VoidEnergyExtractorTile) tile).energyHandler());
    }
}
