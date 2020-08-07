/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = ForestryPlugin.FORESTRY_ID)
public class IcemanBackpack extends BaseBackpack {
    private static IcemanBackpack instance;
    private static final ItemStack SNOWBALL = new ItemStack(Items.SNOWBALL);
    private static final ItemStack SNOW_BLOCK = new ItemStack(Blocks.SNOW);
    private static final Predicate<ItemStack> SNOWBALL_MATCHER = StackFilters.of(Items.SNOWBALL);
    private static final Predicate<ItemStack> SNOW_BLOCK_MATCHER = StackFilters.of(Blocks.SNOW);
    private static final String INV_TAG = "Items";

    public static IcemanBackpack getInstance() {
        if (instance == null) {
            instance = new IcemanBackpack();
        }
        return instance;
    }

    protected IcemanBackpack() {
        super("railcraft.iceman");
    }

    public void setup() {
        add(Blocks.SNOW);
        add(Blocks.SNOW_LAYER);
        add(Blocks.ICE);
        add(Blocks.PACKED_ICE);
        add(Items.SNOWBALL);
    }

    @Override
    public int getPrimaryColour() {
        return 0xFFFFFF;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }

    @Override
    public boolean stow(IInventory backpackInventory, ItemStack stackToStow) {
        manageSnow(backpackInventory);
        return false;
    }

    @Override
    public boolean resupply(IInventory backpackInventory) {
        manageSnow(backpackInventory);
        return false;
    }

    private void manageSnow(IInventory backpackInventory) {
        InventoryComposite inv = InventoryComposite.of(backpackInventory);
        if (inv.hasItems()) {
            int numSnowballs = inv.countItems(SNOWBALL_MATCHER);
            while (numSnowballs > 16
                    && inv.canFit(SNOW_BLOCK)
                    && inv.removeItems(4, SNOWBALL_MATCHER)) {
                inv.addStack(new ItemStack(Blocks.SNOW));
                numSnowballs -= 4;
            }
            while (numSnowballs < 8
                    && inv.canFit(new ItemStack(Items.SNOWBALL, 4))
                    && inv.removeItems(1, SNOW_BLOCK_MATCHER)) {
                inv.addStack(new ItemStack(Items.SNOWBALL, 4));
                numSnowballs += 4;
            }
        }
    }
}
