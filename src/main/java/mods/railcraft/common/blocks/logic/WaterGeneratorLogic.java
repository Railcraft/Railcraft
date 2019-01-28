/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WaterGeneratorLogic extends Logic {
    private static final int REFILL_INTERVAL = 16;
    private static final float REFILL_PENALTY_FROZEN = 0.5f;
    private static final float REFILL_BOOST_RAIN = 3.0f;

    public WaterGeneratorLogic(Adapter adapter) {
        super(adapter);
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        World world = theWorldAsserted();
        if (world.provider.getDimension() != -1 && clock(REFILL_INTERVAL)) {

            BlockPos up = getPos().up();

            if (!world.canBlockSeeSky(up))
                return;

            double rate = RailcraftConfig.getBaseWaterGeneratorRate();
            if (rate <= 0.0)
                return;

            Biome biome = world.getBiome(getPos());
            rate *= biome.getRainfall();

            if (world.canSnowAt(up, false))
                rate *= REFILL_PENALTY_FROZEN;
            else if (world.isRainingAt(up))
                rate *= REFILL_BOOST_RAIN;
            else {
                double temp = biome.getTemperature(up);
                if (temp > 1.0)
                    rate -= temp - 1.0;
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
