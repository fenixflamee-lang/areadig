package com.treki.areadig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AreaDigMod.MODID)
public class AreaDigMod {
    public static final String MODID = "areadig";

    public AreaDigMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEnchantments.ENCHANTMENTS.register(modBus);
        MinecraftForge.EVENT_BUS.register(new MiningHandler());
    }
}
