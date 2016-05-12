/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.core;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.client.render.*;
import mods.railcraft.client.render.carts.*;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamMagic;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamSolid;
import mods.railcraft.client.sounds.RCSoundHandler;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import mods.railcraft.common.blocks.machine.beta.*;
import mods.railcraft.common.blocks.machine.delta.TileCage;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileFluidUnloader;
import mods.railcraft.common.blocks.signals.TileSignalFoundation;
import mods.railcraft.common.blocks.tracks.TileTrackTESR;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.core.CommonProxy;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.firestone.TileFirestoneRecharge;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        return stack.getItem().getItemStackDisplayName(stack);
    }

    @Override
    public String getCurrentLanguage() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    @Override
    public int getRenderId() {
        return RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void preInitClient() {
        MinecraftForge.EVENT_BUS.register(RCSoundHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new TextureHook());
    }

    public static class TextureHook {
        @SubscribeEvent
        public void textureStitch(TextureStitchEvent.Pre event) {
            if (event.map.getTextureType() == 0) {
                CartContentRendererRedstoneFlux.instance().setRedstoneIcon(event.map.registerIcon("railcraft:cart.redstone.flux"));
                CartContentRendererRedstoneFlux.instance().setFrameIcon(event.map.registerIcon("railcraft:cart.redstone.flux.frame"));
            }
        }
    }

    @Override
    public void initClient() {
        MinecraftForge.EVENT_BUS.register(new LatestVersionMessage());

        SoundRegistry.setupBlockSounds();

        MinecraftForge.EVENT_BUS.register(LocomotiveKeyHandler.INSTANCE);

        if (!RailcraftItems.goggles.isEnabled())
            MinecraftForge.EVENT_BUS.register(AuraKeyHandler.INSTANCE);

        Game.log(Level.TRACE, "Init Start: Renderer");

        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.solid.default", new ModelLocomotiveSteamSolid()));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:magic", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:electric", "locomotive.model.electric.default", new ModelLocomotiveElectric()));
        LocomotiveRenderType.STEAM_MAGIC.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
        LocomotiveRenderType.ELECTRIC.registerRenderer(new LocomotiveRendererElectric());

        ItemStack stack = LocomotiveRenderType.STEAM_SOLID.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_SOLID, (EntityLocomotive) EnumCart.LOCO_STEAM_SOLID.makeCart(stack, null, 0, 0, 0)));

        stack = LocomotiveRenderType.STEAM_MAGIC.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_MAGIC, (EntityLocomotive) EnumCart.LOCO_STEAM_MAGIC.makeCart(stack, null, 0, 0, 0)));

        stack = LocomotiveRenderType.ELECTRIC.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.ELECTRIC, (EntityLocomotive) EnumCart.LOCO_ELECTRIC.makeCart(stack, null, 0, 0, 0)));

        RenderTESRFluidLoader fluidLoaderRenderer = new RenderTESRFluidLoader();
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluidLoader.class, fluidLoaderRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluidUnloader.class, fluidLoaderRenderer);

        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronGauge.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronWall.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronValve.class, new RenderIronTank());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelGauge.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelWall.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelValve.class, new RenderIronTank());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamHobby.class, RenderPneumaticEngine.renderHobby);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamLow.class, RenderPneumaticEngine.renderLow);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamHigh.class, RenderPneumaticEngine.renderHigh);

        ClientRegistry.bindTileEntitySpecialRenderer(TileCage.class, new RenderCagedEntity());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTrackTESR.class, new RenderTrackBuffer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileChestVoid.class, new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_void.png", new TileChestVoid()));
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_metals.png", new TileChestMetals()));

        ClientRegistry.bindTileEntitySpecialRenderer(TilePostEmblem.class, new RenderBlockPost.EmblemPostTESR());

        ClientRegistry.bindTileEntitySpecialRenderer(TileFirestoneRecharge.class, new RenderTESRFirestone());

        ClientRegistry.bindTileEntitySpecialRenderer(TileSteamTurbine.class, new RenderTurbineGauge());

        RenderTESRSignals controllerRenderer = new RenderTESRSignals();
        ClientRegistry.bindTileEntitySpecialRenderer(TileSignalFoundation.class, controllerRenderer);

        if (RailcraftBlocksOld.getBlockTrack() != null)
            RenderingRegistry.registerBlockHandler(new RenderTrack());

        if (RailcraftBlocksOld.getBlockElevator() != null)
            RenderingRegistry.registerBlockHandler(new RenderElevator());

        registerBlockRenderer(new RenderBlockMachineBeta());
        registerBlockRenderer(new RenderBlockMachineDelta());
        registerBlockRenderer(new RenderBlockSignal());
        registerBlockRenderer(RenderBlockPost.make());
        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.post));
        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.platform));
        registerBlockRenderer(new RenderBlockOre());
        registerBlockRenderer(new RenderBlockFrame());
        registerBlockRenderer(new RenderBlockStrengthGlass());
        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockStone()));
        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockMetal()));
        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockAlpha()));
        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockBeta()));
        registerBlockRenderer(new RenderStair());
        registerBlockRenderer(new RenderSlab());

        RenderingRegistry.registerEntityRenderingHandler(EntityTunnelBore.class, new RenderTunnelBore());
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, new RenderCart());

        stack = EnumCart.TANK.getCartItem();
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Tank));

        stack = EnumCart.CARGO.getCartItem();
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Cargo));

        if (RailcraftConfig.isWorldGenEnabled("workshop")) {
            int id = RailcraftConfig.villagerID();
            VillagerRegistry.instance().registerVillagerSkin(id, ModuleWorld.VILLAGER_TEXTURE);
        }

        Game.log(Level.TRACE, "Init Complete: Renderer");
    }

    private void registerBlockRenderer(BlockRenderer renderer) {
        if (renderer.getBlock() != null) {
            RenderingRegistry.registerBlockHandler(renderer);
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
        }
    }
}
