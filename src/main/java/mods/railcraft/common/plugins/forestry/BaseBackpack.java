/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import cpw.mods.fml.common.Optional;
import forestry.api.storage.IBackpackDefinition;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = "Forestry")
public abstract class BaseBackpack implements IBackpackDefinition {
    protected List<ItemStack> items = new ArrayList<ItemStack>(50);

    @Override
    public void addValidItem(ItemStack stack) {
        addItem(stack);
    }

    @Override
    public void addValidItems(List<ItemStack> validItems) {
        for (ItemStack stack : validItems) {
            addItem(stack);
        }
    }

    public void addItem(ItemStack stack) {
        if (stack == null) return;
        items.add(stack);
    }

    public void addItem(RailcraftItem item) {
        if (item == null) return;
        items.add(item.getWildcard());
    }

    public void addItem(Item item) {
        if (item == null) return;
        items.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void addItem(Block block) {
        if (block == null) return;
        items.add(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
    }

    @Deprecated
    @Optional.Method(modid = "Forestry")
    public boolean isValidItem(EntityPlayer player, ItemStack pickup) {
        return isValidItem(pickup);
    }

    @Override
    public boolean isValidItem(ItemStack pickup) {
        for (ItemStack stack : items) {
            if (InvTools.isItemEqualIgnoreNBT(stack, pickup))
                return true;
        }
        return false;
    }

    @Deprecated
    @Optional.Method(modid = "Forestry")
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
