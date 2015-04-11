/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

public class TileBoxCapacitor extends TileBoxBase implements IGuiReturnHandler {

    private short ticksPowered;
    public short ticksToPower = 200;
    private SignalAspect aspect = SignalAspect.OFF;
    private final MultiButtonController<EnumStateMode> stateModeController = new MultiButtonController(EnumStateMode.IMMEDIATE.ordinal(), EnumStateMode.values());

    public TileBoxCapacitor() {
    }
    
    public enum EnumStateMode implements IMultiButtonState {
    	IMMEDIATE("railcraft.gui.box.capacitor.immediate"),
    	DELAYED("railcraft.gui.box.capacitor.delayed");
    	private final String label;
        private final ToolTip tip;
        
        private EnumStateMode(String label){
        	this.label = label;
            this.tip = ToolTip.buildToolTip(label + ".tip");
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
    
    public MultiButtonController<EnumStateMode> getStateModeController(){
    	return stateModeController;
    }

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_CAPACITOR;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (player.isSneaking())
            return false;
        GuiHandler.openGui(EnumGui.BOX_CAPACITOR, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj))
            return;
        
        if (ticksPowered > 0) {
            ticksPowered--;
            if (stateModeController.getButtonState().equals(EnumStateMode.DELAYED)){ //new behavior
                SignalAspect tmpaspect = SignalAspect.GREEN;
                Boolean hasInput = false;
                if (PowerPlugin.isBlockBeingPoweredByRepeater(worldObj, xCoord, yCoord, zCoord))
                    hasInput = true;
                for (int side = 2; side < 6; side++) { //get most restrictive aspect from adjacent (active) boxes
                    ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
                    TileEntity tile = tileCache.getTileOnSide(forgeSide);
                    if (tile instanceof TileBoxBase) {
                        TileBoxBase box = (TileBoxBase) tile;
                        if (box.isEmittingRedstone(forgeSide.getOpposite())) {
                            hasInput = true;
                            tmpaspect = SignalAspect.mostRestrictive(tmpaspect, box.getBoxSignalAspect(forgeSide.getOpposite()));
                        }
                    }
                }
                if (hasInput){
            	    ticksPowered = ticksToPower; //undo any previous decrements
                    if (!aspect.equals(tmpaspect) ) {
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
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (worldObj.isRemote)
            return;
        boolean p = PowerPlugin.isBlockBeingPoweredByRepeater(worldObj, xCoord, yCoord, zCoord);
        if (ticksPowered <= 0 && p) {
            ticksPowered = ticksToPower;
            if (stateModeController.getButtonState().equals(EnumStateMode.IMMEDIATE))
                aspect = SignalAspect.GREEN;
            updateNeighbors();
        }
    }

    @Override
    public void onNeighborStateChange(TileBoxBase neighbor, ForgeDirection side) {
        if (neighbor.isEmittingRedstone(side)) {
            ticksPowered = ticksToPower;
            if (stateModeController.getButtonState().equals(EnumStateMode.IMMEDIATE))
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
    public int getPowerOutput(int side) {
        TileEntity tile = tileCache.getTileOnSide(MiscTools.getOppositeSide(side));
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return ticksPowered > 0 ? FULL_POWER : NO_POWER;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setShort("ticksPowered", ticksPowered);
        data.setShort("ticksToPower", ticksToPower);
        data.setByte("aspect", (byte) aspect.ordinal());
        stateModeController.writeToNBT(data, "mode");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        ticksPowered = data.getShort("ticksPowered");
        ticksToPower = data.getShort("ticksToPower");
        aspect = SignalAspect.values()[data.getByte("aspect")];
        if (data.hasKey("mode"))
            stateModeController.readFromNBT(data, "mode");
        else //set old boxes to immediate mode to retain old behavior
            stateModeController.setCurrentState(EnumStateMode.IMMEDIATE.ordinal());
            	
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(ticksPowered > 0);
        data.writeShort(ticksToPower);
        data.writeByte(aspect.ordinal());
        data.writeByte(stateModeController.getCurrentState());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        ticksPowered = (short) (data.readBoolean() ? 1 : 0);
        ticksToPower = data.readShort();
        aspect = SignalAspect.values()[data.readByte()];
        stateModeController.setCurrentState(data.readByte());

        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeShort(ticksToPower);
        data.writeByte(stateModeController.getCurrentState());
        
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        ticksToPower = data.readShort();
        stateModeController.setCurrentState(data.readByte());
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canTransferAspect() || ((TileBoxBase) tile).canReceiveAspect();
        return false;
    }

    @Override
    public SignalAspect getBoxSignalAspect(ForgeDirection side) {
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
