/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.api.helpers.Helpers;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.util.sounds.SoundLimiterTicker;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.CapabilityCartBatterySetup;
import mods.railcraft.common.blocks.machine.MachineTileRegistry;
import mods.railcraft.common.blocks.machine.MultiBlockHelper;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.commands.CommandAdmin;
import mods.railcraft.common.commands.CommandDebug;
import mods.railcraft.common.commands.CommandTrack;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.RailcraftFluids;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.CrowbarHandler;
import mods.railcraft.common.items.EntityItemFireproof;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemRailbed.EnumRailbed;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.buildcraft.BuildcraftPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.crafting.*;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RailcraftModule("railcraft:core")
public class ModuleCore extends RailcraftModulePayload {

    public ModuleCore() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                LinkageManager.reset();
                CartToolsAPI.transferHelper = TrainTransferHelper.INSTANCE;

                Railcraft.rootCommand.addChildCommand(new CommandDebug());
                Railcraft.rootCommand.addChildCommand(new CommandAdmin());
                Railcraft.rootCommand.addChildCommand(new CommandTrack());

                RailcraftCraftingManager.cokeOven = new CokeOvenCraftingManager();
                RailcraftCraftingManager.blastFurnace = new BlastFurnaceCraftingManager();
                RailcraftCraftingManager.rockCrusher = new RockCrusherCraftingManager();
                RailcraftCraftingManager.rollingMachine = new RollingMachineCraftingManager();

                SignalTools.packetBuilder = PacketBuilder.instance();

                RailcraftFluids.preInitFluids();
                // TODO: do we need a bucket handler still?
//                MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
                MinecraftForge.EVENT_BUS.register(RailcraftDamageSource.EVENT_HANDLER);
                MinecraftForge.EVENT_BUS.register(LootPlugin.INSTANCE);

                Helpers.structures = new MultiBlockHelper();

                EntityItemFireproof.register();

                RecipeSorter.register("railcraft:rotor.repair", RotorRepairRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:locomotive.painting", LocomotivePaintingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:routing.table.copy", RoutingTableCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:routing.ticket.copy", RoutingTicketCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:cart.uncrafting", CartDisassemblyRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
                RecipeSorter.register("railcraft:cart.uncrafting.railcraft", CartDisassemblyRecipe.RailcraftVariant.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
                RecipeSorter.register("railcraft:prototype", PrototypeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

                OreDictPlugin.registerNewTags();

                add(
                        RailcraftItems.CROWBAR_IRON,
                        RailcraftItems.CROWBAR_STEEL,

                        RailcraftItems.AXE_STEEL,
                        RailcraftItems.HOE_STEEL,
                        RailcraftItems.PICKAXE_STEEL,
                        RailcraftItems.SHEARS_STEEL,
                        RailcraftItems.SHOVEL_STEEL,
                        RailcraftItems.SWORD_STEEL,

                        RailcraftItems.ARMOR_HELMET_STEEL,
                        RailcraftItems.ARMOR_CHESTPLATE_STEEL,
                        RailcraftItems.ARMOR_LEGGINGS_STEEL,
                        RailcraftItems.ARMOR_BOOTS_STEEL,

                        RailcraftItems.MAG_GLASS,
                        RailcraftItems.GOGGLES,
                        RailcraftItems.OVERALLS,
                        RailcraftItems.NOTEPAD,

                        RailcraftItems.RAIL,
                        RailcraftItems.TIE,
                        RailcraftItems.REBAR
                );
            }

            @Override
            public void preInit() {
                NetworkRegistry.INSTANCE.registerGuiHandler(Railcraft.getMod(), new GuiHandler());

                EntityEnderman.setCarriable(Blocks.GRAVEL, false);

                BuildcraftPlugin.init();

                CapabilityCartBatterySetup.register();

                MinecraftForge.EVENT_BUS.register(CrowbarHandler.instance());
                MinecraftForge.EVENT_BUS.register(MinecartHooks.getInstance());
                MinecraftForge.EVENT_BUS.register(LinkageHandler.getInstance());
                MinecraftForge.EVENT_BUS.register(new CraftingHandler());
                MinecraftForge.EVENT_BUS.register(new SoundLimiterTicker());
                MinecraftForge.EVENT_BUS.register(new MinecartRiderAIDisabler());
                MinecraftForge.EVENT_BUS.register(new ShuntingAuraTickHandler());
                MinecraftForge.EVENT_BUS.register(new Object() {
                    @SubscribeEvent
                    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
                        Entity riding = event.player.getRidingEntity();
                        if (riding instanceof EntityMinecart) {
                            EntityMinecart cart = (EntityMinecart) riding;
                            if (Train.getTrain(cart).size() > 1)
                                CartTools.removePassengers(cart, event.player.getPositionVector().addVector(0, 1, 0));
                        }
                    }
                });

                if (RailcraftConfig.useCollisionHandler()) {
                    //noinspection ConstantConditions
                    if (EntityMinecart.getCollisionHandler() != null)
                        Game.log(Level.WARN, "Existing Minecart Collision Handler detected, overwriting. Please check your configs to ensure this is desired behavior.");
                    EntityMinecart.setCollisionHandler(MinecartHooks.getInstance());
                }

                Set<Item> testSet = new HashSet<Item>();
                if (!RailcraftConfig.vanillaTrackRecipes()) {
                    testSet.add(Item.getItemFromBlock(Blocks.RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.GOLDEN_RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.DETECTOR_RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.ACTIVATOR_RAIL));
                }

                if (!RailcraftConfig.getRecipeConfig("railcraft.cart.vanilla.furnace"))
                    testSet.add(Items.FURNACE_MINECART);

//        MiscTools.addShapelessRecipe(new ItemStack(Item.coal, 20), Block.dirt);
                Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
                while (it.hasNext()) {
                    IRecipe r = (IRecipe) it.next();
                    ItemStack output = InvTools.emptyStack();
                    try {
                        output = r.getRecipeOutput();
                    } catch (Exception ignored) {
                    }
                    if (!InvTools.isEmpty(output))
                        if (testSet.contains(output.getItem()))
                            it.remove();
                }

                // Items
                replaceVanillaCart(RailcraftCarts.COMMAND_BLOCK, Items.COMMAND_BLOCK_MINECART, EntityMinecart.Type.COMMAND_BLOCK, 40);
                replaceVanillaCart(RailcraftCarts.BASIC, Items.MINECART, EntityMinecart.Type.RIDEABLE, 42);
                replaceVanillaCart(RailcraftCarts.CHEST, Items.CHEST_MINECART, EntityMinecart.Type.CHEST, 43);
                replaceVanillaCart(RailcraftCarts.FURNACE, Items.FURNACE_MINECART, EntityMinecart.Type.FURNACE, 44);
                replaceVanillaCart(RailcraftCarts.TNT, Items.TNT_MINECART, EntityMinecart.Type.TNT, 45);
                replaceVanillaCart(RailcraftCarts.HOPPER, Items.HOPPER_MINECART, EntityMinecart.Type.HOPPER, 46);

                float h = TrackConstants.HARDNESS;
                Blocks.RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.GOLDEN_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.DETECTOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.ACTIVATOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);

                // Define Recipes
                if (RailcraftConfig.getRecipeConfig("railcraft.cart.bronze")) {
                    IRecipe recipe = new ShapedOreRecipe(new ItemStack(Items.MINECART), false,
                            "I I",
                            "III",
                            'I', "ingotBronze");
                    CraftingPlugin.addRecipe(recipe);
                }

                if (RailcraftConfig.getRecipeConfig("railcraft.cart.steel")) {
                    IRecipe recipe = new ShapedOreRecipe(new ItemStack(Items.MINECART, 2), false,
                            "I I",
                            "III",
                            'I', "ingotSteel");
                    CraftingPlugin.addRecipe(recipe);
                }

                // Old rails
                if (!RailcraftConfig.vanillaTrackRecipes()) {
                    ItemStack stackRailNormal = new ItemStack(Blocks.RAIL, 32);
                    ItemStack stackRailBooster = new ItemStack(Blocks.GOLDEN_RAIL, 16);
                    ItemStack stackRailDetector = new ItemStack(Blocks.DETECTOR_RAIL, 16);
                    ItemStack stackRailActivator = new ItemStack(Blocks.ACTIVATOR_RAIL, 16);

                    Object woodRailbed = RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.WOOD);
                    CraftingPlugin.addRecipe(stackRailNormal,
                            "I I",
                            "I#I",
                            "I I",
                            'I', RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD),
                            '#', woodRailbed);
                    CraftingPlugin.addRecipe(stackRailBooster,
                            "I I",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getRecipeObject(EnumRail.ADVANCED),
                            '#', woodRailbed,
                            'r', "dustRedstone");
                    CraftingPlugin.addRecipe(stackRailDetector,
                            "IsI",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD),
                            '#', Blocks.STONE_PRESSURE_PLATE,
                            'r', "dustRedstone",
                            's', woodRailbed);
                    CraftingPlugin.addRecipe(stackRailActivator,
                            "ItI",
                            "I#I",
                            "ItI",
                            'I', RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD),
                            '#', woodRailbed,
                            't', new ItemStack(Blocks.REDSTONE_TORCH));

                    CraftingPlugin.addShapelessRecipe(RailcraftItems.RAIL.getStack(1, EnumRail.STANDARD),
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL);
                }

                MachineTileRegistry.registerTileEntities();
            }

            private void replaceVanillaCart(RailcraftCarts cartType, Item original, EntityMinecart.Type minecartType, int entityId) {
                cartType.register();

                Class<? extends Entity> minecartClass = EntityList.NAME_TO_CLASS.remove(minecartType.getName());

                CartTools.classReplacements.put(minecartClass, cartType);
                CartTools.vanillaCartItemMap.put(original, cartType);

                EntityList.ID_TO_CLASS.remove(entityId);
                EntityList.addMapping(cartType.getCartClass(), minecartType.getName(), entityId);

                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(original, new BehaviorDefaultDispenseItem());

                original.setMaxStackSize(RailcraftConfig.getMinecartStackSize());
                original.setCreativeTab(CreativeTabs.TRANSPORTATION);
            }

            @Override
            public void init() {

                // Finish initializing ItemRegistry
                //TODO: this is obsolete?
//                for (EnumWayObject type : EnumWayObject.values()) {
//                    if (type.isEnabled())
//                        RailcraftRegistry.register(type.getItem());
//                }
//
//                for (EnumGeneric type : EnumGeneric.values()) {
//                    if (type.isEnabled())
//                        RailcraftRegistry.register(type.getStack());
//                }
//
//                Set<IEnumMachine> machines = new HashSet<IEnumMachine>();
//                machines.addAll(EnumSet.allOf(EnumMachineAlpha.class));
//                machines.addAll(EnumSet.allOf(EnumMachineBeta.class));
//                machines.addAll(EnumSet.allOf(EnumMachineGamma.class));
//                machines.addAll(EnumSet.allOf(EnumMachineDelta.class));
//                machines.addAll(EnumSet.allOf(EnumMachineEpsilon.class));
//
//                for (IEnumMachine machine : machines) {
//                    if (machine.isAvailable())
//                        RailcraftRegistry.register(machine.getItem());
//                }
            }

            @Override
            public void postInit() {
                RailcraftFluids.finalizeDefinitions();
                RailcraftBlocks.finalizeDefinitions();
                RailcraftItems.finalizeDefinitions();
                RailcraftCarts.finalizeDefinitions();

                GameRegistry.registerFuelHandler(FuelPlugin.getFuelHandler());

                addLiquidFuels();

                // TODO: restore this when you figure out the new fluid containers, with a config
//                FluidTools.nerfWaterBottle();

//----------------------------------------------
// Boiler Test Setup
// ---------------------------------------------
//        StandardTank tankWater = new StandardTank(FluidTools.BUCKET_VOLUME * 1000);
//        StandardTank tankSteam = new StandardTank(FluidTools.BUCKET_VOLUME * 1000);
//        tankWater.setFluid(Fluids.WATER.get(tankWater.getCapacity()));
//        SteamBoiler boiler = new SteamBoiler(tankWater, tankSteam);
//        class TestProvider implements IFuelProvider {
//
//            public int fuel = 3200;
//
//            @Override
//            public double getMoreFuel() {
//                if (fuel > 0) {
//                    fuel--;
//                    return 1;
//                }
//                return 0;
//            }
//
//            @Override
//            public double getHeatStep() {
//                return Steam.HEAT_STEP;
//            }
//
//        }
//        TestProvider provider = new TestProvider();
//        boiler.setFuelProvider(provider);
//        int ticks = 0;
//        while (provider.fuel > 0 || boiler.burnTime > boiler.getFuelPerCycle(1) || boiler.getHeat() > 20) {
//            boiler.tick(1);
//            ticks++;
//        }
//        System.out.printf("Ran for %d ticks.%n", ticks);
//        System.out.printf("Steam Produced=%s%n", tankSteam.getFluidAmount());
//        System.exit(0);
            }

            private void addLiquidFuels() {
                int bioHeat = (int) (16000 * RailcraftConfig.boilerBiofuelMultiplier());
                Fluid ethanol = Fluids.BIOETHANOL.get();
                if (ethanol != null)
                    FuelManager.addBoilerFuel(ethanol, bioHeat); // Biofuel

                Fluid biofuel = Fluids.BIOFUEL.get();
                if (biofuel != null)
                    FuelManager.addBoilerFuel(biofuel, bioHeat); // Biofuel

                Fluid fuel = Fluids.FUEL.get();
                if (fuel != null)
                    FuelManager.addBoilerFuel(fuel, (int) (48000 * RailcraftConfig.boilerFuelMultiplier())); // Fuel

                Fluid coal = Fluids.COAL.get();
                if (coal != null)
                    FuelManager.addBoilerFuel(coal, (int) (32000 * RailcraftConfig.boilerFuelMultiplier())); // Liquefaction Coal

                Fluid pyrotheum = Fluids.PYROTHEUM.get();
                if (pyrotheum != null)
                    FuelManager.addBoilerFuel(pyrotheum, (int) (64000 * RailcraftConfig.boilerFuelMultiplier())); // Blazing Pyrotheum

                Fluid creosote = Fluids.CREOSOTE.get();
                if (creosote != null)
                    FuelManager.addBoilerFuel(creosote, 4800); // Creosote
            }
        });
    }

}
