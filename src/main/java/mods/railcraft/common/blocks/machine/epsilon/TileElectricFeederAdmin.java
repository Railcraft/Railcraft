package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileElectricFeederAdmin extends TileMachineBase implements IElectricGrid {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.0);
    private boolean powered;

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityliving, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(getWorld()))
            return;

        if (powered) {
            double capacity = chargeHandler.getCapacity();
            try {
                chargeHandler.setCharge(capacity);
            } catch (Throwable err) {
                chargeHandler.addCharge(capacity - chargeHandler.getCharge());
                Game.logErrorAPI("Railcraft", err, IElectricGrid.class);
            }
        }
        chargeHandler.tick();
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.ELECTRIC_FEEDER_ADMIN;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Nonnull
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }
}
