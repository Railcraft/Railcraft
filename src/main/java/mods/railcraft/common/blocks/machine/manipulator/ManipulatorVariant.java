/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.modules.*;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum ManipulatorVariant implements IEnumMachine<ManipulatorVariant> {

    ITEM_LOADER(ModuleTransport.class, "loader_item", TileItemLoader.class),
    ITEM_UNLOADER(ModuleTransport.class, "unloader_item", TileItemUnloader.class),
    ITEM_LOADER_ADVANCED(ModuleTransport.class, "loader_item_advanced", TileItemLoaderAdvanced.class),
    ITEM_UNLOADER_ADVANCED(ModuleTransport.class, "unloader_item_advanced", TileItemUnloaderAdvanced.class),
    FLUID_LOADER(ModuleTransport.class, "loader_fluid", TileFluidLoader.class),
    FLUID_UNLOADER(ModuleTransport.class, "unloader_fluid", TileFluidUnloader.class),
    ENERGY_LOADER(ModuleIC2.class, "loader_ic2", TileIC2Loader.class),
    ENERGY_UNLOADER(ModuleIC2.class, "unloader_ic2", TileIC2Unloader.class),
    DISPENSER_CART(ModuleAutomation.class, "dispenser_cart", TileDispenserCart.class),
    DISPENSER_TRAIN(ModuleTrain.class, "dispenser_train", TileDispenserTrain.class),
    RF_LOADER(ModuleRF.class, "loader_rf", TileRFLoader.class),
    RF_UNLOADER(ModuleRF.class, "unloader_rf", TileRFUnloader.class);
    private static final List<ManipulatorVariant> creativeList = new ArrayList<>();
    public static final ManipulatorVariant[] VALUES = values();

    static {
        // content fluid rendering
        FLUID_LOADER.def.passesLight = true;
        FLUID_UNLOADER.def.passesLight = true;

        creativeList.add(ITEM_LOADER);
        creativeList.add(ITEM_UNLOADER);
        creativeList.add(ITEM_LOADER_ADVANCED);
        creativeList.add(ITEM_UNLOADER_ADVANCED);
        creativeList.add(FLUID_LOADER);
        creativeList.add(FLUID_UNLOADER);
        creativeList.add(ENERGY_LOADER);
        creativeList.add(ENERGY_UNLOADER);
        creativeList.add(RF_LOADER);
        creativeList.add(RF_UNLOADER);
        creativeList.add(DISPENSER_CART);
        creativeList.add(DISPENSER_TRAIN);
    }

    private final Definition def;

    ManipulatorVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, module);
    }

    public static ManipulatorVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<ManipulatorVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.manipulator_" + getBaseTag();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        switch (this) {
            case RF_LOADER:
            case RF_UNLOADER:
            case FLUID_LOADER:
            case FLUID_UNLOADER:
                return IEnumMachine.super.getTextureDimensions();
        }
        return new Tuple<>(3, 1);
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MANIPULATOR;
    }
}
