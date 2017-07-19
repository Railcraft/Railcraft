/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.ai.EntityAIMateBreeding;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryAdaptor;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.ITileExtraDataHandler;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileExtraData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;
import static net.minecraft.util.EnumParticleTypes.HEART;

public class TileFeedStation extends TileMachineItem implements ITileExtraDataHandler {

    private static final int AREA = 3;
    private static final int MIN_FEED_INTERVAL = 128;
    private static final int FEED_VARIANCE = 256;
    private static final byte ANIMALS_PER_FOOD = 2;
    private static final Random rand = MiscTools.RANDOM;
    private int feedTime;
    private byte feedCounter;
    private boolean powered;
    private InventoryComposite feedInv = InventoryComposite.of(InventoryAdaptor.get(this));

    public TileFeedStation() {
        super(1);
    }

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.FEED_STATION;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.FEED_STATION, player, worldObj, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld())) {
            return;
        }

        ItemStack feed = getStackInSlot(0);

        if (clock % (MIN_FEED_INTERVAL / 4) == 0 && (feed == null || sizeOf(feed) < feed.getMaxStackSize())) {
            InventoryComposite chests = InvTools.getAdjacentInventories(worldObj, getPos());
            InvTools.moveOneItem(chests, feedInv, StandardStackFilters.FEED);
        }

        feed = getStackInSlot(0);

        feedTime--;
        if (!powered && !isEmpty(feed) && feedTime <= 0) {
            feedTime = MIN_FEED_INTERVAL + rand.nextInt(FEED_VARIANCE);

            //TODO: test (maybe we can draw this somehow?)
            AxisAlignedBB box = AABBFactory.start().createBoxForTileAt(getPos()).raiseFloor(-1).raiseCeiling(2).expandHorizontally(AREA).build();
            List<EntityAnimal> animals = worldObj.getEntitiesWithinAABB(EntityAnimal.class, box);

            for (EntityAnimal target : animals) {
                if (target.isBreedingItem(getStackInSlot(0)) && feedAnimal(target)) {
                    if (feedCounter <= 0) {
                        setInventorySlotContents(0, InvTools.depleteItem(feed));
                        feedCounter = ANIMALS_PER_FOOD;
                    }
                    feedCounter--;
                    sendFeedPacket(target);
                    break;
                }
            }
        }
    }

    public void sendFeedPacket(EntityAnimal animal) {
        try {
            PacketTileExtraData pkt = new PacketTileExtraData(this);
            DataOutputStream data = pkt.getDataStream();
            data.writeInt(animal.getEntityId());

            PacketDispatcher.sendToAllAround(pkt, new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), getX(), getY(), getZ(), 80));
        } catch (IOException ignored) {
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onUpdatePacket(DataInputStream data) throws IOException {
        Entity e = worldObj.getEntityByID(data.readInt());
        if (e instanceof EntityAnimal) {
            feedAnimal((EntityAnimal) e);
        }
    }

    private boolean feedAnimal(EntityAnimal animal) {
        if (animal == null) {
            return false;
        }
        try {
            if (animal.getGrowingAge() == 0 && !animal.isInLove()) {
                EntityPlayer player;
                if (Game.isHost(worldObj)) {
                    EntityAIMateBreeding.modifyAI(animal);
                    player = RailcraftFakePlayer.get((WorldServer) worldObj, getPos());
                } else {
                    player = null;
                }

                //noinspection ConstantConditions
                animal.setInLove(player);

                for (int i = 0; i < 7; i++) {
                    double d = rand.nextGaussian() * 0.02D;
                    double d1 = rand.nextGaussian() * 0.02D;
                    double d2 = rand.nextGaussian() * 0.02D;
                    worldObj.spawnParticle(HEART, (animal.posX + rand.nextFloat() * animal.width * 2.0F) - animal.width, animal.posY + 0.5D + rand.nextFloat() * animal.height, (animal.posZ + rand.nextFloat() * animal.width * 2.0F) - animal.width, d, d1, d2);
                }

                return true;
            }
        } catch (Throwable ex) {
            Game.log(Level.ERROR, "Feed Station encountered error, {0}", ex);
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        feedCounter = data.getByte("feedCounter");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("powered", powered);
        data.setByte("feedCounter", feedCounter);
        return data;
    }
}
