package mods.railcraft.common.blocks.signals;

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;

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
    public boolean blockActivated(EnumFacing side, EntityPlayer player) {
        if (player.isSneaking())
            return false;
        GuiHandler.openGui(EnumGui.BOX_ANALOG_CONTROLLER, player, worldObj, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();

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
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
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
        for (EnumFacing side : EnumFacing.VALUES) {
            if (side == EnumFacing.UP)
                continue;
            if (tileCache.getTileOnSide(side) instanceof TileBoxBase)
                continue;
            if ((tmp = PowerPlugin.getBlockPowerLevel(worldObj, getPos(), side)) > p)
                p = tmp;
            if ((tmp = PowerPlugin.getBlockPowerLevel(worldObj, getPos().down(), side)) > p)
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

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
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
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        strongestSignal = data.getInteger("strongestSignal");

        try {
            for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
                String n = entry.getKey().name();
                byte[] bytes = data.getByteArray("aspect_" + n);
                DataTools.byteArray2BitSet(entry.getValue(), bytes);
            }
        } catch (Exception ignored) {
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
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);

        writeGuiData(data);

        controller.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
        super.readPacketData(data);

        readGuiData(data, null);

        controller.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(@Nonnull DataOutputStream data) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            byte[] bytes = new byte[2];
            DataTools.bitSet2ByteArray(entry.getValue(), bytes);
            data.write(bytes);
        }
    }

    @Override
    public void readGuiData(@Nonnull DataInputStream data, EntityPlayer sender) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            byte[] bytes = new byte[2];
            //noinspection ResultOfMethodCallIgnored
            data.read(bytes);
            DataTools.byteArray2BitSet(entry.getValue(), bytes);
        }
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        return false;
    }

    @Override
    public SignalAspect getBoxSignalAspect(EnumFacing side) {
        return controller.getAspect();
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }

}
