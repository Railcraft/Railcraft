/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import java.util.Locale;
import mods.railcraft.common.blocks.detector.types.*;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum EnumDetector {

    ITEM(Module.AUTOMATION, DetectorItem.class),
    ANY(Module.AUTOMATION, Detector.class),
    EMPTY(Module.AUTOMATION, DetectorEmpty.class),
    MOB(Module.AUTOMATION, DetectorMob.class),
    POWERED(Module.AUTOMATION, DetectorPowered.class),
    PLAYER(Module.AUTOMATION, DetectorPlayer.class),
    EXPLOSIVE(Module.AUTOMATION, DetectorExplosive.class),
    ANIMAL(Module.AUTOMATION, DetectorAnimal.class),
    TANK(Module.AUTOMATION, DetectorTank.class),
    ADVANCED(Module.AUTOMATION, DetectorAdvanced.class),
    ENERGY(Module.IC2, DetectorEnergy.class),
    AGE(Module.AUTOMATION, DetectorAge.class),
    TRAIN(Module.TRAIN, DetectorTrain.class),
    SHEEP(Module.AUTOMATION, DetectorSheep.class),
    VILLAGER(Module.AUTOMATION, DetectorVillager.class),
    LOCOMOTIVE(Module.AUTOMATION, DetectorLocomotive.class),
    ROUTING(Module.ROUTING, DetectorRouting.class);
    public static final EnumDetector[] VALUES = values();
    public IIcon[] textures;
    private final Class<? extends Detector> handler;
    private final Module module;

    private EnumDetector(Module module, Class<? extends Detector> handler) {
        this.handler = handler;
        this.module = module;
    }

    public static EnumDetector fromOrdinal(int meta) {
        if (meta >= EnumDetector.VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }

    public Detector buildHandler() {
        try {
            return handler.newInstance();
        } catch (Exception ex) {
        }
        throw new RuntimeException("Failed to create Detector!");
    }

    public String getTag() {
        return "tile.railcraft.detector." + name().toLowerCase(Locale.ENGLISH);
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        if (BlockDetector.getBlock() == null) {
            return null;
        }
        return new ItemStack(BlockDetector.getBlock(), qty, ordinal());
    }

    public boolean isEnabled(){
        return BlockDetector.getBlock() != null && ModuleManager.isModuleLoaded(module);
    }
}
