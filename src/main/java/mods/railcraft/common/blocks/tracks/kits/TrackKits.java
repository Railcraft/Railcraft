/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.tracks.kits.variants.*;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemRailbed.EnumRailbed;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public enum TrackKits implements IRailcraftObjectContainer {

    ONEWAY(ModuleTracks.class, 4, 1, "oneway", 8, TrackKitOneWay.class),
    CONTROL(ModuleTracks.class, 2, 0, "control", 16, TrackControl.class),
    LAUNCHER(ModuleExtras.class, 2, 1, "launcher", 1, TrackKitLauncher.class),
    PRIMING(ModuleExtras.class, 2, 1, "priming", 8, TrackKitPriming.class),
    JUNCTION(ModuleTracks.class, 1, 0, "junction", 8, TrackJunction.class),
    SWITCH(ModuleSignals.class, 4, 0, "switch", 8, TrackSwitch.class),
    DISEMBARK(ModuleTracks.class, 4, 1, "disembarking", 8, TrackKitDisembark.class),
    SUSPENDED(ModuleExtras.class, 1, 0, "suspended", 8, TrackSuspended.class),
    GATED_ONEWAY(ModuleTracks.class, 2, 0, "gated.oneway", 4, TrackKitGatedOneWay.class),
    GATED(ModuleTracks.class, 1, 0, "gated", 4, TrackKitGated.class),
    BOOSTER(ModuleTracksWood.class, 2, 1, "slow.boost", 8, TrackKitBooster.class),
    SPEED_BOOST(ModuleTracksHighSpeed.class, 2, 1, "speed.boost", 8, TrackSpeedBoost.class),
    HIGH_SPEED_TRANSITION(ModuleTracksHighSpeed.class, 4, 1, "speed.transition", 8, TrackSpeedTransition.class),
    COUPLER(ModuleTracks.class, 6, 1, "coupler", 8, TrackKitCoupler.class),
    DECOUPLER(ModuleTracks.class, 0, 0, "decoupler", 8, TrackKitCoupler.class),
    BUFFER_STOP(ModuleTracks.class, 2, 0, "buffer.stop", 8, TrackBufferStop.class),
    DISPOSAL(ModuleTracks.class, 2, 0, "disposal", 8, TrackDisposal.class),
    DETECTOR_DIRECTION(ModuleTracks.class, 4, 0, "detector.direction", 8, TrackKitDetectorDirection.class),
    EMBARKING(ModuleTracks.class, 2, 1, "embarking", 8, TrackKitEmbarking.class),
    WYE(ModuleTracks.class, 2, 0, "wye", 8, TrackKitWye.class),
    WHISTLE(ModuleLocomotives.class, 2, 1, "whistle", 8, TrackKitWhistle.class),
    LOCOMOTIVE(ModuleLocomotives.class, 6, 3, "locomotive", 8, TrackKitLocomotive.class),
    LIMITER(ModuleLocomotives.class, 6, 5, "limiter", 8, TrackKitLimiter.class),
    ROUTING(ModuleRouting.class, 2, 1, "routing", 8, TrackKitRouting.class),
    LOCKING(ModuleTracks.class, 16, 1, "locking", 8, TrackKitLocking.class),
    FORCE(ModuleElectricity.class, 1, 0, "force", 1, TrackForce.class);
    public static final TrackKits[] VALUES = values();
    private static final List<TrackKits> creativeList = new ArrayList<TrackKits>(50);
    private static final Set<TrackKit> TRACK_KITS = new HashSet<TrackKit>(50);
    private static final Function<ItemStack, List<String>> tooltipProvider = stack -> {
        ToolTip toolTip = ToolTip.buildToolTip(stack.getUnlocalizedName() + ".tip");
        return toolTip != null ? toolTip.convertToStrings() : Collections.emptyList();
    };

    static {
        TRACK_KITS.add(TrackRegistry.getDefaultTrackKit());

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
        creativeList.add(BOOSTER);
        creativeList.add(SPEED_BOOST);
        creativeList.add(HIGH_SPEED_TRANSITION);
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
    private final Class<? extends TrackKitRailcraft> trackInstance;
    private TrackKit trackKit;
    private boolean depreciated;

    TrackKits(Class<? extends IRailcraftModule> module, int numIcons, int itemIconIndex, String tag, int recipeOutput, Class<? extends TrackKitRailcraft> trackInstance) {
        this.module = module;
        this.numIcons = numIcons;
        this.itemIconIndex = itemIconIndex;
        this.tag = tag;
        this.recipeOutput = recipeOutput;
        this.trackInstance = trackInstance;
    }

    public static TrackKits fromId(int id) {
        if (id < 0 || id >= TrackKits.values().length)
            id = 0;
        return TrackKits.values()[id];
    }

    public static List<TrackKits> getCreativeList() {
        return creativeList;
    }

    public static Collection<TrackKit> getRailcraftTrackKits() {
        return TRACK_KITS;
    }

    @Override
    public void register() {
        if (trackKit == null && RailcraftBlocks.TRACK.isLoaded() && RailcraftConfig.isSubBlockEnabled(getTag())) {
            trackKit = new TrackKit(getTag(), /* TODO: create a ModelResourceLocation */ null, trackInstance);
            try {
                TrackRegistry.registerTrackKit(trackKit);
                TRACK_KITS.add(trackKit);
                registerRecipe();
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKit.class);
            }
            //TODO: Should we register outfitted track items?
//            ItemStack stack = getTrackKitSpec().getItem();
//            RailcraftRegistry.register(stack);
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return stack.getItem() instanceof ItemTrackKit && ((ItemTrackKit) stack.getItem()).getTrackKit(stack) == getTrackKit();
    }

    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(module) && RailcraftBlocks.TRACK_OUTFITTED.isEnabled() && RailcraftItems.TRACK_KIT.isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag()) && !isDepreciated();
    }

    @Override
    public boolean isLoaded() {
        return trackKit != null && isEnabled() && RailcraftBlocks.TRACK_OUTFITTED.isLoaded() && RailcraftItems.TRACK_KIT.isLoaded();
    }

    public boolean isDepreciated() {
        return depreciated;
    }

    @Override
    @Nullable
    public ItemStack getStack() {
        return getStack(1);
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty) {
        if (trackKit != null)
            return RailcraftItems.TRACK_KIT.getStack(qty, getTrackKit());
        return null;
    }

    @Nullable
    @Override
    public IRailcraftObject getObject() {
        return null;
    }

    public TrackKit getTrackKit() {
        return trackKit;
    }

    public String getTextureTag() {
        return "railcraft:tracks/track." + tag;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    public String getTag() {
        return "railcraft:" + getBaseTag();
    }

    public int getNumIcons() {
        return numIcons;
    }

    public int getItemIconIndex() {
        return itemIconIndex;
    }

    @Nullable
    private ItemStack registerRecipe() {
        if (getStack() == null)
            return null;
        ItemStack output = getStack(recipeOutput * 2);
        Object railWood = RailcraftConfig.useOldRecipes() ? "slabWood" : RailcraftItems.RAIL.getRecipeObject(EnumRail.WOOD);
        Object railStandard = RailcraftConfig.useOldRecipes() ? new ItemStack(Items.IRON_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD);
        Object railAdvanced = RailcraftConfig.useOldRecipes() ? new ItemStack(Items.GOLD_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.ADVANCED);
        Object railSpeed = RailcraftConfig.useOldRecipes() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.SPEED);
        Object railReinforced = RailcraftConfig.useOldRecipes() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.REINFORCED);
        Object railElectric = RailcraftConfig.useOldRecipes() ? "ingotCopper" : RailcraftItems.RAIL.getRecipeObject(EnumRail.ELECTRIC);
        Object woodTie = RailcraftItems.TIE.getRecipeObject(EnumTie.WOOD);
        Object woodRailbed = RailcraftConfig.useOldRecipes() ? "stickWood" : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.WOOD);
        Object stoneRailbed = RailcraftConfig.useOldRecipes() ? Blocks.STONE_SLAB : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.STONE);
        Object reinforcedRailbed = RailcraftConfig.useOldRecipes() || !RailcraftItems.RAIL.isEnabled() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? new ItemStack(Blocks.OBSIDIAN) : stoneRailbed;

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
                        'b', Blocks.STONE_PRESSURE_PLATE);
                break;
            case ONEWAY:
                CraftingPlugin.addRecipe(output,
                        "IbI",
                        "IsI",
                        "IpI",
                        'I', railStandard,
                        's', woodRailbed,
                        'b', Blocks.STONE_PRESSURE_PLATE,
                        'p', Blocks.PISTON);
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
//            case SPEED:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "IsI",
//                        "I I",
//                        'I', railSpeed,
//                        's', stoneRailbed);
//                break;
            case SPEED_BOOST:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IsI",
                        "IrI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                break;
            case HIGH_SPEED_TRANSITION:
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
//            case SPEED_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "IsI",
//                        "III",
//                        "III",
//                        'I', railSpeed,
//                        's', stoneRailbed);
//                break;
            case LAUNCHER:
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "BPB",
                        "IsI",
                        'I', railReinforced,
                        'B', "blockSteel",
                        's', stoneRailbed,
                        'P', Blocks.PISTON);
                break;
            case PRIMING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "IsI",
                        "IfI",
                        'I', railReinforced,
                        's', stoneRailbed,
                        'p', Blocks.STONE_PRESSURE_PLATE,
                        'f', Items.FLINT_AND_STEEL);
                break;
            case JUNCTION:
                CraftingPlugin.addRecipe(output,
                        "III",
                        "I#I",
                        "III",
                        'I', railStandard,
                        '#', woodRailbed);
                break;
//            case SLOW:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
            case BOOSTER:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "G#G",
                        "IrI",
                        'G', Items.GOLD_INGOT,
                        'I', railWood,
                        '#', woodRailbed,
                        'r', "dustRedstone");
                break;
//            case SLOW_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case SLOW_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case ELECTRIC:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
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
//            case SLOW_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case REINFORCED_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case SPEED_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railSpeed,
//                        '#', stoneRailbed);
//                break;
            case DISEMBARK:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IrI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        'p', Blocks.STONE_PRESSURE_PLATE);
                break;
            case EMBARKING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IpI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'p', Items.ENDER_PEARL);
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
                        'P', RailcraftItems.PLATE, Metal.STEEL,
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
                        's', Blocks.STONE_PRESSURE_PLATE);
                break;
            case GATED:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "I#I",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.OAK_FENCE_GATE);
                break;
            case GATED_ONEWAY:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "G#G",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.OAK_FENCE_GATE,
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
                        'L', RailcraftItems.SIGNAL_LAMP.getRecipeObject());
                break;
            case LIMITER:
                CraftingPlugin.addRecipe(output,
                        "IlI",
                        "I#I",
                        "IlI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'l', Items.REPEATER);
                break;
            case ROUTING:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.TICKET);
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.TICKET_GOLD);
                break;
//            case REINFORCED:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_BOOSTER:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "IrI",
//                        'I', railReinforced,
//                        'r', "dustRedstone",
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
        }
        return output;
    }

}