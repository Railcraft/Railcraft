/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.common.blocks.charge.Charge;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChargeNetworkIndicator extends IndicatorController {

    private double chargePercent;
    private final World world;
    private final BlockPos pos;

    public ChargeNetworkIndicator(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void refreshToolTip() {
        ToolTip newTip = ToolTip.buildToolTip("gui.railcraft.charge.network.usage", "{percent}=" + String.format("%.0f%%", chargePercent * 100.0));
        if (newTip != null)
            getToolTip().set(newTip);
    }

    @Override
    public double getMeasurement() {
        return chargePercent;
    }

    @Override
    public double getServerValue() {
        return Charge.util.getNetwork(world).getGraph(pos).getUsageRatio();
    }

    @Override
    public void setClientValue(double value) {
        chargePercent = value;
    }

}
