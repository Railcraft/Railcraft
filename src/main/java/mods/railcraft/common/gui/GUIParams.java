/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 6/23/2022 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GUIParams {
    private final EnumGui gui;
    private final InventoryPlayer inv;
    @Nullable
    private final Object obj;
    private final World world;
    private final BlockPos pos;

    public GUIParams(EnumGui gui, InventoryPlayer inv, @Nullable Object obj, World world, int x, int y, int z) {
        this.gui = gui;
        this.inv = inv;
        this.obj = obj;
        this.world = world;
        this.pos = new BlockPos(x, y, z);
    }

    public EnumGui getGui() {
        return gui;
    }

    public InventoryPlayer getInv() {
        return inv;
    }

    public Object getObj() {
        return obj;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }
}
