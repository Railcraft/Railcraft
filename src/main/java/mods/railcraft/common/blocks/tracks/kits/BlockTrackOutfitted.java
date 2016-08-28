/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.UnlistedProperty;
import mods.railcraft.common.blocks.tracks.BlockTrackTile;
import mods.railcraft.common.blocks.tracks.IRailcraftTrack;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.behaivor.TrackSupportTools;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BlockTrackOutfitted extends BlockTrackTile implements IPostConnection, IRailcraftTrack {
    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, TrackShapeHelper::isStraight);
    public static final PropertyBool TICKING = PropertyBool.create("ticking");
    public static final IUnlistedProperty<TrackType> TRACK_TYPE = UnlistedProperty.create("track_type", TrackType.class);
    public static final IUnlistedProperty<TrackKit> TRACK_KIT = UnlistedProperty.create("track_kit", TrackKit.class);
    public static final IUnlistedProperty<Integer> STATE = Properties.toUnlisted(PropertyInteger.create("state", 0, 15));

    public BlockTrackOutfitted() {
        setCreativeTab(CreativePlugin.TRACK_TAB);
        setHarvestLevel("crowbar", 0);
        setDefaultState(getDefaultState().withProperty(TICKING, false));

        GameRegistry.registerTileEntity(TileTrackOutfitted.class, "railcraft:track.outfitted");
        GameRegistry.registerTileEntity(TileTrackOutfittedTESR.class, "railcraft:track.outfitted.tesr");
        GameRegistry.registerTileEntity(TileTrackOutfittedTicking.class, "railcraft:track.outfitted.ticking");
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getExtendedState(state, world, pos);
        state = ((IExtendedBlockState) state).withProperty(TRACK_TYPE, getTrackType(world, pos));
        state = ((IExtendedBlockState) state).withProperty(TRACK_KIT, getTrackKit(world, pos));
        Optional<TileTrackOutfitted> tile = WorldPlugin.getTileEntity(world, pos, TileTrackOutfitted.class);
        state = ((IExtendedBlockState) state).withProperty(STATE, tile.map(TileTrackOutfitted::getTrackKitInstance).map(ITrackKitInstance::getRenderState).orElse(0));
        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(TICKING, (meta & 8) > 0);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(getShapeProperty()).getMetadata();
        if (state.getValue(TICKING)) {
            i |= 8;
        }
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{getShapeProperty(), TICKING}, new IUnlistedProperty[]{TRACK_TYPE, TRACK_KIT, STATE});
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(TICKING))
            return new TileTrackOutfittedTicking();
        return new TileTrackOutfitted();
    }

    @Override
    public void initializeDefinintion() {
        TrackKit.blockTrackOutfitted = this;
    }

    @Override
    public void initializeClient() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(getShapeProperty()).ignore(TICKING).build());
    }

    @Override
    public void registerItemModel(ItemStack stack, @Nullable IVariantEnum variant) {
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (TrackType trackType : TrackRegistry.TRACK_TYPE.getVariants().values()) {
            for (TrackKit trackKit : TrackRegistry.TRACK_KIT.getVariants().values()) {
                if (trackKit.isVisible())
                    list.add(trackKit.getOutfittedTrack(trackType));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                TileTrackOutfitted trackTile = (TileTrackOutfitted) tile;
                ITrackKitInstance track = trackTile.getTrackKitInstance();
                ItemStack itemStack = track.getTrackKit().getOutfittedTrack(trackTile.getTrackType());
                if (itemStack != null)
                    return itemStack;
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKit.class);
        }
        return new ItemStack(this);
    }

    @Override
    public TrackType getTrackType(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            return ((TileTrackOutfitted) tile).getTrackType();
        }
        return TrackTypes.IRON.getTrackType();
    }

    @Nullable
    public TrackKit getTrackKit(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                return ((TileTrackOutfitted) tile).getTrackKitInstance().getTrackKit();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).getCollisionBoundingBox(state);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).getSelectedBoundingBox();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class);
        }
        return getBoundingBox(state, world, pos).offset(pos);
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d startVec, Vec3d endVec) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).collisionRayTrace(startVec, endVec);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class);
        }
        return super.collisionRayTrace(state, world, pos, startVec, endVec);
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
                if (track instanceof ITrackKitMovementBlocker)
                    return !((ITrackKitMovementBlocker) track).blocksMovement();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class, ITrackKitMovementBlocker.class);
        }
        return super.isPassable(world, pos);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;

        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ((TileTrackOutfitted) tile).getTrackType().onEntityCollidedWithBlock(world, pos, state, entity);
        }
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
            return track instanceof ITrackKitEmitter;
        }
        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
            return track instanceof ITrackKitEmitter ? ((ITrackKitEmitter) track).getPowerOutput() : PowerPlugin.NO_POWER;
        }
        return PowerPlugin.NO_POWER;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            ((TileTrackOutfitted) tile).getTrackKitInstance().onMinecartPass(cart);
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            return ((TileTrackOutfitted) tile).getTrackKitInstance().getRailDirection(state, cart);
        return state.getValue(getShapeProperty());
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            return ((TileTrackOutfitted) tile).getTrackKitInstance().getRailMaxSpeed(world, cart, pos);
        return 0.4f;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        return tile instanceof TileTrackOutfitted && ((TileTrackOutfitted) tile).getTrackKitInstance().blockActivated(playerIn, hand, heldItem);
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return !(tile instanceof TileTrackOutfitted) || ((TileTrackOutfitted) tile).getTrackKitInstance().getTrackKit().isAllowedOnSlopes();
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        try {
            if (tile instanceof TileTrackOutfitted) {
                items.addAll(((TileTrackOutfitted) tile).getTrackKitInstance().getDrops(fortune));
            } else {
                Game.log(Level.WARN, "Rail Tile was invalid when harvesting rail");
                items.add(new ItemStack(Blocks.RAIL));
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class, TrackKitInstance.class);
        }
        return items;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 1;
    }

    @Override
    public boolean canReplace(World worldIn, BlockPos pos, EnumFacing side, @Nullable ItemStack stack) {
        if (TrackTools.isRailBlockAt(worldIn, pos.up()) || TrackTools.isRailBlockAt(worldIn, pos.down()))
            return false;
        if (super.canPlaceBlockAt(worldIn, pos))
            return true;
        if (stack != null) {
            TrackType trackType = TrackRegistry.TRACK_TYPE.get(stack);
            if (trackType.getMaxSupportDistance() > 0 && TrackSupportTools.isSupported(worldIn, pos, trackType.getMaxSupportDistance()))
                return true;
            TrackKit trackKit = TrackRegistry.TRACK_KIT.get(stack);
            if (trackKit.getMaxSupportDistance() > 0 && TrackSupportTools.isSupported(worldIn, pos, trackKit.getMaxSupportDistance()))
                return true;
        }
        return false;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState();
    }

    // Determine direction here
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrackOutfitted) {
            TrackType trackType = TrackRegistry.TRACK_TYPE.get(stack);
            TrackKit trackKit = TrackRegistry.TRACK_KIT.get(stack);
            TrackTileFactory.initTrackTile((TileTrackOutfitted) tile, trackType, trackKit);
            ((TileTrackOutfitted) tile).onBlockPlacedBy(state, placer, stack);
            ((TileTrackOutfitted) tile).getTrackKitInstance().onBlockPlacedBy(state, placer, stack);
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        try {
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof TileTrackOutfitted)
                ((TileTrackOutfitted) tile).getTrackKitInstance().onBlockRemoved();

        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKitInstance.class);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
        try {
            if (Game.isClient(world))
                return;
            TileEntity t = WorldPlugin.getBlockTile(world, pos);
            if (t instanceof TileTrackOutfitted) {
                TileTrackOutfitted tile = (TileTrackOutfitted) t;
                tile.onNeighborBlockChange(state, neighborBlock);
                tile.getTrackKitInstance().onNeighborBlockChange(state, neighborBlock);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Stack Overflow Error in BlockTrack.onNeighborBlockChange()");
            if (Game.DEVELOPMENT_ENVIRONMENT)
                throw error;
        }
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted && ((TileTrackOutfitted) tile).getTrackKitInstance().isProtected())
            return -1;
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            return ((TileTrackOutfitted) tile).getTrackType().getResistance() * 3f / 5f;
        return getExplosionResistance(exploder);
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKitInstance track = ((TileTrackOutfitted) tile).getTrackKitInstance();
                if (track instanceof IPostConnection)
                    return ((IPostConnection) track).connectsToPost(world, pos, state, side);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, IPostConnection.class, ITrackKitInstance.class);
        }
        return ConnectStyle.NONE;
    }
}
