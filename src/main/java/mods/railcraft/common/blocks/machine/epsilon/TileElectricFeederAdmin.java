/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.io.IOException;

//TODO: migrate to new charge API
public class TileElectricFeederAdmin extends TileMachineBase {

    //    private final ChargeHandler chargeHandler = new ChargeHandler(this, IChargeBlock.ConnectType.BLOCK, 0.0);
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
        if (Game.isClient(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

//    @Override
//    public void update() {
//        super.update();
//
//        if (Game.isClient(getWorld()))
//            return;
//
//        if (powered) {
//            double capacity = chargeHandler.getCapacity();
//            try {
//                chargeHandler.setCharge(capacity);
//            } catch (Throwable err) {
//                chargeHandler.addCharge(capacity - chargeHandler.getCharge());
//                Game.logErrorAPI(Railcraft.NAME, err, IElectricGrid.class);
//            }
//        }
//        chargeHandler.tick();
//    }

//    @Override
//    public ChargeHandler getChargeHandler() {
//        return chargeHandler;
//    }

    //    @Override
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
//        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
//        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
        return data;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }
}
