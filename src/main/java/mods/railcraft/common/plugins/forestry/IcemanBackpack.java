/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

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
public class IcemanBackpack extends BaseBackpack {

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
        addValidItem(Blocks.snow);
        addValidItem(Blocks.snow_layer);
        addValidItem(Blocks.ice);
        addValidItem(EnumWallAlpha.SNOW.getItem());
        addValidItem(EnumWallAlpha.ICE.getItem());
        addValidItem(BlockRailcraftStairs.getItem(EnumBlockMaterial.SNOW));
        addValidItem(BlockRailcraftStairs.getItem(EnumBlockMaterial.ICE));
        addValidItem(BlockRailcraftSlab.getItem(EnumBlockMaterial.SNOW));
        addValidItem(BlockRailcraftSlab.getItem(EnumBlockMaterial.ICE));
        addValidItem(Items.snowball);
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
