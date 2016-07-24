/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.delta;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MachineProxy;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleTransport;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumMachineDelta implements IEnumMachine<EnumMachineDelta> {

    CAGE(ModuleTransport.class, "cage", TileCage.class, 4, 1);
    public static final PropertyEnum<EnumMachineDelta> VARIANT = PropertyEnum.create("variant", EnumMachineDelta.class);
    private static final List<EnumMachineDelta> creativeList = new ArrayList<EnumMachineDelta>();
    private static final EnumMachineDelta[] VALUES = values();
    public static final MachineProxy<EnumMachineDelta> PROXY = MachineProxy.create(VALUES, VARIANT, creativeList);

    static {
//        creativeList.add(SAWMILL);
    }

    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final int textureWidth, textureHeight;
    private ToolTip tip;

    EnumMachineDelta(@Nullable Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, int textureWidth, int textureHeight) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public static EnumMachineDelta fromId(int id) {
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
        return "tile.railcraft.machine.delta." + tag;
    }

    @Override
    public boolean passesLight() {
        return true;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(textureWidth, textureHeight);
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
    public RailcraftBlocks getBlockContainer() {
        return RailcraftBlocks.machine_delta;
    }

    @Override
    public PropertyEnum<EnumMachineDelta> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public boolean isAvailable() {
        return getBlock() != null && isEnabled();
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
        return tag.replace(".", "_");
    }
}
