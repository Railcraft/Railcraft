/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.charge.ChargeNetwork;
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
        getToolTip().set(newTip);
    }

    @Override
    public double getServerValue() {
        return ((ChargeNetwork) Charge.distribution.network(world)).grid(pos).getUtilization();
    }

    @Override
    public void setClientValue(double value) {
        chargePercent = value;
    }

    @Override
    public double getClientValue() {
        return chargePercent;
    }
}
