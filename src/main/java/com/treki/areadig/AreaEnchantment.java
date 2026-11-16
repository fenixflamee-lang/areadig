package com.treki.areadig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Один чар "Область": уровень I = 2x2, II = 3x3, III = 4x4, IV = 5x5. */
public class AreaEnchantment extends Enchantment {

    public AreaEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /** Размер стороны квадрата для данного уровня. */
    public static int sizeFor(int level) {
        return level + 1;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int level) {
        return 5 + level * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem;
    }

    @Override
    public MutableComponent getFullname(int level) {
        int s = sizeFor(level);
        return Component.translatable(getDescriptionId())
                .append(" " + s + "x" + s)
                .withStyle(ChatFormatting.GRAY);
    }
}
