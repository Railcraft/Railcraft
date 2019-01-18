/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.types.*;
import mods.railcraft.common.modules.ModuleAutomation;
import mods.railcraft.common.modules.ModuleRouting;
import mods.railcraft.common.modules.ModuleTrain;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum EnumDetector implements IVariantEnumBlock<EnumDetector> {

    ITEM(ModuleAutomation.class, DetectorItem.class),
    ANY(ModuleAutomation.class, Detector.class),
    EMPTY(ModuleAutomation.class, DetectorEmpty.class),
    MOB(ModuleAutomation.class, DetectorEnemy.class),
    PLAYER(ModuleAutomation.class, DetectorPlayer.class),
    ANIMAL(ModuleAutomation.class, DetectorAnimal.class),
    TANK(ModuleAutomation.class, DetectorTank.class),
    ADVANCED(ModuleAutomation.class, DetectorAdvanced.class),
    AGE(ModuleAutomation.class, DetectorAge.class),
    TRAIN(ModuleTrain.class, DetectorTrain.class),
    SHEEP(ModuleAutomation.class, DetectorSheep.class),
    VILLAGER(ModuleAutomation.class, DetectorVillager.class),
    LOCOMOTIVE(ModuleAutomation.class, DetectorLocomotive.class),
    ROUTING(ModuleRouting.class, DetectorRouting.class);
    public static final EnumDetector[] VALUES = values();
    public static final Map<String, EnumDetector> nameMap = new HashMap<>();

    static {
        for (EnumDetector detector : VALUES) {
            nameMap.put(detector.getName(), detector);
        }
    }

    private final Class<? extends Detector> handler;
    private final Definition def;

    EnumDetector(Class<? extends IRailcraftModule> module, Class<? extends Detector> handler) {
        this.handler = handler;
        this.def = new Definition(name().toLowerCase(Locale.ENGLISH), module);
    }

    @Override
    public Definition getDef() {
        return def;
    }

    public static EnumDetector fromOrdinal(int meta) {
        if (meta >= EnumDetector.VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }

    public static EnumDetector fromName(String name) {
        EnumDetector detector = nameMap.get(name);
        if (detector == null)
            return ANY;
        return detector;
    }

    public Detector buildHandler() {
        try {
            return handler.newInstance();
        } catch (Exception ignored) {
        }
        throw new RuntimeException("Failed to create Detector!");
    }

    @Override
    public String getTag() {
        return "tile.railcraft.detector_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.DETECTOR;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }
}
