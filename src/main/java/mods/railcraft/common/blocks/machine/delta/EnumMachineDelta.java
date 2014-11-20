/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.delta;

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
import mods.railcraft.common.blocks.machine.BoundingBoxManager;
import mods.railcraft.common.blocks.machine.BoundingBoxManager.BoundingBox;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar
 */
public enum EnumMachineDelta implements IEnumMachine {

    WIRE(Module.ELECTRICITY, "wire", TileWire.class, 1, 1, 0, 0, 0, 0, 0, 0),
    CAGE(Module.TRANSPORT, "cage", TileCage.class, 4, 1, 0, 1, 2, 2, 2, 2, 3);
    private final Module module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private IIcon[] texture = new IIcon[12];
    private final int[] textureInfo;
    private static final List<EnumMachineDelta> creativeList = new ArrayList<EnumMachineDelta>();
    private static final EnumMachineDelta[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(WIRE);
//        creativeList.add(SAWMILL);

        BoundingBoxManager.registerBoundingBox(WIRE, new TileWire.WireBoundingBox());
    }

    private EnumMachineDelta(Module module, String tag, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureInfo = textureInfo;
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineDelta();
            return getBlock() != null;
        }
        return false;
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
        for (EnumMachineDelta machine : VALUES) {
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

    public static EnumMachineDelta fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineDelta> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.delta." + tag;
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
        return RailcraftBlocks.getBlockMachineDelta();
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
