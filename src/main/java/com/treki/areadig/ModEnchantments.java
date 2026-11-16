package com.treki.areadig;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AreaDigMod.MODID);

    public static final RegistryObject<Enchantment> AREA =
            ENCHANTMENTS.register("area", AreaEnchantment::new);
}
