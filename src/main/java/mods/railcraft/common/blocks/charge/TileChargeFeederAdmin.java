/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.charge;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeFeederAdmin extends TileChargeFeeder {
    public final InfiniteBattery chargeBattery = new InfiniteBattery();

    private static class InfiniteBattery extends IChargeBlock.ChargeBattery {
        private boolean enabled;

        @Override
        public boolean isInfinite() {
            return enabled;
        }

        @Override
        public double getCharge() {
            return enabled ? getCapacity() : 0.0;
        }

        @Override
        public double removeCharge(double request) {
            if (enabled)
                return request;
            return 0.0;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            nbt.setBoolean("enabled", enabled);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            enabled = nbt.getBoolean("enabled");
        }
    }

    @Override
    public IChargeBlock.ChargeBattery getChargeBattery() {
        return chargeBattery;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        chargeBattery.enabled = state.getValue(BlockChargeFeeder.REDSTONE);
    }
}
