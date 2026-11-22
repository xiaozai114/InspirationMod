package com.starcrosser.inspiration.item.armor;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

/**
 * 护甲材质登记。1.21 起护甲材质是注册表里的数据对象，不能再像旧版那样写个枚举。
 * <p>
 * 虚空护目镜只有头盔一件：防御值表里只声明 HELMET=2，其余槽位用不到。
 * 贴图路径由 {@link ArmorMaterial.Layer} 的 {@code void_goggles} 决定，
 * 客户端会去找 {@code textures/models/armor/void_goggles_layer_1.png}。
 */
public final class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Inspiration.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOID_GOGGLES =
            ARMOR_MATERIALS.register("void_goggles", () -> new ArmorMaterial(
                    Map.of(ArmorItem.Type.HELMET, 2),
                    15,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.of(ModItems.VOID_DEW.get()),
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "void_goggles"))),
                    0.0F,
                    0.0F));

    private ModArmorMaterials() {
    }

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}
