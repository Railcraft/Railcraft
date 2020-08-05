/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import com.google.common.base.Throwables;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.fuel.FluidFuelManager;
import mods.railcraft.api.helpers.Helpers;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.util.sounds.SoundLimiterTicker;
import mods.railcraft.common.advancements.criterion.RailcraftAdvancementTriggers;
import mods.railcraft.common.blocks.machine.MachineTileRegistry;
import mods.railcraft.common.blocks.structures.MultiBlockHelper;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.commands.*;
import mods.railcraft.common.core.IInterModMessageHandler;
import mods.railcraft.common.core.InterModMessageRegistry;
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
import mods.railcraft.common.util.charge.CapabilityCartBatterySetup;
import mods.railcraft.common.util.crafting.PrototypeRecipe;
import mods.railcraft.common.util.crafting.RollingMachineCrafter;
import mods.railcraft.common.util.entity.RailcraftDamageSource;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@RailcraftModule("railcraft:core")
public class ModuleCore extends RailcraftModulePayload {

    public ModuleCore() {
        setEnabledEventHandler(new ModuleEventHandler() {
            private final Field modField = ObfuscationReflectionHelper.findField(EntityEntryBuilder.class, "mod");

            @Override
            public void construction() {
                Code.setValue(CartToolsAPI.class, null, LinkageManager.INSTANCE, "linkageManager");
                Code.setValue(CartToolsAPI.class, null, TrainTransferHelper.INSTANCE, "transferHelper");
                Code.setValue(Crafters.class, null, RollingMachineCrafter.INSTANCE, "rollingMachine");

                Railcraft.ROOT_COMMAND.addChildCommand(new CommandDebug());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandAdmin());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandTrack());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandTile());
                Railcraft.ROOT_COMMAND.addChildCommand(new CommandCrafting());

                SignalTools.packetBuilder = PacketBuilder.instance();

                RailcraftFluids.preInitFluids();
                if (RailcraftConfig.handleBottles())
                    MinecraftForge.EVENT_BUS.register(CustomContainerHandler.INSTANCE);
                MinecraftForge.EVENT_BUS.register(RailcraftDamageSource.EVENT_HANDLER);
                LootPlugin.INSTANCE.init();

                Helpers.structures = new MultiBlockHelper();

                EntityItemFireproof.register();

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
                        RailcraftItems.REBAR,

                        RailcraftCarts.BASIC,
                        RailcraftCarts.CHEST,
                        RailcraftCarts.FURNACE,
                        RailcraftCarts.TNT,
                        RailcraftCarts.HOPPER,
                        RailcraftCarts.COMMAND_BLOCK,
                        RailcraftCarts.SPAWNER
                );
            }

            @Override
            public void preInit() {
                NetworkRegistry.INSTANCE.registerGuiHandler(Railcraft.getMod(), new GuiHandler());

                EntityEnderman.setCarriable(Blocks.GRAVEL, false);

                BuildcraftPlugin.init();

                CapabilityCartBatterySetup.register();

                MinecraftForge.EVENT_BUS.register(CrowbarHandler.instance());
                MinecraftForge.EVENT_BUS.register(MinecartHooks.INSTANCE);
                MinecraftForge.EVENT_BUS.register(LinkageHandler.getInstance());
                MinecraftForge.EVENT_BUS.register(new SoundLimiterTicker());
                MinecraftForge.EVENT_BUS.register(new MinecartRiderAIDisabler());
                MinecraftForge.EVENT_BUS.register(new ShuntingAuraTickHandler());
                MinecraftForge.EVENT_BUS.register(Train.getTicker());
                MinecraftForge.EVENT_BUS.register(new Object() {
                    @SubscribeEvent
                    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
                        Entity riding = event.player.getRidingEntity();
                        if (riding instanceof EntityMinecart) {
                            EntityMinecart cart = (EntityMinecart) riding;
                            if (Train.isPartOfTrain(cart))
                                CartTools.removePassengers(cart, event.player.getPositionVector().add(0, 1, 0));
                        }
                    }
                });

                if (RailcraftConfig.cartsInvulnerableFromMonsters()) {
                    MinecraftForge.EVENT_BUS.register(new Object() {
                        // Prevent mobs from killing minecarts!
                        @SubscribeEvent
                        public void onMinecartDamagedByProjectile(ProjectileImpactEvent event) {
                            RayTraceResult result = event.getRayTraceResult();
                            Entity hit = result.entityHit;
                            if (!(hit instanceof EntityMinecart))
                                return;
                            Entity offender = getOwner(event.getEntity());

                            if (offender instanceof IMob || offender instanceof EntityShulkerBullet) {
                                event.setCanceled(true);
                            }
                        }

                        private @Nullable Entity getOwner(Entity projectile) {
                            if (projectile instanceof EntityThrowable) {
                                return ((EntityThrowable) projectile).getThrower();
                            }
                            if (projectile instanceof EntityArrow) {
                                return ((EntityArrow) projectile).shootingEntity;
                            }
                            if (projectile instanceof EntityLlamaSpit) {
                                return ((EntityLlamaSpit) projectile).owner;
                            }
                            // Left shulker bullet as its owner is inaccessible

                            return projectile; // Fallback
                        }
                    });
                }

                if (RailcraftConfig.useCollisionHandler()) {
                    if (EntityMinecart.getCollisionHandler() != null)
                        Game.log().msg(Level.WARN, "Existing Minecart Collision Handler detected, overwriting. Please check your configs to ensure this is desired behavior.");
                    EntityMinecart.setCollisionHandler(MinecartHooks.INSTANCE);

                    InterModMessageRegistry.getInstance().register("high-speed-explosion-excluded-entities", mess -> {
                        NBTTagCompound nbt = mess.getNBTValue();
                        if (nbt.hasKey("entities")) {
                            String entities = nbt.getString("entities");
                            Iterable<String> split = IInterModMessageHandler.SPLITTER.split(entities);
                            RailcraftConfig.excludedAllEntityFromHighSpeedExplosions(split);
                        } else {
                            Game.log().msg(Level.WARN, "Mod %s attempted to exclude an entity from H.S. explosions but failed: %s", mess.getSender(), nbt);
                        }
                    });
                }

                Set<Item> testSet = new HashSet<>();
//                if (!RailcraftConfig.vanillaTrackRecipes()) {
//                    testSet.add(Item.getItemFromBlock(Blocks.RAIL));
//                    testSet.add(Item.getItemFromBlock(Blocks.GOLDEN_RAIL));
//                    testSet.add(Item.getItemFromBlock(Blocks.DETECTOR_RAIL));
//                    testSet.add(Item.getItemFromBlock(Blocks.ACTIVATOR_RAIL));
//                }

                if (!RailcraftConfig.getRecipeConfig("railcraft.cart.vanilla.furnace"))
                    testSet.add(Items.FURNACE_MINECART);

                IForgeRegistryModifiable<IRecipe> recipeRegistry = (IForgeRegistryModifiable<IRecipe>) ForgeRegistries.RECIPES;
                Collection<ResourceLocation> toRemove = new ArrayList<>();
                for (IRecipe each : recipeRegistry) {
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
                    recipeRegistry.remove(each);
                }

                // Vanilla ids:
                Map<EntityMinecart.Type, ResourceLocation> names = new EnumMap<>(EntityMinecart.Type.class);
                names.put(EntityMinecart.Type.RIDEABLE, new ResourceLocation("minecart")); // 42
                names.put(EntityMinecart.Type.COMMAND_BLOCK, new ResourceLocation("commandblock_minecart")); // 40
                names.put(EntityMinecart.Type.CHEST, new ResourceLocation("chest_minecart")); // 43
                names.put(EntityMinecart.Type.FURNACE, new ResourceLocation("furnace_minecart")); // 44
                names.put(EntityMinecart.Type.TNT, new ResourceLocation("tnt_minecart")); // 45
                names.put(EntityMinecart.Type.HOPPER, new ResourceLocation("hopper_minecart")); // 46
                names.put(EntityMinecart.Type.SPAWNER, new ResourceLocation("spawner_minecart")); // 47

                // Items
                replaceVanillaCart(names, RailcraftCarts.COMMAND_BLOCK, Items.COMMAND_BLOCK_MINECART, EntityMinecart.Type.COMMAND_BLOCK, 40);
                replaceVanillaCart(names, RailcraftCarts.BASIC, Items.MINECART, EntityMinecart.Type.RIDEABLE, 42);
                replaceVanillaCart(names, RailcraftCarts.CHEST, Items.CHEST_MINECART, EntityMinecart.Type.CHEST, 43);
                replaceVanillaCart(names, RailcraftCarts.FURNACE, Items.FURNACE_MINECART, EntityMinecart.Type.FURNACE, 44);
                replaceVanillaCart(names, RailcraftCarts.TNT, Items.TNT_MINECART, EntityMinecart.Type.TNT, 45);
                replaceVanillaCart(names, RailcraftCarts.HOPPER, Items.HOPPER_MINECART, EntityMinecart.Type.HOPPER, 46);
                if (RailcraftCarts.SPAWNER.isLoaded())
                    replaceVanillaCart(names, RailcraftCarts.SPAWNER, null, EntityMinecart.Type.SPAWNER, 47);

                float h = TrackConstants.HARDNESS;
                Blocks.RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.GOLDEN_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.DETECTOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);
                Blocks.ACTIVATOR_RAIL.setHardness(h).setHarvestLevel("crowbar", 0);

                MachineTileRegistry.registerTileEntities();
                RailcraftAdvancementTriggers.getInstance().register();
            }

            private void replaceVanillaCart(Map<EntityMinecart.Type, ResourceLocation> names,
                                            RailcraftCarts cartType, @Nullable Item original,
                                            EntityMinecart.Type minecartType, int entityId) {
                ResourceLocation key = names.get(minecartType);
                EntityEntry old = checkNotNull(ForgeRegistries.ENTITIES.getValue(key));
                Class<? extends Entity> minecartClass = old.getEntityClass();

                CartTools.classReplacements.put(minecartClass, cartType);
                if (original != null)
                    CartTools.vanillaCartItemMap.put(original, cartType);

                EntityEntry substitute = createHackedEntityEntryBuilder()
                        .id(key, entityId)
                        .entity(minecartClass)
                        .name(old.getName())
                        .factory(cartType.getFactory())
                        .tracker(80, 2, true)
                        .build();
                ForgeRegistries.ENTITIES.register(substitute);
                Game.log().msg(Level.INFO, "Successfully substituted {0} with {1}.", key, cartType.getRegistration().getRegistryName());

                if (original != null) {
                    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(original, new BehaviorDefaultDispenseItem());

                    original.setMaxStackSize(RailcraftConfig.getMinecartStackSize());
                    original.setCreativeTab(CreativeTabs.TRANSPORTATION);
                }
            }

            private EntityEntryBuilder<Entity> createHackedEntityEntryBuilder() {
                EntityEntryBuilder<Entity> ret = EntityEntryBuilder.create();

                // So that entity registry does not add substitute entity entries with wrong ids to railcraft's network ids
                try {
                    EnumHelper.setFailsafeFieldValue(modField, ret, Loader.instance().getMinecraftModContainer());
                } catch (Throwable ex) {
                    Throwables.throwIfUnchecked(ex);
                    throw new RuntimeException("cannot substitute vanilla carts", ex);
                }

                return ret;
            }

            @Override
            public void init() {
                // Define Recipes
                CraftingPlugin.addRecipe(new PrototypeRecipe());

                if (RailcraftConfig.getRecipeConfig("railcraft.cart.bronze")) {
                    CraftingPlugin.addShapedRecipe(RailcraftConstantsAPI.locationOf("cart_bronze"),
                            new ItemStack(Items.MINECART),
                            false,
                            "I I",
                            "III",
                            'I', "ingotBronze");
                }

                if (RailcraftConfig.getRecipeConfig("railcraft.cart.steel")) {
                    CraftingPlugin.addShapedRecipe(RailcraftConstantsAPI.locationOf("cart_steel"), new ItemStack(Items.MINECART, 2),
                            false,
                            "I I",
                            "III",
                            'I', "ingotSteel");
                }

                // Old rails
                if (!RailcraftConfig.vanillaTrackRecipes()) {
                    ItemStack stackRailNormal = new ItemStack(Blocks.RAIL, 32);
                    ItemStack stackRailBooster = new ItemStack(Blocks.GOLDEN_RAIL, 16);
                    ItemStack stackRailDetector = new ItemStack(Blocks.DETECTOR_RAIL, 16);
                    ItemStack stackRailActivator = new ItemStack(Blocks.ACTIVATOR_RAIL, 16);

                    Object woodRailbed = RailcraftItems.RAILBED.getIngredient(ItemRailbed.EnumRailbed.WOOD);
                    CraftingPlugin.addShapedRecipe(
                            "minecraft:rail",
                            stackRailNormal,
                            "I I",
                            "I#I",
                            "I I",
                            'I', RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.STANDARD),
                            '#', woodRailbed);
                    CraftingPlugin.addShapedRecipe(
                            "minecraft:golden_rail",
                            stackRailBooster,
                            "I I",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.ADVANCED),
                            '#', woodRailbed,
                            'r', "dustRedstone");
                    CraftingPlugin.addShapedRecipe(
                            "minecraft:detector_rail",
                            stackRailDetector,
                            "IsI",
                            "I#I",
                            "IrI",
                            'I', RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.STANDARD),
                            '#', Blocks.STONE_PRESSURE_PLATE,
                            'r', "dustRedstone",
                            's', woodRailbed);
                    CraftingPlugin.addShapedRecipe(
                            "minecraft:activator_rail",
                            stackRailActivator,
                            "ItI",
                            "I#I",
                            "ItI",
                            'I', RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.STANDARD),
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

                InterModMessageRegistry.getInstance().register("fluid-fuel", mess -> {
                    FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(mess.getNBTValue());
                    int fuel = mess.getNBTValue().getInteger("Fuel");
                    if (fuel == 0 || fluidStack == null) {
                        Game.log().msg(Level.WARN, String.format("Mod %s attempted to register a fluid fuel, but failed: %s", mess.getSender(), mess.getNBTValue()));
                        return;
                    }
                    FluidFuelManager.addFuel(fluidStack, fuel);
                    Game.log().msg(Level.DEBUG, String.format("Mod %s registered %s as a valid liquid Boiler fuel", mess.getSender(), mess.getNBTValue()));
                });
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
                Fluids.BIOETHANOL.ifPresent(f -> FluidFuelManager.addFuel(f, bioHeat));
                Fluids.BIOFUEL.ifPresent(f -> FluidFuelManager.addFuel(f, bioHeat));
                Fluids.IC2BIOGAS.ifPresent(f -> FluidFuelManager.addFuel(f, bioHeat));
                Fluids.BIODIESEL.ifPresent(f -> FluidFuelManager.addFuel(f, bioHeat));

                Fluids.FUEL.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (48000 * RailcraftConfig.boilerFuelMultiplier())));

                Fluids.COAL.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (32000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.PYROTHEUM.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (64000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.CREOSOTE.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (4800 * RailcraftConfig.boilerFuelMultiplier())));

                Fluids.DIESEL.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (96000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.GASOLINE.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (48000 * RailcraftConfig.boilerFuelMultiplier())));

                Fluids.OIL_HEAVY.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (42666 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.OIL_DENSE.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (64000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.OIL_DISTILLED.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (20000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.FUEL_DENSE.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (192000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.FUEL_MIXED_HEAVY.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (51200 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.FUEL_LIGHT.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (48000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.FUEL_MIXED_LIGHT.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (16000 * RailcraftConfig.boilerFuelMultiplier())));
                Fluids.FUEL_GASEOUS.ifPresent(f -> FluidFuelManager.addFuel(f, (int) (8000 * RailcraftConfig.boilerFuelMultiplier())));
            }
        });
    }

}
