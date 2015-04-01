/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSmoker extends TileMachineBase {
    private static final int SNOW_MELT_INTERVAL = 32;
    private static final Random rand = MiscTools.getRand();
    private boolean powered;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.SMOKER;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!powered) {
            if (Game.isHost(worldObj)) {
                if (clock % SNOW_MELT_INTERVAL == 0) {
                    Block blockAbove = WorldPlugin.getBlock(worldObj, xCoord, yCoord + 1, zCoord);
                    if (blockAbove == Blocks.snow_layer)
                        WorldPlugin.setBlockToAir(worldObj, xCoord, yCoord + 1, zCoord);
                }
            } else {
                if (!WorldPlugin.blockIsAir(worldObj, xCoord, yCoord + 1, zCoord)) return;
                double px = xCoord + rand.nextFloat();
                double py = yCoord + rand.nextFloat() * 0.5F + 1;
                double pz = zCoord + rand.nextFloat();
                EffectManager.instance.chimneyEffect(worldObj, px, py, pz);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        sendUpdateToClient();
    }

    @Override
    public boolean isSideSolid(ForgeDirection side) {
        return side != ForgeDirection.UP;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

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
        powered = data.readBoolean();
    }

//    @Override
//    public int getLightValue() {
//        return 12;
//    }
}
