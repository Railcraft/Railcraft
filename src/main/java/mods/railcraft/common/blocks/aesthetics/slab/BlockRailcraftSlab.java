/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import mods.railcraft.common.util.sounds.RailcraftSoundTypes;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.blocks.aesthetics.slab.ItemSlab.MATERIAL_KEY;
import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public class BlockRailcraftSlab extends BlockContainer implements IBlockSoundProvider {

    public static final PropertyEnum<BlockMaterial> TOP_MATERIAL = PropertyEnum.create("top_material", BlockMaterial.class);
    public static final PropertyEnum<BlockMaterial> BOTTOM_MATERIAL = PropertyEnum.create("bottom_material", BlockMaterial.class);
    public static int currentRenderPass;
    static BlockRailcraftSlab block;

    BlockRailcraftSlab() {
        super(Material.ROCK);
        setSoundType(RailcraftSoundTypes.OVERRIDE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
    }

    public static BlockRailcraftSlab getBlock() {
        return block;
    }

    public static ItemStack getItem(BlockMaterial mat) {
        return getItem(mat, 1);
    }

    public static ItemStack getItem(BlockMaterial mat, int qty) {
        if (block == null) return null;
        ItemStack stack = new ItemStack(block, qty);
        MaterialRegistry.tagItemStack(stack, MATERIAL_KEY, mat);
        return stack;
    }

    public static String getTag(BlockMaterial mat) {
        return "tile.railcraft.slab." + mat.getLocalizationSuffix();
    }

    public static boolean isEnabled(BlockMaterial mat) {
        return RailcraftModuleManager.isModuleEnabled(ModuleStructures.class) && RailcraftConfig.isSubBlockEnabled(getTag(mat)) && getBlock() != null;
    }

    @SuppressWarnings("unused")
    public static BlockMaterial getTopSlab(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getTopSlab();
        return null;
    }

    @SuppressWarnings("unused")
    public static BlockMaterial getBottomSlab(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getBottomSlab();
        return null;
    }

    static TileSlab getSlabTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile);
        return null;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileSlab tile = getSlabTile(worldIn, pos);
        if (tile != null)
            state = state.withProperty(TOP_MATERIAL, tile.getTopSlab()).withProperty(BOTTOM_MATERIAL, tile.getBottomSlab());
        return state;
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            BlockMaterial slab = ((TileSlab) tile).getUpmostSlab();
            if (slab != null)
                return getItem(slab);
        }
        return null;
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockMaterial mat : BlockMaterial.CREATIVE_LIST) {
            if (isEnabled(mat) && BlockMaterial.SLAB_MATS.contains(mat))
                list.add(getItem(mat));
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileSlab) {
            BlockMaterial top = ((TileSlab) tile).getTopSlab();
            BlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            if (top != null)
                items.add(getItem(top));
            if (bottom != null)
                items.add(getItem(bottom));
        }
        return items;
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
        return world.setBlockToAir(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            BlockMaterial top = ((TileSlab) tile).getTopSlab();
            BlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            float hardness = 0;
            if (top != null)
                hardness += top.getBlockHardness(worldIn, pos);
            if (bottom != null)
                hardness += bottom.getBlockHardness(worldIn, pos);
            if (top != null && bottom != null)
                hardness = hardness / 2.0F;
            return hardness;
        }
        return super.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nonnull Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            BlockMaterial top = ((TileSlab) tile).getTopSlab();
            BlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            float resist = 0;
            if (top != null)
                resist += top.getExplosionResistance(exploder);
            if (bottom != null)
                resist += bottom.getExplosionResistance(exploder);
            if (top != null && bottom != null)
                resist = resist / 2.0F;
            return resist;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return ParticleHelper.addHitEffects(worldObj, block, target, manager, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, block, pos, state, manager, null);
    }

    @Override
    public SoundType getSound(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            BlockMaterial slab = ((TileSlab) tile).getUpmostSlab();
            if (slab != null)
                return slab.getSound();
        }
        return null;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileSlab slab = getSlabTile(source, pos);
        AABBFactory boxFactory = AABBFactory.start().box();
        if (slab != null)
            if (slab.isBottomSlab())
                boxFactory.raiseCeiling(-0.5);
            else if (slab.isTopSlab())
                boxFactory.raiseFloor(0.5);
        return boxFactory.build();
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether
     * or not to render the shared face of two adjacent blocks and also whether
     * the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if
     * the adjacent block is at the given coordinates. Args: blockAccess, x, y,
     * z, side
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        BlockPos offsetPos = pos.offset(side);

        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            BlockMaterial top = slab.getTopSlab();
            BlockMaterial bottom = slab.getBottomSlab();

            if (slab.isDoubleSlab())
                return super.shouldSideBeRendered(state, worldIn, pos, side);

            if (side != UP && side != DOWN && !super.shouldSideBeRendered(state, worldIn, pos, side))
                return false;

            if (top != null) {
                if (side == DOWN)
                    return true;
                if (side == UP && super.shouldSideBeRendered(state, worldIn, pos, side))
                    return true;
                if (!WorldPlugin.isBlockAt(worldIn, offsetPos, this))
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, offsetPos);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isBottomSlab() || (otherSlab.isTopSlab() && otherSlab.getTopSlab().isTransparent());
                }
            }
            if (bottom != null) {
                if (side == UP)
                    return true;
                if (side == DOWN && super.shouldSideBeRendered(state, worldIn, pos, side))
                    return true;
                if (!WorldPlugin.isBlockAt(worldIn, offsetPos, this))
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, offsetPos);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isTopSlab() || (otherSlab.isBottomSlab() && otherSlab.getBottomSlab().isTransparent());
                }
            }
        }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        TileSlab tile = getSlabTile(world, pos);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            if (side == DOWN && tile.isBottomSlab())
                return true;
            if (side == UP && tile.isTopSlab())
                return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        TileSlab tile = getSlabTile(world, pos);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            if (tile.isTopSlab())
                return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileSlab();
    }

}
