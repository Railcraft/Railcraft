/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
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
        for (Materials mat : Materials.MAT_SET_FROZEN) {
            add(RailcraftBlocks.WALL.getStack(mat));
            add(RailcraftBlocks.STAIR.getStack(mat));
            add(RailcraftBlocks.SLAB.getStack(mat));
        }
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
        if (!inv.isEmpty()) {
            int numSnowballs = inv.countItems(SNOWBALL_MATCHER);
            InventoryManipulator im = InventoryManipulator.get(backpackInventory);
            while (numSnowballs > 16 && im.canRemoveItems(SNOWBALL_MATCHER, 4) && im.canAddStack(SNOW_BLOCK)) {
                im.removeItems(SNOWBALL_MATCHER, 4);
                im.addStack(new ItemStack(Blocks.SNOW));
                numSnowballs -= 4;
            }
            while (numSnowballs < 8 && im.canRemoveItem(SNOW_BLOCK_MATCHER) && im.canAddStack(new ItemStack(Items.SNOWBALL, 4))) {
                im.removeItem(SNOW_BLOCK_MATCHER);
                im.addStack(new ItemStack(Items.SNOWBALL, 4));
                numSnowballs += 4;
            }
        }
    }
}
