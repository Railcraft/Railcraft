/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SteelTank implements MetalTank {

    private final Set<IBlockState> tankBlocks = new HashSet<>();

    public SteelTank() {
//        tankBlocks.add(EnumMachineBeta.TANK_STEEL_WALL.ordinal());
//        tankBlocks.add(EnumMachineBeta.TANK_STEEL_VALVE.ordinal());
//        tankBlocks.add(EnumMachineBeta.TANK_STEEL_GAUGE.ordinal());
    }

    @Override
    public String getTitle() {
        return LocalizationPlugin.translate("gui.railcraft.tank.steel");
    }

    @Override
    public boolean isTankBlock(IBlockState meta) {
        return tankBlocks.contains(meta);
    }

    @Override
    public boolean isWallBlock(IBlockState meta) {
//        return meta == EnumMachineBeta.TANK_STEEL_WALL.ordinal(); TODO
        return false;
    }

    @Override
    public float getResistance(Entity exploder) {
        return 25F;
    }
}
