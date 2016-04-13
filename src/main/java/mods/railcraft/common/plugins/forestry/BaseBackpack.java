/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import com.google.common.base.Predicate;
import forestry.api.storage.IBackpackDefinition;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = "Forestry")
public abstract class BaseBackpack implements IBackpackDefinition {
    protected List<ItemStack> items = new ArrayList<ItemStack>(50);
    protected List<Predicate<ItemStack>> filters = new ArrayList<Predicate<ItemStack>>();

    @Override
    public void addValidItem(ItemStack stack) {
        add(stack);
    }

    @Override
    public void addValidItems(List<ItemStack> validItems) {
        for (ItemStack stack : validItems) {
            add(stack);
        }
    }

    public void add(ItemStack stack) {
        if (stack == null) return;
        items.add(stack);
    }

    public void add(RailcraftItems item) {
        if (item == null) return;
        items.add(item.getWildcard());
    }

    public void add(Item item) {
        if (item == null) return;
        items.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void add(Block block) {
        if (block == null) return;
        items.add(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void add(Predicate<ItemStack> filter) {
        filters.add(filter);
    }

    public void addOre(String oreTag) {
        add(StackFilters.ofOreType(oreTag));
    }

    @Override
    public boolean isValidItem(ItemStack pickup) {
        for (Predicate<ItemStack> filter : filters) {
            if (filter.apply(pickup))
                return true;
        }
        for (ItemStack stack : items) {
            if (InvTools.isItemEqualIgnoreNBT(stack, pickup))
                return true;
        }
        return false;
    }

    @Override
    public String getName(ItemStack backpack) {
        Item item = backpack.getItem();
        String name = ("" + StatCollector.translateToLocal(item.getUnlocalizedNameInefficiently(backpack) + ".name")).trim();

        if (backpack.getTagCompound() != null && backpack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbt = backpack.getTagCompound().getCompoundTag("display");

            if (nbt.hasKey("Name", 8))
                name = nbt.getString("Name");
        }

        return name;
    }
}
