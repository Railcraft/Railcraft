/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.interfaces.*;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Random;

public abstract class BlockEntityDelegate<T extends TileRailcraft & ISmartTile> extends BlockContainerRailcraft<T> implements IPostConnection {

    protected BlockEntityDelegate(Material materialIn) {
        super(materialIn);
    }

    protected BlockEntityDelegate(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    {
        // TODO: This can't be right
        setSoundType(SoundType.STONE);
        this.fullBlock = true;
        lightOpacity = 255;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.blockActivated(playerIn, hand, side, hitX, hitY, hitZ)).orElse(false);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return TileManager.forTile(this::getTileClass, WorldPlugin.getBlockState(world, pos), world, pos)
                .retrieve(ITileRotate.class, t -> t.rotateBlock(axis)).orElse(false);
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, WorldPlugin.getBlockState(world, pos), world, pos)
                .retrieve(ITileRotate.class, ITileRotate::getValidRotations).orElse(null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.randomDisplayTick(rand));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return TileManager.forTile(this::getTileClass, state, worldIn, pos)
                .retrieve(ITileNonSolid.class, t -> t.getShape(face)).orElseGet(() -> super.getBlockFaceShape(worldIn, state, pos, face));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getBlockFaceShape(world, state, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return TileManager.forTile(this::getTileClass, state, worldIn, pos)
                .retrieve(ITileRedstoneEmitter.class, t -> t.getPowerOutput(side)).orElse(PowerPlugin.NO_POWER);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canConnectRedstone(side)).orElse(false);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, neighborBlock, fromPos);
        if (needsSupport() && !worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
            WorldPlugin.destroyBlock(worldIn, pos, true);
        }
    }

    public boolean needsSupport() {
        return false;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::onBlockAdded);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::onBlockRemoval);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getBoundingBox(world, pos)).orElse(super.getBoundingBox(state, world, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getCollisionBoundingBox(world, pos)).orElse(super.getCollisionBoundingBox(state, world, pos));
    }

    @Override
//    @SideOnly(Side.CLIENT) TODO Server crash hotfix, wait for Forge PR 5127
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getSelectedBoundingBox(world, pos)).orElse(super.getSelectedBoundingBox(state, world, pos));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
//        if (pos.getY() < 0)
//            return 0; // cubic chunks
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileLit.class, ITileLit::getLightValue).orElse(0);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getResistance(exploder) * 3f / 5f).orElse(0f);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canCreatureSpawn(type)).orElseGet(() -> super.canCreatureSpawn(state, world, pos, type));
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(ISmartTile::getHardness).orElse(0F);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(IBlockState state) {
        return TileManager.isInstance(this::getTileClass, ITileCompare.class, state);
    }

    /**
     * Value is provided by the tile entity
     */

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, worldIn, pos)
                .retrieve(ITileCompare.class, ITileCompare::getComparatorInputOverride).orElseGet(() -> super.getComparatorInputOverride(state, worldIn, pos));
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.connectsToPost(side)).orElse(IPostConnection.ConnectStyle.NONE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.getActualState(state)).orElseGet(() -> super.getActualState(state, worldIn, pos));
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getExtendedState(state)).orElseGet(() -> super.getExtendedState(state, world, pos));
    }
}
