/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CovertJaguar
 */
public enum EnumMachineAlpha implements IEnumMachine {

    WORLD_ANCHOR(Module.CHUNK_LOADING, "anchor.world", TileAnchorWorld.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TURBINE(Module.ELECTRICITY, "turbine", TileSteamTurbine.class, 3, 3, 2, 2, 2, 2, 6, 2, 0, 1, 3, 4, 5, 7),
    PERSONAL_ANCHOR(Module.CHUNK_LOADING, "anchor.personal", TileAnchorPersonal.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    STEAM_OVEN(Module.FACTORY, "steam.oven", TileSteamOven.class, 4, 2, 2, 2, 3, 3, 6, 3, 0, 1, 4, 5),
    ADMIN_ANCHOR(Module.CHUNK_LOADING, "anchor.admin", TileAnchorAdmin.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    SMOKER(Module.STRUCTURES, "smoker", TileSmoker.class, 3, 1, 0, 1, 2, 2, 2, 2),
    TRADE_STATION(Module.AUTOMATION, "trade.station", TileTradeStation.class, 3, 1, 0, 0, 1, 1, 2, 1),
    COKE_OVEN(Module.FACTORY, "coke.oven", TileCokeOven.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    ROLLING_MACHINE(Module.FACTORY, "rolling.machine", TileRollingMachine.class, 3, 1, 0, 1, 2, 2, 2, 2),
    STEAM_TRAP_MANUAL(Module.EXTRAS, "steam.trap", TileSteamTrapManual.class, 3, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2),
    STEAM_TRAP_AUTO(Module.EXTRAS, "steam.trap.auto", TileSteamTrapAuto.class, 4, 1, 0, 2, 1, 1, 1, 1, 0, 1, 2, 3),
    FEED_STATION(Module.AUTOMATION, "feed.station", TileFeedStation.class, 2, 1, 0, 0, 1, 1, 1, 1),
    BLAST_FURNACE(Module.FACTORY, "blast.furnace", TileBlastFurnace.class, 3, 1, 0, 0, 0, 0, 1, 0, 1, 2),
    PASSIVE_ANCHOR(Module.CHUNK_LOADING, "anchor.passive",TileAnchorPassive.class, 3, 1, 0, 0, 1, 1, 1, 1, 2),
    TANK_WATER(Module.TRANSPORT, "tank.water", TileTankWater.class, 2, 1, 0, 0, 1, 1, 1, 1),
    ROCK_CRUSHER(Module.FACTORY, "rock.crusher", TileRockCrusher.class, 4, 3, 3, 11, 3, 3, 7, 3, 7, 0, 1, 2, 4, 6, 8, 9, 10);
    private final Module module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final int[] textureInfo;
    private IIcon[] texture = new IIcon[12];
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

    private EnumMachineAlpha(Module module, String tag, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureInfo = textureInfo;
    }

    @Override
    public boolean isDepreciated() {
        return module == null;
    }

    @Override
    public IIcon getTexture(int index) {
        if (index < 0 || index >= texture.length)
            index = 0;
        return texture[index];
    }

    @SideOnly(Side.CLIENT)
    public static void registerIcons(IIconRegister iconRegister) {
        for (EnumMachineAlpha machine : VALUES) {
            if (machine.isDepreciated()) continue;
            machine.texture = new IIcon[machine.textureInfo.length - 2];
            int columns = machine.textureInfo[0];
            int rows = machine.textureInfo[1];
            IIcon[] icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:" + machine.tag, columns, rows);
            for (int i = 0; i < machine.texture.length; i++) {
                machine.texture[i] = icons[machine.textureInfo[i + 2]];
            }
        }
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
    public Class getTileClass() {
        return tile;
    }

    public TileMachineBase getTileEntity() {
        try {
            return tile.newInstance();
        } catch (Exception ex) {
        }
        return null;
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

    public Module getModule() {
        return module;
    }

    @Override
    public Block getBlock() {
        return RailcraftBlocks.getBlockMachineAlpha();
    }

    /**
     * Block is enabled, but may not be defined yet.
     *
     * @return
     */
    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    /**
     * Block is enabled and defined.
     *
     * @return
     */
    @Override
    public boolean isAvaliable() {
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
            case PASSIVE_ANCHOR:
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

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineAlpha();
            return getBlock() != null;
        }
        return false;
    }

}
