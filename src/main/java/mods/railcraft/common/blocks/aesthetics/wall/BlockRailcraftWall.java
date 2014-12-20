/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.wall;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.*;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class BlockRailcraftWall extends BlockWall {

    private static BlockRailcraftWall alpha;
    private static BlockRailcraftWall beta;
    public static int currentRenderPass;

    public static BlockRailcraftWall getBlockAlpha() {
        return alpha;
    }

    public static BlockRailcraftWall getBlockBeta() {
        return beta;
    }

    public static void registerBlocks() {
        if (alpha == null && RailcraftConfig.isBlockEnabled("wall.alpha")) {
            int renderId = Railcraft.getProxy().getRenderId();
            alpha = new BlockRailcraftWall(renderId, true, new WallProxyApha());
            alpha.setBlockName("railcraft.wall.alpha");
            RailcraftRegistry.register(alpha, ItemWall.class);

            for (EnumWallAlpha wall : EnumWallAlpha.VALUES) {
                switch (wall) {
                    case SNOW:
                        HarvestPlugin.setHarvestLevel(alpha, wall.ordinal(), "shovel", 0);
                        break;
                    case OBSIDIAN:
                        HarvestPlugin.setHarvestLevel(alpha, wall.ordinal(), "pickaxe", 3);
                        break;
                    default:
                        HarvestPlugin.setHarvestLevel(alpha, wall.ordinal(), "pickaxe", 2);
                }

                RailcraftRegistry.register(wall.getItem());

                if (wall != EnumWallAlpha.SNOW || wall != EnumWallAlpha.ICE)
                    ForestryPlugin.addBackpackItem("builder", wall.getItem());
            }

        }
        if (beta == null && RailcraftConfig.isBlockEnabled("wall.beta")) {
            int renderId = Railcraft.getProxy().getRenderId();
            beta = new BlockRailcraftWall(renderId, false, new WallProxyBeta());
            beta.setBlockName("railcraft.wall.beta");
            RailcraftRegistry.register(beta, ItemWall.class);

            for (EnumWallBeta wall : EnumWallBeta.VALUES) {
                switch (wall) {
                    default:
                        HarvestPlugin.setHarvestLevel(beta, wall.ordinal(), "pickaxe", 2);
                }

                RailcraftRegistry.register(wall.getItem());

                ForestryPlugin.addBackpackItem("builder", wall.getItem());
            }
        }
    }

    public static void initialize() {
        EnumWallAlpha.initialize();
        EnumWallBeta.initialize();
    }

    private final int renderId;
    public final WallProxy proxy;
    private final boolean alphaBlend;

    public BlockRailcraftWall(int renderId, boolean alphaBlend, WallProxy proxy) {
        super(Blocks.stonebrick);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            this.renderId = 32;
        else
            this.renderId = renderId;
        
        this.alphaBlend = alphaBlend;
        this.proxy = proxy;
        this.setStepSound(RailcraftSound.getInstance());
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        WallInfo wall = proxy.fromMeta(meta);
        Block source = wall.getSource();
        if (source == null)
            return Blocks.cobblestone.getIcon(side, 0);
        return source.getIcon(side, wall.getSourceMeta());
    }

    /**
     * The type of render function that is called for this block
     * @return 
     */
    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public int getRenderBlockPass() {
        return alphaBlend ? 1 : 0;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        currentRenderPass = pass;
        if (!alphaBlend) return getRenderBlockPass() == pass;
        return pass == 0 || pass == 1;
    }

    /**
     * Return whether an adjacent block can connect to a wall.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Override
    public boolean canConnectWallTo(IBlockAccess world, int x, int y, int z) {
        Block block = WorldPlugin.getBlock(world, x, y, z);

        if (block instanceof BlockRailcraftWall)
            return true;
        else if (block != this && block != Blocks.fence_gate)
            return block != null && block.getMaterial().isOpaque() && block.renderAsNormalBlock() ? block.getMaterial() != Material.gourd : false;
        else
            return true;
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as
     * fences.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return True to allow the torch to be placed
     */
    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (WallInfo wall : proxy.getCreativeList()) {
            if (wall.isEnabled())
                list.add(wall.getItem());
        }
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WallInfo wall = proxy.fromMeta(meta);
        return wall.getBlockHardness(world, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        int meta = world.getBlockMetadata(x, y, z);
        WallInfo wall = proxy.fromMeta(meta);
        return wall.getExplosionResistance(entity);
    }

}
