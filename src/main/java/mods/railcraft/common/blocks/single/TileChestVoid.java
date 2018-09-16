/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.entity.ChestLogic;
import mods.railcraft.common.util.entity.VoidChestLogic;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestVoid extends TileChestRailcraft {

    private static final int TICK_PER_VOID = 8;

    @Override
    protected ChestLogic createLogic() {
        return new VoidChestLogic(getWorld(), this);
    }

    @Nullable
    @Override
    public EnumGui getGui() {
        return null;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world) && clock % TICK_PER_VOID == 0) {
            logic.update();
        }
    }
}
