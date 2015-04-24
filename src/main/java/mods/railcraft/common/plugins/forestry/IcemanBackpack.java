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
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.stairs.BlockRailcraftStairs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = "Forestry")
public class IcemanBackpack extends BaseBackpack implements IBackpackDefinition {

    private static IcemanBackpack instance;
    private static final ItemStack SNOWBALL = new ItemStack(Items.snowball);
    private static final ItemStack SNOWBLOCK = new ItemStack(Blocks.snow);
    private static final String INV_TAG = "Items";

    public static IcemanBackpack getInstance() {
        if (instance == null) {
            instance = new IcemanBackpack();
        }
        return instance;
    }

    protected IcemanBackpack() {
    }

    public void setup() {
        addItem(Blocks.snow);
        addItem(Blocks.snow_layer);
        addItem(Blocks.ice);
        addItem(EnumWallAlpha.SNOW.getItem());
        addItem(EnumWallAlpha.ICE.getItem());
        addItem(BlockRailcraftStairs.getItem(EnumBlockMaterial.SNOW));
        addItem(BlockRailcraftStairs.getItem(EnumBlockMaterial.ICE));
        addItem(BlockRailcraftSlab.getItem(EnumBlockMaterial.SNOW));
        addItem(BlockRailcraftSlab.getItem(EnumBlockMaterial.ICE));
        addItem(Items.snowball);
    }

    public void compactInventory(ItemStack backpack) {
        StandaloneInventory inv = new StandaloneInventory(45);
        NBTTagCompound data = backpack.getTagCompound();
        if (data == null) return;
        InvTools.readInvFromNBT(inv, INV_TAG, data);
        int numSnowballs = InvTools.countItems(inv, SNOWBALL);
        if (numSnowballs >= 16) {
            for (int i = 0; i < 4; i++) {
                InvTools.removeOneItem(inv, SNOWBALL);
            }
            if (InvTools.moveItemStack(new ItemStack(Blocks.snow), inv) == null) {
                InvTools.writeInvToNBT(inv, INV_TAG, data);
            }
        } else if (numSnowballs < 8 && InvTools.removeOneItem(inv, SNOWBLOCK) != null) {
            if (InvTools.moveItemStack(new ItemStack(Items.snowball, 4), inv) == null) {
                InvTools.writeInvToNBT(inv, INV_TAG, data);
            }
        }
    }

    @Override
    public String getKey() {
        return "ICEMAN";
    }

    @Override
    public int getPrimaryColour() {
        return 0xFFFFFF;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }
}
