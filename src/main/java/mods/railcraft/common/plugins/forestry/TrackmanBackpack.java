/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.api.core.items.IMinecartItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.signals.ItemSignalBlockSurveyor;
import mods.railcraft.common.blocks.signals.ItemSignalTuner;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.items.ItemCrowbarReinforced;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.items.ItemWhistleTuner;
import mods.railcraft.common.items.RailcraftPartItems;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.fluids.FluidContainers;
import mods.railcraft.common.items.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackmanBackpack extends BaseBackpack {

    private static TrackmanBackpack instance;

    public static TrackmanBackpack getInstance() {
        if (instance == null)
            instance = new TrackmanBackpack();
        return instance;
    }

    protected TrackmanBackpack() {
    }

    public void setup() {
        addValidItem(ItemCrowbar.getItem());
        addValidItem(ItemCrowbarReinforced.getItem());
        addValidItem(ItemWhistleTuner.getItem());
        addValidItem(ItemMagnifyingGlass.getItem());
        addValidItem(ItemGoggles.getItem());
        addValidItem(ItemSignalBlockSurveyor.getItem());
        addValidItem(ItemSignalTuner.getItem());
        addValidItem(RailcraftToolItems.getOveralls());

        for (Object id : Block.blockRegistry.getKeys()) {
            Block block = (Block) Block.blockRegistry.getObject(id);
            if (block == null) continue;
            if (TrackTools.isRailBlock(block))
                addValidItem(block);
        }

        for (Object id : Item.itemRegistry.getKeys()) {
            Item item = (Item) Item.itemRegistry.getObject(id);
            if (item instanceof ItemMinecart || item instanceof IMinecartItem)
                addValidItem(item);
        }

        addValidItem(FluidContainers.getCreosoteOilBottle());
        addValidItem(FluidContainers.getCreosoteOilBucket());
        addValidItem(FluidContainers.getCreosoteOilCell());
        addValidItem(FluidContainers.getCreosoteOilCan());
        addValidItem(FluidContainers.getCreosoteOilRefactory());
        addValidItem(FluidContainers.getCreosoteOilWax());

        addValidItem(EnumMachineAlpha.WORLD_ANCHOR.getItem());
        addValidItem(EnumMachineAlpha.PERSONAL_ANCHOR.getItem());
        addValidItem(EnumMachineBeta.SENTINEL.getItem());

        addValidItem(RailcraftItem.rail);
        addValidItem(RailcraftItem.railbed);
        addValidItem(RailcraftItem.tie);
        addValidItem(RailcraftItem.signalLamp);
        addValidItem(RailcraftItem.circuit);

        addValidItem(RailcraftBlocks.getBlockMachineGamma());
        addValidItem(RailcraftBlocks.getBlockElevator());
        addValidItem(RailcraftBlocks.getBlockSignal());
        addValidItem(BlockDetector.getBlock());
    }

    @Override
    public String getKey() {
        return "TRACKMAN";
    }

    @Override
    public int getPrimaryColour() {
        return 0x0094FF;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }

}
