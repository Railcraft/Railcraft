/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.slab;

import net.minecraft.block.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockRailcraftSlab extends BlockContainer implements IBlockSoundProvider {

    static BlockRailcraftSlab block;

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
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getTopSlab();
        return null;
    }

    public static EnumBlockMaterial getBottomSlab(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getBottomSlab();
        return null;
    }

    public static TileSlab getSlabTile(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile);
        return null;
    }

    private final int renderId;
    public static int currentRenderPass;
    public static EnumBlockMaterial textureSlab = null;

    protected BlockRailcraftSlab(int renderId) {
        super(Material.rock);
        this.renderId = renderId;
        this.setStepSound(RailcraftSound.getInstance());
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
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
    public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
        TileEntity tile = world.getTileEntity(i, j, k);
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
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, x, y, z, 0, 0);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial top = ((TileSlab) tile).getTopSlab();
            EnumBlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            float hardness = 0;
            if (top != null)
                hardness += top.getBlockHardness(world, x, y, z);
            if (bottom != null)
                hardness += bottom.getBlockHardness(world, x, y, z);
            if (top != null && bottom != null)
                hardness = hardness / 2.0F;
            return hardness;
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial top = ((TileSlab) tile).getTopSlab();
            EnumBlockMaterial bottom = ((TileSlab) tile).getBottomSlab();
            float resist = 0;
            if (top != null)
                resist += top.getExplosionResistance(entity);
            if (bottom != null)
                resist += bottom.getExplosionResistance(entity);
            if (top != null && bottom != null)
                resist = resist / 2.0F;
            return resist;
        }
        return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        currentRenderPass = pass;
        return pass == 0 || pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return EnumBlockMaterial.fromOrdinal(meta).getIcon(side);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (textureSlab != null)
            return textureSlab.getIcon(side);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getTexture(side);
        return super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, block, target, effectRenderer, null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World worldObj, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(worldObj, block, x, y, z, meta, effectRenderer, null);
    }

    @Override
    public SoundType getSound(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab) {
            EnumBlockMaterial slab = ((TileSlab) tile).getUpmostSlab();
            if (slab != null)
                return slab.getSound();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y,
     * z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileSlab slab = getSlabTile(world, x, y, z);
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
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
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
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if
     * the adjacent block is at the given coordinates. Args: blockAccess, x, y,
     * z, side
     */
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {

        int i1 = x + Facing.offsetsXForSide[Facing.oppositeSide[side]];
        int j1 = y + Facing.offsetsYForSide[Facing.oppositeSide[side]];
        int k1 = z + Facing.offsetsZForSide[Facing.oppositeSide[side]];

        TileEntity tile = world.getTileEntity(i1, j1, k1);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            EnumBlockMaterial top = slab.getTopSlab();
            EnumBlockMaterial bottom = slab.getBottomSlab();

            if (slab.isDoubleSlab())
                return super.shouldSideBeRendered(world, x, y, z, side);

            if (side != 1 && side != 0 && !super.shouldSideBeRendered(world, x, y, z, side))
                return false;

            if (top != null) {
                if (side == 0)
                    return true;
                if (side == 1 && super.shouldSideBeRendered(world, x, y, z, side))
                    return true;
                if (WorldPlugin.getBlock(world, x, y, z) != this)
                    return true;
                TileSlab otherSlab = getSlabTile(world, x, y, z);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isBottomSlab() || (otherSlab.isTopSlab() && otherSlab.getTopSlab().isTransparent());
                }
            }
            if (bottom != null) {
                if (side == 1)
                    return true;
                if (side == 0 && super.shouldSideBeRendered(world, x, y, z, side))
                    return true;
                if (WorldPlugin.getBlock(world, x, y, z) != this)
                    return true;
                TileSlab otherSlab = getSlabTile(world, x, y, z);
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileSlab tile = getSlabTile(world, x, y, z);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            if (side == ForgeDirection.DOWN && tile.isBottomSlab())
                return true;
            if (side == ForgeDirection.UP && tile.isTopSlab())
                return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        TileSlab tile = getSlabTile(world, x, y, z);
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
