package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;

import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SimpleSignalController;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.DataTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileBoxAnalogController extends TileBoxBase implements IControllerTile, IGuiReturnHandler {

    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private int strongestSignal;

    public EnumMap<SignalAspect, BitSet> aspects = new EnumMap<SignalAspect, BitSet>(SignalAspect.class);

    public TileBoxAnalogController() {
        for (SignalAspect aspect : SignalAspect.VALUES) {
            aspects.put(aspect, new BitSet());
        }
    }

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_ANALOG_CONTROLLER;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (player.isSneaking())
            return false;
        GuiHandler.openGui(EnumGui.BOX_ANALOG_CONTROLLER, player, worldObj, xCoord, yCoord, zCoord);
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
        int s = getPowerLevel();
        if (s != strongestSignal) {
            strongestSignal = s;
            sendUpdateToClient();
        }
    }

    private int getPowerLevel() {
        int p = 0, tmp;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (side == ForgeDirection.UP)
                continue;
            if (tileCache.getTileOnSide(side) instanceof TileBoxBase)
                continue;
            if ((tmp = PowerPlugin.getBlockPowerLevel(worldObj, xCoord, yCoord, zCoord, side)) > p)
                p = tmp;
            if ((tmp = PowerPlugin.getBlockPowerLevel(worldObj, xCoord, yCoord - 1, zCoord, side)) > p)
                p = tmp;
        }
        return p;
    }

    private SignalAspect determineAspect() {
        SignalAspect aspect = SignalAspect.OFF;
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            SignalAspect current = entry.getKey();
            if (entry.getValue().get(strongestSignal))
                aspect = (aspect == SignalAspect.OFF) ? current : SignalAspect.mostRestrictive(aspect, current);
        }
        return aspect;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("strongestSignal", strongestSignal);

        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            String n = entry.getKey().name();
            byte[] bytes = new byte[2];
            DataTools.bitSet2ByteArray(entry.getValue(), bytes);
            data.setByteArray("aspect_" + n, bytes);
        }

        controller.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        strongestSignal = data.getInteger("strongestSignal");

        try {
            for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
                String n = entry.getKey().name();
                byte[] bytes = data.getByteArray("aspect_" + n);
                DataTools.byteArray2BitSet(entry.getValue(), bytes);
            }
        } catch (Exception ex) {
        }

        controller.readFromNBT(data);

        // Legacy Support Code - remove in the future
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            String n = entry.getKey().toString();
            boolean on = data.getBoolean("mode" + n);
            if (on) {
                int low = data.getInteger("low" + n);
                int high = data.getInteger("high" + n);
                entry.getValue().set(low, high);
            }
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        writeGuiData(data);

        controller.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        readGuiData(data, null);

        controller.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            byte[] bytes = new byte[2];
            DataTools.bitSet2ByteArray(entry.getValue(), bytes);
            data.write(bytes);
        }
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            byte[] bytes = new byte[2];
            data.read(bytes);
            DataTools.byteArray2BitSet(entry.getValue(), bytes);
        }
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return false;
    }

    @Override
    public SignalAspect getBoxSignalAspect(ForgeDirection side) {
        return controller.getAspect();
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }

}
