/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = ForestryPlugin.FORESTRY_ID)
public abstract class BaseBackpack implements IBackpackDefinition {
    private final String id;
    protected final IBackpackFilterConfigurable backpackFilter = BackpackManager.backpackInterface.createBackpackFilter();

    protected Predicate<ItemStack> compoundFilter = backpackFilter;

    protected BaseBackpack(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Predicate<ItemStack> getFilter() {
        return compoundFilter;
    }

    public void add(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack)) return;
        backpackFilter.acceptItem(stack);
    }

    public void add(IRailcraftObjectContainer objectContainer) {
        if (!objectContainer.isLoaded())
            return;
        add(objectContainer.getWildcard());
    }

    public void add(@Nullable Item item) {
        if (item == null) return;
        add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void add(@Nullable Block block) {
        if (block == null) return;
        add(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
    }

    public void add(Predicate<ItemStack> filter) {
        compoundFilter = compoundFilter.or(filter);
    }

    public void add(String oreTag) {
        backpackFilter.acceptOreDictName(oreTag);
    }

    @Override
    public String getName(ItemStack backpack) {
        Item item = backpack.getItem();
        String name = LocalizationPlugin.translateFast(LocalizationPlugin.convertTag(item.getTranslationKey()) + ".name").trim();

        if (backpack.getTagCompound() != null && backpack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbt = backpack.getTagCompound().getCompoundTag("display");

            if (nbt.hasKey("Name", 8))
                name = nbt.getString("Name");
        }

        return name;
    }

    public boolean stow(IInventory backpackInventory, ItemStack stackToStow) {
        return false;
    }

    public boolean resupply(IInventory backpackInventory) {
        return false;
    }
}
