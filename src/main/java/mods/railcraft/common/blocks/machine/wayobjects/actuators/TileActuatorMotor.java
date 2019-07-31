/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.interfaces.ITileAspectResponder;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.BitSet;

public class TileActuatorMotor extends TileActuatorSecured implements ITileAspectResponder, IReceiverTile {

    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);
    private BitSet switchOnAspects = new BitSet(SignalAspect.VALUES.length);
    private boolean switchAspect;
    private boolean switchOnRedstone = true;

    public TileActuatorMotor() {
        doActionOnAspect(SignalAspect.RED, true);
    }

    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.MOTOR;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.SWITCH_MOTOR, player, world, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        boolean active = isSwitchAspect();
        if (switchAspect != active) {
            switchAspect = active;
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos pos) {
        super.onNeighborBlockChange(state, neighborBlock, pos);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power) {
            setPowered(power);
        }
    }

    @Override
    public boolean canConnectRedstone(@Nullable EnumFacing dir) {
        return true;
    }

    private boolean isSwitchAspect() {
        return doesActionOnAspect(receiver.getAspect());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setByteArray("PowerOnAspect", switchOnAspects.toByteArray());

        data.setBoolean("switchAspect", switchAspect);

        data.setBoolean("switchOnRedstone", switchOnRedstone);

        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("PowerOnAspect"))
            switchOnAspects = BitSet.valueOf(data.getByteArray("PowerOnAspect"));

        switchAspect = data.getBoolean("switchAspect");

        if (data.hasKey("switchOnRedstone"))
            switchOnRedstone = data.getBoolean("switchOnRedstone");

        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        receiver.writePacketData(data);

        writeGuiData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        receiver.readPacketData(data);

        readGuiData(data, null);

        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        super.writeGuiData(data);
        data.writeBitSet(switchOnAspects);
        data.writeBoolean(switchOnRedstone);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        switchOnAspects = data.readBitSet();
        switchOnRedstone = data.readBoolean();
    }

    @Override
    public boolean doesActionOnAspect(SignalAspect aspect) {
        return switchOnAspects.get(aspect.ordinal());
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        switchOnAspects.set(aspect.ordinal(), trigger);
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        return switchAspect || (shouldSwitchOnRedstone() && isPowered());
    }

    public boolean shouldSwitchOnRedstone() {
        return switchOnRedstone;
    }

    public void setSwitchOnRedstone(boolean switchOnRedstone) {
        this.switchOnRedstone = switchOnRedstone;
    }
}
