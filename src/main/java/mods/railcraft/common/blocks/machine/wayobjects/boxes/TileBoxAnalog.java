/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SimpleSignalController;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;

public class TileBoxAnalog extends TileBoxBase implements IControllerTile, IGuiReturnHandler {

    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private int strongestSignal;

    public EnumMap<SignalAspect, BitSet> aspects = new EnumMap<>(SignalAspect.class);

    public TileBoxAnalog() {
        for (SignalAspect aspect : SignalAspect.VALUES) {
            aspects.put(aspect, new BitSet());
        }
    }


    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalBoxVariant.ANALOG;
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.BOX_ANALOG_CONTROLLER;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world)) {
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
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos pos) {
        super.onNeighborBlockChange(state, neighborBlock, pos);
        if (Game.isClient(getWorld()))
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
            if ((tmp = PowerPlugin.getBlockPowerLevel(world, getPos(), side)) > p)
                p = tmp;
            if ((tmp = PowerPlugin.getBlockPowerLevel(world, getPos().down(), side)) > p)
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("strongestSignal", strongestSignal);

        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            String n = entry.getKey().name();
            byte[] bytes = entry.getValue().toByteArray();
            data.setByteArray("aspect_" + n, bytes);
        }

        controller.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        strongestSignal = data.getInteger("strongestSignal");

        try {
            for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
                String n = entry.getKey().name();
                byte[] bytes = data.getByteArray("aspect_" + n);
                BitSet bitSet = entry.getValue();
                bitSet.clear();
                bitSet.or(BitSet.valueOf(bytes));
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
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        writeGuiData(data);

        controller.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        readGuiData(data, null);

        controller.readPacketData(data);
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            data.writeBitSet(entry.getValue());
        }
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            BitSet bitSet = entry.getValue();
            bitSet.clear();
            bitSet.or(data.readBitSet());
        }
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        return false;
    }

    @Override
    public SignalAspect getBoxSignalAspect(@Nullable EnumFacing side) {
        return controller.getAspect();
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }

}
