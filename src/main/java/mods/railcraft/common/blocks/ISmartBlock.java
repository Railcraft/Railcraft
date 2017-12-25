package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.machine.interfaces.*;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An interface for tile-delegated blocks
 */
public interface ISmartBlock {

    @SuppressWarnings("unchecked")
    default Block block() {
        return (Block) this;
    }

    @SuppressWarnings("unchecked")
    <T extends TileEntity & ISmartTile> Class<T> getTileClass(IBlockState state);

    default IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) -> {
            //TODO: this probably not entirely correct, may need to handle this differently if world/pos null
            if (worldIn != null && pos != null) {
                WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::colorMultiplier);
            }
            return EnumColor.WHITE.getHexColor();
        };
    }

    default boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.recolourBlock(color)).orElse(false);
    }

    default int damageDropped(IBlockState state) {
        return block().getMetaFromState(state);
    }

    default boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand == EnumHand.OFF_HAND)
            return false;
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.blockActivated(playerIn, hand, heldItem, side, hitX, hitY, hitZ)).orElse(false);
    }

    default boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return WorldPlugin.getTileEntity(world, pos, ITileRotate.class).map(t -> t.rotateBlock(axis)).orElse(false);
    }

    default EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return WorldPlugin.getTileEntity(world, pos, ITileRotate.class).map(ITileRotate::getValidRotations).orElseGet(() -> new EnumFacing[]{});
    }

    @SideOnly(Side.CLIENT)
    default void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.randomDisplayTick(rand));
    }

    default boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileNonSolid.class, t -> t.isSideSolid(side)).orElse(true);
    }

    default List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getDrops(fortune)).orElse(Collections.emptyList());
    }

    default List<ItemStack> getBlockDroppedSilkTouch(World world, BlockPos pos, IBlockState state, int fortune) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getBlockDroppedSilkTouch(fortune)).orElse(Collections.emptyList());
    }

    default boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canSilkHarvest(player)).orElse(false);
    }

    @Nullable
    default ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, world.getBlockState(pos), 0);
        if (drops.isEmpty())
            return block().getItem(world, pos, state);
        return drops.get(0);
    }

    default boolean canProvidePower(IBlockState state) {
        return true;
    }

    default int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return WorldPlugin.getTileEntity(worldIn, pos, ITileRedstoneEmitter.class).map(t -> t.getPowerOutput(side)).orElse(PowerPlugin.NO_POWER);
    }

    default boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canConnectRedstone(side)).orElse(false);
    }

    default void initFromItem(World world, BlockPos pos, ItemStack stack) {
        WorldPlugin.getTileEntity(world, pos, ISmartTile.class).ifPresent(t -> t.initFromItem(stack));
    }

    default void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.onBlockPlacedBy(state, placer, stack));
    }

    default void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
        if (needsSupport() && !worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
            WorldPlugin.destroyBlock(worldIn, pos, true);
            return;
        }

        try {
            WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.onNeighborBlockChange(state, neighborBlock));
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()");
            if (Game.DEVELOPMENT_ENVIRONMENT)
                throw error;
        }
    }

    default boolean needsSupport() {
        return false;
    }

    default void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::onBlockAdded);
    }

    default void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::onBlockRemoval);
    }

    default AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getBoundingBox(world, pos)).orElse(Block.FULL_BLOCK_AABB);
    }

    default AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getCollisionBoundingBox(world, pos)).orElse(Block.FULL_BLOCK_AABB);
    }

    default AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileShaped.class, t -> t.getSelectedBoundingBox(world, pos)).orElse(Block.FULL_BLOCK_AABB);
    }

    default int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (pos.getY() < 0)
            return 0;
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileLit.class, ITileLit::getLightValue).orElse(0);
    }

    default boolean hasTileEntity(IBlockState state) {
        return true;
    }

    default float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getResistance(exploder) * 3f / 5f).orElse(0f);
    }

    default boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    default boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canCreatureSpawn(type)).orElse(false);
    }

    default float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(ISmartTile::getHardness).orElse(0F);
    }

    default boolean hasComparatorInputOverride(IBlockState state) {
        return TileManager.isInstance(this::getTileClass, ITileCompare.class, state);
    }

    /**
     * Value is provided by the tile entity
     */

    default int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, worldIn, pos)
                .retrieve(ITileCompare.class, ITileCompare::getComparatorInputOverride).orElse(0);
    }

    default IPostConnection.ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing face) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.connectsToPost(face)).orElse(IPostConnection.ConnectStyle.NONE);
    }

    default IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.getActualState(state)).orElse(state);
    }
}
