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

import java.lang.reflect.Constructor;
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
import mods.railcraft.common.blocks.machine.beta.MetalTank;
import mods.railcraft.common.blocks.machine.tank.GenericMultiTankBase;
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
public enum EnumMachineZeta implements IEnumMachine {

    
    TANK_ALUMINIUM_WALL(Module.ADVTANKS, "tank.aluminium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_ALUMINIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_ALUMINIUM_GAUGE(Module.ADVTANKS, "tank.aluminium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_ALUMINIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_ALUMINIUM_VALVE(Module.ADVTANKS, "tank.aluminium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_ALUMINIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_STAINLESS_WALL(Module.ADVTANKS, "tank.stainless.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_STAINLESS, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_STAINLESS_GAUGE(Module.ADVTANKS, "tank.stainless.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_STAINLESS, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_STAINLESS_VALVE(Module.ADVTANKS, "tank.stainless.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_STAINLESS, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_TITANIUM_WALL(Module.ADVTANKS, "tank.titanium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TITANIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_TITANIUM_GAUGE(Module.ADVTANKS, "tank.titanium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TITANIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_TITANIUM_VALVE(Module.ADVTANKS, "tank.titanium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TITANIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_TUNGSTENSTEEL_WALL(Module.ADVTANKS, "tank.tungstensteel.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TUNGSTENSTEEL, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_TUNGSTENSTEEL_GAUGE(Module.ADVTANKS, "tank.tungstensteel.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TUNGSTENSTEEL, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_TUNGSTENSTEEL_VALVE(Module.ADVTANKS, "tank.tungstensteel.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_TUNGSTENSTEEL, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3),
    TANK_PALLADIUM_WALL(Module.ADVTANKS, "tank.palladium.wall", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_PALLADIUM, TileGenericMultiTankWall.class, 2, 1, 0, 0, 1, 1, 1, 1),
    TANK_PALLADIUM_GAUGE(Module.ADVTANKS, "tank.palladium.gauge", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_PALLADIUM, TileGenericMultiTankGauge.class, 1, 5, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4),
    TANK_PALLADIUM_VALVE(Module.ADVTANKS, "tank.palladium.valve", ModuleAdvancedTanks.CAPACITY_PER_BLOCK_PALLADIUM, TileGenericMultiTankValve.class, 4, 1, 0, 0, 1, 1, 1, 1, 2, 3);
	
	
	
    private final Module module;
    private final String tag;
    private final int capacity;
    private GenericMultiTankBase tankType;
    private final Class<? extends TileMachineBase> tile;
    private IIcon[] texture = new IIcon[12];
    private final int[] textureInfo;
    private static final List<EnumMachineZeta> creativeList = new ArrayList<EnumMachineZeta>();
    private static final EnumMachineZeta[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(TANK_ALUMINIUM_WALL);
        creativeList.add(TANK_ALUMINIUM_GAUGE);
        creativeList.add(TANK_ALUMINIUM_VALVE);
        creativeList.add(TANK_STAINLESS_WALL);
        creativeList.add(TANK_STAINLESS_GAUGE);
        creativeList.add(TANK_STAINLESS_VALVE);
        creativeList.add(TANK_TITANIUM_WALL);
        creativeList.add(TANK_TITANIUM_GAUGE);
        creativeList.add(TANK_TITANIUM_VALVE);
        creativeList.add(TANK_TUNGSTENSTEEL_WALL);
        creativeList.add(TANK_TUNGSTENSTEEL_GAUGE);
        creativeList.add(TANK_TUNGSTENSTEEL_VALVE);
        creativeList.add(TANK_PALLADIUM_WALL);
        creativeList.add(TANK_PALLADIUM_GAUGE);
        creativeList.add(TANK_PALLADIUM_VALVE);
    }

    private EnumMachineZeta(Module module, String tag, int cap, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.capacity = cap;
        this.textureInfo = textureInfo;
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineZeta();
            return getBlock() != null;
        }
        return false;
    }
    
    public void setTankType(GenericMultiTankBase tankMaterial) {
    	tankType = tankMaterial;
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
        for (EnumMachineZeta machine : VALUES) {
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

    public static EnumMachineZeta fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineZeta> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.zeta." + tag;
    }

    @Override
    public Class getTileClass() {
        return tile;
    }

    public TileMachineBase getTileEntity() {
        try {
        	Constructor<? extends TileMachineBase> cons = tile.getDeclaredConstructor(MetalTank.class, IEnumMachine.class);        	
            return (TileMachineBase) cons.newInstance(tankType, this);
        } catch (Exception ex) {
        	ex.printStackTrace();
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
        return RailcraftBlocks.getBlockMachineZeta();
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
