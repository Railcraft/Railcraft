/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import forestry.api.storage.IBackpackDefinition;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.fluids.FluidContainers;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = "Forestry")
public class TrackmanBackpack extends BaseBackpack implements IBackpackDefinition {

    private static TrackmanBackpack instance;

    public static TrackmanBackpack getInstance() {
        if (instance == null)
            instance = new TrackmanBackpack();
        return instance;
    }

    protected TrackmanBackpack() {
        super("railcraft.trackman");
    }

    public void setup() {

        for (ResourceLocation id : Block.REGISTRY.getKeys()) {
            Block block = Block.REGISTRY.getObject(id);
            if (block == null) continue;
            if (TrackTools.isRailBlock(block))
                add(block);
        }

        for (ResourceLocation id : Item.REGISTRY.getKeys()) {
            Item item = Item.REGISTRY.getObject(id);
            if (item instanceof ItemMinecart || item instanceof IMinecartItem)
                add(item);
        }

        add(IToolCrowbar.ORE_TAG);

        add(FluidContainers.getCreosoteOilBottle());
        add(FluidContainers.getCreosoteOilBucket());
        add(FluidContainers.getCreosoteOilCell());
        add(FluidContainers.getCreosoteOilCan());
        add(FluidContainers.getCreosoteOilRefactory());
        add(FluidContainers.getCreosoteOilWax());

        add(EnumMachineAlpha.ANCHOR_WORLD.getItem());
        add(EnumMachineAlpha.ANCHOR_PERSONAL.getItem());
        add(EnumMachineAlpha.ANCHOR_ADMIN.getItem());
        add(EnumMachineAlpha.ANCHOR_PASSIVE.getItem());
        add(EnumMachineBeta.SENTINEL.getItem());

        add(RailcraftItems.crowbarIron);
        add(RailcraftItems.crowbarSteel);
        add(RailcraftItems.signalBlockSurveyor);
        add(RailcraftItems.signalTuner);
        add(RailcraftItems.rail);
        add(RailcraftItems.railbed);
        add(RailcraftItems.tie);
        add(RailcraftItems.signalLamp);
        add(RailcraftItems.circuit);
        add(RailcraftItems.signalLabel);
        add(RailcraftItems.whistleTuner);
        add(RailcraftItems.magGlass);
        add(RailcraftItems.goggles);
        add(RailcraftItems.overalls);

        add(RailcraftBlocks.machine_gamma);
        add(RailcraftBlocksOld.getBlockElevator());
        add(RailcraftBlocks.signal);
        add(RailcraftBlocks.detector);
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
