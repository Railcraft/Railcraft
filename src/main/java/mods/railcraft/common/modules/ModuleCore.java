/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.logging.log4j.Level;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.api.helpers.Helpers;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.sounds.SoundLimiterTicker;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MachineTileRegistery;
import mods.railcraft.common.blocks.machine.MultiBlockHelper;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.CrowbarHandler;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.items.ItemNugget;
import mods.railcraft.common.items.ItemCrowbarReinforced;
import mods.railcraft.common.items.RailcraftPartItems;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.fluids.BucketHandler;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.buildcraft.BuildcraftPlugin;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.CokeOvenCraftingManager;
import mods.railcraft.common.util.crafting.CraftingHandler;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.fluids.FluidContainers;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.RailcraftFluids;
import mods.railcraft.common.items.*;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemRailbed.EnumRailbed;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.crafting.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.RecipeSorter;

public class ModuleCore extends RailcraftModule {

    @Override
    public void preInit() {
        LinkageManager.reset();

        RailcraftCraftingManager.cokeOven = new CokeOvenCraftingManager();
        RailcraftCraftingManager.blastFurnace = new BlastFurnaceCraftingManager();
        RailcraftCraftingManager.rockCrusher = new RockCrusherCraftingManager();
        RailcraftCraftingManager.rollingMachine = new RollingMachineCraftingManager();

        SignalTools.packetBuilder = PacketBuilder.instance();

        RailcraftFluids.preInit();
        MinecraftForge.EVENT_BUS.register(RailcraftFluids.getTextureHook());
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        Helpers.structures = new MultiBlockHelper();

        EntityItemFireproof.register();

        RecipeSorter.register("railcraft:rotor.repair", RotorRepairRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("railcraft:locomotive.painting", LocomotivePaintingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("railcraft:routing.table.copy", RoutingTableCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("railcraft:routing.ticket.copy", RoutingTicketCopyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("railcraft:tank.cart.filter", TankCartFilterRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public void initFirst() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Railcraft.getMod(), new GuiHandler());

        LootPlugin.init();

        RailcraftEnchantments.registerEnchantment();

        ItemCrowbar.registerItem();
        ItemCrowbarReinforced.registerItem();
        MinecraftForge.EVENT_BUS.register(CrowbarHandler.instance());

        ItemMagnifyingGlass.register();
        ItemGoggles.registerItem();

        RailcraftToolItems.initializeToolsArmor();

        ItemNugget.getNugget(ItemNugget.EnumNugget.IRON);

        EntityEnderman.setCarriable(Blocks.gravel, false);

        BuildcraftPlugin.init();

        MinecraftForge.EVENT_BUS.register(MinecartHooks.getInstance());
        MinecraftForge.EVENT_BUS.register(LinkageHandler.getInstance());

        FMLCommonHandler.instance().bus().register(new CraftingHandler());
        FMLCommonHandler.instance().bus().register(new SoundLimiterTicker());

        if (RailcraftConfig.useCollisionHandler()) {
            if (EntityMinecart.getCollisionHandler() != null)
                Game.log(Level.WARN, "Existing Minecart Collision Handler detected, overwriting. Please check your configs to ensure this is desired behavior.");
            EntityMinecart.setCollisionHandler(MinecartHooks.getInstance());
        }

        Set<Item> testSet = new HashSet<Item>();
        if (!RailcraftConfig.useOldRecipes()) {
            testSet.add(Item.getItemFromBlock(Blocks.rail));
            testSet.add(Item.getItemFromBlock(Blocks.golden_rail));
            testSet.add(Item.getItemFromBlock(Blocks.detector_rail));
            testSet.add(Item.getItemFromBlock(Blocks.activator_rail));
        }

        if (RailcraftConfig.getRecipeConfig("railcraft.cart.furnace"))
            testSet.add(Items.furnace_minecart);

//        MiscTools.addShapelessRecipe(new ItemStack(Item.coal, 20), Block.dirt);
        Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
        while (it.hasNext()) {
            IRecipe r = (IRecipe) it.next();
            ItemStack output = null;
            try {
                output = r.getRecipeOutput();
            } catch (Exception ex) {
            }
            if (output != null)
                if (testSet.contains(output.getItem()))
                    it.remove();
        }

        // Items
        replaceVanillaCart(EnumCart.BASIC, Items.minecart, "MinecartRideable", 42);
        replaceVanillaCart(EnumCart.CHEST, Items.chest_minecart, "MinecartChest", 43);
        replaceVanillaCart(EnumCart.FURNACE, Items.furnace_minecart, "MinecartFurnace", 44);
        replaceVanillaCart(EnumCart.TNT, Items.tnt_minecart, "MinecartTNT", 45);
        replaceVanillaCart(EnumCart.HOPPER, Items.hopper_minecart, "MinecartHopper", 46);

        CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), Items.chest_minecart);
        CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), Items.furnace_minecart);
        CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), Items.tnt_minecart);
        CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), Items.hopper_minecart);

        LootPlugin.addLootRailway(EnumCart.BASIC.getCartItem(), 1, 1, "cart.basic");
        LootPlugin.addLootRailway(EnumCart.CHEST.getCartItem(), 1, 1, "cart.chest");
        LootPlugin.addLootRailway(EnumCart.TNT.getCartItem(), 1, 3, "cart.tnt");
        LootPlugin.addLootRailway(new ItemStack(Blocks.rail), 8, 32, "track.basic");
        LootPlugin.addLootRailway(EnumCart.HOPPER.getCartItem(), 1, 1, "cart.hopper");

        Blocks.rail.setHarvestLevel("pickaxe", 0);
        Blocks.rail.setHarvestLevel("crowbar", 0);
        Blocks.golden_rail.setHarvestLevel("pickaxe", 0);
        Blocks.golden_rail.setHarvestLevel("crowbar", 0);
        Blocks.detector_rail.setHarvestLevel("pickaxe", 0);
        Blocks.detector_rail.setHarvestLevel("crowbar", 0);
        Blocks.activator_rail.setHarvestLevel("pickaxe", 0);
        Blocks.activator_rail.setHarvestLevel("crowbar", 0);

        // Define Recipies
        if (RailcraftConfig.getRecipeConfig("railcraft.cart.bronze")) {
            IRecipe recipe = new ShapedOreRecipe(new ItemStack(Items.minecart), false, new Object[]{
                "I I",
                "III",
                'I', "ingotBronze",});
            CraftingManager.getInstance().getRecipeList().add(recipe);
        }

        if (RailcraftConfig.getRecipeConfig("railcraft.cart.steel")) {
            IRecipe recipe = new ShapedOreRecipe(new ItemStack(Items.minecart, 2), false, new Object[]{
                "I I",
                "III",
                'I', "ingotSteel",});
            CraftingManager.getInstance().getRecipeList().add(recipe);
        }

        // Old rails
        if (!RailcraftConfig.useOldRecipes()) {
            ItemStack stackRailNormal = new ItemStack(Blocks.rail, 32);
            ItemStack stackRailBooster = new ItemStack(Blocks.golden_rail, 16);
            ItemStack stackRailDetector = new ItemStack(Blocks.detector_rail, 16);
            ItemStack stackRailActivator = new ItemStack(Blocks.activator_rail, 16);

            Object woodRailbed = RailcraftItem.railbed.getRecipeObject(EnumRailbed.WOOD);
            CraftingPlugin.addShapedRecipe(stackRailNormal,
                    "I I",
                    "I#I",
                    "I I",
                    'I', RailcraftItem.rail.getRecipeObject(EnumRail.STANDARD),
                    '#', woodRailbed);
            CraftingPlugin.addShapedRecipe(stackRailBooster,
                    "I I",
                    "I#I",
                    "IrI",
                    'I', RailcraftItem.rail.getRecipeObject(EnumRail.ADVANCED),
                    '#', woodRailbed,
                    'r', Items.redstone);
            CraftingPlugin.addShapedRecipe(stackRailDetector,
                    "IsI",
                    "I#I",
                    "IrI",
                    'I', RailcraftItem.rail.getRecipeObject(EnumRail.STANDARD),
                    '#', Blocks.stone_pressure_plate,
                    'r', Items.redstone,
                    's', woodRailbed);
            CraftingPlugin.addShapedRecipe(stackRailActivator,
                    "ItI",
                    "I#I",
                    "ItI",
                    'I', RailcraftItem.rail.getRecipeObject(EnumRail.STANDARD),
                    '#', woodRailbed,
                    't', new ItemStack(Blocks.redstone_torch));

            CraftingPlugin.addShapelessRecipe(RailcraftItem.rail.getStack(1, EnumRail.STANDARD),
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail,
                    Blocks.rail);
        }

        MachineTileRegistery.registerTileEntities();
    }

    private void replaceVanillaCart(EnumCart cartType, Item original, String entityTag, int entityId) {
        cartType.registerEntity();

        Class<? extends EntityMinecart> minecartClass = (Class<? extends EntityMinecart>) EntityList.stringToClassMapping.remove(entityTag);

        CartUtils.classReplacements.put(minecartClass, cartType);
        CartUtils.vanillaCartItemMap.put(original, cartType);

        EntityList.IDtoClassMapping.remove(entityId);
        EntityList.addMapping(cartType.getCartClass(), entityTag, entityId);

        BlockDispenser.dispenseBehaviorRegistry.putObject(original, null);

        original.setMaxStackSize(RailcraftConfig.getMinecartStackSize());
        cartType.setCartItem(new ItemStack(original));
    }

    @Override

    public void initSecond() {
        if (RailcraftConfig.useCreosoteFurnaceRecipes() || !EnumMachineAlpha.COKE_OVEN.isAvaliable()) {
            FurnaceRecipes.smelting().func_151394_a(new ItemStack(Items.coal, 1, 0), FluidContainers.getCreosoteOilBottle(2), 0.0F);
            FurnaceRecipes.smelting().func_151394_a(new ItemStack(Items.coal, 1, 1), FluidContainers.getCreosoteOilBottle(1), 0.0F);
        }
    }

    @Override
    public void postInit() {
        RailcraftFluids.postInit();

        GameRegistry.registerFuelHandler(FuelPlugin.getFuelHandler());

        addLiquidFuels();

        FluidHelper.nerfWaterBottle();

        Set<IEnumMachine> machines = new HashSet<IEnumMachine>();
        machines.addAll(EnumSet.allOf(EnumMachineAlpha.class));
        machines.addAll(EnumSet.allOf(EnumMachineBeta.class));
        machines.addAll(EnumSet.allOf(EnumMachineGamma.class));
        machines.addAll(EnumSet.allOf(EnumMachineDelta.class));

        for (IEnumMachine machine : machines) {
            if (machine.isAvaliable())
                ItemRegistry.registerItemStack(machine.getTag(), machine.getItem());
        }

//----------------------------------------------
// Boiler Test Setup
// ---------------------------------------------
//        StandardTank tankWater = new StandardTank(FluidHelper.BUCKET_VOLUME * 1000);
//        StandardTank tankSteam = new StandardTank(FluidHelper.BUCKET_VOLUME * 1000);
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

    public static void addLiquidFuels() {
        int bioheat = (int) (16000 * RailcraftConfig.boilerBiofuelMultiplier());
        Fluid ethanol = Fluids.BIOETHANOL.get();
        if (ethanol != null)
            FuelManager.addBoilerFuel(ethanol, bioheat); // Biofuel

        Fluid biofuel = Fluids.BIOFUEL.get();
        if (biofuel != null)
            FuelManager.addBoilerFuel(biofuel, bioheat); // Biofuel

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

}
