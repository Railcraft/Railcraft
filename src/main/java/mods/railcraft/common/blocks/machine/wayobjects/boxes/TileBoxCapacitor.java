/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.interfaces.ITileRedstoneEmitter;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class TileBoxCapacitor extends TileBoxBase implements IGuiReturnHandler, ITileRedstoneEmitter {

    private short ticksPowered;
    public short ticksToPower = 200;
    private SignalAspect aspect = SignalAspect.OFF;
    private final MultiButtonController<EnumStateMode> stateModeController = MultiButtonController.create(EnumStateMode.RISING_EDGE.ordinal(), EnumStateMode.values());

    public TileBoxCapacitor() {
    }

    public enum EnumStateMode implements IMultiButtonState {
        RISING_EDGE("rising"),
        FALLING_EDGE("falling");
        private final String label;
        private final ToolTip tip;

        EnumStateMode(String label) {
            this.label = "gui.railcraft.box.capacitor." + label + ".name";
            this.tip = ToolTip.buildToolTip(label + ".tips");
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate(label);
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tip;
        }

    }

    public MultiButtonController<EnumStateMode> getStateModeController() {
        return stateModeController;
    }

    @Nonnull
    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalBoxVariant.CAPACITOR;
    }

    @Nullable
    @Override
    public EnumGui getGui() {
        return EnumGui.BOX_CAPACITOR;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(worldObj))
            return;

        if (ticksPowered > 0) {
            ticksPowered--;
            if (Objects.equals(stateModeController.getButtonState(), EnumStateMode.FALLING_EDGE)) { //new behavior
                SignalAspect tmpaspect = SignalAspect.GREEN;
                Boolean hasInput = false;
                if (PowerPlugin.isBlockBeingPoweredByRepeater(worldObj, getPos()))
                    hasInput = true;
                for (int side = 2; side < 6; side++) { //get most restrictive aspect from adjacent (active) boxes
                    EnumFacing forgeSide = EnumFacing.VALUES[side];
                    TileEntity tile = tileCache.getTileOnSide(forgeSide);
                    if (tile instanceof TileBoxBase) {
                        TileBoxBase box = (TileBoxBase) tile;
                        if (box.isEmittingRedstone(forgeSide.getOpposite())) {
                            hasInput = true;
                            tmpaspect = SignalAspect.mostRestrictive(tmpaspect, box.getBoxSignalAspect(forgeSide.getOpposite()));
                        }
                    }
                }
                if (hasInput) {
                    ticksPowered = ticksToPower; //undo any previous decrements
                    if (!Objects.equals(aspect, tmpaspect)) {
                        aspect = tmpaspect; //change to the most restrictive aspect found above.
                        updateNeighbors();
                    }
                }
            }
            //in all cases:
            if (ticksPowered <= 0)
                updateNeighbors();
        }
    }

    @Override
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        if (worldObj.isRemote)
            return;
        boolean p = PowerPlugin.isBlockBeingPoweredByRepeater(worldObj, getPos());
        if (ticksPowered <= 0 && p) {
            ticksPowered = ticksToPower;
            if (Objects.equals(stateModeController.getButtonState(), EnumStateMode.RISING_EDGE))
                aspect = SignalAspect.GREEN;
            updateNeighbors();
        }
    }

    @Override
    public void onNeighborStateChange(TileBoxBase neighbor, EnumFacing side) {
        if (neighbor.isEmittingRedstone(side)) {
            ticksPowered = ticksToPower;
            if (Objects.equals(stateModeController.getButtonState(), EnumStateMode.RISING_EDGE))
                aspect = neighbor.getBoxSignalAspect(side);
            updateNeighbors();
        }
    }

    private void updateNeighbors() {
        sendUpdateToClient();
        notifyBlocksOfNeighborChange();
        updateNeighborBoxes();
    }

    @Override
    public int getPowerOutput(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side.getOpposite());
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return ticksPowered > 0 ? FULL_POWER : NO_POWER;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        data.setShort("ticksPowered", ticksPowered);
        data.setShort("ticksToPower", ticksToPower);
        data.setByte("aspect", (byte) aspect.ordinal());
        stateModeController.writeToNBT(data, "mode");
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        ticksPowered = data.getShort("ticksPowered");
        ticksToPower = data.getShort("ticksToPower");
        aspect = SignalAspect.values()[data.getByte("aspect")];
        if (data.hasKey("mode"))
            stateModeController.readFromNBT(data, "mode");
        else //set old boxes to immediate mode to retain old behavior
            stateModeController.setCurrentState(EnumStateMode.RISING_EDGE.ordinal());

    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(ticksPowered > 0);
        data.writeShort(ticksToPower);
        data.writeByte(aspect.ordinal());
        data.writeByte(stateModeController.getCurrentState());
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        ticksPowered = (short) (data.readBoolean() ? 1 : 0);
        ticksToPower = data.readShort();
        aspect = SignalAspect.values()[data.readByte()];
        stateModeController.setCurrentState(data.readByte());

        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(@Nonnull RailcraftOutputStream data) throws IOException {
        data.writeShort(ticksToPower);
        data.writeByte(stateModeController.getCurrentState());

    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        ticksToPower = data.readShort();
        stateModeController.setCurrentState(data.readByte());
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        return tile instanceof TileBoxBase && (((TileBoxBase) tile).canTransferAspect() || ((TileBoxBase) tile).canReceiveAspect());
    }

    @Override
    public SignalAspect getBoxSignalAspect(EnumFacing side) {
        return ticksPowered > 0 ? aspect : SignalAspect.RED;
    }

    @Override
    public boolean canReceiveAspect() {
        return true;
    }

    @Override
    public boolean canTransferAspect() {
        return true;
    }
}
