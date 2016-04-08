/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;

import javax.annotation.Nonnull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("tracks|wood")
public class ModuleTrain extends RailcraftModulePayload {

    @Nonnull
    @Override
    public ModuleEventHandler getModuleEventHandler(boolean enabled) {
        if (enabled)
            return enabledEventHandler;
        return DEFAULT_DISABLED_EVENT_HANDLER;
    }

    private final ModuleEventHandler enabledEventHandler = new BaseModuleEventHandler() {
        @Override
        public void preInit() {
            super.preInit();
            BlockDetector.registerBlock();
            RailcraftBlocks.registerBlockTrack();

            MiscTools.registerTrack(EnumTrack.COUPLER);

            if (BlockDetector.getBlock() != null) {
                CraftingPlugin.addRecipe(EnumDetector.TRAIN.getItem(),
                        "XXX",
                        "XPX",
                        "XXX",
                        'X', Blocks.nether_brick,
                        'P', Blocks.stone_pressure_plate);
            }

            EnumMachineGamma.DISPENSER_TRAIN.register();
        }

        @Override
        public void init() {
            super.init();
            EnumMachineGamma type = EnumMachineGamma.DISPENSER_TRAIN;
            if (type.isAvailable() && EnumMachineGamma.DISPENSER_CART.isAvailable()) {
                CraftingPlugin.addRecipe(type.getItem(),
                        "rcr",
                        "cdc",
                        "rcr",
                        'd', EnumMachineGamma.DISPENSER_CART.getItem(),
                        'c', IToolCrowbar.ORE_TAG,
                        'r', "dustRedstone");
            }
        }
    };

}
