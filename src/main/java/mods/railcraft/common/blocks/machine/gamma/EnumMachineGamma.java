/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 * @author CovertJaguar
 */
public enum EnumMachineGamma implements IEnumMachine {

    ITEM_LOADER(Module.TRANSPORT, "loader.item", 0, TileItemLoader.class),
    ITEM_UNLOADER(Module.TRANSPORT, "unloader.item", 0, TileItemUnloader.class),
    ITEM_LOADER_ADVANCED(Module.TRANSPORT, "loader.item.advanced", 0, TileItemLoaderAdvanced.class),
    ITEM_UNLOADER_ADVANCED(Module.TRANSPORT, "unloader.item.advanced", 0, TileItemUnloaderAdvanced.class),
    FLUID_LOADER(Module.TRANSPORT, "loader.liquid", 2, TileFluidLoader.class),
    FLUID_UNLOADER(Module.TRANSPORT, "unloader.liquid", 2, TileFluidUnloader.class),
    ENERGY_LOADER(Module.IC2, "loader.energy", 0, TileEnergyLoader.class),
    ENERGY_UNLOADER(Module.IC2, "unloader.energy", 0, TileEnergyUnloader.class),
    DISPENSER_CART(Module.AUTOMATION, "dispenser.cart", 0, TileDispenserCart.class),
    DISPENSER_TRAIN(Module.TRAIN, "dispenser.train", 0, TileDispenserTrain.class);
    private final Module module;
    private final String tag;
    private final int extraIcons;
    private final Class<? extends TileMachineBase> tile;
    private IIcon[] texture;
    private static final List<EnumMachineGamma> creativeList = new ArrayList<EnumMachineGamma>();
    private static final EnumMachineGamma[] VALUES = values();
    public static final IIcon[] pipeTexture = new IIcon[6];
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

    private EnumMachineGamma(Module module, String tag, int numTextures, Class<? extends TileMachineBase> tile) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.extraIcons = numTextures;
    }

    @Override
    public boolean isDepreciated() {
        return module == null;
    }

    public void setTexture(IIcon... tex) {
        this.texture = tex;
    }

    @Override
    public IIcon getTexture(int index) {
        if (index < 0 || index >= texture.length)
            index = 0;
        return texture[index];
    }

    @SideOnly(Side.CLIENT)
    public static void registerIcons(IIconRegister iconRegister) {
        for (EnumMachineGamma machine : VALUES) {
            machine.texture = new IIcon[machine.extraIcons + 6];
            IIcon[] icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:" + machine.tag, machine.extraIcons + 3);
            IIcon cap = icons[0];
            IIcon side = icons[1];
            IIcon face = icons[2];

            machine.texture[0] = cap;
            machine.texture[1] = cap;
            for (int i = 2; i < 6; i++) {
                machine.texture[i] = side;
            }

            switch (machine) {
                case ITEM_LOADER:
                case FLUID_LOADER:
                    machine.texture[0] = face;
                    break;
                case ITEM_UNLOADER:
                case FLUID_UNLOADER:
                    machine.texture[1] = face;
                    break;
                default:
                    machine.texture[3] = face;
            }

            if (machine.extraIcons > 0)
                System.arraycopy(icons, 3, machine.texture, 6, machine.extraIcons);
        }

        IIcon[] pipe = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:loader.pipe", 2);
        pipeTexture[0] = pipe[0];
        pipeTexture[1] = pipe[0];
        for (int i = 2; i < 6; i++) {
            pipeTexture[i] = pipe[1];
        }
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
    public Class getTileClass() {
        return tile;
    }

    @Override
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
        return RailcraftBlocks.getBlockMachineGamma();
    }

    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Override
    public boolean isAvaliable() {
        return getBlock() != null && isEnabled();
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineGamma();
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

}
