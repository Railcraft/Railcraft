package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.machine.interfaces.*;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
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
import net.minecraft.util.NonNullList;
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
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 */
public abstract class BlockEntityDelegate extends BlockContainerRailcraft implements ColorPlugin.IColoredBlock {

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

    public abstract Class<? extends TileEntity> getTileClass(IBlockState state);

    @Override
    public void finalizeDefinition() {
        ColorPlugin.instance.register(this, this);
    }

    @Override
    public IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) -> {
            //TODO: this probably not entirely correct, may need to handle this differently if world/pos null
            if (worldIn != null && pos != null) {
                WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::colorMultiplier);
            }
            return EnumColor.WHITE.getHexColor();
        };
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.recolourBlock(color)).orElse(false);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.blockActivated(playerIn, hand, playerIn.getHeldItem(hand), facing, hitX, hitY, hitZ)).orElse(false);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return WorldPlugin.getTileEntity(world, pos, ITileRotate.class).map(t -> t.rotateBlock(axis)).orElse(false);
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return WorldPlugin.getTileEntity(world, pos, ITileRotate.class).map(ITileRotate::getValidRotations).orElseGet(() -> new EnumFacing[]{});
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.randomDisplayTick(rand));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return WorldPlugin.getTileEntity(worldIn, pos, ITileNonSolid.class).map(t -> t.getShape(face)).orElseGet(() -> super.getBlockFaceShape(worldIn, state, pos, face));
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getBlockFaceShape(world, state, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        WorldPlugin.getTileEntity(world, pos, ISmartTile.class).ifPresent(t -> t.addDrops(drops, fortune));
    }

    public List<ItemStack> getBlockDroppedSilkTouch(World world, BlockPos pos, IBlockState state, int fortune) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.getBlockDroppedSilkTouch(fortune)).orElseGet(() -> Collections.singletonList(getSilkTouchDrop(state)));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canSilkHarvest(player)).orElse(super.canSilkHarvest(world, pos, state, player));
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, world.getBlockState(pos), 0);
        if (drops.isEmpty())
            return getItem(world, pos, state);
        return drops.get(0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return WorldPlugin.getTileEntity(worldIn, pos, ITileRedstoneEmitter.class).map(t -> t.getPowerOutput(side)).orElse(PowerPlugin.NO_POWER);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.canConnectRedstone(side)).orElse(false);
    }

    public void initFromItem(World world, BlockPos pos, ItemStack stack) {
        WorldPlugin.getTileEntity(world, pos, ISmartTile.class).ifPresent(t -> t.initFromItem(stack));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.onBlockPlacedBy(state, placer, stack));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
        if (needsSupport() && !worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
            WorldPlugin.destroyBlock(worldIn, pos, true);
            return;
        }

        try {
            WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(t -> t.onNeighborBlockChange(state, neighborBlock, fromPos));
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()");
            if (Game.DEVELOPMENT_ENVIRONMENT)
                throw error;
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
        super.breakBlock(worldIn, pos, state);
        WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).ifPresent(ISmartTile::onBlockRemoval);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WorldPlugin.retrieveFromTile(world, pos, ITileShaped.class, t -> t.getBoundingBox(world, pos)).orElseGet(() -> super.getBoundingBox(state, world, pos));
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WorldPlugin.retrieveFromTile(world, pos, ITileShaped.class, t -> t.getCollisionBoundingBox(world, pos)).orElseGet(() -> super.getCollisionBoundingBox(state, world, pos));
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return WorldPlugin.retrieveFromTile(world, pos, ITileShaped.class, t -> t.getSelectedBoundingBox(world, pos)).orElseGet(() -> super.getSelectedBoundingBox(state, world, pos));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (pos.getY() < 0)
            return 0;
        return WorldPlugin.retrieveFromTile(world, pos, ITileLit.class, ITileLit::getLightValue).orElse(0);
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
        return WorldPlugin.retrieveFromTile(worldIn, pos, ITileCompare.class, ITileCompare::getComparatorInputOverride).orElseGet(() -> super.getComparatorInputOverride(state, worldIn, pos));
    }

    public IPostConnection.ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return WorldPlugin.getTileEntity(world, pos, ISmartTile.class).map(t -> t.connectsToPost(face)).orElse(IPostConnection.ConnectStyle.NONE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return WorldPlugin.getTileEntity(worldIn, pos, ISmartTile.class).map(t -> t.getActualState(state)).orElseGet(() -> super.getActualState(state, worldIn, pos));
    }
}
