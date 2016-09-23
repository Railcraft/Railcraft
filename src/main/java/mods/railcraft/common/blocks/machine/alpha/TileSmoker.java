/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.IOException;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSmoker extends TileMachineBase {

    private static final int SNOW_MELT_INTERVAL = 32;
    private static final Random rand = MiscTools.RANDOM;
    private boolean powered;

    @Override
    public EnumMachineAlpha getMachineType() {
        return EnumMachineAlpha.SMOKER;
    }

    @Override
    public void update() {
        super.update();
        if (!powered) {
            if (Game.isHost(worldObj)) {
                if (clock % SNOW_MELT_INTERVAL == 0) {
                    Block blockAbove = WorldPlugin.getBlock(worldObj, getPos().up());
                    if (blockAbove == Blocks.SNOW_LAYER)
                        WorldPlugin.setBlockToAir(worldObj, getPos().up());
                }
            } else {
                if (!WorldPlugin.isBlockAir(worldObj, getPos().up())) return;
                double px = getX() + rand.nextFloat();
                double py = getY() + rand.nextFloat() * 0.5F + 1;
                double pz = getZ() + rand.nextFloat();
                EffectManager.instance.chimneyEffect(worldObj, px, py, pz);
            }
        }
    }

    @Override
    public void onNeighborBlockChange( IBlockState state,  Block block) {
        super.onNeighborBlockChange(state, block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        sendUpdateToClient();
    }

    @Override
    public boolean isSideSolid(EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    public void readFromNBT( NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }


    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("powered", powered);
        return data;
    }

    @Override
    public void writePacketData( RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData( RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        powered = data.readBoolean();
    }

//    @Override
//    public int getLightValue() {
//        return 12;
//    }
}
