/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.types.*;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.modules.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum EnumDetector implements IVariantEnumBlock {

    ITEM(ModuleAutomation.class, DetectorItem.class),
    ANY(ModuleAutomation.class, Detector.class),
    EMPTY(ModuleAutomation.class, DetectorEmpty.class),
    MOB(ModuleAutomation.class, DetectorEnemy.class),
    POWERED(ModuleAutomation.class, DetectorPowered.class),
    PLAYER(ModuleAutomation.class, DetectorPlayer.class),
    EXPLOSIVE(ModuleAutomation.class, DetectorExplosive.class),
    ANIMAL(ModuleAutomation.class, DetectorAnimal.class),
    TANK(ModuleAutomation.class, DetectorTank.class),
    ADVANCED(ModuleAutomation.class, DetectorAdvanced.class),
    ENERGY(ModuleIC2.class, DetectorEnergy.class),
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
    private final Class<? extends IRailcraftModule> module;

    EnumDetector(Class<? extends IRailcraftModule> module, Class<? extends Detector> handler) {
        this.handler = handler;
        this.module = module;
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

    public String getTag() {
        return "tile.railcraft.detector." + getName();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        return RailcraftBlocks.detector.getStack(qty, this);
    }

    @Override
    public Block getBlock() {
        return RailcraftBlocks.detector.block();
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftObjectContainer container) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return getBlock() != null && RailcraftModuleManager.isModuleEnabled(module);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    /**
     * Careful with this one, Detectors store the type and facing in the Tile Entity.
     */
    @Nullable
    @Override
    public IBlockState getState() {
        if (getBlock() == null) return null;
        return getBlock().getDefaultState();
    }
}
