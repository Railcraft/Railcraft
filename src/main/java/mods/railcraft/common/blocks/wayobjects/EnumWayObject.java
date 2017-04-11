/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleSignals;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum EnumWayObject implements IWayObjectDefinition, IVariantEnum {

    // Name (module, hardness, needsSupport, tag, tile)
//    BOX_INTERLOCK(ModuleSignals.class, 3, true, "box.interlock", TileBoxInterlock.class),
    DUAL_HEAD_BLOCK_SIGNAL(ModuleSignals.class, 8, false, "block.signal.dual", TileSignalDualHeadBlockSignal.class),
    //    SWITCH_MOTOR(ModuleSignals.class, 8, true, "switch.motor", TileActuatorMotor.class),
    BLOCK_SIGNAL(ModuleSignals.class, 8, false, "block.signal", TileSignalBlockSignal.class),
    //    SWITCH_LEVER(ModuleSignals.class, 8, true, "switch.lever", TileActuatorLever.class),
//    SWITCH_ROUTING(ModuleRouting.class, 8, true, "switch.routing", TileActuatorRouting.class),
//    BOX_SEQUENCER(ModuleSignals.class, 3, true, "box.sequencer", TileBoxSequencer.class),
//    BOX_CAPACITOR(ModuleSignals.class, 3, true, "box.capacitor", TileBoxCapacitor.class),
//    BOX_RECEIVER(ModuleSignals.class, 3, true, "box.receiver", TileBoxReceiver.class),
//    BOX_CONTROLLER(ModuleSignals.class, 3, true, "box.controller", TileBoxController.class),
//    BOX_ANALOG_CONTROLLER(ModuleSignals.class, 3, true, "box.analog", TileBoxAnalogController.class),
    DISTANT_SIGNAL(ModuleSignals.class, 8, false, "distant", TileSignalDistantSignal.class),
    DUAL_HEAD_DISTANT_SIGNAL(ModuleSignals.class, 8, false, "distant.dual", TileSignalDualHeadDistantSignal.class),
//    BOX_BLOCK_RELAY(ModuleSignals.class, 3, true, "box.block.relay", TileBoxBlockRelay.class),
    ;
    private final Class<? extends IRailcraftModule> module;
    private final float hardness;
    private final boolean needsSupport;
    private final String tag;
    private final Class<? extends TileWayObject> tile;
    private static final List<EnumWayObject> creativeList = new ArrayList<EnumWayObject>();
    public static final EnumWayObject[] VALUES = values();

    static {
//        creativeList.add(SWITCH_LEVER);
//        creativeList.add(SWITCH_MOTOR);
//        creativeList.add(SWITCH_ROUTING);
        creativeList.add(BLOCK_SIGNAL);
        creativeList.add(DISTANT_SIGNAL);
        creativeList.add(DUAL_HEAD_BLOCK_SIGNAL);
        creativeList.add(DUAL_HEAD_DISTANT_SIGNAL);
//        creativeList.add(BOX_BLOCK_RELAY);
//        creativeList.add(BOX_RECEIVER);
//        creativeList.add(BOX_CONTROLLER);
//        creativeList.add(BOX_ANALOG_CONTROLLER);
//        creativeList.add(BOX_CAPACITOR);
//        creativeList.add(BOX_SEQUENCER);
//        creativeList.add(BOX_INTERLOCK);
    }

    EnumWayObject(Class<? extends IRailcraftModule> module, float hardness, boolean needsSupport, String tag, Class<? extends TileWayObject> tile) {
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
        return new ItemStack(getBlock(), qty, ordinal());
    }

    @Override
    public String getTag() {
        return "tile.railcraft.wayobject." + tag;
    }

    public Class<? extends IRailcraftModule> getModule() {
        return module;
    }

    @Override
    public Class<? extends TileWayObject> getTileClass() {
        return tile;
    }

    public TileWayObject getBlockEntity() {
        if (tile == null)
            return null;
        try {
            return tile.newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    public static EnumWayObject fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return BLOCK_SIGNAL;
        return VALUES[id];
    }

    public static List<EnumWayObject> getCreativeList() {
        return creativeList;
    }

    @Override
    public boolean needsSupport() {
        return needsSupport;
    }

    @Override
    public boolean isEnabled() {
        return module != null && RailcraftModuleManager.isModuleEnabled(getModule()) && getBlock() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Nullable
    @Override
    public Block getBlock() {
        return RailcraftBlocks.WAY_OBJECT.block();
    }

    @Override
    public int getMeta() {
        return ordinal();
    }

    @Override
    public String getName() {
        return tag.replace(".", "_");
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}
