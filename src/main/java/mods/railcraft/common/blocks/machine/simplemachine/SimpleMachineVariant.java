/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.simplemachine;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MachineProxy;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleAutomation;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum SimpleMachineVariant implements IEnumMachine<SimpleMachineVariant> {

    FEED_STATION(ModuleAutomation.class, "feed_station", TileFeedStation.class),
    ROLLING_MACHINE(ModuleFactory.class, "rolling_machine", TileRollingMachine.class);

    private static final List<SimpleMachineVariant> creativeList = new ArrayList<SimpleMachineVariant>();
    public static final SimpleMachineVariant[] VALUES = values();
    public static final MachineProxy<SimpleMachineVariant> PROXY = MachineProxy.create(VALUES, creativeList);

    static {
        creativeList.add(ROLLING_MACHINE);
        creativeList.add(FEED_STATION);
    }

    private final Definition def;
    private ToolTip tip;

    SimpleMachineVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(module, tag, tile);
    }

    public static SimpleMachineVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<SimpleMachineVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine_simple_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MACHINE_SIMPLE;
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
