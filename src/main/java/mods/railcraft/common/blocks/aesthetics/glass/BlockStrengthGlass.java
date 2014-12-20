/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.glass;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.*;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockStrengthGlass extends BlockGlass {

    private static BlockStrengthGlass instance;
    public static boolean renderingHighlight;

    public static BlockStrengthGlass getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("glass")) {
                instance = new BlockStrengthGlass(Railcraft.proxy.getRenderId());
                RailcraftRegistry.register(instance, ItemStrengthGlass.class);

                ForestryPlugin.addBackpackItem("builder", instance);

                for (int meta = 0; meta < 16; meta++) {
                    MicroBlockPlugin.addMicroBlockCandidate(instance, meta);
                }
            }
    }

    public static ItemStack getItem(int meta) {
        return getItem(1, meta);
    }

    public static ItemStack getItem(int qty, int meta) {
        if (instance == null) return null;
        return new ItemStack(instance, qty, meta);
    }

    private final int renderId;
    private IIcon[] icons;
    private final Map<EnumSet<Neighbors>, IIcon> patterns = new HashMap<EnumSet<Neighbors>, IIcon>();

    public BlockStrengthGlass(int renderId) {
        super(Material.glass, false);
        this.renderId = renderId;
        setResistance(5);
        setHardness(1);
        setStepSound(Block.soundTypeGlass);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setBlockName("railcraft.glass");
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:glass", 1, 5);

        patterns.put(EnumSet.noneOf(Neighbors.class), icons[0]);
        patterns.put(EnumSet.of(Neighbors.BOTTOM), icons[1]);
        patterns.put(EnumSet.of(Neighbors.TOP, Neighbors.BOTTOM), icons[2]);
        patterns.put(EnumSet.of(Neighbors.TOP), icons[3]);
    }

    private enum Neighbors {

        TOP, BOTTOM;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (renderingHighlight)
            return icons[4];
        if (side <= 1)
            return icons[0];
        int meta = world.getBlockMetadata(x, y, z);

        EnumSet neighbors = EnumSet.noneOf(Neighbors.class);

        if (WorldPlugin.getBlock(world, x, y + 1, z) == this && world.getBlockMetadata(x, y + 1, z) == meta)
            neighbors.add(Neighbors.TOP);

        if (WorldPlugin.getBlock(world, x, y - 1, z) == this && world.getBlockMetadata(x, y - 1, z) == meta)
            neighbors.add(Neighbors.BOTTOM);
        return patterns.get(neighbors);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (renderingHighlight)
            return icons[4];
        return icons[0];
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int meta = 0; meta < 16; meta++) {
            list.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != colour) {
            world.setBlockMetadataWithNotify(x, y, z, colour, 3);
            return true;
        }
        return false;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        if (renderingHighlight)
            return super.colorMultiplier(world, x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        return EnumColor.fromId(15 - meta).getHexColor();
    }

}
