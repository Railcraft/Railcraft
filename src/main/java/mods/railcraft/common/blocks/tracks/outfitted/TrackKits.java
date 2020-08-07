/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted;

import com.google.common.collect.ObjectArrays;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.kits.*;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleTracks;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum TrackKits implements IRailcraftObjectContainer<IRailcraftObject<TrackKit>> {

    ACTIVATOR(2, "activator", 8, TrackKitActivator.class, () -> recipe(Items.REDSTONE, Items.REDSTONE)),
    BOOSTER(2, "booster", 16, TrackKitBooster.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    BUFFER_STOP(2, "buffer", 2, TrackBufferStop.class, () -> recipe("ingotIron", "ingotIron")),
    CONTROL(2, "control", 16, TrackKitControl.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    COUPLER(6, "coupler", 4, TrackKitCoupler.class, () -> recipe(Items.LEAD, Items.REDSTONE)),
    DETECTOR(6, "detector", 8, TrackKitDetector.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Items.REDSTONE)),
    DISEMBARK(4, "disembarking", 4, TrackKitDisembark.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Items.LEAD, Items.REDSTONE)),
    DUMPING(2, "dumping", 4, TrackKitDumping.class, () -> recipe(RailcraftItems.PLATE, Metal.STEEL, Items.REDSTONE)),
    EMBARKING(2, "embarking", 4, TrackKitEmbarking.class, () -> recipe(Items.ENDER_PEARL, Items.LEAD, Items.REDSTONE)),
    GATED(8, "gated", 4, TrackKitGated.class, () -> recipe("fenceGateWood", RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    HIGH_SPEED_TRANSITION(4, "transition", 8, TrackKitSpeedTransition.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE, Items.REDSTONE)),
    LAUNCHER(2, "launcher", 1, TrackKitLauncher.class, () -> recipe(Blocks.PISTON, "blockSteel", "blockSteel", Items.REDSTONE)) {
        {
            conditions().add(ModuleTracks.class);
        }
    },
    THROTTLE(16, "throttle", 4, TrackKitThrottle.class, () -> recipe("dyeYellow", "dyeBlack", Items.REDSTONE)),
    LOCKING(16, "locking", 4, TrackKitLocking.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Blocks.STICKY_PISTON, Items.REDSTONE)),
    DELAYED(3, "delayed", 4, TrackKitDelayedLocking.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Blocks.STICKY_PISTON, Items.REPEATER)),
    LOCOMOTIVE(6, "locomotive", 4, TrackKitLocomotive.class, () -> recipe(RailcraftItems.SIGNAL_LAMP, Items.REDSTONE)),
    ONE_WAY(4, "one_way", 8, TrackKitOneWay.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Blocks.PISTON, Items.REDSTONE)),
    PRIMING(2, "priming", 2, TrackKitPriming.class, () -> recipe(Items.FLINT_AND_STEEL, Items.REDSTONE)) {
        {
            conditions().add(ModuleTracks.class);
        }
    },
    ROUTING(2, "routing", 8, TrackKitRouting.class, () -> recipes(craft(RailcraftItems.TICKET, Items.REDSTONE), craft(RailcraftItems.TICKET_GOLD, Items.REDSTONE))),
    WHISTLE(2, "whistle", 8, TrackKitWhistle.class, () -> recipe("dyeYellow", "dyeBlack", Blocks.NOTEBLOCK, Items.REDSTONE)),
    JUNCTION(1, "junction", 8, TrackKitJunction.class),
    TURNOUT(8, "turnout", 8, TrackKitSwitchTurnout.class),
    WYE(4, "wye", 8, TrackKitSwitchWye.class),
    MESSENGER(2, "messenger", 4, TrackKitMessenger.class, () -> recipe(Items.SIGN, Items.REDSTONE)),
    ;

    public static final TrackKits[] VALUES = values();
    private static final Set<TrackKit> TRACK_KITS = new HashSet<>(50);
    private static final Predicate<TrackType> IS_HIGH_SPEED = TrackType::isHighSpeed;
    private static final Predicate<TrackType> NOT_HIGH_SPEED = IS_HIGH_SPEED.negate();

    static {
        TRACK_KITS.add(TrackRegistry.getMissingTrackKit());

        DETECTOR.requiresTicks = true;
        LOCKING.requiresTicks = true;
        DELAYED.requiresTicks = true;
        TURNOUT.requiresTicks = true;
        WYE.requiresTicks = true;

        ACTIVATOR.allowedOnSlopes = true;
        BOOSTER.allowedOnSlopes = true;
        CONTROL.allowedOnSlopes = true;
        COUPLER.allowedOnSlopes = true;
        DETECTOR.allowedOnSlopes = true;
        HIGH_SPEED_TRANSITION.allowedOnSlopes = true;
        LOCOMOTIVE.allowedOnSlopes = true;
        PRIMING.allowedOnSlopes = true;

        DUMPING.trackTypeFilter = NOT_HIGH_SPEED;
        GATED.trackTypeFilter = NOT_HIGH_SPEED;
        ONE_WAY.trackTypeFilter = NOT_HIGH_SPEED;
        LAUNCHER.trackTypeFilter = NOT_HIGH_SPEED;
        COUPLER.trackTypeFilter = NOT_HIGH_SPEED;
        CONTROL.trackTypeFilter = NOT_HIGH_SPEED;
        BUFFER_STOP.trackTypeFilter = NOT_HIGH_SPEED;
        EMBARKING.trackTypeFilter = NOT_HIGH_SPEED;
        DISEMBARK.trackTypeFilter = NOT_HIGH_SPEED;
        ROUTING.trackTypeFilter = NOT_HIGH_SPEED;
        HIGH_SPEED_TRANSITION.trackTypeFilter = IS_HIGH_SPEED;

        JUNCTION.renderer = TrackKit.Renderer.UNIFIED;
        JUNCTION.visible = false;

        TURNOUT.renderer = TrackKit.Renderer.UNIFIED;
        TURNOUT.visible = false;

        WYE.renderer = TrackKit.Renderer.UNIFIED;
        WYE.visible = false;

        DUMPING.maxSupportDistance = 2;
    }

    public final int recipeOutput;
    private final int states;
    private final Class<? extends TrackKitRailcraft> trackInstance;
    private final Supplier<List<Object[]>> recipeSupplier;
    private final SimpleDef def;
    private TrackKit trackKit;
    private boolean visible = true;
    private boolean allowedOnSlopes;
    private boolean requiresTicks;
    private int maxSupportDistance;
    private Predicate<TrackType> trackTypeFilter = (t) -> true;
    private TrackKit.Renderer renderer = TrackKit.Renderer.COMPOSITE;

    TrackKits(int states, String tag, int recipeOutput, Class<? extends TrackKitRailcraft> trackInstance) {
        this(states, tag, recipeOutput, trackInstance, Collections::emptyList);
    }

    TrackKits(int states, String tag, int recipeOutput, Class<? extends TrackKitRailcraft> trackInstance, Supplier<List<Object[]>> recipeSupplier) {
        this.states = states;
        this.def = new SimpleDef(this, tag);
        this.recipeOutput = recipeOutput;
        this.trackInstance = trackInstance;
        this.recipeSupplier = recipeSupplier;
    }

    @Override
    public SimpleDef getDef() {
        return def;
    }

    public static TrackKits fromId(int id) {
        if (id < 0 || id >= TrackKits.values().length)
            id = 0;
        return TrackKits.values()[id];
    }

    public static Object[] craft(Object... recipe) {
        return recipe;
    }

    public static List<Object[]> recipes(Object[]... recipes) {
        return Arrays.asList(recipes);
    }

    public static List<Object[]> recipe(Object... recipe) {
        List<Object[]> list = new ArrayList<>();
        list.add(recipe);
        return list;
    }

    public static Collection<TrackKit> getRailcraftTrackKits() {
        return TRACK_KITS;
    }

    @Override
    public void register() {
        if (!RailcraftItems.TRACK_KIT.isLoaded() || !RailcraftModuleManager.isModuleEnabled(ModuleTracks.class))
            return;
        //TODO: Add way to disable track kits
        if (trackKit == null) {
            trackKit = new TrackKit.Builder(getRegistryName(), trackInstance)
                    .setRequiresTicks(requiresTicks)
                    .setRenderer(renderer)
                    .setRenderStates(states)
                    .setVisible(visible)
                    .setAllowedOnSlopes(allowedOnSlopes)
                    .setTrackTypeFilter(trackTypeFilter)
                    .setMaxSupportDistance(maxSupportDistance)
                    .build();
            try {
                TrackRegistry.TRACK_KIT.register(trackKit);
                TRACK_KITS.add(trackKit);
            } catch (Exception error) {
                Game.log().api(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKit.class);
            }
        }
    }

    @Override
    public void defineRecipes() {
        if (!RailcraftItems.TRACK_KIT.isLoaded() || !RailcraftModuleManager.isModuleEnabled(ModuleTracks.class))
            return;
        List<Object[]> recipes = recipeSupplier.get();
        if (recipes != null) {
            recipes.stream().filter(ArrayUtils::isNotEmpty).forEach(recipe -> {
                Object[] commonIngredients = {"plankWood", RailcraftItems.TRACK_PARTS};
                Object[] finalRecipe = ObjectArrays.concat(commonIngredients, recipe, Object.class);
                CraftingPlugin.addShapelessRecipe(trackKit.getTrackKitItem(recipeOutput), finalRecipe);
            });
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return stack.getItem() instanceof ItemTrackKit && TrackRegistry.TRACK_KIT.get(stack) == getTrackKit();
    }

    @Override
    public boolean isEnabled() {
        // TODO: Convert to conditionals
        return RailcraftModuleManager.isModuleEnabled(ModuleTracks.class) && IRailcraftObjectContainer.super.isEnabled() && RailcraftBlocks.TRACK_OUTFITTED.isEnabled() && RailcraftItems.TRACK_KIT.isEnabled() && RailcraftConfig.isSubBlockEnabled(getRegistryName()) && !isDeprecated();
    }

    @Override
    public boolean isLoaded() {
        return trackKit != null && isEnabled() && RailcraftBlocks.TRACK_OUTFITTED.isLoaded() && RailcraftItems.TRACK_KIT.isLoaded();
    }

    public boolean isDeprecated() {
        try {
            return getClass().getField(name()).isAnnotationPresent(Deprecated.class);
        } catch (NoSuchFieldException ignored) {
        }
        return false;
    }

    @Override
    public ItemStack getStack() {
        return getStack(1);
    }

    @Override
    public ItemStack getStack(int qty) {
        if (trackKit != null)
            return RailcraftItems.TRACK_KIT.getStack(qty, getTrackKit());
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<IRailcraftObject<TrackKit>> getObject() {
        return Optional.empty();
    }

    public TrackKit getTrackKit() {
        return trackKit;
    }
    /*
    @Nullable
    private ItemStack registerRecipe() {
        if (getStack() == null)
            return null;
        ItemStack output = getStack(recipeOutput * 2);
        Object railWood = RailcraftConfig.vanillaTrackRecipes() ? "slabWood" : RailcraftItems.RAIL.getRecipeObject(EnumRail.WOOD);
        Object railStandard = RailcraftConfig.vanillaTrackRecipes() ? new ItemStack(Items.IRON_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD);
        Object railAdvanced = RailcraftConfig.vanillaTrackRecipes() ? new ItemStack(Items.GOLD_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.ADVANCED);
        Object railSpeed = RailcraftConfig.vanillaTrackRecipes() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.SPEED);
        Object railReinforced = RailcraftConfig.vanillaTrackRecipes() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.REINFORCED);
        Object railElectric = RailcraftConfig.vanillaTrackRecipes() ? "ingotCopper" : RailcraftItems.RAIL.getRecipeObject(EnumRail.ELECTRIC);
        Object woodTie = RailcraftItems.TIE.getRecipeObject(EnumTie.WOOD);
        Object woodRailbed = RailcraftConfig.vanillaTrackRecipes() ? "stickWood" : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.WOOD);
        Object stoneRailbed = RailcraftConfig.vanillaTrackRecipes() ? Blocks.STONE_SLAB : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.STONE);
        Object reinforcedRailbed = RailcraftConfig.vanillaTrackRecipes() || !RailcraftItems.RAIL.isEnabled() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? new ItemStack(Blocks.OBSIDIAN) : stoneRailbed;
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
            case ONE_WAY:
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
//            case SPEED_BOOST:
//                CraftingPlugin.addRecipe(output,
//                        "IrI",
//                        "IsI",
//                        "IrI",
//                        'I', railSpeed,
//                        's', stoneRailbed,
//                        'r', "dustRedstone");
//                break;
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
//            case JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
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
//            case SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
//            case WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
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
//            case SUSPENDED:
//                CraftingPlugin.addRecipe(output,
//                        "ItI",
//                        "ItI",
//                        "ItI",
//                        'I', railStandard,
//                        't', woodTie);
//                break;
            case DUMPING:
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
//            case DETECTOR_TRAVEL:
//                CraftingPlugin.addRecipe(output,
//                        "IrI",
//                        "I#I",
//                        "IsI",
//                        'I', railStandard,
//                        '#', woodRailbed,
//                        'r', "dustRedstone",
//                        's', Blocks.STONE_PRESSURE_PLATE);
//                break;
            case GATED:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "I#I",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.OAK_FENCE_GATE);
                break;
//            case GATED_ONE_WAY:
//                CraftingPlugin.addRecipe(output,
//                        "IgI",
//                        "G#G",
//                        "IgI",
//                        'I', railStandard,
//                        '#', woodRailbed,
//                        'g', Blocks.OAK_FENCE_GATE,
//                        'G', railAdvanced);
//                break;
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
            case THROTTLE:
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
    */

}
