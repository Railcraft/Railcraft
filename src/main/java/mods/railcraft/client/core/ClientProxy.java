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
import mods.railcraft.client.render.broken.*;
import mods.railcraft.client.render.carts.*;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamMagic;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamSolid;
import mods.railcraft.client.sounds.RCSoundHandler;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.machine.IEnumMachine;
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
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.firestone.TileFirestoneRecharge;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;

import java.util.function.Function;
import java.util.function.Supplier;

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
    public void preInitClient() {
        MinecraftForge.EVENT_BUS.register(RCSoundHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new TextureHook());
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

        TESRFluidLoader fluidLoaderRenderer = new TESRFluidLoader();
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluidLoader.class, fluidLoaderRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluidUnloader.class, fluidLoaderRenderer);

        bindTESR(EnumMachineBeta.TANK_IRON_GAUGE, TESRHollowTank::new);
        bindTESR(EnumMachineBeta.TANK_IRON_WALL, TESRHollowTank::new);
        bindTESR(EnumMachineBeta.TANK_IRON_VALVE, TESRHollowTank::new);

        bindTESR(EnumMachineBeta.TANK_STEEL_GAUGE, TESRHollowTank::new);
        bindTESR(EnumMachineBeta.TANK_STEEL_WALL, TESRHollowTank::new);
        bindTESR(EnumMachineBeta.TANK_STEEL_VALVE, TESRHollowTank::new);

        bindTESR(EnumMachineBeta.ENGINE_STEAM_HOBBY, TESRPneumaticEngine::new);
        bindTESR(EnumMachineBeta.ENGINE_STEAM_LOW, TESRPneumaticEngine::new);
        bindTESR(EnumMachineBeta.ENGINE_STEAM_HIGH, TESRPneumaticEngine::new);

        bindTESR(EnumMachineBeta.VOID_CHEST, TESRChest::new);
        bindTESR(EnumMachineBeta.METALS_CHEST, TESRChest::new);

        ClientRegistry.bindTileEntitySpecialRenderer(TileCage.class, new TESRCagedEntity());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTrackTESR.class, new TESRTrackBuffer());


        ClientRegistry.bindTileEntitySpecialRenderer(TilePostEmblem.class, new RenderBlockPost.EmblemPostTESR());

        ClientRegistry.bindTileEntitySpecialRenderer(TileFirestoneRecharge.class, new TESRFirestone());

        ClientRegistry.bindTileEntitySpecialRenderer(TileSteamTurbine.class, new TESRTurbineGauge());

        ClientRegistry.bindTileEntitySpecialRenderer(TileSignalFoundation.class, new TESRSignals());

        if (RailcraftBlocks.track.block() != null)
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
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, new IRenderFactory<EntityMinecart>() {
            @Override
            public Render<? super EntityMinecart> createRenderFor(RenderManager manager) {
                return new RenderCart(manager);
            }
        });

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

    private <T extends TileEntity> void bindTESR(IEnumMachine<?> machineType, Supplier<TileEntitySpecialRenderer<? super T>> factory) {
        ClientRegistry.bindTileEntitySpecialRenderer(machineType.getTileClass().asSubclass(TileEntity.class), factory.get());
    }

    private <T extends TileEntity> void bindTESR(IEnumMachine<?> machineType, Function<IEnumMachine<?>, TileEntitySpecialRenderer<? super T>> factory) {
        ClientRegistry.bindTileEntitySpecialRenderer(machineType.getTileClass().asSubclass(TileEntity.class), factory.apply(machineType));
    }

    private void registerBlockRenderer(BlockRenderer renderer) {
        if (renderer.getBlock() != null) {
            RenderingRegistry.registerBlockHandler(renderer);
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
        }
    }

    public static class TextureHook {
        @SubscribeEvent
        public void textureStitch(TextureStitchEvent.Pre event) {
            CartContentRendererRedstoneFlux.instance().setRedstoneIcon(event.map.registerSprite(new ResourceLocation("railcraft:cart.redstone.flux")));
            CartContentRendererRedstoneFlux.instance().setFrameIcon(event.map.registerSprite(new ResourceLocation("railcraft:cart.redstone.flux.frame")));
        }
    }
}
