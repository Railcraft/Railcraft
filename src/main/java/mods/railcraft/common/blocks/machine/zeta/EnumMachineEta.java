/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.zeta;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankGauge;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankValve;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankWall;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.modules.ModuleAdvancedTanks;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar
 */
public enum EnumMachineEta implements IEnumMachine {

    
    TANK_IRIDIUM_WALL(Module.ADVTANKS, "tank.iridium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_IRIDIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_IRIDIUM_GAUGE(Module.ADVTANKS, "tank.iridium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_IRIDIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_IRIDIUM_VALVE(Module.ADVTANKS, "tank.iridium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_IRIDIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_OSMIUM_WALL(Module.ADVTANKS, "tank.osmium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_OSMIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_OSMIUM_GAUGE(Module.ADVTANKS, "tank.osmium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_OSMIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_OSMIUM_VALVE(Module.ADVTANKS, "tank.osmium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_OSMIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_NEUTRONIUM_WALL(Module.ADVTANKS, "tank.neutronium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_NEUTRONIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_NEUTRONIUM_GAUGE(Module.ADVTANKS, "tank.neutronium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_NEUTRONIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_NEUTRONIUM_VALVE(Module.ADVTANKS, "tank.neutronium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_NEUTRONIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3);
	
    private final Module module;
    private final String tag;
    private final int capacity;
    private final Class<? extends TileMachineBase> tile;
    private IIcon[] texture = new IIcon[12];
    private final int[] textureInfo;
    private static final List<EnumMachineEta> creativeList = new ArrayList<EnumMachineEta>();
    private static final EnumMachineEta[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(TANK_IRIDIUM_WALL);
        creativeList.add(TANK_IRIDIUM_GAUGE);
        creativeList.add(TANK_IRIDIUM_VALVE);
        creativeList.add(TANK_OSMIUM_WALL);
        creativeList.add(TANK_OSMIUM_GAUGE);
        creativeList.add(TANK_OSMIUM_VALVE);
        creativeList.add(TANK_NEUTRONIUM_WALL);
        creativeList.add(TANK_NEUTRONIUM_GAUGE);
        creativeList.add(TANK_NEUTRONIUM_VALVE);
    }

    private EnumMachineEta(Module module, String tag, int cap, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.capacity = cap;
        this.textureInfo = textureInfo;
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineEta();
            return getBlock() != null;
        }
        return false;
    }
    
    public int getCapacity() {
    	return capacity;
    }
    
    @Override
    public boolean isDepreciated() {
        return module == null;
    }

    public void setTexture(IIcon[] tex) {
        this.texture = tex;
    }

    public IIcon[] getTexture() {
        return texture;
    }

    @Override
    public IIcon getTexture(int index) {
        if (index < 0 || index >= texture.length)
            index = 0;
        return texture[index];
    }

    @SideOnly(Side.CLIENT)
    public static void registerIcons(IIconRegister iconRegister) {
        for (EnumMachineEta machine : VALUES) {
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

    public static EnumMachineEta fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineEta> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.eta." + tag;
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
        return RailcraftBlocks.getBlockMachineEta();
    }

    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Override
    public boolean isAvaliable() {
        return getBlock() != null && isEnabled();
    }

    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        if (tip != null)
            return tip;
        String tipTag = getTag() + ".tip";
        if (LocalizationPlugin.hasTag(tipTag))
            tip = ToolTip.buildToolTip(tipTag);
        return tip;
    }

}
