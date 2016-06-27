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
    private static final ItemStack SNOWBALL = new ItemStack(Items.SNOWBALL);
    private static final ItemStack SNOW_BLOCK = new ItemStack(Blocks.SNOW);
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
        add(EnumWallAlpha.SNOW.getItem());
        add(EnumWallAlpha.ICE.getItem());
        for (BlockMaterial mat : coldMaterials) {
            add(BlockRailcraftStairs.getItem(mat));
            add(BlockRailcraftSlab.getItem(mat));
        }
        add(Items.SNOWBALL);
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
            if (InvTools.moveItemStack(new ItemStack(Blocks.SNOW), inv) == null) {
                InvTools.writeInvToNBT(inv, INV_TAG, data);
            }
        } else if (numSnowballs < 8 && InvTools.removeOneItem(inv, SNOW_BLOCK) != null) {
            if (InvTools.moveItemStack(new ItemStack(Items.SNOWBALL, 4), inv) == null) {
                InvTools.writeInvToNBT(inv, INV_TAG, data);
            }
        }
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
