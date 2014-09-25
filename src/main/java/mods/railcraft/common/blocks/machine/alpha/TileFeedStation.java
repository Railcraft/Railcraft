/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.alpha.ai.EntityAIMateBreeding;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.Level;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.ITileExtraDataHandler;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileExtraData;
import net.minecraft.block.Block;

public class TileFeedStation extends TileMachineItem implements ITileExtraDataHandler {

    private static final int AREA = 3;
    private static final int MIN_FEED_INTERVAL = 128;
    private static final int FEED_VARIANCE = 256;
    private static final byte ANIMALS_PER_FOOD = 2;
    private static final Random rand = MiscTools.getRand();
    private int feedTime;
    private byte feedCounter;
    private boolean powered;

    public TileFeedStation() {
        super(1);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.FEED_STATION;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.FEED_STATION, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld())) {
            return;
        }

        ItemStack feed = getStackInSlot(0);

        if (clock % (MIN_FEED_INTERVAL / 4) == 0 && (feed == null || feed.stackSize < feed.getMaxStackSize())) {
            List<IInventory> chests = InvTools.getAdjacentInventories(worldObj, xCoord, yCoord, zCoord);

            for (IInventory inv : chests) {
                if (InvTools.moveOneItem(inv, this, StackFilter.FEED) != null) {
                    break;
                }
            }
        }

        feed = getStackInSlot(0);

        feedTime--;
        if (!powered && feed != null && feed.stackSize > 0 && feedTime <= 0) {
            feedTime = MIN_FEED_INTERVAL + rand.nextInt(FEED_VARIANCE);

            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
            box = box.expand(AREA, 0, AREA);
            List<EntityAnimal> animals = (List<EntityAnimal>) worldObj.getEntitiesWithinAABB(EntityAnimal.class, box);

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

            PacketDispatcher.sendToAllAround(pkt, new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 80));
        } catch (IOException ex) {
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
                if (Game.isHost(worldObj)) {
                    EntityAIMateBreeding.modifyAI(animal);
                }

                animal.func_146082_f(null);

                for (int i = 0; i < 7; i++) {
                    double d = rand.nextGaussian() * 0.02D;
                    double d1 = rand.nextGaussian() * 0.02D;
                    double d2 = rand.nextGaussian() * 0.02D;
                    worldObj.spawnParticle("heart", (animal.posX + (double) (rand.nextFloat() * animal.width * 2.0F)) - animal.width, animal.posY + 0.5D + (double) (rand.nextFloat() * animal.height), (animal.posZ + (double) (rand.nextFloat() * animal.width * 2.0F)) - animal.width, d, d1, d2);
                }

                return true;
            }
        } catch (Throwable ex) {
            Game.log(Level.ERROR, "Feed Station encountered error, {0}", ex);
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        feedCounter = data.getByte("feedCounter");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("powered", powered);
        data.setByte("feedCounter", feedCounter);
    }
}
