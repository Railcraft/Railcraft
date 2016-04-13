/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.tracks.instances.*;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemRailbed.EnumRailbed;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.*;

public enum EnumTrack {

    //    BOARDING(ModuleTracks.class, 4, 1, "boarding", 8, TrackBoarding.class),
//    HOLDING(ModuleTracks.class, 2, 1, "holding", 8, TrackHolding.class),
    ONEWAY(ModuleTracks.class, 4, 1, "oneway", 8, TrackOneWay.class),
    CONTROL(ModuleTracks.class, 2, 0, "control", 16, TrackControl.class),
    LAUNCHER(ModuleExtras.class, 2, 1, "launcher", 1, TrackLauncher.class),
    PRIMING(ModuleExtras.class, 2, 1, "priming", 8, TrackPriming.class),
    JUNCTION(ModuleTracks.class, 1, 0, "junction", 8, TrackJunction.class),
    SWITCH(ModuleSignals.class, 4, 0, "switch", 8, TrackSwitch.class),
    DISEMBARK(ModuleTracks.class, 4, 1, "disembarking", 8, TrackDisembark.class),
    SUSPENDED(ModuleExtras.class, 1, 0, "suspended", 8, TrackSuspended.class),
    GATED_ONEWAY(ModuleTracks.class, 2, 0, "gated.oneway", 4, TrackGatedOneWay.class),
    GATED(ModuleTracks.class, 1, 0, "gated", 4, TrackGated.class),
    SLOW(ModuleTracksWood.class, 2, 0, "slow", 16, TrackSlow.class),
    SLOW_BOOSTER(ModuleTracksWood.class, 2, 1, "slow.boost", 8, TrackSlowBooster.class),
    SLOW_JUNCTION(ModuleTracksWood.class, 1, 0, "slow.junction", 8, TrackSlowJunction.class),
    SLOW_SWITCH(ModuleSignals.class, 4, 0, "slow.switch", 8, TrackSlowSwitch.class),
    SPEED(ModuleTracksHighSpeed.class, 2, 0, "speed", 16, TrackSpeed.class),
    SPEED_BOOST(ModuleTracksHighSpeed.class, 2, 1, "speed.boost", 8, TrackSpeedBoost.class),
    SPEED_TRANSITION(ModuleTracksHighSpeed.class, 4, 1, "speed.transition", 8, TrackSpeedTransition.class),
    SPEED_SWITCH(ModuleSignals.class, 4, 0, "speed.switch", 8, TrackSpeedSwitch.class),
    //    BOARDING_TRAIN(Module.TRAIN, 4, 1, "boarding.train", 8, TrackBoardingTrain.class),
//    HOLDING_TRAIN(Module.TRAIN, 2, 1, "holding.train", 8, TrackHoldingTrain.class),
    COUPLER(ModuleTracks.class, 6, 1, "coupler", 8, TrackCoupler.class),
    DECOUPLER(ModuleTracks.class, 0, 0, "decoupler", 8, TrackCoupler.class),
    REINFORCED(ModuleTracksReinforced.class, 2, 0, "reinforced", 16, TrackReinforced.class),
    REINFORCED_BOOSTER(ModuleTracksReinforced.class, 2, 1, "reinforced.boost", 8, TrackReinforcedBooster.class),
    REINFORCED_JUNCTION(ModuleTracksReinforced.class, 1, 0, "reinforced.junction", 8, TrackReinforcedJunction.class),
    REINFORCED_SWITCH(ModuleTracksReinforced.class, 4, 0, "reinforced.switch", 8, TrackReinforcedSwitch.class),
    BUFFER_STOP(ModuleTracks.class, 2, 0, "buffer.stop", 8, TrackBufferStop.class),
    DISPOSAL(ModuleTracks.class, 2, 0, "disposal", 8, TrackDisposal.class),
    DETECTOR_DIRECTION(ModuleTracks.class, 4, 0, "detector.direction", 8, TrackDetectorDirection.class),
    EMBARKING(ModuleTracks.class, 2, 1, "embarking", 8, TrackEmbarking.class),
    WYE(ModuleTracks.class, 2, 0, "wye", 8, TrackWye.class),
    SLOW_WYE(ModuleTracksWood.class, 2, 0, "slow.wye", 8, TrackSlowWye.class),
    REINFORCED_WYE(ModuleTracksReinforced.class, 2, 0, "reinforced.wye", 8, TrackReinforcedWye.class),
    SPEED_WYE(ModuleTracksHighSpeed.class, 2, 0, "speed.wye", 8, TrackSpeedWye.class),
    //    LOCKDOWN(ModuleTracks.class, 2, 1, "lockdown", 8, TrackLockdown.class),
//    LOCKDOWN_TRAIN(Module.TRAIN, 2, 1, "lockdown.train", 8, TrackLockdownTrain.class),
    WHISTLE(ModuleLocomotives.class, 2, 1, "whistle", 8, TrackWhistle.class),
    LOCOMOTIVE(ModuleLocomotives.class, 6, 3, "locomotive", 8, TrackLocomotive.class),
    LIMITER(ModuleLocomotives.class, 6, 5, "limiter", 8, TrackLimiter.class),
    ROUTING(ModuleRouting.class, 2, 1, "routing", 8, TrackRouting.class),
    LOCKING(ModuleTracks.class, 16, 1, "locking", 8, TrackLocking.class),
    ELECTRIC(ModuleTracksElectric.class, 2, 0, "electric", 16, TrackElectric.class),
    ELECTRIC_JUNCTION(ModuleTracksElectric.class, 1, 0, "electric.junction", 8, TrackElectricJunction.class),
    ELECTRIC_SWITCH(ModuleTracksElectric.class, 4, 0, "electric.switch", 8, TrackElectricSwitch.class),
    ELECTRIC_WYE(ModuleTracksElectric.class, 2, 0, "electric.wye", 8, TrackElectricWye.class),
    FORCE(ModuleElectricity.class, 1, 0, "force", 1, TrackForce.class);
    public static final EnumTrack[] VALUES = values();
    private static final List<EnumTrack> creativeList = new ArrayList<EnumTrack>(50);
    private static final Set<TrackSpec> trackSpecs = new HashSet<TrackSpec>(50);

    static {
        trackSpecs.add(TrackRegistry.getDefaultTrackSpec());

        creativeList.add(SWITCH);
        creativeList.add(WYE);
        creativeList.add(JUNCTION);
        creativeList.add(CONTROL);
        creativeList.add(LOCKING);
        creativeList.add(DISEMBARK);
        creativeList.add(EMBARKING);
        creativeList.add(COUPLER);
        creativeList.add(WHISTLE);
        creativeList.add(LOCOMOTIVE);
        creativeList.add(LIMITER);
        creativeList.add(ROUTING);
        creativeList.add(BUFFER_STOP);
        creativeList.add(ONEWAY);
        creativeList.add(DETECTOR_DIRECTION);
        creativeList.add(GATED_ONEWAY);
        creativeList.add(GATED);
        creativeList.add(SUSPENDED);
        creativeList.add(DISPOSAL);
        creativeList.add(SLOW);
        creativeList.add(SLOW_BOOSTER);
        creativeList.add(SLOW_JUNCTION);
        creativeList.add(SLOW_SWITCH);
        creativeList.add(SLOW_WYE);
        creativeList.add(REINFORCED);
        creativeList.add(REINFORCED_BOOSTER);
        creativeList.add(REINFORCED_JUNCTION);
        creativeList.add(REINFORCED_SWITCH);
        creativeList.add(REINFORCED_WYE);
        creativeList.add(ELECTRIC);
        creativeList.add(ELECTRIC_JUNCTION);
        creativeList.add(ELECTRIC_SWITCH);
        creativeList.add(ELECTRIC_WYE);
        creativeList.add(SPEED);
        creativeList.add(SPEED_BOOST);
        creativeList.add(SPEED_TRANSITION);
        creativeList.add(SPEED_SWITCH);
        creativeList.add(SPEED_WYE);
        creativeList.add(PRIMING);
        creativeList.add(LAUNCHER);

        DECOUPLER.depreciated = true;
//        LOCKDOWN.depreciated = true;
//        LOCKDOWN_TRAIN.depreciated = true;
//        BOARDING.depreciated = true;
//        BOARDING_TRAIN.depreciated = true;
//        HOLDING.depreciated = true;
//        HOLDING_TRAIN.depreciated = true;
    }

    public final int recipeOutput;
    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final int numIcons;
    private final int itemIconIndex;
    private final Class<? extends TrackBaseRailcraft> trackInstance;
    private TrackSpec trackSpec;
    private boolean depreciated;

    EnumTrack(Class<? extends IRailcraftModule> module, int numIcons, int itemIconIndex, String tag, int recipeOutput, Class<? extends TrackBaseRailcraft> trackInstance) {
        this.module = module;
        this.numIcons = numIcons;
        this.itemIconIndex = itemIconIndex;
        this.tag = tag;
        this.recipeOutput = recipeOutput;
        this.trackInstance = trackInstance;
    }

    public static EnumTrack fromId(int id) {
        if (id < 0 || id >= EnumTrack.values().length)
            id = 0;
        return EnumTrack.values()[id];
    }

    public static List<EnumTrack> getCreativeList() {
        return creativeList;
    }

    public static Collection<TrackSpec> getRailcraftTrackSpecs() {
        return trackSpecs;
    }

    public void register() {
        if (RailcraftBlocks.track.isLoaded() && RailcraftConfig.isSubBlockEnabled(getTag())) {
            ToolTip toolTip = ToolTip.buildToolTip("tile.railcraft." + MiscTools.cleanTag(getTag()) + ".tip");
            List<String> tips = toolTip != null ? toolTip.convertToStrings() : null;
            trackSpec = new TrackSpec((short) ordinal(), getTag(), /* TODO: create a ModelResourceLocation */ null, trackInstance, tips);
            try {
                TrackRegistry.registerTrackSpec(trackSpec);
                trackSpecs.add(trackSpec);
                registerRecipe();
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackSpec.class);
            }
            ItemStack stack = getTrackSpec().getItem();

            RailcraftRegistry.register(stack);
        }
    }

    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(module) && RailcraftConfig.isBlockEnabled("track") && RailcraftConfig.isSubBlockEnabled(getTag()) && !isDepreciated();
    }

    public boolean isDepreciated() {
        return depreciated;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        if (trackSpec != null)
            return trackSpec.getItem(qty);
        return null;
    }

    public TrackSpec getTrackSpec() {
        return trackSpec;
    }

    public String getTextureTag() {
        return "railcraft:tracks/track." + tag;
    }

    public String getTag() {
        return "railcraft:track." + tag;
    }

    public int getNumIcons() {
        return numIcons;
    }

    public int getItemIconIndex() {
        return itemIconIndex;
    }

    private ItemStack registerRecipe() {
        if (getItem() == null)
            return null;
        ItemStack output = getItem(recipeOutput * 2);
        Object railWood = RailcraftConfig.useOldRecipes() ? "slabWood" : RailcraftItems.rail.getRecipeObject(EnumRail.WOOD);
        Object railStandard = RailcraftConfig.useOldRecipes() ? new ItemStack(Items.iron_ingot) : RailcraftItems.rail.getRecipeObject(EnumRail.STANDARD);
        Object railAdvanced = RailcraftConfig.useOldRecipes() ? new ItemStack(Items.gold_ingot) : RailcraftItems.rail.getRecipeObject(EnumRail.ADVANCED);
        Object railSpeed = RailcraftConfig.useOldRecipes() ? "ingotSteel" : RailcraftItems.rail.getRecipeObject(EnumRail.SPEED);
        Object railReinforced = RailcraftConfig.useOldRecipes() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? "ingotSteel" : RailcraftItems.rail.getRecipeObject(EnumRail.REINFORCED);
        Object railElectric = RailcraftConfig.useOldRecipes() ? "ingotCopper" : RailcraftItems.rail.getRecipeObject(EnumRail.ELECTRIC);
        Object woodTie = RailcraftItems.tie.getRecipeObject(EnumTie.WOOD);
        Object woodRailbed = RailcraftConfig.useOldRecipes() ? "stickWood" : RailcraftItems.railbed.getRecipeObject(EnumRailbed.WOOD);
        Object stoneRailbed = RailcraftConfig.useOldRecipes() ? Blocks.stone_slab : RailcraftItems.railbed.getRecipeObject(EnumRailbed.STONE);
        Object reinforcedRailbed = RailcraftConfig.useOldRecipes() || !RailcraftItems.rail.isEnabled() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? new ItemStack(Blocks.obsidian) : stoneRailbed;

        Object crowbar = IToolCrowbar.ORE_TAG;

        switch (this) {
            case LOCKING:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IbI",
                        "IsI",
                        'I', railAdvanced,
                        's', woodRailbed,
                        'r', "dustRedstone",
                        'b', Blocks.stone_pressure_plate);
                break;
            case ONEWAY:
                CraftingPlugin.addRecipe(output,
                        "IbI",
                        "IsI",
                        "IpI",
                        'I', railStandard,
                        's', woodRailbed,
                        'b', Blocks.stone_pressure_plate,
                        'p', Blocks.piston);
                break;
            case CONTROL:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "GsG",
                        "IrI",
                        'I', railStandard,
                        'G', railAdvanced,
                        's', woodRailbed,
                        'r', "dustRedstone");
                break;
            case SPEED:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "IsI",
                        "I I",
                        'I', railSpeed,
                        's', stoneRailbed);
                break;
            case SPEED_BOOST:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IsI",
                        "IrI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                break;
            case SPEED_TRANSITION:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IrI",
                        "IsI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "IrI",
                        "IrI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                break;
            case SPEED_SWITCH:
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "III",
                        "III",
                        'I', railSpeed,
                        's', stoneRailbed);
                break;
            case LAUNCHER:
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "BPB",
                        "IsI",
                        'I', railReinforced,
                        'B', "blockSteel",
                        's', stoneRailbed,
                        'P', Blocks.piston);
                break;
            case PRIMING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "IsI",
                        "IfI",
                        'I', railReinforced,
                        's', stoneRailbed,
                        'p', Blocks.stone_pressure_plate,
                        'f', Items.flint_and_steel);
                break;
            case JUNCTION:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "I#I",
                        "III",
                        'I', railStandard,
                        '#', woodRailbed);
                break;
            case SLOW:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "I I",
                        'I', railWood,
                        '#', woodRailbed);
                break;
            case SLOW_BOOSTER:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "G#G",
                        "IrI",
                        'G', Items.gold_ingot,
                        'I', railWood,
                        '#', woodRailbed,
                        'r', "dustRedstone");
                break;
            case SLOW_SWITCH:
                CraftingPlugin.addRecipe(output,
                        "I#I",
                        "III",
                        "III",
                        'I', railWood,
                        '#', woodRailbed);
                break;
            case SLOW_JUNCTION:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "I#I",
                        "III",
                        'I', railWood,
                        '#', woodRailbed);
                break;
            case ELECTRIC:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "I I",
                        'I', railElectric,
                        '#', stoneRailbed);
                break;
            case ELECTRIC_SWITCH:
                CraftingPlugin.addRecipe(output,
                        "I#I",
                        "III",
                        "III",
                        'I', railElectric,
                        '#', stoneRailbed);
                break;
            case ELECTRIC_JUNCTION:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "I#I",
                        "III",
                        'I', railElectric,
                        '#', stoneRailbed);
                break;
            case ELECTRIC_WYE:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "II#",
                        "III",
                        'I', railElectric,
                        '#', stoneRailbed);
                break;
            case SWITCH:
                CraftingPlugin.addRecipe(output,
                        "I#I",
                        "III",
                        "III",
                        'I', railStandard,
                        '#', woodRailbed);
                break;
            case WYE:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "II#",
                        "III",
                        'I', railStandard,
                        '#', woodRailbed);
                break;
            case SLOW_WYE:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "II#",
                        "III",
                        'I', railWood,
                        '#', woodRailbed);
                break;
            case REINFORCED_WYE:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "II#",
                        "III",
                        'I', railReinforced,
                        '#', reinforcedRailbed);
                break;
            case SPEED_WYE:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "II#",
                        "III",
                        'I', railSpeed,
                        '#', stoneRailbed);
                break;
            case DISEMBARK:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IrI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        'p', Blocks.stone_pressure_plate);
                break;
            case EMBARKING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IpI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'p', Items.ender_pearl);
                break;
            case SUSPENDED:
                CraftingPlugin.addRecipe(output,
                        "ItI",
                        "ItI",
                        "ItI",
                        'I', railStandard,
                        't', woodTie);
                break;
            case DISPOSAL:
                CraftingPlugin.addRecipe(output,
                        "ItI",
                        "IPI",
                        "ItI",
                        'I', railStandard,
                        'P', RailcraftItems.plate, EnumPlate.STEEL,
                        't', woodTie);
                break;
            case BUFFER_STOP:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "IbI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'b', "blockIron");
                break;
            case DETECTOR_DIRECTION:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "IsI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        's', Blocks.stone_pressure_plate);
                break;
            case GATED:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "I#I",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.oak_fence_gate);
                break;
            case GATED_ONEWAY:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "G#G",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.oak_fence_gate,
                        'G', railAdvanced);
                break;
            case COUPLER:
                CraftingPlugin.addRecipe(output,
                        "IcI",
                        "I#I",
                        "IcI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'c', crowbar);
                break;
            case WHISTLE:
                CraftingPlugin.addRecipe(output,
                        "IyI",
                        "I#I",
                        "IbI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'y', "dyeYellow",
                        'b', "dyeBlack");
                break;
            case LOCOMOTIVE:
                CraftingPlugin.addRecipe(output,
                        "ILI",
                        "I#I",
                        "ILI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'L', RailcraftItems.signalLamp.getRecipeObject());
                break;
            case LIMITER:
                CraftingPlugin.addRecipe(output,
                        "IlI",
                        "I#I",
                        "IlI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'l', Items.repeater);
                break;
            case ROUTING:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.ticket);
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.ticketGold);
                break;
            case REINFORCED:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "I I",
                        'I', railReinforced,
                        '#', reinforcedRailbed);
                break;
            case REINFORCED_BOOSTER:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "IrI",
                        'I', railReinforced,
                        'r', "dustRedstone",
                        '#', reinforcedRailbed);
                break;
            case REINFORCED_JUNCTION:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "I#I",
                        "III",
                        'I', railReinforced,
                        '#', reinforcedRailbed);
                break;
            case REINFORCED_SWITCH:
                CraftingPlugin.addRecipe(output,
                        "I#I",
                        "III",
                        "III",
                        'I', railReinforced,
                        '#', reinforcedRailbed);
                break;
        }
        return output;
    }

}