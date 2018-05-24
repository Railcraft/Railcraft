/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IronTank implements MetalTank {

    private final Set<IBlockState> tankBlocks = new HashSet<>();

    public IronTank() {
        tankBlocks.add(RailcraftBlocks.TANK_IRON_GAUGE.getDefaultState());
        tankBlocks.add(RailcraftBlocks.TANK_IRON_WALL.getDefaultState());
        tankBlocks.add(RailcraftBlocks.TANK_IRON_VALVE.getDefaultState());
    }

    @Override
    public String getTitle() {
        return LocalizationPlugin.translate("gui.railcraft.tank.iron");
    }

    @Override
    public boolean isTankBlock(IBlockState meta) {
        return tankBlocks.contains(meta);
    }

    @Override
    public boolean isWallBlock(IBlockState meta) {
        return RailcraftBlocks.TANK_IRON_WALL.isEqual(meta.getBlock());
    }

    @Override
    public float getResistance(@Nullable Entity exploder) {
        return 20F;
    }
}
