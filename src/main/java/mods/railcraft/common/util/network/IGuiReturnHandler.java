/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IGuiReturnHandler {
    @Nullable
    World theWorld();

    void writeGuiData(RailcraftOutputStream data) throws IOException;

    void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException;
}
