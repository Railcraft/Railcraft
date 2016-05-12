/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.stairs.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = "Forestry")
public class IcemanBackpack extends BaseBackpack {
    private static final BlockMaterial[] coldMaterials = {BlockMaterial.SNOW, BlockMaterial.ICE, BlockMaterial.PACKED_ICE};
    private static IcemanBackpack instance;
    private static final ItemStack SNOWBALL = new ItemStack(Items.snowball);
    private static final ItemStack SNOW_BLOCK = new ItemStack(Blocks.snow);
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
        add(Blocks.snow);
        add(Blocks.snow_layer);
        add(Blocks.ice);
        add(Blocks.packed_ice);
        add(EnumWallAlpha.SNOW.getItem());
        add(EnumWallAlpha.ICE.getItem());
        for (BlockMaterial mat : coldMaterials) {
            add(BlockRailcraftStairs.getItem(mat));
            add(BlockRailcraftSlab.getItem(mat));
        }
        add(Items.snowball);
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
        } else if (numSnowballs < 8 && InvTools.removeOneItem(inv, SNOW_BLOCK) != null) {
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
