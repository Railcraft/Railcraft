/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ITrackKitComparator;
import mods.railcraft.api.tracks.ITrackKitEmitter;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class TrackKitDetector extends TrackKitRailcraft implements ITrackKitEmitter, ITrackKitComparator {
    private byte delay;
    private Mode mode = Mode.BI_DIRECTIONAL;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DETECTOR;
    }

    @Override
    public int getRenderState() {
        int state = mode.ordinal();
        if (delay > 0)
            state += Mode.VALUES.length;
        return state;
    }

    @Override
    public boolean onCrowbarWhack(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (player.isSneaking())
            mode = EnumTools.previous(mode, Mode.VALUES);
        else
            mode = EnumTools.next(mode, Mode.VALUES);
        markBlockNeedsUpdate();
        return true;
    }

    @Override
    public void update() {
        if (Game.isClient(theWorldAsserted())) {
            return;
        }
        if (delay > 0) {
            mode.updatePowerState(this);
            delay--;
            if (delay == 0)
                notifyNeighbors();
        }
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        mode.updatePowerState(this);
    }

    protected void notifyNeighbors() {
        World world = theWorldAsserted();
        world.notifyNeighborsOfStateChange(getPos(), getTile().getBlockType(), true);
        world.notifyNeighborsOfStateChange(getPos().down(), getTile().getBlockType(), true);
        sendUpdateToClient();
    }

    protected List<EntityMinecart> findCarts() {
        return EntitySearcher.findMinecarts().around(getPos()).upTo(-0.2F).in(theWorldAsserted());
    }

    protected void setTrackPowering() {
        boolean notify = delay == 0;
        delay = CartConstants.DETECTED_POWER_OUTPUT_FADE;
        theWorldAsserted().updateComparatorOutputLevel(getPos(), getTile().getBlockType());
        if (notify) {
            notifyNeighbors();
        }
    }

    @Override
    public int getPowerOutput() {
        return delay > 0 ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
    }

    public boolean isEmittingPower() {
        return getPowerOutput() > 0;
    }

    @Override
    public int getComparatorInputOverride() {
        if (isEmittingPower()) {
            World world = theWorldAsserted();

            List<EntityMinecart> carts = EntitySearcher.findMinecarts().around(getPos()).upTo(-0.2F).in(world);
            if (!carts.isEmpty() && carts.get(0).getComparatorLevel() > -1) return carts.get(0).getComparatorLevel();

            List<EntityMinecartCommandBlock> commandCarts = EntitySearcher.find(EntityMinecartCommandBlock.class)
                    .around(getPos()).upTo(-0.2F).in(world);

            if (!commandCarts.isEmpty()) {
                return commandCarts.get(0).getCommandBlockLogic().getSuccessCount();
            }

            List<EntityMinecart> chestCarts = EntitySearcher.findMinecarts().around(getPos()).upTo(-0.2F).and(EntitySelectors.HAS_INVENTORY).in(world);

            if (!chestCarts.isEmpty()) {
                return Container.calcRedstoneFromInventory((IInventory) chestCarts.get(0));
            }
        }
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("delay", delay);
        NBTPlugin.writeEnumOrdinal(nbt, "mode", mode);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        delay = nbt.getByte("delay");
        mode = NBTPlugin.readEnumOrdinal(nbt, "mode", Mode.VALUES, Mode.BI_DIRECTIONAL);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(delay);
        ((RailcraftOutputStream) data).writeEnum(mode);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        byte delayData = data.readByte();
        boolean update = false;
        if ((delay == 0) != (delayData == 0)) {
            delay = delayData;
            update = true;
        }
        Mode m = ((RailcraftInputStream) data).readEnum(Mode.VALUES);
        if (mode != m) {
            mode = m;
            update = true;
        }
        if (update)
            markBlockNeedsUpdate();
    }

    private enum Mode {
        BI_DIRECTIONAL {
            @Override
            protected void updatePowerState(TrackKitDetector detector) {
                List<EntityMinecart> carts = detector.findCarts();
                if (!carts.isEmpty())
                    detector.setTrackPowering();
            }
        },
        TRAVEL {
            @Override
            protected void updatePowerState(TrackKitDetector detector) {
                updatePowerState(detector, false);
            }
        },
        TRAVEL_REVERSED {
            @Override
            protected void updatePowerState(TrackKitDetector detector) {
                updatePowerState(detector, true);
            }
        };
        private static final Mode[] VALUES = values();

        protected abstract void updatePowerState(TrackKitDetector detector);

        protected void updatePowerState(TrackKitDetector detector, boolean reversed) {
            List<EntityMinecart> carts = detector.findCarts();
            if (!carts.isEmpty()) {
                BlockRailBase.EnumRailDirection shape = detector.getRailDirectionRaw();
                Predicate<EntityMinecart> isTravelling;
                if (TrackShapeHelper.isEastWest(shape))
                    isTravelling = cart -> reversed ? cart.motionX < 0.0D : cart.motionX > 0.0D;
                else
                    isTravelling = cart -> reversed ? cart.motionZ > 0.0D : cart.motionZ < 0.0D;
                if (carts.stream().anyMatch(isTravelling)) {
                    detector.setTrackPowering();
                }
            }
        }
    }
}
