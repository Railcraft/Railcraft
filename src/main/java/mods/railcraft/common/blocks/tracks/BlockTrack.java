/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.IBlockTrack;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.tracks.behaivor.TrackSupportTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

/**
 * Created by CovertJaguar on 8/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTrack extends BlockRailBase implements IBlockTrack, IRailcraftBlock {

    protected BlockTrack() {
        super(false);
        setResistance(TrackConstants.RESISTANCE);
        setHardness(TrackConstants.HARDNESS);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setDefaultState(blockState.getBaseState().withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getShapeProperty());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getShapeProperty()).getMetadata();
    }

    public void breakRail(World world, BlockPos pos) {
        if (Game.isHost(world))
            world.destroyBlock(pos, true);
    }

    @Override
    public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!TrackSupportTools.isSupported(world, pos, getTrackType(world, pos).getMaxSupportDistance()))
            breakRail(world, pos);
    }

    public int getMaxSupportedDistance(World worldIn, BlockPos pos) {
        return getTrackType(worldIn, pos).getMaxSupportDistance();
    }

    protected boolean isRailValid(IBlockState state, World world, BlockPos pos, int maxSupportedDistance) {
        boolean valid = true;
        EnumRailDirection dir = TrackTools.getTrackDirectionRaw(state);
        if (!TrackSupportTools.isSupported(world, pos, maxSupportedDistance))
            valid = false;
        if (maxSupportedDistance == 0) {
            if (dir == ASCENDING_EAST && !world.isSideSolid(pos.east(), EnumFacing.UP))
                valid = false;
            else if (dir == ASCENDING_WEST && !world.isSideSolid(pos.west(), EnumFacing.UP))
                valid = false;
            else if (dir == ASCENDING_NORTH && !world.isSideSolid(pos.north(), EnumFacing.UP))
                valid = false;
            else if (dir == ASCENDING_SOUTH && !world.isSideSolid(pos.south(), EnumFacing.UP))
                valid = false;
        }
        return valid;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (Game.isClient(worldIn))
            return;
        if (!isRailValid(state, worldIn, pos, getMaxSupportedDistance(worldIn, pos))) {
            breakRail(worldIn, pos);
            return;
        }
        updateState(state, worldIn, pos, neighborBlock);
        TrackTools.traverseConnectedTracks(worldIn, pos, (w, p) -> {
            IBlockState s = WorldPlugin.getBlockState(w, p);
            Block b = s.getBlock();
            if (!TrackTools.isRail(s))
                return false;
            if (b instanceof BlockTrack) {
                BlockTrack track = (BlockTrack) b;
                int maxSupportedDistance = track.getMaxSupportedDistance(w, p);
                if (maxSupportedDistance <= 0 || TrackSupportTools.isSupportedDirectly(w, p))
                    return false;
                if (!track.isRailValid(s, w, p, maxSupportedDistance)) {
                    breakRail(w, p);
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        getTrackType(world, pos).getEventHandler().onMinecartPass(world, cart, pos, null);
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        EnumRailDirection shape = getTrackType(world, pos).getEventHandler().getRailDirectionOverride(world, pos, state, cart);
        if (shape != null)
            return shape;
        return super.getRailDirection(world, pos, state, cart);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;
        getTrackType(world, pos).getEventHandler().onEntityCollision(world, pos, state, entity);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return getTrackType(world, pos).getEventHandler().getMaxSpeed(world, cart, pos);
    }

    @Override
    public abstract TrackType getTrackType(IBlockAccess world, BlockPos pos);

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return TrackSupportTools.isSupportedDirectly(world, pos);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        if (TrackToolsAPI.isRailBlockAt(world, pos.up())
                || TrackTools.isRailBlockAt(world, pos.down()))
            return false;
        TrackType trackType = getTrackType(world, pos);
        return TrackSupportTools.isSupported(world, pos, trackType.getMaxSupportDistance());
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    @SuppressWarnings({"incomplete-switch", "deprecation"})
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:

                switch (state.getValue(getShapeProperty())) {
                    case ASCENDING_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_WEST);
                }

            case COUNTERCLOCKWISE_90:

                switch (state.getValue(getShapeProperty())) {
                    case ASCENDING_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_SOUTH);
                }

            case CLOCKWISE_90:

                switch (state.getValue(getShapeProperty())) {
                    case ASCENDING_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_SOUTH);
                }

            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    @SuppressWarnings({"incomplete-switch", "deprecation"})
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        BlockRailBase.EnumRailDirection trackShape = state.getValue(getShapeProperty());

        switch (mirrorIn) {
            case LEFT_RIGHT:

                switch (trackShape) {
                    case ASCENDING_NORTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_EAST);
                    default:
                        return super.withMirror(state, mirrorIn);
                }

            case FRONT_BACK:

                switch (trackShape) {
                    case ASCENDING_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(getShapeProperty(), BlockRailBase.EnumRailDirection.NORTH_WEST);
                }
        }

        return super.withMirror(state, mirrorIn);
    }
}
