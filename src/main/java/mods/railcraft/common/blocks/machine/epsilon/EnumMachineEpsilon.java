/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.RailcraftBlocks;
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
public enum EnumMachineEpsilon implements IEnumMachine {

    ELECTRIC_FEEDER(Module.ELECTRICITY, "electric.feeder", TileElectricFeeder.class, 1, 1, 0),
    ELECTRIC_FEEDER_ADMIN(Module.ELECTRICITY, "electric.feeder.admin", TileElectricFeederAdmin.class, 2, 1, 0, 0, 0, 0, 0, 0, 1),
    ADMIN_STEAM_PRODUCER(Module.STEAM, "admin.steam.producer", TileAdminSteamProducer.class, 2, 1, 0, 0, 0, 0, 0, 0, 1),
    FORCE_TRACK_EMITTER(Module.ELECTRICITY, "force.track.emitter", TileForceTrackEmitter.class),
    FLUX_TRANSFORMER(Module.ELECTRICITY, "flux.transformer", TileFluxTransformer.class),
    ENGRAVING_BENCH(Module.EMBLEM, "engraving.bench", TileEngravingBench.class, 4, 1, 0, 1, 3, 3, 3, 3, 2);
    private final Module module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private IIcon[] texture = new IIcon[12];
    private final int[] textureInfo;
    private static final List<EnumMachineEpsilon> creativeList = new ArrayList<EnumMachineEpsilon>();
    private static final EnumMachineEpsilon[] VALUES = values();
    private ToolTip tip;

    static {
        creativeList.add(ELECTRIC_FEEDER);
        creativeList.add(ELECTRIC_FEEDER_ADMIN);
        creativeList.add(FLUX_TRANSFORMER);
        creativeList.add(FORCE_TRACK_EMITTER);
        creativeList.add(ADMIN_STEAM_PRODUCER);
        creativeList.add(ENGRAVING_BENCH);
    }

    private EnumMachineEpsilon(Module module, String tag, Class<? extends TileMachineBase> tile, int... textureInfo) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureInfo = textureInfo;
    }

    public boolean register() {
        if (RailcraftConfig.isSubBlockEnabled(getTag())) {
            RailcraftBlocks.registerBlockMachineEpsilon();
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
        for (EnumMachineEpsilon machine : VALUES) {
            if (machine.isDepreciated()) continue;
            if (machine.textureInfo.length == 0) continue;
            machine.texture = new IIcon[machine.textureInfo.length - 2];
            int columns = machine.textureInfo[0];
            int rows = machine.textureInfo[1];
            IIcon[] icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:" + machine.tag, columns, rows);
            for (int i = 0; i < machine.texture.length; i++) {
                machine.texture[i] = icons[machine.textureInfo[i + 2]];
            }
        }

        IIcon emitterSide = iconRegister.registerIcon("railcraft:" + FORCE_TRACK_EMITTER.tag + ".side");
        FORCE_TRACK_EMITTER.texture = new IIcon[9];
        Arrays.fill(FORCE_TRACK_EMITTER.texture, emitterSide);
        FORCE_TRACK_EMITTER.texture[6] = iconRegister.registerIcon("railcraft:" + FORCE_TRACK_EMITTER.tag + ".side.unpowered");
        FORCE_TRACK_EMITTER.texture[7] = iconRegister.registerIcon("railcraft:" + FORCE_TRACK_EMITTER.tag + ".facing");
        FORCE_TRACK_EMITTER.texture[8] = iconRegister.registerIcon("railcraft:" + FORCE_TRACK_EMITTER.tag + ".facing.unpowered");

        IIcon transformerSide = iconRegister.registerIcon("railcraft:" + FLUX_TRANSFORMER.tag + ".side");
        IIcon transformerCap = iconRegister.registerIcon("railcraft:" + FLUX_TRANSFORMER.tag + ".cap");
        FLUX_TRANSFORMER.texture = new IIcon[6];
        Arrays.fill(FLUX_TRANSFORMER.texture, transformerSide);
        FLUX_TRANSFORMER.texture[0] = transformerCap;
        FLUX_TRANSFORMER.texture[1] = transformerCap;
    }

    public static EnumMachineEpsilon fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EnumMachineEpsilon> getCreativeList() {
        return creativeList;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.epsilon." + tag;
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
        return RailcraftBlocks.getBlockMachineEpsilon();
    }

    @Override
    public boolean isAvaliable() {
        return ModuleManager.isModuleLoaded(getModule()) && getBlock() != null && RailcraftConfig.isSubBlockEnabled(getTag());
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
