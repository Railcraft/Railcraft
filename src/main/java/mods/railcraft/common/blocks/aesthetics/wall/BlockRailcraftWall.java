/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.wall;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.sounds.RailcraftSoundTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class BlockRailcraftWall extends BlockWall {

    public static int currentRenderPass;
    private static BlockRailcraftWall alpha;
    private static BlockRailcraftWall beta;
    public final WallProxy proxy;
    private final int renderId;
    private final boolean alphaBlend;

    public BlockRailcraftWall(int renderId, boolean alphaBlend, WallProxy proxy) {
        super(Blocks.STONEBRICK);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            this.renderId = 32;
        else
            this.renderId = renderId;

        this.alphaBlend = alphaBlend;
        this.proxy = proxy;
        this.setSoundType(RailcraftSoundTypes.OVERRIDE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

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
            alpha.setUnlocalizedName("railcraft.wall.alpha");
            RailcraftRegistry.register(alpha, ItemWall.class);

            for (EnumWallAlpha wall : EnumWallAlpha.VALUES) {
                switch (wall) {
                    case SNOW:
                        HarvestPlugin.setHarvestLevel(wall.ordinal(), "shovel", 0);
                        break;
                    case OBSIDIAN:
                        HarvestPlugin.setHarvestLevel(wall.ordinal(), "pickaxe", 3);
                        break;
                    default:
                        HarvestPlugin.setHarvestLevel(wall.ordinal(), "pickaxe", 2);
                }

                RailcraftRegistry.register(wall.getItem());

                if (wall != EnumWallAlpha.SNOW || wall != EnumWallAlpha.ICE)
                    ForestryPlugin.addBackpackItem("builder", wall.getItem());
            }

        }
        if (beta == null && RailcraftConfig.isBlockEnabled("wall.beta")) {
            int renderId = Railcraft.getProxy().getRenderId();
            beta = new BlockRailcraftWall(renderId, false, new WallProxyBeta());
            beta.setUnlocalizedName("railcraft.wall.beta");
            RailcraftRegistry.register(beta, ItemWall.class);

            for (EnumWallBeta wall : EnumWallBeta.VALUES) {
                switch (wall) {
                    default:
                        HarvestPlugin.setHarvestLevel(wall.ordinal(), "pickaxe", 2);
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

    /**
     * The type of render function that is called for this block
     *
     * @return
     */
    @Override
    public int getRenderType() {
        return renderId;
    }

    /**
     * Return whether an adjacent block can connect to a wall.
     *
     * @param worldIn
     * @param pos
     * @return
     */
    @Override
    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
        Block block = WorldPlugin.getBlock(worldIn, pos);

        if (block instanceof BlockRailcraftWall)
            return true;
        else if (block != this && !(block instanceof BlockFenceGate))
            return (block != null && block.getMaterial().isOpaque() && block.isBlockNormalCube()) && block.getMaterial() != Material.gourd;
        else
            return true;
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as
     * fences.
     *
     * @param world The current world
     * @param pos
     * @return True to allow the torch to be placed
     */

    @Override
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
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
    public float getBlockHardness(World worldIn, BlockPos pos) {
        int meta = worldIn.getBlockMetadata(pos);
        WallInfo wall = proxy.fromMeta(meta);
        return wall.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = world.getBlockMetadata(pos);
        WallInfo wall = proxy.fromMeta(meta);
        return wall.getExplosionResistance(exploder);
    }
}
