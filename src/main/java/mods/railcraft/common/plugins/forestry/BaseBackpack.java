/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import forestry.api.storage.IBackpackDefinition;
import java.util.ArrayList;
import java.util.List;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BaseBackpack implements IBackpackDefinition {

    protected List<ItemStack> items = new ArrayList<ItemStack>(50);

    @Override
    public void addValidItem(ItemStack stack) {
        if (stack == null) return;
        items.add(stack);
    }

    @Override
    public void addValidItems(List<ItemStack> validItems) {
        for (ItemStack stack : validItems) {
            addValidItem(stack);
        }
    }

    public void addValidItem(RailcraftItem item) {
        if (item == null) return;
        items.add(item.getWildcard());
    }

    public void addValidItem(Item item) {
        if (item == null) return;
        items.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void addValidItem(Block block) {
        if (block == null) return;
        items.add(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
    }

    @Override
    public boolean isValidItem(EntityPlayer player, ItemStack pickup) {
        for (ItemStack stack : items) {
            if (InvTools.isItemEqualIgnoreNBT(stack, pickup))
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Update Forestry!";
    }

    @Override
    public String getName(ItemStack backpack) {
        Item item = backpack.getItem();
        String name = ("" + StatCollector.translateToLocal(item.getUnlocalizedNameInefficiently(backpack) + ".name")).trim();

        if (backpack.stackTagCompound != null && backpack.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound nbt = backpack.stackTagCompound.getCompoundTag("display");

            if (nbt.hasKey("Name", 8))
                name = nbt.getString("Name");
        }

        return name;
    }

}
