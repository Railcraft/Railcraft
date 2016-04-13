/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
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
public enum EnumMachineAlpha implements IEnumMachine<EnumMachineAlpha> {

    WORLD_ANCHOR(ModuleChunkLoading.class, "anchor.world", TileAnchorWorld.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TURBINE(ModuleElectricity.class, "turbine", TileSteamTurbine.class, 3, 3, 2, 2, 2, 2, 6, 2, 0, 1, 3, 4, 5, 7),
    PERSONAL_ANCHOR(ModuleChunkLoading.class, "anchor.personal", TileAnchorPersonal.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    STEAM_OVEN(ModuleFactory.class, "steam.oven", TileSteamOven.class, 4, 2, 2, 2, 3, 3, 6, 3, 0, 1, 4, 5),
    ADMIN_ANCHOR(ModuleChunkLoading.class, "anchor.admin", TileAnchorAdmin.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    SMOKER(ModuleStructures.class, "smoker", TileSmoker.class, 3, 1, 0, 1, 2, 2, 2, 2),
    TRADE_STATION(ModuleAutomation.class, "trade.station", TileTradeStation.class, 3, 1, 0, 0, 1, 1, 2, 1),
    COKE_OVEN(ModuleFactory.class, "coke.oven", TileCokeOven.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    ROLLING_MACHINE(ModuleFactory.class, "rolling.machine", TileRollingMachine.class, 3, 1, 0, 1, 2, 2, 2, 2),
    STEAM_TRAP_MANUAL(ModuleExtras.class, "steam.trap", TileSteamTrapManual.class, 3, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2),
    STEAM_TRAP_AUTO(ModuleExtras.class, "steam.trap.auto", TileSteamTrapAuto.class, 4, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2, 3),
    FEED_STATION(ModuleAutomation.class, "feed.station", TileFeedStation.class, 2, 1, 0, 0, 1, 1, 1, 1),
    BLAST_FURNACE(ModuleFactory.class, "blast.furnace", TileBlastFurnace.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    PASSIVE_ANCHOR(ModuleChunkLoading.class, "anchor.passive", TileAnchorPassive.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TANK_WATER(ModuleTransport.class, "tank.water", TileTankWater.class, 2, 1, 0, 0, 1, 1, 1, 1),
    ROCK_CRUSHER(ModuleFactory.class, "rock.crusher", TileRockCrusher.class, 4, 3, 3, 11, 3, 3, 7, 3, 7, 0, 1, 2, 4, 6, 8, 9, 10);
    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final int[] textureInfo;
    private static final List<EnumMachineAlpha> creativeList = new ArrayList<EnumMachineAlpha>();
    private static final EnumMachineAlpha[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(COKE_OVEN);
        creativeList.add(BLAST_FURNACE);
        creativeList.add(STEAM_OVEN);
        creativeList.add(TANK_WATER);
        creativeList.add(ROLLING_MACHINE);
        creativeList.add(ROCK_CRUSHER);
        creativeList.add(FEED_STATION);
        creativeList.add(TRADE_STATION);
        creativeList.add(WORLD_ANCHOR);
        creativeList.add(PERSONAL_ANCHOR);
        creativeList.add(PASSIVE_ANCHOR);
        creativeList.add(ADMIN_ANCHOR);
        creativeList.add(TURBINE);
        creativeList.add(SMOKER);
        creativeList.add(STEAM_TRAP_MANUAL);
        creativeList.add(STEAM_TRAP_AUTO);
    }

    EnumMachineAlpha(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureInfo = textureInfo;
    }

    @Override
    public boolean isDepreciated() {
        return module == null;
    }

    public static EnumMachineAlpha fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineAlpha> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.alpha." + tag;
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
        return RailcraftBlocksOld.getBlockMachineAlpha();
    }

    @Override
    public IBlockState getState() {
        return getBlock().getDefaultState().withProperty(MachineProxyAlpha.VARIANT, this);
    }

    /**
     * Block is enabled, but may not be defined yet.
     */
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    /**
     * Block is enabled and defined.
     */
    @Override
    public boolean isAvailable() {
        return getBlock() != null && isEnabled();
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        if (tip != null)
            return tip;
        switch (this) {
            case WORLD_ANCHOR:
                if (!RailcraftConfig.anchorFuelWorld.isEmpty())
                    return addAnchorInfo(stack);
                break;
            case PERSONAL_ANCHOR:
                if (!RailcraftConfig.anchorFuelPersonal.isEmpty())
                    return addAnchorInfo(stack);
                break;
            default:
                String tipTag = getTag() + ".tip";
                if (LocalizationPlugin.hasTag(tipTag))
                    tip = ToolTip.buildToolTip(tipTag);
                break;
        }
        return tip;
    }

    private ToolTip addAnchorInfo(ItemStack stack) {
        ToolTip toolTip = new ToolTip();
        long fuel = ItemCartAnchor.getFuel(stack);
        double hours = (double) fuel / RailcraftConstants.TICKS_PER_HOUR;
        String format = LocalizationPlugin.translate("railcraft.gui.anchor.fuel.remaining");
        toolTip.add(String.format(format, hours));
        return toolTip;
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocksOld.registerBlockMachineAlpha();
            return getBlock() != null;
        }
        return false;
    }

    @Override
    public String getName() {
        return name();
    }
}
