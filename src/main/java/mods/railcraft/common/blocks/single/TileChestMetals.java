/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.logic.AbstractLogic;
import mods.railcraft.common.util.logic.MetalsChestLogic;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestMetals extends TileRailcraftChest<MetalsChestLogic> {

    private final MetalsChestLogic logic = new MetalsChestLogic(AbstractLogic.LogicAdapter.of(this), this);

    @Override
    public void update() {
        super.update();
        logic.update();
    }

    @Override
    public MetalsChestLogic getLogic() {
        return logic;
    }

    @Override
    public @Nullable EnumGui getGui() {
        return null;
    }

}
