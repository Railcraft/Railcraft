/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.ITextureLoader;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;

import java.util.*;

public class BlockTrack extends BlockRailBase implements IPostConnection {

    public static final float HARDNESS = 2F;

    protected final int renderType;

    public BlockTrack(int modelID) {
        super(false);
        renderType = modelID;
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
            Game.logErrorAPI(Railcraft.getModId(), error, TrackSpec.class);
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumTrack track : EnumTrack.getCreativeList()) {
            if (track.isEnabled())
                list.add(track.getItem());
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
            Game.logErrorAPI(Railcraft.getModId(), error, TrackRegistry.class, TrackSpec.class);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                return track.getTrackSpec().getItem();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, TrackRegistry.class, TrackSpec.class);
        }
        return null;
    }

    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).getCollisionBoundingBoxFromPool();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).getSelectedBoundingBoxFromPool();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class);
        }
        return AxisAlignedBB.getBoundingBox((double) x + minX, (double) y + minY, (double) z + minZ, (double) x + maxX, (double) y + maxY, (double) z + maxZ);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec3d, Vec3 vec3d1) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackCustomShape)
                    return ((ITrackCustomShape) track).collisionRayTrace(vec3d, vec3d1);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class);
        }
        return super.collisionRayTrace(world, x, y, z, vec3d, vec3d1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z) {
        int l = iblockaccess.getBlockMetadata(x, y, z);
        if (l >= 2 && l <= 5)
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        else
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackBlocksMovement)
                    return !((ITrackBlocksMovement) track).blocksMovement();
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class, ITrackBlocksMovement.class);
        }
        return super.getBlocksMovement(world, x, y, z);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (Game.isNotHost(world))
            return;

        if (!MiscTools.isKillabledEntity(entity))
            return;

        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (!(tile instanceof TileTrack))
            return;

        ITrackInstance track = ((TileTrack) tile).getTrackInstance();
        if (!(track instanceof IElectricGrid))
            return;

        IElectricGrid.ChargeHandler chargeHandler = ((IElectricGrid) track).getChargeHandler();
        if (chargeHandler.getCharge() > 2000)
            if (entity instanceof EntityPlayer && ItemOveralls.isPlayerWearing((EntityPlayer) entity)) {
                if (!((EntityPlayer) entity).capabilities.isCreativeMode && MiscTools.RANDOM.nextInt(150) == 0) {
                    EntityPlayer player = ((EntityPlayer) entity);
                    ItemStack pants = player.getCurrentArmor(MiscTools.ArmorSlots.LEGS.ordinal());
                    player.setCurrentItemOrArmor(MiscTools.ArmorSlots.LEGS.ordinal() + 1, InvTools.damageItem(pants, 1));
                }
            } else if (((EntityLivingBase) entity).attackEntityFrom(RailcraftDamageSource.TRACK_ELECTRIC, 2))
                chargeHandler.removeCharge(2000);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return !TrackTools.isRailBlockAt(world, x, y + 1, z);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track instanceof ITrackEmitter;
        }
        return false;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack) {
            ITrackInstance track = ((TileTrack) tile).getTrackInstance();
            return track instanceof ITrackEmitter ? ((ITrackEmitter) track).getPowerOutput() : PowerPlugin.NO_POWER;
        }
        return PowerPlugin.NO_POWER;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            ((TileTrack) tile).getTrackInstance().onMinecartPass(cart);
    }

    @Override
    public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().getBasicRailMetadata(cart);
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().getRailMaxSpeed(cart);
        return 0.4f;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float u1, float u2, float u3) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().blockActivated(player);
        return false;
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().isFlexibleRail();
        return false;
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().canMakeSlopes();
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.rail.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance().getIcon();
        return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        try {
            for (ITextureLoader iconLoader : TrackRegistry.getIconLoaders()) {
                iconLoader.registerIcons(iconRegister);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, TrackRegistry.class);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, RailcraftBlocks.getBlockTrack(), target, effectRenderer, null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World worldObj, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(worldObj, RailcraftBlocks.getBlockTrack(), x, y, z, meta, effectRenderer, null);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int md, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        try {
            if (tile instanceof TileTrack) {
                List<ItemStack> drops = ((TileTrack) tile).getTrackInstance().getDrops(fortune);
                if (drops != null)
                    items.addAll(drops);
            } else {
                Game.log(Level.WARN, "Rail Tile was invalid when harvesting rail");
                items.add(new ItemStack(Blocks.rail));
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class, TrackInstanceBase.class);
        }
        return items;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }
//
//    @Override
//    public int idDropped(int i, Random random, int j) {
//        Game.log(Level.WARN, "Wrong function called when harvesting rail");
//        return Blocks.rail.idDropped(i, random, j);
//    }
//

    public TileEntity getBlockEntity(int md) {
        return null;
    }

    // Determine direction here
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack) {
            ((TileTrack) tile).onBlockPlacedBy(entityliving, stack);
            ((TileTrack) tile).getTrackInstance().onBlockPlacedBy(entityliving);
        }
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
//        if(Game.isNotHost(world)) {
//            return;
//        }
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack)
                ((TileTrack) tile).getTrackInstance().onBlockPlaced();
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class
            );
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int x, int y, int z, int l) {
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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);

        try {
            TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
            if (tile instanceof TileTrack)
                ((TileTrack) tile).getTrackInstance().onBlockRemoved();

        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class
            );
        }

        world.removeTileEntity(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        try {
            if (Game.isNotHost(world))
                return;
            TileEntity t = WorldPlugin.getBlockTile(world, x, y, z);
            if (t instanceof TileTrack) {
                TileTrack tile = (TileTrack) t;
                tile.onNeighborBlockChange(block);
                tile.getTrackInstance().onNeighborBlockChange(block);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Stack Overflow Error in BlockTrack.onNeighborBlockChange()", 10, error);
            if (Game.IS_DEBUG)
                throw error;
        }
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            try {
                return ((TileTrack) tile).getTrackInstance().getHardness();
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class
                );
            }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double srcX, double srcY, double srcZ) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            try {
                return ((TileTrack) tile).getTrackInstance().getExplosionResistance(srcX, srcY, srcZ, exploder) * 3f / 5f;
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.getModId(), error, ITrackInstance.class
                );
            }
        return getExplosionResistance(exploder);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        try {
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof IPostConnection)
                    return ((IPostConnection) track).connectsToPost(world, x, y, z, side);
            }
        } catch (Error error) {
            Game.logErrorAPI(Railcraft.getModId(), error, IPostConnection.class, ITrackInstance.class);
        }
        return ConnectStyle.NONE;
    }

}
