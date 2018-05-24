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
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.CraftingApiAccess;
import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.api.helpers.Helpers;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.util.sounds.SoundLimiterTicker;
import mods.railcraft.common.blocks.charge.CapabilityCartBatterySetup;
import mods.railcraft.common.blocks.machine.MachineTileRegistry;
import mods.railcraft.common.blocks.multi.MultiBlockHelper;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.commands.*;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.CustomContainerHandler;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.RailcraftFluids;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.*;
import mods.railcraft.common.plugins.buildcraft.BuildcraftPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
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
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@RailcraftModule("railcraft:core")
public class ModuleCore extends RailcraftModulePayload {

    public ModuleCore() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                LinkageManager.reset();
                CartToolsAPI.transferHelper = TrainTransferHelper.INSTANCE;

                Railcraft.ROOT_COMMAND.addChildCommand(new CommandDebug());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandAdmin());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandTrack());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandTile());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandCrafting());

                CraftingApiAccess.initialize();

                SignalTools.packetBuilder = PacketBuilder.instance();

                RailcraftFluids.preInitFluids();
                MinecraftForge.EVENT_BUS.register(CustomContainerHandler.INSTANCE);
                MinecraftForge.EVENT_BUS.register(RailcraftDamageSource.EVENT_HANDLER);
                LootPlugin.INSTANCE.init();

                Helpers.structures = new MultiBlockHelper();

                EntityItemFireproof.register();

                //TODO move all these mess
                RecipeSorter.register("railcraft:rotor.repair", RotorRepairRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:locomotive.painting", LocomotivePaintingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:routing.table.copy", RoutingTableCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:routing.ticket.copy", RoutingTicketCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:cart.uncrafting", CartDisassemblyRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
                RecipeSorter.register("railcraft:cart.uncrafting.railcraft", CartDisassemblyRecipe.RailcraftVariant.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
                RecipeSorter.register("railcraft:prototype", PrototypeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
                RecipeSorter.register("railcraft:fluid.shaped", ShapedFluidRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
                RecipeSorter.register("railcraft:fluid.shapeless", ShapelessFluidRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

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

                Set<Item> testSet = new HashSet<>();
                if (!RailcraftConfig.vanillaTrackRecipes()) {
                    testSet.add(Item.getItemFromBlock(Blocks.RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.GOLDEN_RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.DETECTOR_RAIL));
                    testSet.add(Item.getItemFromBlock(Blocks.ACTIVATOR_RAIL));
                }

                if (!RailcraftConfig.getRecipeConfig("railcraft.cart.vanilla.furnace"))
                    testSet.add(Items.FURNACE_MINECART);

                IForgeRegistry<IRecipe> registry = ForgeRegistries.RECIPES;
                Collection<ResourceLocation> toRemove = new ArrayList<>();
                for (IRecipe each : registry) {
                    ItemStack output = InvTools.emptyStack();
                    try {
                        output = each.getRecipeOutput();
                    } catch (Exception ignored) {
                    }
                    if (!InvTools.isEmpty(output) && testSet.contains(output.getItem())) {
                        toRemove.add(each.getRegistryName());
                    }
                }

                for (ResourceLocation each : toRemove) {
                    registry.register(CraftingPlugin.disabledRecipe(each));
                }

//                register(40, "commandblock_minecart", EntityMinecartCommandBlock.class, EntityMinecart.Type.COMMAND_BLOCK.getName());
//                register(41, "boat", EntityBoat.class, "Boat");
//                register(42, "minecart", EntityMinecartEmpty.class, EntityMinecart.Type.RIDEABLE.getName());
//                register(43, "chest_minecart", EntityMinecartChest.class, EntityMinecart.Type.CHEST.getName());
//                register(44, "furnace_minecart", EntityMinecartFurnace.class, EntityMinecart.Type.FURNACE.getName());
//                register(45, "tnt_minecart", EntityMinecartTNT.class, EntityMinecart.Type.TNT.getName());
//                register(46, "hopper_minecart", EntityMinecartHopper.class, EntityMinecart.Type.HOPPER.getName());
//                register(47, "spawner_minecart", EntityMinecartMobSpawner.class, EntityMinecart.Type.SPAWNER.getName());

                Map<EntityMinecart.Type, ResourceLocation> names = new EnumMap<>(EntityMinecart.Type.class);
                names.put(EntityMinecart.Type.RIDEABLE, new ResourceLocation("minecart"));
                names.put(EntityMinecart.Type.COMMAND_BLOCK, new ResourceLocation("commandblock_minecart"));
                names.put(EntityMinecart.Type.CHEST, new ResourceLocation("chest_minecart"));
                names.put(EntityMinecart.Type.FURNACE, new ResourceLocation("furnace_minecart"));
                names.put(EntityMinecart.Type.TNT, new ResourceLocation("tnt_minecart"));
                names.put(EntityMinecart.Type.HOPPER, new ResourceLocation("hopper_minecart"));
                names.put(EntityMinecart.Type.SPAWNER, new ResourceLocation("spawner_minecart"));

                // Items
                replaceVanillaCart(names, RailcraftCarts.COMMAND_BLOCK, Items.COMMAND_BLOCK_MINECART, EntityMinecart.Type.COMMAND_BLOCK, 40);
                replaceVanillaCart(names, RailcraftCarts.BASIC, Items.MINECART, EntityMinecart.Type.RIDEABLE, 42);
                replaceVanillaCart(names, RailcraftCarts.CHEST, Items.CHEST_MINECART, EntityMinecart.Type.CHEST, 43);
                replaceVanillaCart(names, RailcraftCarts.FURNACE, Items.FURNACE_MINECART, EntityMinecart.Type.FURNACE, 44);
                replaceVanillaCart(names, RailcraftCarts.TNT, Items.TNT_MINECART, EntityMinecart.Type.TNT, 45);
                replaceVanillaCart(names, RailcraftCarts.HOPPER, Items.HOPPER_MINECART, EntityMinecart.Type.HOPPER, 46);

                float h = TrackConstants.HARDNESS;
                Blocks.RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.GOLDEN_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.DETECTOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.ACTIVATOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);

                MachineTileRegistry.registerTileEntities();
            }

            private void replaceVanillaCart(Map<EntityMinecart.Type, ResourceLocation> names, RailcraftCarts cartType, Item original, EntityMinecart.Type minecartType, int entityId) {
                cartType.register();

                //TODO fix this
                ResourceLocation key = names.get(minecartType);
                EntityEntry old = checkNotNull(ForgeRegistries.ENTITIES.getValue(key));
                Class<? extends Entity> minecartClass = old.getEntityClass();
//                Class<? extends Entity> minecartClass = EntityList.NAME_TO_CLASS.remove(minecartType.getName());

                CartTools.classReplacements.put(minecartClass, cartType);
                CartTools.vanillaCartItemMap.put(original, cartType);

                EntityEntry substitute = new EntityEntry(cartType.getCartClass(), old.getName());
                substitute.setRegistryName(key);
                ForgeRegistries.ENTITIES.register(substitute);
//                EntityList.ID_TO_CLASS.remove(entityId);
//                EntityList.addMapping(cartType.getCartClass(), minecartType.getName(), entityId);

                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(original, new BehaviorDefaultDispenseItem());

                original.setMaxStackSize(RailcraftConfig.getMinecartStackSize());
                original.setCreativeTab(CreativeTabs.TRANSPORTATION);
            }

            @Override
            public void init() {
                // Define Recipes
                if (RailcraftConfig.getRecipeConfig("railcraft.cart.bronze")) {
                    IRecipe recipe = new ShapedOreRecipe(RailcraftConstantsAPI.locationOf("cart_bronze"), new ItemStack(Items.MINECART), false,
                            "I I",
                            "III",
                            'I', "ingotBronze");
                    CraftingPlugin.addRecipe(recipe);
                }

                if (RailcraftConfig.getRecipeConfig("railcraft.cart.steel")) {
                    IRecipe recipe = new ShapedOreRecipe(RailcraftConstantsAPI.locationOf("cart_steel"), new ItemStack(Items.MINECART, 2), false,
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

                    Object woodRailbed = RailcraftItems.RAILBED.getRecipeObject(ItemRailbed.EnumRailbed.WOOD);
                    CraftingPlugin.addRecipe(stackRailNormal,
                            "I I",
                            "I#I",
                            "I I",
                            'I', RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD),
                            '#', woodRailbed);
                    CraftingPlugin.addRecipe(stackRailBooster,
                            "I I",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ADVANCED),
                            '#', woodRailbed,
                            'r', "dustRedstone");
                    CraftingPlugin.addRecipe(stackRailDetector,
                            "IsI",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD),
                            '#', Blocks.STONE_PRESSURE_PLATE,
                            'r', "dustRedstone",
                            's', woodRailbed);
                    CraftingPlugin.addRecipe(stackRailActivator,
                            "ItI",
                            "I#I",
                            "ItI",
                            'I', RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD),
                            '#', woodRailbed,
                            't', new ItemStack(Blocks.REDSTONE_TORCH));

                    CraftingPlugin.addShapelessRecipe(RailcraftItems.RAIL.getStack(1, ItemRail.EnumRail.STANDARD),
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL,
                            Blocks.RAIL);
                }

            }

            @Override
            public void postInit() {
                RailcraftFluids.finalizeDefinitions();

                addLiquidFuels();

                FluidTools.initWaterBottle(RailcraftConfig.nerfWaterBottle());

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
