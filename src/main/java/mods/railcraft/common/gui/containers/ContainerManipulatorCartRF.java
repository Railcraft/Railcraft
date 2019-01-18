/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileRFManipulator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;

public class ContainerManipulatorCartRF extends ContainerManipulatorCart<TileRFManipulator> {

    public ContainerManipulatorCartRF(TileRFManipulator device) {
        super(null, device, false);
        addWidget(new IndicatorWidget(device.rfIndicator, 57, 38, 176, 0, 62, 8, false));
    }
}
