/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidContainers {

    public static final FluidContainers INSTANCE = new FluidContainers();
    private static Item itemCreosoteOilBucket;
    private static Item itemCreosoteOilBottle;
    private static Item itemSteamBottle;
    private static ItemFluidContainer itemCreosoteOilCell;
    private static ItemFluidContainer itemCreosoteOilCan;
    private static ItemFluidContainer itemCreosoteOilWax;
    private static ItemFluidContainer itemCreosoteOilRefactory;

    private FluidContainers() {
    }

    public static ItemStack getCreosoteOilBucket() {
        Item item = itemCreosoteOilBucket;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.bucket";
            item = new ItemBucketRailcraft(Fluids.CREOSOTE.get());
            item.setUnlocalizedName(tag);
            RailcraftRegistry.register(item);

            FluidHelper.registerBucket(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));

            itemCreosoteOilBucket = item;
        }
        return new ItemStack(item);
    }

    public static ItemStack getCreosoteOilBottle() {
        return getCreosoteOilBottle(1);
    }

    public static ItemStack getCreosoteOilBottle(int qty) {
        Item item = itemCreosoteOilBottle;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.bottle";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFluidContainer().setUnlocalizedName(tag).setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
                RailcraftRegistry.register(item);

                LootPlugin.addLootRailway(new ItemStack(item), 4, 16, "creosote.bottle");

                if (!RailcraftConfig.useCreosoteFurnaceRecipes() && RailcraftConfig.isSubBlockEnabled(EnumMachineAlpha.COKE_OVEN.getTag()))
                    FluidHelper.registerBottle(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));

                itemCreosoteOilBottle = item;
            } else
                return null;
        }
        return new ItemStack(item, qty);
    }

    public static ItemStack getCreosoteOilCan() {
        return getCreosoteOilCan(1);
    }

    public static ItemStack getCreosoteOilCan(int qty) {
        ItemFluidContainer item = itemCreosoteOilCan;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.can";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFluidContainer();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                boolean forestry = FluidHelper.registerCan(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));
                if (forestry)
                    item.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

                itemCreosoteOilCan = item;
            } else
                return null;
        }
        return new ItemStack(item, qty);
    }

    public static ItemStack getCreosoteOilWax() {
        return getCreosoteOilWax(1);
    }

    public static ItemStack getCreosoteOilWax(int qty) {
        ItemFluidContainer item = itemCreosoteOilWax;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.wax";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFluidContainer();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                boolean forestry = FluidHelper.registerWax(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));
                if (forestry)
                    item.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

                itemCreosoteOilWax = item;
            } else
                return null;
        }
        return new ItemStack(item, qty);
    }

    public static ItemStack getCreosoteOilRefactory() {
        return getCreosoteOilRefactory(1);
    }

    public static ItemStack getCreosoteOilRefactory(int qty) {
        ItemFluidContainer item = itemCreosoteOilRefactory;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.refactory";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFluidContainer();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                boolean forestry = FluidHelper.registerRefactory(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));
                if (forestry)
                    item.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

                itemCreosoteOilRefactory = item;
            } else
                return null;
        }
        return new ItemStack(item, qty);
    }

    public static ItemStack getCreosoteOilCell() {
        return getCreosoteOilCell(1);
    }

    public static ItemStack getCreosoteOilCell(int qty) {
        ItemFluidContainer item = itemCreosoteOilCell;
        if (item == null) {
            String tag = "railcraft.fluid.creosote.cell";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFluidContainer();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                boolean ic2 = FluidHelper.registerCell(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));
                if (ic2)
                    item.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

                itemCreosoteOilCell = item;
            } else
                return null;
        }
        return new ItemStack(item, qty);
    }

    public static ItemStack getSteamBottle() {
        return getSteamBottle(1);
    }

    public static ItemStack getSteamBottle(int qty) {
        Item item = itemSteamBottle;
        if (item == null) {
            String tag = "railcraft.fluid.steam.bottle";

            item = new ItemBucketRailcraft(Fluids.STEAM.get()).setContainerItemStack(new ItemStack(Items.glass_bottle));
            item.setUnlocalizedName(tag);
            RailcraftRegistry.register(item);

            FluidHelper.registerBottle(Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME), new ItemStack(item));

            itemSteamBottle = item;
        }
        return new ItemStack(item);
    }

}
