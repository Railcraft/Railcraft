/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleAutomation;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EquipmentVariant implements IEnumMachine<EquipmentVariant> {

    ROLLING_MACHINE_MANUAL(ModuleFactory.class, "rolling_manual", TileRollingMachineManual.class),
    ROLLING_MACHINE_POWERED(ModuleFactory.class, "rolling_powered", TileRollingMachinePowered.class),
    FEED_STATION(ModuleAutomation.class, "feed_station", TileFeedStation.class),
    SMOKER(ModuleStructures.class, "smoker", TileSmoker.class),;

    private static final List<EquipmentVariant> creativeList = new ArrayList<EquipmentVariant>();
    public static final EquipmentVariant[] VALUES = values();

    static {
        creativeList.add(ROLLING_MACHINE_MANUAL);
        creativeList.add(ROLLING_MACHINE_POWERED);
        creativeList.add(FEED_STATION);
        creativeList.add(SMOKER);
    }

    private final Definition def;
    private ToolTip tip;

    EquipmentVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(module, tag, tile);
    }

    public static EquipmentVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EquipmentVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.equipment_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.EQUIPMENT;
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        if (tip != null)
            return tip;
        String tipTag = getLocalizationTag() + ".tips";
        if (LocalizationPlugin.hasTag(tipTag))
            tip = ToolTip.buildToolTip(tipTag);
        return tip;
    }
}
