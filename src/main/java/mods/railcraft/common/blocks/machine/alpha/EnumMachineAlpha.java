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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MachineProxy;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumMachineAlpha implements IEnumMachine<EnumMachineAlpha> {

    ANCHOR_WORLD(ModuleChunkLoading.class, "anchor.world", TileAnchorWorld.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TURBINE(ModuleElectricity.class, "turbine", TileSteamTurbine.class, 3, 3, 2, 2, 2, 2, 6, 2, 0, 1, 3, 4, 5, 7),
    ANCHOR_PERSONAL(ModuleChunkLoading.class, "anchor.personal", TileAnchorPersonal.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    STEAM_OVEN(ModuleFactory.class, "steam.oven", TileSteamOven.class, 4, 2, 2, 2, 3, 3, 6, 3, 0, 1, 4, 5),
    ANCHOR_ADMIN(ModuleChunkLoading.class, "anchor.admin", TileAnchorAdmin.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    SMOKER(ModuleStructures.class, "smoker", TileSmoker.class, 3, 1, 0, 1, 2, 2, 2, 2),
    TRADE_STATION(ModuleAutomation.class, "trade.station", TileTradeStation.class, 3, 1, 0, 0, 1, 1, 2, 1),
    COKE_OVEN(ModuleFactory.class, "coke.oven", TileCokeOven.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    ROLLING_MACHINE(ModuleFactory.class, "rolling.machine", TileRollingMachine.class, 3, 1, 0, 1, 2, 2, 2, 2),
    STEAM_TRAP_MANUAL(ModuleExtras.class, "steam.trap", TileSteamTrapManual.class, 3, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2),
    STEAM_TRAP_AUTO(ModuleExtras.class, "steam.trap.auto", TileSteamTrapAuto.class, 4, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2, 3),
    FEED_STATION(ModuleAutomation.class, "feed.station", TileFeedStation.class, 2, 1, 0, 0, 1, 1, 1, 1),
    BLAST_FURNACE(ModuleFactory.class, "blast.furnace", TileBlastFurnace.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    ANCHOR_PASSIVE(ModuleChunkLoading.class, "anchor.passive", TileAnchorPassive.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TANK_WATER(ModuleTransport.class, "tank.water", TileTankWater.class, 2, 1, 0, 0, 1, 1, 1, 1),
    ROCK_CRUSHER(ModuleFactory.class, "rock.crusher", TileRockCrusher.class, 4, 3, 3, 11, 3, 3, 7, 3, 7, 0, 1, 2, 4, 6, 8, 9, 10);
    public static final PropertyEnum<EnumMachineAlpha> VARIANT = PropertyEnum.create("variant", EnumMachineAlpha.class);
    public static final EnumMachineAlpha[] VALUES = values();
    private static final List<EnumMachineAlpha> creativeList = new ArrayList<EnumMachineAlpha>();
    public static final MachineProxy<EnumMachineAlpha> PROXY = MachineProxy.create(VALUES, VARIANT, creativeList);

    static {
        String pickaxe3 = HarvestPlugin.ToolClass.PICKAXE.getToolString(3);
        ANCHOR_WORLD.toolClass = pickaxe3;
        ANCHOR_PASSIVE.toolClass = pickaxe3;
        ANCHOR_PERSONAL.toolClass = pickaxe3;
        ANCHOR_ADMIN.toolClass = pickaxe3;

        String axe1 = HarvestPlugin.ToolClass.AXE.getToolString(1);
        TANK_WATER.toolClass = axe1;
        FEED_STATION.toolClass = axe1;

        creativeList.add(COKE_OVEN);
        creativeList.add(BLAST_FURNACE);
        creativeList.add(STEAM_OVEN);
        creativeList.add(TANK_WATER);
        creativeList.add(ROLLING_MACHINE);
        creativeList.add(ROCK_CRUSHER);
        creativeList.add(FEED_STATION);
        creativeList.add(TRADE_STATION);
        creativeList.add(ANCHOR_WORLD);
        creativeList.add(ANCHOR_PERSONAL);
        creativeList.add(ANCHOR_PASSIVE);
        creativeList.add(ANCHOR_ADMIN);
        creativeList.add(TURBINE);
        creativeList.add(SMOKER);
        creativeList.add(STEAM_TRAP_MANUAL);
        creativeList.add(STEAM_TRAP_AUTO);
    }

    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final int[] textureInfo;
    private ToolTip tip;
    private String toolClass = HarvestPlugin.ToolClass.PICKAXE.getToolString(2);

    EnumMachineAlpha(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureInfo = textureInfo;
    }

    public static EnumMachineAlpha fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
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
        return "tile.railcraft.machine.alpha." + getBaseTag();
    }

    @Override
    public String getToolClass() {
        return toolClass;
    }

    @Override
    public boolean passesLight() {
        return false;
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
    public RailcraftBlocks getBlockContainer() {
        return RailcraftBlocks.machine_alpha;
    }

    @Override
    public Block getBlock() {
        return getBlockContainer().block();
    }

    @Override
    public IBlockState getState() {
        return getBlock().getDefaultState().withProperty(VARIANT, this);
    }

    /**
     * Block is enabled, but may not be defined yet.
     */
    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && getBlockContainer().isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag());
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
            case ANCHOR_WORLD:
                if (!RailcraftConfig.anchorFuelWorld.isEmpty())
                    return addAnchorInfo(stack);
                break;
            case ANCHOR_PERSONAL:
                if (!RailcraftConfig.anchorFuelPersonal.isEmpty())
                    return addAnchorInfo(stack);
                break;
            case ANCHOR_PASSIVE:
                if (!RailcraftConfig.anchorFuelPassive.isEmpty())
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

    @Override
    public String getName() {
        return name();
    }
}
