/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.core;

import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.commands.RootCommand;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.effects.HostEffects;
import mods.railcraft.common.util.misc.BlinkTick;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketHandler;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;

import java.io.File;

@SuppressWarnings("unused")
@Mod(modid = Railcraft.MOD_ID, name = Railcraft.NAME,
        version = Railcraft.VERSION,
        certificateFingerprint = "a0c255ac501b2749537d5824bb0f0588bf0320fa",
        acceptedMinecraftVersions = Railcraft.MC_VERSION,
        guiFactory = "mods.railcraft.client.core.RailcraftGuiConfigFactory",
        updateJSON = "http://www.railcraft.info/railcraft_versions",
        dependencies = "required-after:forge@[14.23.5.2779,);"
                + "after:buildcraftcore@[7.99.17,);"
                + "after:buildcraftenergy;"
                + "after:buildcraftbuilders;"
                + "after:buildcraftfactory;"
                + "after:buildcraftapi_statements@[1.0,);"
                + "after:buildcraftapi_transport@[1.0,);"
                + "after:forestry@[5.8.0.256,);"
                + "after:thaumcraft;"
                + "after:ic2@[2.8.67-ex112,);")
public final class Railcraft {
    public static final String NAME = "Railcraft";
    public static final String MOD_ID = "railcraft";
    public static final String MC_VERSION = "[1.12.2,1.13)";
    public static final RootCommand ROOT_COMMAND = new RootCommand();
    static final String VERSION = "@VERSION@";
    @Instance(Railcraft.MOD_ID)
    public static Railcraft instance;
    @SidedProxy(modId = Railcraft.MOD_ID, clientSide = "mods.railcraft.client.core.ClientProxy", serverSide = "mods.railcraft.common.core.CommonProxy")
    public static CommonProxy proxy;
    private File configFolder;

    public static CommonProxy getProxy() {
        return proxy;
    }

    public static Railcraft getMod() {
        return instance;
    }

    public static String getVersion() {
        return VERSION;
    }

    static {
        FluidRegistry.enableUniversalBucket();
    }

    public File getConfigFolder() {
        return configFolder;
    }

    @Mod.EventHandler
    public void processIMCRequests(FMLInterModComms.IMCEvent event) {
        InterModMessageRegistry messageRegistry = InterModMessageRegistry.getInstance();

        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            try {
                messageRegistry.handle(message);
            } catch (RuntimeException ex) {
                Game.log().msg(Level.FATAL, "Failed to interpret IMC message from mod {0} with key {1}", message.getSender(), message.key);
                throw ex;
            }
        }
    }

    @Mod.EventHandler
    public void fingerprintError(FMLFingerprintViolationEvent event) {
        if (Game.isObfuscated()) {
            Game.log().fingerprint(MOD_ID);
            throw new RuntimeException("Invalid Fingerprint");
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RailcraftModuleManager.loadModules(event.getAsmData());

        configFolder = new File(event.getModConfigurationDirectory(), "railcraft");
        RailcraftConfig.preInit();

        PacketHandler.init();
        DataManagerPlugin.register();

        Metal.init();

        RailcraftModuleManager.preInit();

        proxy.initializeClient();

        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.railcraft.info/donate/");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        TrackRegistry.TRACK_TYPE.finalizeRegistry();
        TrackRegistry.TRACK_KIT.finalizeRegistry();

        RailcraftModuleManager.init();

        HostEffects.init();

        MinecraftForge.EVENT_BUS.register(new BlinkTick());
        MinecraftForge.EVENT_BUS.register(BetaMessageTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(Remapper.class);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RailcraftConfig.postInit();
        RailcraftModuleManager.postInit();

        proxy.finalizeClient();

        CraftingPlugin.areAllBuildersRegistered();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommandHandler commandManager = (CommandHandler) event.getServer().getCommandManager();
        commandManager.registerCommand(ROOT_COMMAND);
    }

//    @Mod.EventHandler
//    public void missingMapping(FMLModIdMappingEvent event) {
//        Remapper.handle(event);
//    }

}
