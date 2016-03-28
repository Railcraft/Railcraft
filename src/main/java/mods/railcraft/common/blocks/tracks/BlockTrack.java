/* Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar and may only be used with explicit written permission unless otherwise
 * specified on the license page at http://railcraft.info/wiki/info:license. */
package mods.railcraft.common.blocks.tracks;

import java.util.*;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
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
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.tracks.*;
import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.ItemOveralls;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;

public class BlockTrack extends BlockRailBase implements IPostConnection {
    public static final PropertyEnum<EnumRailDirection> TRACK_DIRECTION =  PropertyEnum.create("direction", EnumRailDirection.class);
    
    public static final float HARDNESS = 2F;

    public BlockTrack() {
        super(false);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        setResistance(3.5F);
        setHardness(HARDNESS);
        setStepSound(soundTypeMetal);
        setCreativeTab(CreativeTabs.tabTransport);
        setHarvestLevel("crowbar", 0);

        GameRegistry.registerTileEntity(TileTrack.class, "RailcraftTrackTile");
        GameRegistry.registerTileEntity(TileTrackTESR.class, "RailcraftTrackTESRTile");

        try {
            TrackSpec.blockTrack = this;
        } catch (Throwable error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackSpec.class);
        }
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, TRACK_DIRECTION);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TRACK_DIRECTION).getMetadata();
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TRACK_DIRECTION, EnumRailDirection.byMetadata(meta));
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return TRACK_DIRECTION;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumTrack track : EnumTrack.getCreativeList()) {
            if (track.isEnabled()) list.add(track.getItem());
        }

        try {
            Collection<TrackSpec> railcraftSpecs = EnumTrack.getRailcraftTrackSpecs();
            Map<Short, TrackSpec> registeredSpecs = TrackRegistry.getTrackSpecIDs();
            Set<TrackSpec> otherSpecs = new HashSet<TrackSpec>(registeredSpecs.values());
            otherSpecs.removeAll(railcraftSpecs);
            for (TrackSpec spec : otherSpecs) {
                list.add(spec.getItem());
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackSpec.class);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                return track.getTrackSpec().getItem();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackSpec.class);
        }
        return null;
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
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape) return ((ITrackCustomShape) track).getCollisionBoundingBoxFromPool();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape) return ((ITrackCustomShape) track).getSelectedBoundingBoxFromPool();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return AxisAlignedBB.fromBounds(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 vec3d, Vec3 vec3d1) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape) return ((ITrackCustomShape) track).collisionRayTrace(vec3d, vec3d1);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return super.collisionRayTrace(world, pos, vec3d, vec3d1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, BlockPos pos) {
        EnumRailDirection direction = iblockaccess.getBlockState(pos).getValue(TRACK_DIRECTION);
        if (direction.isAscending()) setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        else setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackBlocksMovement) return !((ITrackBlocksMovement) track).blocksMovement();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class, ITrackBlocksMovement.class);
        }
        return super.isPassable(world, pos);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
        if (Game.isNotHost(world)) return;

        if (!MiscTools.isKillabledEntity(entity)) return;

        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (!(tile instanceof TileTrack)) return;

        ITrackInstance track = ((TileTrack) tile).getTrackInstance();
        if (!(track instanceof IElectricGrid)) return;

        IElectricGrid.ChargeHandler chargeHandler = ((IElectricGrid) track).getChargeHandler();
        if (chargeHandler.getCharge() > 2000) if (entity instanceof EntityPlayer && ItemOveralls.isPlayerWearing((EntityPlayer) entity)) {
            if (!((EntityPlayer) entity).capabilities.isCreativeMode && MiscTools.RANDOM.nextInt(150) == 0) {
                EntityPlayer player = ((EntityPlayer) entity);
                ItemStack pants = player.getCurrentArmor(MiscTools.ArmorSlots.LEGS.ordinal());
                player.setCurrentItemOrArmor(MiscTools.ArmorSlots.LEGS.ordinal() + 1, InvTools.damageItem(pants, 1));
            }
        } else if (entity.attackEntityFrom(RailcraftDamageSource.TRACK_ELECTRIC, 2)) chargeHandler.removeCharge(2000);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return !TrackTools.isRailBlockAt(world, pos.up());
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing face) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track instanceof ITrackEmitter;
        }
        return false;
    }
    
    @Override
    public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
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
        if (tile instanceof TileTrack) ((TileTrack) tile).getTrackInstance().onMinecartPass(cart);
    }

    // FIXME 1.8.9 port removes this
//    @Override
//    public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
//        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
//        if (tile instanceof TileTrack) return ((TileTrack) tile).getTrackInstance().getBasicRailMetadata(cart);
//        return world.getBlockMetadata(x, y, z);
//    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) return ((TileTrack) tile).getTrackInstance().getRailMaxSpeed(cart);
        return 0.4f;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float u1, float u2, float u3) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) return ((TileTrack) tile).getTrackInstance().blockActivated(player);
        return false;
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) return ((TileTrack) tile).getTrackInstance().isFlexibleRail();
        return false;
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) return ((TileTrack) tile).getTrackInstance().canMakeSlopes();
        return true;
    }

//    @Override
//    public void registerBlockIcons(IIconRegister iconRegister) {
//        try {
//            for (ITextureLoader iconLoader : TrackRegistry.getIconLoaders()) {
//                iconLoader.registerIcons(iconRegister);
//            }
//        } catch (Error error) {
//            Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class);
//        }
//    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, RailcraftBlocks.getBlockTrack(), target, effectRenderer, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World worldObj, BlockPos pos, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(worldObj, RailcraftBlocks.getBlockTrack(), pos, worldObj.getBlockState(pos), effectRenderer, null);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        try {
            if (tile instanceof TileTrack) {
                List<ItemStack> drops = ((TileTrack) tile).getTrackInstance().getDrops(fortune);
                if (drops != null) items.addAll(drops);
            } else {
                Game.log(Level.WARN, "Rail Tile was invalid when harvesting rail");
                items.add(new ItemStack(Blocks.rail));
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
    // @Override
    // public int idDropped(int i, Random random, int j) {
    // Game.log(Level.WARN, "Wrong function called when harvesting rail");
    // return Blocks.rail.idDropped(i, random, j);
    // }
    //

    public TileEntity getBlockEntity(int md) {
        return null;
    }

    // Determine direction here
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrack) {
            ((TileTrack) tile).onBlockPlacedBy(state, placer, stack);
            ((TileTrack) tile).getTrackInstance().onBlockPlacedBy(state, placer, stack);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, BlockPos pos, IBlockState state, TileEntity te) {}

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        try {
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof TileTrack) ((TileTrack) tile).getTrackInstance().onBlockRemoved();

        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }

        world.removeTileEntity(pos);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        try {
            if (Game.isNotHost(world)) return;
            TileEntity t = WorldPlugin.getBlockTile(world, pos);
            if (t instanceof TileTrack) {
                TileTrack tile = (TileTrack) t;
                tile.onNeighborBlockChange(state, neighborBlock);
                tile.getTrackInstance().onNeighborBlockChange(state, neighborBlock);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Stack Overflow Error in BlockTrack.onNeighborBlockChange()", 10, error);
            if (Game.IS_DEBUG) throw error;
        }
    }

    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) try {
            return ((TileTrack) tile).getTrackInstance().getHardness();
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return super.getBlockHardness(world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack) try {
            return ((TileTrack) tile).getTrackInstance().getExplosionResistance(explosion, exploder) * 3f / 5f;
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, ITrackInstance.class);
        }
        return getExplosionResistance(exploder);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }
    
    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof IPostConnection) return ((IPostConnection) track).connectsToPost(world, pos, state, side);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.MOD_ID, error, IPostConnection.class, ITrackInstance.class);
        }
        return ConnectStyle.NONE;
    }

}
