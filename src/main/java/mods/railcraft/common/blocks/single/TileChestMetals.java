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
import mods.railcraft.common.util.chest.InventoryLogic;
import mods.railcraft.common.util.chest.MetalsChestLogic;
import mods.railcraft.common.util.misc.Game;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestMetals extends TileRailcraftChest<MetalsChestLogic> {

    private static final int TICK_PER_CONDENSE = 16;
    private MetalsChestLogic logic = new MetalsChestLogic(getWorld(), this);

    @Override
    public void update() {
        super.update();

        if (clock % TICK_PER_CONDENSE == 0 && Game.isHost(world))
            logic.update();
    }

    @Override
    protected InventoryLogic createLogic() {
        return new MetalsChestLogic(getWorld(), this);
    }


    @Nullable
    @Override
    public EnumGui getGui() {
        return null;
    }

}
