/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SimpleSignalController;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;

public class TileBoxController extends TileBoxBase implements IControllerTile, IGuiReturnHandler {
    private static final EnumSet<ForgeDirection> powerSides = EnumSet.of(ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH);
    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    public SignalAspect defaultAspect = SignalAspect.GREEN;
    public SignalAspect poweredAspect = SignalAspect.RED;
    private boolean powered;

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_CONTROLLER;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (player.isSneaking())
            return false;
        GuiHandler.openGui(EnumGui.BOX_CONTROLLER, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj)) {
            controller.tickClient();
            return;
        }
        controller.tickServer();
        SignalAspect prevAspect = controller.getAspect();
        if (controller.isBeingPaired())
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        else if (controller.isPaired())
            controller.setAspect(determineAspect());
        else
            controller.setAspect(SignalAspect.BLINK_RED);
        if (prevAspect != controller.getAspect())
            sendUpdateToClient();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = isBeingPowered() || PowerPlugin.isRedstonePowered(worldObj, xCoord, yCoord, zCoord);
        if (p != powered) {
            powered = p;
            sendUpdateToClient();
        }
    }

    private boolean isBeingPowered() {
        for (ForgeDirection side : powerSides) {
            if (tileCache.getTileOnSide(side) instanceof TileBoxBase)
                continue;
            if (PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord, side))
                return true;
//            if (PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord - 1, zCoord, side))
//                return true;
        }
        return false;
    }

    private SignalAspect determineAspect() {
        SignalAspect newAspect = powered ? poweredAspect : defaultAspect;
        for (int side = 2; side < 6; side++) {
            ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
            TileEntity t = tileCache.getTileOnSide(forgeSide);
            if (t instanceof TileBoxBase) {
                TileBoxBase tile = (TileBoxBase) t;
                if (tile.canTransferAspect())
                    newAspect = SignalAspect.mostRestrictive(newAspect, tile.getBoxSignalAspect(forgeSide.getOpposite()));
            }
        }
        return newAspect;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Powered", powered);

        data.setInteger("defaultAspect", defaultAspect.ordinal());
        data.setInteger("PoweredAspect", poweredAspect.ordinal());

        controller.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("Powered");

        defaultAspect = SignalAspect.values()[data.getInteger("defaultAspect")];
        poweredAspect = SignalAspect.values()[data.getInteger("PoweredAspect")];

        controller.readFromNBT(data);

        if (data.hasKey("ReceiverX")) {
            int x = data.getInteger("ReceiverX");
            int y = data.getInteger("ReceiverY");
            int z = data.getInteger("ReceiverZ");
            controller.registerLegacyReceiver(x, y, z);
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(defaultAspect.ordinal());
        data.writeByte(poweredAspect.ordinal());

        controller.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        defaultAspect = SignalAspect.values()[data.readByte()];
        poweredAspect = SignalAspect.values()[data.readByte()];

        controller.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(defaultAspect.ordinal());
        data.writeByte(poweredAspect.ordinal());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        defaultAspect = SignalAspect.values()[data.readByte()];
        poweredAspect = SignalAspect.values()[data.readByte()];
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canTransferAspect();
        return false;
    }

    @Override
    public SignalAspect getBoxSignalAspect(ForgeDirection side) {
        return controller.getAspect();
    }

    @Override
    public boolean canReceiveAspect() {
        return true;
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }
}
