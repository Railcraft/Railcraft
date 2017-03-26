/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleSignals;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum SignalBoxVariant implements IEnumMachine<SignalBoxVariant> {
    ANALOG(ModuleSignals.class, "analog", TileBoxAnalog.class),
    CAPACITOR(ModuleSignals.class, "capacitor", TileBoxCapacitor.class),
    CONTROLLER(ModuleSignals.class, "controller", TileBoxController.class),
    INTERLOCK(ModuleSignals.class, "interlock", TileBoxInterlock.class),
    RECEIVER(ModuleSignals.class, "receiver", TileBoxReceiver.class),
    RELAY(ModuleSignals.class, "relay", TileBoxRelay.class),
    SEQUENCER(ModuleSignals.class, "sequencer", TileBoxSequencer.class),;

    private static final List<SignalBoxVariant> creativeList = new ArrayList<SignalBoxVariant>();
    public static final SignalBoxVariant[] VALUES = values();

    static {
        for (SignalBoxVariant variant : VALUES) {
            variant.def.passesLight = true;
        }

        creativeList.add(CONTROLLER);
        creativeList.add(RECEIVER);
        creativeList.add(ANALOG);
        creativeList.add(CAPACITOR);
        creativeList.add(INTERLOCK);
        creativeList.add(SEQUENCER);
        creativeList.add(RELAY);

        BlockMachineSignalBox.connectionsSenders.add(RECEIVER);
        BlockMachineSignalBox.connectionsSenders.add(RELAY);
        BlockMachineSignalBox.connectionsSenders.add(SEQUENCER);
        BlockMachineSignalBox.connectionsSenders.add(CAPACITOR);

        BlockMachineSignalBox.connectionsSelf.add(INTERLOCK);
        BlockMachineSignalBox.connectionsSelf.add(SEQUENCER);
    }

    private final Definition def;
    private ToolTip tip;

    SignalBoxVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(module, tag, tile);
    }

    public static SignalBoxVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<SignalBoxVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.signal_box_" + getBaseTag();
    }

    @Override
    public String getName() {
        return getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.SIGNAL_BOX;
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
