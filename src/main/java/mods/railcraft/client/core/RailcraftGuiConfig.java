/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.core;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class RailcraftGuiConfig extends GuiConfig {

    RailcraftGuiConfig(GuiScreen parent) {
        super(parent, generateConfig(), Railcraft.MOD_ID, true, true, Railcraft.MOD_ID);
    }

    private static List<IConfigElement> generateConfig() {
        List<IConfigElement> elements = new ArrayList<>();

        elements.addAll(getElements(RailcraftConfig.configClient));
        elements.addAll(getElements(RailcraftModuleManager.config));
        elements.addAll(getElements(RailcraftConfig.configMain));
        elements.addAll(getElements(RailcraftConfig.configBlocks));
        elements.addAll(getElements(RailcraftConfig.configItems));
        elements.addAll(getElements(RailcraftConfig.configEntity));

        return elements;
    }

    private static List<IConfigElement> getElements(Configuration config) {
        return getCategories(config).stream().map(ConfigElement::new).collect(Collectors.toList());
    }

    private static List<ConfigCategory> getCategories(Configuration config) {
        Set<String> catNames = config.getCategoryNames();
        return catNames.stream().map(config::getCategory).filter(cat -> !cat.isChild()).collect(Collectors.toList());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        RailcraftConfig.saveConfigs();
        if (RailcraftModuleManager.config.hasChanged())
            RailcraftModuleManager.config.save();
    }
}
