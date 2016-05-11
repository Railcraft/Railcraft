/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.commands.RootCommand;
import mods.railcraft.common.fluids.RailcraftFluids;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.items.firestone.BlockFirestoneRecharge;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.craftguide.CraftGuidePlugin;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.misc.BallastRegistry;
import mods.railcraft.common.util.misc.BlinkTick;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.Level;

import java.io.File;

@Mod(modid = Railcraft.MOD_ID, name = "Railcraft",
        version = Railcraft.VERSION,
        certificateFingerprint = "a0c255ac501b2749537d5824bb0f0588bf0320fa",
        acceptedMinecraftVersions = "[1.7.10,1.8)",
        dependencies = "required-after:Forge@[10.13.4.1448,);"
                + "after:BuildCraft|Core[6.1.7,);"
                + "after:BuildCraft|Energy;"
                + "after:BuildCraft|Builders;"
                + "after:BuildCraft|Factory;"
                + "after:BuildCraftAPI|statements[1.0,);"
                + "after:BuildCraftAPI|transport[1.0,);"
                + "after:Forestry[3,);"
                + "after:Thaumcraft;"
                + "after:IC2@[2.2,)")
public final class Railcraft {
    public static final String MOD_ID = "Railcraft";
    public static final String VERSION = "@VERSION@";
    public static final String MC_VERSION = "[1.7.10,1.8)";
    public static final RootCommand rootCommand = new RootCommand();
    @Instance("Railcraft")
    public static Railcraft instance;
    //    public int totalMultiBlockUpdates = 0;
//    public int ticksSinceLastMultiBlockPrint = 0;
    @SidedProxy(clientSide = "mods.railcraft.client.core.ClientProxy", serverSide = "mods.railcraft.common.core.CommonProxy")
    public static CommonProxy proxy;
    private File configFolder;

    public static CommonProxy getProxy() {
        return proxy;
    }

    public static Railcraft getMod() {
        return instance;
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return VERSION;
    }

    public File getConfigFolder() {
        return configFolder;
    }

    @Mod.EventHandler
    public void processIMCRequests(FMLInterModComms.IMCEvent event) {
        Splitter splitter = Splitter.on("@").trimResults();
        for (FMLInterModComms.IMCMessage mess : event.getMessages()) {
            if (mess.key.equals("ballast")) {
                String[] tokens = Iterables.toArray(splitter.split(mess.getStringValue()), String.class);
                if (tokens.length != 2) {
                    Game.log(Level.WARN, String.format("Mod %s attempted to register a ballast, but failed: %s", mess.getSender(), mess.getStringValue()));
                    continue;
                }
                String blockName = tokens[0];
                Integer metadata = Ints.tryParse(tokens[1]);
                if (blockName == null || metadata == null) {
                    Game.log(Level.WARN, String.format("Mod %s attempted to register a ballast, but failed: %s", mess.getSender(), mess.getStringValue()));
                    continue;
                }
                BallastRegistry.registerBallast(Block.getBlockFromName(blockName), metadata);
                Game.log(Level.DEBUG, String.format("Mod %s registered %s as a valid ballast", mess.getSender(), mess.getStringValue()));
            } else if (mess.key.equals("boiler-fuel-liquid")) {
                String[] tokens = Iterables.toArray(splitter.split(mess.getStringValue()), String.class);
                if (tokens.length != 2) {
                    Game.log(Level.WARN, String.format("Mod %s attempted to register a liquid Boiler fuel, but failed: %s", mess.getSender(), mess.getStringValue()));
                    continue;
                }
                Fluid fluid = FluidRegistry.getFluid(tokens[0]);
                Integer fuel = Ints.tryParse(tokens[1]);
                if (fluid == null || fuel == null) {
                    Game.log(Level.WARN, String.format("Mod %s attempted to register a liquid Boiler fuel, but failed: %s", mess.getSender(), mess.getStringValue()));
                    continue;
                }
                FuelManager.addBoilerFuel(fluid, fuel);
                Game.log(Level.DEBUG, String.format("Mod %s registered %s as a valid liquid Boiler fuel", mess.getSender(), mess.getStringValue()));
            } else if (mess.key.equals("rock-crusher")) {
                NBTTagCompound nbt = mess.getNBTValue();
                ItemStack input = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("input"));
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(input, nbt.getBoolean("matchMeta"), nbt.getBoolean("matchNBT"));
                for (int i = 0; i < 9; i++) {
                    if (nbt.hasKey("output" + i)) {
                        NBTTagCompound outputNBT = nbt.getCompoundTag("output" + i);
                        recipe.addOutput(ItemStack.loadItemStackFromNBT(outputNBT), outputNBT.getFloat("chance"));
                    }
                }
            } else if (mess.key.equals("high-speed-explosion-excluded-entities")) {
                NBTTagCompound nbt = mess.getNBTValue();
                if (nbt.hasKey("entities")) {
                    String entities = nbt.getString("entities");
                    Iterable<String> split = splitter.split(entities);
                    RailcraftConfig.excludedAllEntityFromHighSpeedExplosions(split);
                } else {
                    Game.log(Level.WARN, "Mod %s attempted to exclude an entity from H.S. explosions, but failed: %s", mess.getSender(), nbt);
                }
            }
        }
    }

    @Mod.EventHandler
    public void fingerprintError(FMLFingerprintViolationEvent event) {
        if (Game.isObfuscated()) {
            Game.logErrorFingerprint(MOD_ID);
//            FMLCommonHandler.instance().exitJava(1, false);
            throw new RuntimeException("Invalid Fingerprint");
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
//        Game.log(Level.FINE, "Pre-Init Phase");

        configFolder = new File(event.getModConfigurationDirectory(), "railcraft");
        RailcraftConfig.preInit();

        PacketHandler.init();

        StartupChecks.checkForNewVersion();

        StackFilter.initialize();

        ModuleManager.preInit();

        proxy.preInitClient();

        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.railcraft.info/donate/");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
//        Game.log(Level.FINE, "Init Phase");

        ModuleManager.init();

        FMLCommonHandler.instance().bus().register(new BlinkTick());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
//        Game.log(Level.FINE, "Post-Init Phase");
        ModuleManager.postInit();

        proxy.initClient();

        CraftGuidePlugin.init();

        RailcraftConfig.postInit();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommandHandler commandManager = (CommandHandler) event.getServer().getCommandManager();
        commandManager.registerCommand(rootCommand);
    }

    @Mod.EventHandler
    public void serverCleanUp(FMLServerStoppingEvent event) {
        LinkageManager.reset();
    }

    @Mod.EventHandler
    public void missingMapping(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (mapping.type == GameRegistry.Type.BLOCK) {
                Block block = GameRegistry.findBlock(getModId(), MiscTools.cleanTag(mapping.name));
                if (block != null)
                    remap(block, mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.creosote") && RailcraftFluids.CREOSOTE.getBlock() != null)
                    remap(RailcraftFluids.CREOSOTE.getBlock(), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.steam") && RailcraftFluids.STEAM.getBlock() != null)
                    remap(RailcraftFluids.STEAM.getBlock(), mapping);
                else if (mapping.name.equals("Railcraft:tile.block.firestone.recharge") && BlockFirestoneRecharge.getBlock() != null)
                    remap(BlockFirestoneRecharge.getBlock(), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.anvil") && BlockRCAnvil.getBlock() != null)
                    remap(BlockRCAnvil.getBlock(), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.hidden"))
                    mapping.ignore();
                else if (mapping.name.equals("Railcraft:tile.railcraft.stonelamp"))
                    remap(BlockLantern.getBlockStone(), mapping);
            } else if (mapping.type == GameRegistry.Type.ITEM) {
                Block block = GameRegistry.findBlock(getModId(), MiscTools.cleanTag(mapping.name));
                if (block != null)
                    remap(Item.getItemFromBlock(block), mapping);
                else if (mapping.name.equals("Railcraft:tool.mag.glass") && ItemMagnifyingGlass.item != null)
                    remap(ItemMagnifyingGlass.item, mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.creosote") && RailcraftFluids.CREOSOTE.getBlock() != null)
                    remap(Item.getItemFromBlock(RailcraftFluids.CREOSOTE.getBlock()), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.steam") && RailcraftFluids.STEAM.getBlock() != null)
                    remap(Item.getItemFromBlock(RailcraftFluids.STEAM.getBlock()), mapping);
                else if (mapping.name.equals("Railcraft:tile.block.firestone.recharge") && BlockFirestoneRecharge.getBlock() != null)
                    remap(Item.getItemFromBlock(BlockFirestoneRecharge.getBlock()), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.anvil") && BlockRCAnvil.getBlock() != null)
                    remap(Item.getItemFromBlock(BlockRCAnvil.getBlock()), mapping);
                else if (mapping.name.equals("Railcraft:tile.railcraft.hidden"))
                    mapping.ignore();
                else if (mapping.name.equals("Railcraft:tile.railcraft.stonelamp"))
                    remap(Item.getItemFromBlock(BlockLantern.getBlockStone()), mapping);
            }
        }
    }

    private void remap(Block block, FMLMissingMappingsEvent.MissingMapping mapping) {
        mapping.remap(block);
        Game.log(Level.WARN, "Remapping block " + mapping.name + " to " + getModId() + ":" + MiscTools.cleanTag(block.getUnlocalizedName()));
    }

    private void remap(Item item, FMLMissingMappingsEvent.MissingMapping mapping) {
        mapping.remap(item);
        Game.log(Level.WARN, "Remapping item " + mapping.name + " to " + getModId() + ":" + MiscTools.cleanTag(item.getUnlocalizedName()));
    }
}
