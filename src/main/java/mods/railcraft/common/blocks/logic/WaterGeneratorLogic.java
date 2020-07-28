/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WaterGeneratorLogic extends Logic {
    private static final int REFILL_INTERVAL = 20;
    private static final float REFILL_PENALTY_FROZEN = 0.5f;
    private static final float REFILL_BOOST_RAIN = 3.0f;
    public final GeneratorStatus status = new GeneratorStatus();

    public static class GeneratorStatus {
        public int canSeeSky;
        public double tempPenalty;
        public double humidityMultiplier = 1.0;
        public double precipitationMultiplier = 1.0;

        private boolean testSky(World world, BlockPos pos) {
            canSeeSky = world.canBlockSeeSky(pos) ? 1 : 0;
            if (canSeeSky <= 0) {
                humidityMultiplier = 1.0;
                precipitationMultiplier = 1.0;
            }
            return canSeeSky > 0;
        }

        private double calculatePrecipitation(World world, BlockPos pos) {
            if (world.canSnowAt(pos, false))
                precipitationMultiplier = REFILL_PENALTY_FROZEN;
            else if (world.isRainingAt(pos))
                precipitationMultiplier = REFILL_BOOST_RAIN;
            else
                precipitationMultiplier = 1.0;
            return precipitationMultiplier;
        }

        public double baseRate() {
            return RailcraftConfig.getBaseWaterGeneratorRate();
        }

        public void writeData(DataOutputStream data) throws IOException {
            data.writeInt(canSeeSky);
            data.writeDouble(tempPenalty);
            data.writeDouble(humidityMultiplier);
            data.writeDouble(precipitationMultiplier);
        }

        public void readData(DataInputStream data) throws IOException {
            canSeeSky = data.readInt();
            tempPenalty = data.readDouble();
            humidityMultiplier = data.readDouble();
            precipitationMultiplier = data.readDouble();
        }

    }

    public WaterGeneratorLogic(Adapter adapter) {
        super(adapter);
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        World world = theWorldAsserted();
        if (clock(REFILL_INTERVAL)) {

            BlockPos up = getPos().up();
            if (status.testSky(world, up)) {
                double rate = 0.0;
                rate += status.baseRate();
                Biome biome = world.getBiome(getPos());
                status.humidityMultiplier = biome.getRainfall();
                rate *= status.humidityMultiplier;
                rate *= status.calculatePrecipitation(world, up);
                double temp = biome.getTemperature(up);
                if (temp > 1.0) {
                    status.tempPenalty = temp - 1.0;
                    rate -= status.tempPenalty;
                }
                final int rateFinal = MathHelper.floor(rate);
                getLogic(IFluidHandler.class).ifPresent(tank -> {
                    if (rateFinal > 0)
                        tank.fill(Fluids.WATER.get(rateFinal), true);
                    else
                        tank.drain(Fluids.WATER.get(Math.abs(rateFinal)), true);
                });
            }
        }
    }
}
