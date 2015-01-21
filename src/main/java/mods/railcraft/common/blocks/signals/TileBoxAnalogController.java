package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import net.minecraftforge.common.util.ForgeDirection;

public class TileBoxAnalogController extends TileBoxBase implements IControllerTile, IGuiReturnHandler {

    private final SimpleSignalController controller = new SimpleSignalController(getName(), this);
    public static final int N_OF_ASPECTS = SignalAspect.values().length - 1;
    private boolean prevBlinkState;
    private int strongestSignal;

    public boolean enableAspect[][] = new boolean[N_OF_ASPECTS][16];

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
            if (controller.getAspect().isBlinkAspect() && prevBlinkState != SignalAspect.isBlinkOn()) {
                prevBlinkState = SignalAspect.isBlinkOn();
                markBlockForUpdate();
            }
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
        for (int i = 0; i < N_OF_ASPECTS; i++) {
            SignalAspect current = SignalAspect.values()[i];
            if(enableAspect[i][strongestSignal])
                aspect = (aspect == SignalAspect.OFF) ? current : SignalAspect.mostRestrictive(aspect, current);
        }
        return aspect;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("strongestSignal", strongestSignal);

        for (int i = 0; i < N_OF_ASPECTS; i++) {
            String n = SignalAspect.values()[i].toString();
            int s = 0;
            for(boolean b : enableAspect[i])
            	s = (s << 1) | (b ? 1 : 0);
            data.setShort("aspect_" + n, (short) s);
        }

        controller.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        strongestSignal = data.getInteger("strongestSignal");

        for (int i = 0; i < N_OF_ASPECTS; i++) {
        	String n = SignalAspect.values()[i].toString();
            short s = data.getShort("aspect_" + n);
            for(int j = 0; j < 16; j++) {
            	enableAspect[i][j] = (s & 0x8000) == 0x8000;
            	s <<= 1;
            }
        }

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
        for(boolean c[] : enableAspect)
        	for(boolean b : c)
        		data.writeBoolean(b);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        for(int i = 0; i < N_OF_ASPECTS; i++)
    		for(int j = 0; j < 16; j++)
    			enableAspect[i][j] = data.readBoolean();
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
