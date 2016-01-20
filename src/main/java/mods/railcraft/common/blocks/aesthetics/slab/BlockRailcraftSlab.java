/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;


public class BlockRailcraftSlab extends BlockContainer implements IBlockSoundProvider {

    public static int currentRenderPass;
    public static EnumBlockMaterial textureSlab = null;
    static BlockRailcraftSlab block;
    private final int renderId;

    protected BlockRailcraftSlab(int renderId) {
        super(Material.rock);
        this.renderId = renderId;
        this.setStepSound(RailcraftSound.getInstance());
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
    }

    public static BlockRailcraftSlab getBlock() {
        return block;
    }

    public static ItemStack getItem(EnumBlockMaterial mat) {
        if (block == null) return null;
        return new ItemStack(block, 1, mat.ordinal());
    }

    public static ItemStack getItem(EnumBlockMaterial mat, int qty) {
        if (block == null) return null;
        return new ItemStack(block, qty, mat.ordinal());
    }

    public static String getTag(EnumBlockMaterial mat) {
        return "tile.railcraft.slab." + mat.name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public static boolean isEnabled(EnumBlockMaterial mat) {
        return ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag(mat)) && getBlock() != null;
    }

    public static EnumBlockMaterial getTopSlab(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getTopSlab();
        return null;
    }

    public static EnumBlockMaterial getBottomSlab(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getBottomSlab();
        return null;
    }

    public static TileSlab getSlabTile(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile);
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial slab = ((TileSlab) tile).getUpmostSlab();
            if (slab != null)
                return new ItemStack(this, 1, slab.ordinal());
        }
        return null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumBlockMaterial mat : EnumBlockMaterial.creativeList) {
            if (isEnabled(mat))
                list.add(getItem(mat));
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileSlab) {
            EnumBlockMaterial top = ((TileSlab) tile).getTopSlab();
            EnumBlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            if (top != null)
                items.add(new ItemStack(this, 1, top.ordinal()));
            if (bottom != null)
                items.add(new ItemStack(this, 1, bottom.ordinal()));
        }
        return items;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, state, 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial top = ((TileSlab) tile).getTopSlab();
            EnumBlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            float hardness = 0;
            if (top != null)
                hardness += top.getBlockHardness(worldIn, pos);
            if (bottom != null)
                hardness += bottom.getBlockHardness(worldIn, pos);
            if (top != null && bottom != null)
                hardness = hardness / 2.0F;
            return hardness;
        }
        return super.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial top = ((TileSlab) tile).getTopSlab();
            EnumBlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
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

    @Override
    public int getRenderType() {
        return renderId;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, block, target, effectRenderer, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(world, block, pos, meta, effectRenderer, null);
    }

    @Override
    public SoundType getSound(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial slab = ((TileSlab) tile).getUpmostSlab();
            if (slab != null)
                return slab.getSound();
        }
        return null;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y,
     * z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        TileSlab slab = getSlabTile(worldIn, pos);
        if (slab != null)
            if (slab.isDoubleSlab())
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            else if (slab.isBottomSlab())
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            else if (slab.isTopSlab())
                setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, X, Y,
     * Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether
     * or not to render the shared face of two adjacent blocks and also whether
     * the player can attach torches, redstone wire, etc to this block.
     *
     * @return
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     *
     * @return
     */
    @Override
    public boolean isFullCube() {
        return false;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if
     * the adjacent block is at the given coordinates. Args: blockAccess, x, y,
     * z, side
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        EnumFacing opposite = side.getOpposite();
        BlockPos offsetPos = pos.offset(opposite);

        TileEntity tile = worldIn.getTileEntity(offsetPos);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            EnumBlockMaterial top = slab.getTopSlab();
            EnumBlockMaterial bottom = slab.getBottomSlab();

            if (slab.isDoubleSlab())
                return super.shouldSideBeRendered(worldIn, pos, side);

            if (side != UP && side != DOWN && !super.shouldSideBeRendered(worldIn, pos, side))
                return false;

            if (top != null) {
                if (side == DOWN)
                    return true;
                if (side == UP && super.shouldSideBeRendered(worldIn, pos, side))
                    return true;
                if (WorldPlugin.getBlock(worldIn, pos) != this)
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, pos);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isBottomSlab() || (otherSlab.isTopSlab() && otherSlab.getTopSlab().isTransparent());
                }
            }
            if (bottom != null) {
                if (side == UP)
                    return true;
                if (side == DOWN && super.shouldSideBeRendered(worldIn, pos, side))
                    return true;
                if (WorldPlugin.getBlock(worldIn, pos) != this)
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, pos);
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
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
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
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
        TileSlab tile = getSlabTile(world, pos);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            if (tile.isTopSlab())
                return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSlab();
    }

}
