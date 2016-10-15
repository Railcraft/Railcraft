/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.blocks.machine.MachineProxy;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    FLUID_LOADER(ModuleTransport.class, "loader_fluid", TileFluidLoader.class, true),
    FLUID_UNLOADER(ModuleTransport.class, "unloader_fluid", TileFluidUnloader.class),
    ENERGY_LOADER(ModuleIC2.class, "loader_ic2", TileIC2Loader.class),
    ENERGY_UNLOADER(ModuleIC2.class, "unloader_ic2", TileIC2Unloader.class),
    DISPENSER_CART(ModuleAutomation.class, "dispenser_cart", TileDispenserCart.class),
    DISPENSER_TRAIN(ModuleTrain.class, "dispenser_train", TileDispenserTrain.class),
    RF_LOADER(ModuleRF.class, "loader_rf", TileRFLoader.class),
    RF_UNLOADER(ModuleRF.class, "unloader_rf", TileRFUnloader.class);
    private static final List<ManipulatorVariant> creativeList = new ArrayList<ManipulatorVariant>();
    public static final ManipulatorVariant[] VALUES = values();
    public static final MachineProxy<ManipulatorVariant> PROXY = MachineProxy.create(VALUES, creativeList);

    static {
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

    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final boolean passesLight;
    private ToolTip tip;

    ManipulatorVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this(module, tag, tile, false);
    }

    ManipulatorVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, boolean passesLight) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.passesLight = passesLight;
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
    public boolean isDepreciated() {
        return module == null;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.manipulator_" + getBaseTag();
    }

    @Override
    public String getResourcePathSuffix() {
        return tag;
    }

    @Override
    public boolean passesLight() {
        return passesLight;
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
    public Class<? extends TileMachineBase> getTileClass() {
        return tile;
    }

    @Override
    public TileMachineBase getTileEntity() {
        try {
            return tile.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Class<? extends IRailcraftModule> getModule() {
        return module;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MANIPULATOR;
    }

    @Override
    public boolean isAvailable() {
        return block() != null && isEnabled();
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

    @Override
    public String getName() {
        return tag;
    }
}
