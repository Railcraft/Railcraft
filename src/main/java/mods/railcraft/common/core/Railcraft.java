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
import java.io.File;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.fluids.RailcraftFluids;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.items.firestone.BlockFirestoneRecharge;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.craftguide.CraftGuidePlugin;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.misc.BallastRegistry;
import mods.railcraft.common.util.misc.BlinkTick;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = Railcraft.MOD_ID, name = "Railcraft",
     version = Railcraft.VERSION,
     certificateFingerprint = "a0c255ac501b2749537d5824bb0f0588bf0320fa",
     acceptedMinecraftVersions = "[1.7.10,1.8)",
     dependencies = "required-after:Forge@[10.13.0.1199,);"
     + "after:BuildCraft|Core[5.0,);"
     + "after:BuildCraft|Energy;"
     + "after:BuildCraft|Builders;"
     + "after:BuildCraft|Factory;"
     + "after:BuildCraftAPI|statements[1.0,);"
     + "after:Forestry[3,);"
     + "after:Thaumcraft;"
     + "after:IC2@[2.0.140,)")
public final class Railcraft {

    public static final String MOD_ID = "Railcraft";
    public static final String VERSION = "@VERSION@";
    public static final String MC_VERSION = "[1.7.10,1.8)";
    @Instance("Railcraft")
    public static Railcraft instance;
    private File configFolder;
//    public int totalMultiBlockUpdates = 0;
//    public int ticksSinceLastMultiBlockPrint = 0;
    @SidedProxy(clientSide = "mods.railcraft.client.core.ClientProxy", serverSide = "mods.railcraft.common.core.CommonProxy")
    public static CommonProxy proxy;

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
                Game.log(Level.INFO, String.format("Mod %s registered %s as a valid ballast", mess.getSender(), mess.getStringValue()));
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
                Game.log(Level.INFO, String.format("Mod %s registered %s as a valid liquid Boiler fuel", mess.getSender(), mess.getStringValue()));
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

        // Finish initializing ItemRegistry
        for (EnumMachineAlpha type : EnumMachineAlpha.values()) {
            if (type.isAvaliable())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumMachineBeta type : EnumMachineBeta.values()) {
            if (type.isAvaliable())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumMachineGamma type : EnumMachineGamma.values()) {
            if (type.isAvaliable())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumMachineDelta type : EnumMachineDelta.values()) {
            if (type.isAvaliable())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumMachineEpsilon type : EnumMachineEpsilon.values()) {
            if (type.isAvaliable())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumSignal type : EnumSignal.values()) {
            if (type.isEnabled())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumCube type : EnumCube.values()) {
            if (type.isEnabled())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }

        for (EnumPost type : EnumPost.values()) {
            if (type.isEnabled())
                ItemRegistry.registerItemStack(type.getTag(), type.getItem());
        }
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
    public void serverCleanUp(FMLServerStoppingEvent event) {
        LinkageManager.reset();
    }

    @Mod.EventHandler
    public void missingMapping(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (mapping.type == GameRegistry.Type.BLOCK) {
                if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.creosote") && RailcraftFluids.CREOSOTE.getBlock() != null)
                    mapping.remap(RailcraftFluids.CREOSOTE.getBlock());
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.steam") && RailcraftFluids.STEAM.getBlock() != null)
                    mapping.remap(RailcraftFluids.STEAM.getBlock());
                else if (mapping.name.equals("Railcraft:tile.block.firestone.recharge") && BlockFirestoneRecharge.getBlock() != null)
                    mapping.remap(BlockFirestoneRecharge.getBlock());
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.anvil") && BlockRCAnvil.getBlock() != null)
                    mapping.remap(BlockRCAnvil.getBlock());
                else if (mapping.name.equals("Railcraft:tile.railcraft.hidden"))
                    mapping.ignore();
                else if (mapping.name.equals("Railcraft:tile.railcraft.stonelamp"))
                    mapping.remap(BlockLantern.getBlockStone());
            } else if (mapping.type == GameRegistry.Type.ITEM)
                if (mapping.name.equals("Railcraft:tool.mag.glass") && ItemMagnifyingGlass.item != null)
                    mapping.remap(ItemMagnifyingGlass.item);
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.creosote") && RailcraftFluids.CREOSOTE.getBlock() != null)
                    mapping.remap(Item.getItemFromBlock(RailcraftFluids.CREOSOTE.getBlock()));
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.fluid.steam") && RailcraftFluids.STEAM.getBlock() != null)
                    mapping.remap(Item.getItemFromBlock(RailcraftFluids.STEAM.getBlock()));
                else if (mapping.name.equals("Railcraft:tile.block.firestone.recharge") && BlockFirestoneRecharge.getBlock() != null)
                    mapping.remap(Item.getItemFromBlock(BlockFirestoneRecharge.getBlock()));
                else if (mapping.name.equals("Railcraft:tile.railcraft.block.anvil") && BlockRCAnvil.getBlock() != null)
                    mapping.remap(Item.getItemFromBlock(BlockRCAnvil.getBlock()));
                else if (mapping.name.equals("Railcraft:tile.railcraft.hidden"))
                    mapping.ignore();
                else if (mapping.name.equals("Railcraft:tile.railcraft.stonelamp"))
                    mapping.remap(Item.getItemFromBlock(BlockLantern.getBlockStone()));
        }
    }

}
