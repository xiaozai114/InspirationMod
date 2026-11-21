// client/ClientExtensionsHandler.java
package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.client.extensions.EndAlloyShieldClientExtensions;
import com.starcrosser.inspiration.client.extensions.InspirationChargeClientExtensions;
import com.starcrosser.inspiration.client.extensions.MindFluidClientExtensions;
import com.starcrosser.inspiration.client.extensions.MoltenEphemeralAlloyClientExtensions;
import com.starcrosser.inspiration.client.extensions.MoltenPlasmaClientExtensions;
import com.starcrosser.inspiration.client.extensions.NutrientFluidClientExtensions;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;


@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public class ClientExtensionsHandler {

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // 创建扩展实例
        MindFluidClientExtensions extensions = new MindFluidClientExtensions();
        // 将扩展绑定到你的流体类型
        event.registerFluidType(extensions, ModFluids.FluidsTypes.MIND_FLUID_TYPE.get());
        event.registerFluidType(new InspirationChargeClientExtensions(),
                ModFluids.FluidsTypes.INSPIRATION_CHARGE_TYPE.get());
        event.registerFluidType(new MoltenEphemeralAlloyClientExtensions(),
                ModFluids.FluidsTypes.MOLTEN_EPHEMERAL_ALLOY_TYPE.get());
        event.registerFluidType(new MoltenPlasmaClientExtensions(),
                ModFluids.FluidsTypes.MOLTEN_PLASMA_TYPE.get());
        event.registerFluidType(new NutrientFluidClientExtensions(),
                ModFluids.FluidsTypes.NUTRIENT_FLUID_TYPE.get());

        // 末地合金盾牌：原版把盾牌的三维渲染写死在 Items.SHIELD 上，自定义盾牌只能自己挂一个渲染器
        event.registerItem(new EndAlloyShieldClientExtensions(), ModItems.END_ALLOY_SHIELD.get());
    }
}