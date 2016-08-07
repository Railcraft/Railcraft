/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.core;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.client.particles.ParticleSpark;
import mods.railcraft.client.render.carts.*;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamMagic;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamSolid;
import mods.railcraft.client.render.tesr.*;
import mods.railcraft.client.render.tools.ModelManager;
import mods.railcraft.client.util.sounds.RCSoundHandler;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.beta.TileTankBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.blocks.tracks.kit.TileTrackOutfittedTESR;
import mods.railcraft.common.blocks.wayobjects.TileWayObject;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.CommonProxy;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItem;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.firestone.TileRitual;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public void initializeClient() {
        MinecraftForge.EVENT_BUS.register(RCSoundHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void textureStitch(TextureStitchEvent.Pre event) {
                CartContentRendererRedstoneFlux.instance().setRedstoneIcon(event.getMap().registerSprite(new ResourceLocation("railcraft:entities/carts/cart_redstone_flux")));
                CartContentRendererRedstoneFlux.instance().setFrameIcon(event.getMap().registerSprite(new ResourceLocation("railcraft:entities/carts/cart_redstone_flux_frame")));

                ParticleSpark.sprite = event.getMap().registerSprite(new ResourceLocation("railcraft:particle/spark"));

                for (RailcraftBlocks blockContainer : RailcraftBlocks.VALUES) {
                    Block block = blockContainer.block();
                    if (block instanceof IRailcraftBlock) {
                        IRailcraftBlock railcraftBlock = (IRailcraftBlock) block;
                        TextureAtlasSheet.unstitchIcons(event.getMap(), railcraftBlock.getBlockTexture(), railcraftBlock.getTextureDimensions());
                        IVariantEnum[] variants = railcraftBlock.getVariants();
                        if (variants != null) {
                            for (IVariantEnum variant : variants) {
                                if (variant instanceof IVariantEnumBlock)
                                    TextureAtlasSheet.unstitchIcons(event.getMap(), new ResourceLocation(block.getRegistryName() + "." + variant.getResourcePathSuffix()), ((IVariantEnumBlock) variant).getTextureDimensions());
                            }
                        }
                    }
                }
            }
        });

        for (RailcraftItems itemContainer : RailcraftItems.VALUES) {
            Item item = itemContainer.item();
            if (item instanceof IRailcraftItem) {
                ((IRailcraftItem) item).initializeClient();
            } else if (item != null) {
                ModelManager.registerItemModel(item, 0);
            }
        }

        for (RailcraftBlocks blockContainer : RailcraftBlocks.VALUES) {
            ItemBlock item = blockContainer.item();
            IRailcraftBlock block = blockContainer.getObject();
            if (block != null) {
                block.initializeClient();
                if (item != null) {
                    ((IRailcraftObject) item).initializeClient();
                    IVariantEnum[] variants = block.getVariants();
                    if (variants != null) {
                        for (IVariantEnum variant : variants) {
                            ItemStack stack = blockContainer.getStack(variant);
                            if (stack != null)
                                block.registerItemModel(stack, variant);
                        }
                    } else {
                        ItemStack stack = blockContainer.getStack();
                        if (stack != null)
                            block.registerItemModel(stack, null);
                    }
                }
            }
        }
    }

    @Override
    public void finalizeClient() {
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

//        ItemStack stack = LocomotiveRenderType.STEAM_SOLID.getItemWithRenderer("railcraft:default");
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_SOLID, (EntityLocomotive) EnumCart.LOCO_STEAM_SOLID.makeCart(stack, null, 0, 0, 0)));

//        stack = LocomotiveRenderType.STEAM_MAGIC.getItemWithRenderer("railcraft:default");
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_MAGIC, (EntityLocomotive) EnumCart.LOCO_STEAM_MAGIC.makeCart(stack, null, 0, 0, 0)));

//        stack = LocomotiveRenderType.ELECTRIC.getItemWithRenderer("railcraft:default");
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.ELECTRIC, (EntityLocomotive) EnumCart.LOCO_ELECTRIC.makeCart(stack, null, 0, 0, 0)));

        bindTESR(EnumMachineAlpha.TURBINE, TESRTurbineGauge::new);

        bindTESR(TileTankBase.class, TESRHollowTank::new);
        bindTESR(TileLoaderFluidBase.class, TESRFluidLoader::new);

        bindTESR(EnumMachineBeta.ENGINE_STEAM_HOBBY, TESRPneumaticEngine::new);
        bindTESR(EnumMachineBeta.ENGINE_STEAM_LOW, TESRPneumaticEngine::new);
        bindTESR(EnumMachineBeta.ENGINE_STEAM_HIGH, TESRPneumaticEngine::new);

        bindTESR(EnumMachineBeta.VOID_CHEST, TESRChest::new);
        bindTESR(EnumMachineBeta.METALS_CHEST, TESRChest::new);

//        bindTESR(EnumMachineDelta.CAGE, TESRCagedEntity::new);

        bindTESR(TileTrackOutfittedTESR.class, TESRTrackBuffer::new);

        bindTESR(TilePostEmblem.class, TESREmblemPost::new);

        bindTESR(TileRitual.class, TESRFirestone::new);

        bindTESR(TileWayObject.class, TESRSignals::new);

        //TODO: this needs a smart model or something
//        if (RailcraftBlocks.track.block() != null)
//            RenderingRegistry.registerBlockHandler(new RenderTrack());

//        if (RailcraftBlocksOld.getBlockElevator() != null)
//            RenderingRegistry.registerBlockHandler(new RenderElevator());

//        registerBlockRenderer(new RenderBlockMachineBeta());
//        registerBlockRenderer(new RenderBlockMachineDelta());
//        registerBlockRenderer(new RenderBlockSignal());
//        registerBlockRenderer(RenderBlockPost.make());
//        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.post));
//        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.platform));
//        registerBlockRenderer(new RenderBlockOre());
//        registerBlockRenderer(new RenderBlockFrame());
//        registerBlockRenderer(new RenderBlockStrengthGlass());
//        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockStone()));
//        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockMetal()));
//        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockAlpha()));
//        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockBeta()));
//        registerBlockRenderer(new RenderStair());
//        registerBlockRenderer(new RenderSlab());

        RenderingRegistry.registerEntityRenderingHandler(EntityTunnelBore.class, RenderTunnelBore::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, RenderCart::new);

//        stack = EnumCart.TANK.getCartItem();
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Tank));
//
//        stack = EnumCart.CARGO.getCartItem();
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Cargo));

        if (RailcraftConfig.isWorldGenEnabled("workshop")) {
            int id = RailcraftConfig.villagerID();
//            VillagerRegistry.instance().registerVillagerSkin(id, ModuleWorld.VILLAGER_TEXTURE);
        }

        Game.log(Level.TRACE, "Init Complete: Renderer");
    }

    private <T extends TileEntity> void bindTESR(Class<T> tileClass, Supplier<TileEntitySpecialRenderer<? super T>> factory) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileClass, factory.get());
    }

    private <T extends TileEntity> void bindTESR(IEnumMachine<?> machineType, Supplier<TileEntitySpecialRenderer<? super T>> factory) {
        if (machineType.isAvailable())
            ClientRegistry.bindTileEntitySpecialRenderer(machineType.getTileClass().asSubclass(TileEntity.class), factory.get());
    }

    private <T extends TileEntity> void bindTESR(IEnumMachine<?> machineType, Function<IEnumMachine<?>, TileEntitySpecialRenderer<? super T>> factory) {
        if (machineType.isAvailable())
            ClientRegistry.bindTileEntitySpecialRenderer(machineType.getTileClass().asSubclass(TileEntity.class), factory.apply(machineType));
    }

//    private void registerBlockRenderer(BlockRenderer renderer) {
//        if (renderer.getBlock() != null) {
//            RenderingRegistry.registerBlockHandler(renderer);
//            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
//        }
//    }

}
