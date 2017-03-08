/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleRouting;
import mods.railcraft.common.modules.ModuleTracks;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum ActuatorVariant implements IEnumMachine<ActuatorVariant> {
    LEVER(ModuleTracks.class, "lever", TileActuatorLever.class),
    MOTOR(ModuleTracks.class, "motor", TileActuatorMotor.class),
    ROUTING(ModuleRouting.class, "routing", TileActuatorRouting.class);

    private static final List<ActuatorVariant> creativeList = new ArrayList<ActuatorVariant>();
    public static final ActuatorVariant[] VALUES = values();

    static {
        for (ActuatorVariant variant : VALUES) {
            variant.def.passesLight = true;
        }

        creativeList.add(LEVER);
        creativeList.add(MOTOR);
        creativeList.add(ROUTING);
    }

    private final Definition def;
    private ToolTip tip;

    ActuatorVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(module, tag, tile);
    }

    public static ActuatorVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<ActuatorVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.actuator_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MACHINE_ACTUATOR;
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
