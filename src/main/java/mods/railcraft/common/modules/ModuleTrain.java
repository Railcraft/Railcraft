/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleTrain extends RailcraftModule {

    @Override
    public void initFirst() {
        BlockDetector.registerBlock();
        RailcraftBlocks.registerBlockTrack();

        MiscTools.registerTrack(EnumTrack.BOARDING_TRAIN);
        MiscTools.registerTrack(EnumTrack.HOLDING_TRAIN);
        MiscTools.registerTrack(EnumTrack.LOCKDOWN_TRAIN);
        MiscTools.registerTrack(EnumTrack.COUPLER);

        if (BlockDetector.getBlock() != null) {
            CraftingPlugin.addShapedRecipe(EnumDetector.TRAIN.getItem(), new Object[]{
                "XXX",
                "XPX",
                "XXX",
                'X', Blocks.nether_brick,
                'P', Blocks.stone_pressure_plate,});
        }

        EnumMachineGamma.DISPENSER_TRAIN.register();
    }

    @Override
    public void initSecond() {
        EnumMachineGamma type = EnumMachineGamma.DISPENSER_TRAIN;
        if (type.isAvaliable() && EnumMachineGamma.DISPENSER_CART.isAvaliable()) {
            ItemStack crowbar = ItemCrowbar.getItem();
            crowbar.setItemDamage(-1);
            CraftingPlugin.addShapedRecipe(type.getItem(),
                    "rcr",
                    "cdc",
                    "rcr",
                    'd', EnumMachineGamma.DISPENSER_CART.getItem(),
                    'c', crowbar,
                    'r', "dustRedstone");
        }
    }

}
