/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.tracks.*;
import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BlockTrack extends BlockRail implements IPostConnection, IRailcraftObject {

    public static final float HARDNESS = 2F;

    public BlockTrack() {
        setResistance(3.5F);
        setHardness(HARDNESS);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHarvestLevel("crowbar", 0);


        GameRegistry.registerTileEntity(TileTrack.class, "RailcraftTrackTile");
        GameRegistry.registerTileEntity(TileTrackTESR.class, "RailcraftTrackTESRTile");
        GameRegistry.registerTileEntity(TileTrackTicking.class, "RailcraftTrackTickingTile");

        try {
            TrackSpec.blockTrack = this;
        } catch (Throwable error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackSpec.class);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track.getActualState(state);
        }
        return state;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.addAll(
                EnumTrack.getCreativeList().stream()
                        .filter(EnumTrack::isEnabled)
                        .map(EnumTrack::getStack)
                        .collect(Collectors.toList())
        );

        try {
            Collection<TrackSpec> railcraftSpecs = EnumTrack.getRailcraftTrackSpecs();
            Map<Short, TrackSpec> registeredSpecs = TrackRegistry.getTrackSpecIDs();
            Set<TrackSpec> otherSpecs = new HashSet<TrackSpec>(registeredSpecs.values());
            otherSpecs.removeAll(railcraftSpecs);
            list.addAll(otherSpecs.stream().map(TrackSpec::getItem).collect(Collectors.toList()));
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackSpec.class);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                ItemStack itemStack = track.getTrackSpec().getItem();
                if (itemStack != null)
                    return itemStack;
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackSpec.class);
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
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).getCollisionBoundingBox(state);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).getSelectedBoundingBox();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
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
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).collisionRayTrace(startVec, endVec);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return super.collisionRayTrace(state, world, pos, startVec, endVec);
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackBlocksMovement)
                    return !((ITrackBlocksMovement) track).blocksMovement();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class, ITrackBlocksMovement.class);
        }
        return super.isPassable(world, pos);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;

        if (!MiscTools.isKillableEntity(entity))
            return;

        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (!(tile instanceof TileTrack))
            return;

        ITrackInstance track = ((TileTrack) tile).getTrackInstance();
        if (!(track instanceof IElectricGrid))
            return;

        IElectricGrid.ChargeHandler chargeHandler = ((IElectricGrid) track).getChargeHandler();
        if (chargeHandler != null && chargeHandler.getCharge() > 2000)
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = ((EntityPlayer) entity);
                ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
                if (pants != null && RailcraftItems.overalls.isInstance(pants)
                        && !((EntityPlayer) entity).capabilities.isCreativeMode
                        && MiscTools.RANDOM.nextInt(150) == 0) {
                    player.setItemStackToSlot(EntityEquipmentSlot.LEGS, InvTools.damageItem(pants, 1));
                }
            } else if (entity.attackEntityFrom(RailcraftDamageSource.TRACK_ELECTRIC, 2))
                chargeHandler.removeCharge(2000);
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
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track instanceof ITrackEmitter;
        }
        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track instanceof ITrackEmitter ? ((ITrackEmitter) track).getPowerOutput() : PowerPlugin.NO_POWER;
        }
        return PowerPlugin.NO_POWER;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            ((TileTrack) tile).getTrackInstance().onMinecartPass(cart);
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().getRailDirection(state, cart);
        return state.getValue(getShapeProperty());
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().getRailMaxSpeed(cart);
        return 0.4f;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        return tile instanceof TileTrack && ((TileTrack) tile).getTrackInstance().blockActivated(playerIn, hand, heldItem);
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return tile instanceof TileTrack && ((TileTrack) tile).getTrackInstance().isFlexibleRail();
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return !(tile instanceof TileTrack) || ((TileTrack) tile).getTrackInstance().canMakeSlopes();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World worldObj, BlockPos pos, ParticleManager effectRenderer) {
        IBlockState state = WorldPlugin.getBlockState(worldObj, pos);
        return ParticleHelper.addDestroyEffects(worldObj, this, pos, state, effectRenderer, null);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        try {
            if (tile instanceof TileTrack) {
                items.addAll(((TileTrack) tile).getTrackInstance().getDrops(fortune));
            } else {
                Game.log(Level.WARN, "Rail Tile was invalid when harvesting rail");
                items.add(new ItemStack(Blocks.RAIL));
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class, TrackInstanceBase.class);
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
        if (tile instanceof TileTrack) {
            ((TileTrack) tile).onBlockPlacedBy(state, placer, stack);
            ((TileTrack) tile).getTrackInstance().onBlockPlacedBy(state, placer, stack);
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
            if (tile instanceof TileTrack)
                ((TileTrack) tile).getTrackInstance().onBlockRemoved();

        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class
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
            if (t instanceof TileTrack) {
                TileTrack tile = (TileTrack) t;
                tile.onNeighborBlockChange(state, neighborBlock);
                tile.getTrackInstance().onNeighborBlockChange(state, neighborBlock);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Stack Overflow Error in BlockTrack.onNeighborBlockChange()");
            if (Game.IS_DEBUG)
                throw error;
        }
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            try {
                return ((TileTrack) tile).getTrackInstance().getHardness();
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class
                );
            }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            try {
                return ((TileTrack) tile).getTrackInstance().getExplosionResistance(explosion, exploder) * 3f / 5f;
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class
                );
            }
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
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof IPostConnection)
                    return ((IPostConnection) track).connectsToPost(world, pos, state, side);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, IPostConnection.class, ITrackInstance.class);
        }
        return ConnectStyle.NONE;
    }
}
