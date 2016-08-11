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
import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BlockTrackOutfitted extends BlockRail implements IPostConnection, IRailcraftBlock {

    public BlockTrackOutfitted() {
        setResistance(TrackConstants.RESISTANCE);
        setHardness(TrackConstants.HARDNESS);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHarvestLevel("crowbar", 0);


        GameRegistry.registerTileEntity(TileTrackOutfitted.class, "RailcraftTrackTile");
        GameRegistry.registerTileEntity(TileTrackOutfittedTESR.class, "RailcraftTrackTESRTile");
        GameRegistry.registerTileEntity(TileTrackOutfittedTicking.class, "RailcraftTrackTickingTile");

        try {
            TrackKitSpec.blockTrack = this;
        } catch (Throwable error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackKitSpec.class);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrackOutfitted) {
            ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
            return track.getActualState(state);
        }
        return state;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.addAll(
                TrackKits.getCreativeList().stream()
                        .filter(TrackKits::isEnabled)
                        .map(TrackKits::getStack)
                        .collect(Collectors.toList())
        );

        try {
            Collection<TrackKitSpec> railcraftSpecs = TrackKits.getRailcraftTrackSpecs();
            Map<String, TrackKitSpec> registeredSpecs = TrackRegistry.getTrackSpecTags();
            Set<TrackKitSpec> otherSpecs = new HashSet<TrackKitSpec>(registeredSpecs.values());
            otherSpecs.removeAll(railcraftSpecs);
            list.addAll(otherSpecs.stream().map(TrackKitSpec::getItem).collect(Collectors.toList()));
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKitSpec.class);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                ItemStack itemStack = track.getTrackKitSpec().getItem();
                if (itemStack != null)
                    return itemStack;
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKitSpec.class);
        }
        return new ItemStack(this);
    }

    @Override
    public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).getCollisionBoundingBox(state);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).getSelectedBoundingBox();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class);
        }
        return getBoundingBox(state, world, pos).offset(pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d startVec, Vec3d endVec) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                if (track instanceof ITrackKitCustomShape)
                    return ((ITrackKitCustomShape) track).collisionRayTrace(startVec, endVec);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class);
        }
        return super.collisionRayTrace(state, world, pos, startVec, endVec);
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                if (track instanceof ITrackKitMovementBlocker)
                    return !((ITrackKitMovementBlocker) track).blocksMovement();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class, ITrackKitMovementBlocker.class);
        }
        return super.isPassable(world, pos);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;

        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ((TileTrackOutfitted) tile).getTrackType().getTrackSpec().onEntityCollidedWithBlock(world, pos, state, entity);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return !TrackTools.isRailBlockAt(world, pos.up());
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
            return track instanceof ITrackKitEmitter;
        }
        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
            return track instanceof ITrackKitEmitter ? ((ITrackKitEmitter) track).getPowerOutput() : PowerPlugin.NO_POWER;
        }
        return PowerPlugin.NO_POWER;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            ((TileTrackOutfitted) tile).getTrackKit().onMinecartPass(cart);
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            return ((TileTrackOutfitted) tile).getTrackKit().getRailDirection(state, cart);
        return state.getValue(getShapeProperty());
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted)
            return ((TileTrackOutfitted) tile).getTrackKit().getRailMaxSpeed(world, cart, pos);
        return 0.4f;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        return tile instanceof TileTrackOutfitted && ((TileTrackOutfitted) tile).getTrackKit().blockActivated(playerIn, hand, heldItem);
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return !(tile instanceof TileTrackOutfitted) || ((TileTrackOutfitted) tile).getTrackKit().getTrackKitSpec().canMakeSlopes();
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        try {
            if (tile instanceof TileTrackOutfitted) {
                items.addAll(((TileTrackOutfitted) tile).getTrackKit().getDrops(fortune));
            } else {
                Game.log(Level.WARN, "Rail Tile was invalid when harvesting rail");
                items.add(new ItemStack(Blocks.RAIL));
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class, TrackKit.class);
        }
        return items;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 1;
    }
//
//    @Override
//    public int idDropped(int i, Random random, int j) {
//        Game.log(Level.WARN, "Wrong function called when harvesting rail");
//        return Blocks.RAIL.idDropped(i, random, j);
//    }
//

    // Determine direction here
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrackOutfitted) {
            ((TileTrackOutfitted) tile).onBlockPlacedBy(state, placer, stack);
            ((TileTrackOutfitted) tile).getTrackKit().onBlockPlacedBy(state, placer, stack);
        }
    }

    //TODO: Move drop code here? We have a reference to the TileEntity now.
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        try {
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof TileTrackOutfitted)
                ((TileTrackOutfitted) tile).getTrackKit().onBlockRemoved();

        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackKit.class
            );
        }

        world.removeTileEntity(pos);
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
                tile.getTrackKit().onNeighborBlockChange(state, neighborBlock);
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
        if (tile instanceof TileTrackOutfitted && ((TileTrackOutfitted) tile).getTrackKit().isProtected())
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
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrackOutfitted) {
                ITrackKit track = ((TileTrackOutfitted) tile).getTrackKit();
                if (track instanceof IPostConnection)
                    return ((IPostConnection) track).connectsToPost(world, pos, state, side);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, IPostConnection.class, ITrackKit.class);
        }
        return ConnectStyle.NONE;
    }
}
