/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import java.util.List;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneCracked extends ItemFirestoneRefined {

    public static int HEAT = 100;
    public static Item item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.firestone.cracked";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFirestoneCracked().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    public static ItemStack getItemCharged() {
        return new ItemStack(item);
    }

    public static ItemStack getItemEmpty() {
        return new ItemStack(item, 1, item.getMaxDamage() - 1);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        double damageLevel = (double) stack.getItemDamage() / (double) stack.getMaxDamage();
        if (MiscTools.RANDOM.nextDouble() < damageLevel * 0.0001)
            return ItemFirestoneRaw.getItem();
        ItemStack newStack = stack.copy();
        newStack.stackSize = 1;
        newStack = InvTools.damageItem(newStack, 1);
        return newStack;
    }

    @Override
    public int getHeatValue(ItemStack stack) {
        if (stack.getItemDamage() < getMaxDamage())
            return HEAT;
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        String tipTag = getUnlocalizedName() + ".tip.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getUnlocalizedName() + ".tip.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

}
