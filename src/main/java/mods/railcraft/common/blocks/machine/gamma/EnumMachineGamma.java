/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumMachineGamma implements IEnumMachine<EnumMachineGamma> {

    ITEM_LOADER(ModuleTransport.class, "loader.item", 0, TileItemLoader.class),
    ITEM_UNLOADER(ModuleTransport.class, "unloader.item", 0, TileItemUnloader.class),
    ITEM_LOADER_ADVANCED(ModuleTransport.class, "loader.item.advanced", 0, TileItemLoaderAdvanced.class),
    ITEM_UNLOADER_ADVANCED(ModuleTransport.class, "unloader.item.advanced", 0, TileItemUnloaderAdvanced.class),
    FLUID_LOADER(ModuleTransport.class, "loader.liquid", 2, TileFluidLoader.class),
    FLUID_UNLOADER(ModuleTransport.class, "unloader.liquid", 2, TileFluidUnloader.class),
    ENERGY_LOADER(ModuleIC2.class, "loader.energy", 0, TileEnergyLoader.class),
    ENERGY_UNLOADER(ModuleIC2.class, "unloader.energy", 0, TileEnergyUnloader.class),
    DISPENSER_CART(ModuleAutomation.class, "dispenser.cart", 0, TileDispenserCart.class),
    DISPENSER_TRAIN(ModuleTrain.class, "dispenser.train", 0, TileDispenserTrain.class);
    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final int extraIcons;
    private final Class<? extends TileMachineBase> tile;
    private static final List<EnumMachineGamma> creativeList = new ArrayList<EnumMachineGamma>();
    private static final EnumMachineGamma[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(ITEM_LOADER);
        creativeList.add(ITEM_UNLOADER);
        creativeList.add(ITEM_LOADER_ADVANCED);
        creativeList.add(ITEM_UNLOADER_ADVANCED);
        creativeList.add(FLUID_LOADER);
        creativeList.add(FLUID_UNLOADER);
        creativeList.add(ENERGY_LOADER);
        creativeList.add(ENERGY_UNLOADER);
        creativeList.add(DISPENSER_CART);
        creativeList.add(DISPENSER_TRAIN);
    }

    EnumMachineGamma(Class<? extends IRailcraftModule> module, String tag, int numTextures, Class<? extends TileMachineBase> tile) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.extraIcons = numTextures;
    }

    @Override
    public boolean isDepreciated() {
        return module == null;
    }

    public static EnumMachineGamma fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineGamma> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.gamma." + tag;
    }

    @Override
    public String getToolClass() {
        return "pickaxe:2";
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
    public ItemStack getItem() {
        return getItem(1);
    }

    @Override
    public ItemStack getItem(int qty) {
        Block block = getBlock();
        if (block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    public Class<? extends IRailcraftModule> getModule() {
        return module;
    }

    @Override
    public Block getBlock() {
        return RailcraftBlocksOld.getBlockMachineGamma();
    }

    @Override
    public IBlockState getState() {
        return getBlock().getDefaultState().withProperty(MachineProxyGamma.VARIANT, this);
    }

    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Override
    public boolean isAvailable() {
        return getBlock() != null && isEnabled();
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocksOld.registerBlockMachineGamma();
            return getBlock() != null;
        }
        return false;
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        if (tip != null)
            return tip;
        String tipTag = getTag() + ".tip";
        if (LocalizationPlugin.hasTag(tipTag))
            tip = ToolTip.buildToolTip(tipTag);
        return tip;
    }

    @Override
    public String getName() {
        return name();
    }
}
