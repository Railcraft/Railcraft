/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import java.util.ArrayList;
import java.util.List;
import mods.railcraft.client.render.IIconProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import net.minecraft.block.Block;

public enum EnumSignal implements IIconProvider, ISignalTileDefinition {

    // Name (module, hardness, needsSupport, tag, tile)
    BOX_INTERLOCK(Module.SIGNALS, 3, true, "box.interlock", TileBoxInterlock.class),
    DUAL_HEAD_BLOCK_SIGNAL(Module.SIGNALS, 8, false, "block.signal.dual", TileSignalDualHeadBlockSignal.class),
    SWITCH_MOTOR(Module.SIGNALS, 8, true, "switch.motor", TileSwitchMotor.class),
    BLOCK_SIGNAL(Module.SIGNALS, 8, false, "block.signal", TileSignalBlockSignal.class),
    SWITCH_LEVER(Module.SIGNALS, 8, true, "switch.lever", TileSwitchLever.class),
    SWITCH_ROUTING(Module.ROUTING, 8, true, "switch.routing", TileSwitchRouting.class),
    BOX_SEQUENCER(Module.SIGNALS, 3, true, "box.sequencer", TileBoxSequencer.class),
    BOX_CAPACITOR(Module.SIGNALS, 3, true, "box.capacitor", TileBoxCapacitor.class),
    BOX_RECEIVER(Module.SIGNALS, 3, true, "box.receiver", TileBoxReceiver.class),
    BOX_CONTROLLER(Module.SIGNALS, 3, true, "box.controller", TileBoxController.class),
    BOX_ANALOG_CONTROLLER(Module.SIGNALS, 3, true, "box.analog", TileBoxAnalogController.class),
    DISTANT_SIGNAL(Module.SIGNALS, 8, false, "distant", TileSignalDistantSignal.class),
    DUAL_HEAD_DISTANT_SIGNAL(Module.SIGNALS, 8, false, "distant.dual", TileSignalDualHeadDistantSignal.class),
    BOX_BLOCK_RELAY(Module.SIGNALS, 3, true, "box.block.relay", TileBoxBlockRelay.class),;
    private final Module module;
    private final float hardness;
    private final boolean needsSupport;
    private final String tag;
    private final Class<? extends TileSignalFoundation> tile;
    private IIcon icon;
    private static final List<EnumSignal> creativeList = new ArrayList<EnumSignal>();
    public static final EnumSignal[] VALUES = values();

    static {
        creativeList.add(SWITCH_LEVER);
        creativeList.add(SWITCH_MOTOR);
        creativeList.add(SWITCH_ROUTING);
        creativeList.add(BLOCK_SIGNAL);
        creativeList.add(DISTANT_SIGNAL);
        creativeList.add(DUAL_HEAD_BLOCK_SIGNAL);
        creativeList.add(DUAL_HEAD_DISTANT_SIGNAL);
        creativeList.add(BOX_BLOCK_RELAY);
        creativeList.add(BOX_RECEIVER);
        creativeList.add(BOX_CONTROLLER);
        creativeList.add(BOX_ANALOG_CONTROLLER);
        creativeList.add(BOX_CAPACITOR);
        creativeList.add(BOX_SEQUENCER);
        creativeList.add(BOX_INTERLOCK);
    }

    private EnumSignal(Module module, float hardness, boolean needsSupport, String tag, Class<? extends TileSignalFoundation> tile) {
        this.module = module;
        this.hardness = hardness;
        this.needsSupport = needsSupport;
        this.tile = tile;
        this.tag = tag;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        return new ItemStack(RailcraftBlocks.getBlockSignal(), qty, ordinal());
    }

    @Override
    public String getTag() {
        return "tile.railcraft.signal." + tag;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public Class<? extends TileSignalFoundation> getTileClass() {
        return tile;
    }

    public TileSignalFoundation getBlockEntity() {
        if (tile == null)
            return null;
        try {
            return tile.newInstance();
        } catch (Exception ex) {
        }
        return null;
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    public void setIcon(IIcon icon) {
        this.icon = icon;
    }

    @Override
    public IIcon getIcon() {
        return icon;
    }

    public static EnumSignal fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            return SWITCH_LEVER;
        return VALUES[id];
    }

    public static List<EnumSignal> getCreativeList() {
        return creativeList;
    }

    @Override
    public boolean needsSupport() {
        return needsSupport;
    }

    @Override
    public boolean isEnabled() {
        if (module == null) return false;
        return ModuleManager.isModuleLoaded(getModule()) && getBlock() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Override
    public Block getBlock() {
        return RailcraftBlocks.getBlockSignal();
    }

    @Override
    public int getMeta() {
        return ordinal();
    }

}
