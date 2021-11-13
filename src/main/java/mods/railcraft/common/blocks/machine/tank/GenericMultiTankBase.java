/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.tank;

import java.util.HashSet;
import java.util.Set;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.beta.MetalTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.Entity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GenericMultiTankBase extends MetalTank {

    private final Set<Integer> tankBlocks = new HashSet<Integer>();
    
    private final int tankWallMeta;
    
    public final String tankMaterial;
    
    private final int capacity;

    public final IEnumMachine TANK_WALL;
    public final IEnumMachine TANK_VALVE;
    public final IEnumMachine TANK_GAUGE;

    public GenericMultiTankBase(String tankMat, int tankCapacity, IEnumMachine tankWall, IEnumMachine tankValve, IEnumMachine tankGauge) {
    	tankMaterial = tankMat;
    	capacity = tankCapacity;
    	tankWallMeta = tankWall.ordinal();
        tankBlocks.add(tankWall.ordinal());
        tankBlocks.add(tankValve.ordinal());
        tankBlocks.add(tankGauge.ordinal());
        TANK_WALL = tankWall;
        TANK_VALVE = tankValve;
        TANK_GAUGE = tankGauge;
    }
    
    public int getCapacity() {
    	return capacity;
    }

    @Override
    public String getTitle() {
        return LocalizationPlugin.translate("railcraft.gui.tank."+tankMaterial);
    }

    @Override
    public boolean isTankBlock(int meta) {
        return tankBlocks.contains(meta);
    }

    @Override
    public boolean isWallBlock(int meta) {
        return meta == tankWallMeta;
    }

    @Override
    public float getResistance(Entity exploder) {
        return 25F;
    }
}
