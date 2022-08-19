/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.core;

import mods.railcraft.client.gui.GuiBookRoutingTable;
import mods.railcraft.client.particles.ParticlePumpkin;
import mods.railcraft.client.particles.ParticleSpark;
import mods.railcraft.client.render.carts.*;
import mods.railcraft.client.render.models.programmatic.locomotives.ModelLocomotiveSteamSolid;
import mods.railcraft.client.render.models.resource.*;
import mods.railcraft.client.render.tesr.*;
import mods.railcraft.client.render.world.GoggleAuraWorldRenderer;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.client.util.sounds.RCSoundHandler;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxBase;
import mods.railcraft.common.blocks.machine.wayobjects.signals.*;
import mods.railcraft.common.blocks.structures.TileSteamTurbine;
import mods.railcraft.common.blocks.structures.TileTank;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.CommonProxy;
import mods.railcraft.common.core.RailcraftObjects;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.firestone.TileRitual;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy implements ISelectiveResourceReloadListener {
    public ClientProxy() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().world;
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
    public void openRoutingTableGui(EntityPlayer player, @Nullable TileEntity tile, ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiBookRoutingTable(player, tile, stack));
    }

    @Override
    public void initializeClient() {
        ModelLoaderRegistry.registerLoader(OutfittedTrackModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(OutfittedTrackItemModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(TrackKitItemModel.Loader.INSTANCE);

        MinecraftForge.EVENT_BUS.register(RCSoundHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(JSONModelRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(FluidModelRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GoggleAuraWorldRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void textureStitch(TextureStitchEvent.Pre event) {
//                CartContentRendererRedstoneFlux.instance().setRedstoneIcon(event.getMap().registerSprite(new ResourceLocation("railcraft:entities/carts/cart_redstone_flux")));
//                CartContentRendererRedstoneFlux.instance().setFrameIcon(event.getMap().registerSprite(new ResourceLocation("railcraft:entities/carts/cart_redstone_flux_frame")));

                ParticleSpark.sprite = event.getMap().registerSprite(new ResourceLocation("railcraft:particle/spark"));
                ParticlePumpkin.sprite = event.getMap().registerSprite(new ResourceLocation("railcraft:particle/pumpkin"));

                for (RailcraftBlocks blockContainer : RailcraftBlocks.VALUES) {
                    Block block = blockContainer.block();
                    if (block instanceof IRailcraftBlock) {
                        IRailcraftBlock railcraftBlock = (IRailcraftBlock) block;
                        railcraftBlock.registerTextures(event.getMap());
                    }
                }
            }
        });

        RailcraftObjects.processItems(IRailcraftItemSimple::initializeClient);

        RailcraftObjects.processBlocks(
                (block, item) -> {
                    block.initializeClient();
                    if (item != null)
                        item.initializeClient();
                },
                (block, variant) -> {
                    ItemStack stack = block.getStack(variant);
                    if (!stack.isEmpty())
                        block.registerItemModel(stack, variant);
                });

//        RailcraftPotions.VALUES.forEach(RailcraftPotions::initializeClient);
//        RailcraftPotionTypes.VALUES.forEach(RailcraftPotionTypes::initializeClient);

        JSONModelRenderer.INSTANCE.registerModel(CartContentRendererRedstoneFlux.CORE_MODEL);
        JSONModelRenderer.INSTANCE.registerModel(CartContentRendererRedstoneFlux.FRAME_MODEL);
        JSONModelRenderer.INSTANCE.registerModel(TESRManipulatorFluid.PIPE_MODEL);

        RenderingRegistry.registerEntityRenderingHandler(EntityTunnelBore.class, RenderTunnelBore::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, RenderCart::new);
    }

    @Override
    public void finalizeClient() {
        // Remove the vanilla cart renderers!
        Minecraft.getMinecraft().getRenderManager().entityRenderMap.remove(EntityMinecartMobSpawner.class);
        Minecraft.getMinecraft().getRenderManager().entityRenderMap.remove(EntityMinecartTNT.class);

        ClientEffects.init();
        SoundRegistry.setupBlockSounds();

        MinecraftForge.EVENT_BUS.register(LocomotiveKeyHandler.INSTANCE);

        if (!RailcraftItems.GOGGLES.isEnabled())
            MinecraftForge.EVENT_BUS.register(AuraKeyHandler.INSTANCE);

        Game.log().msg(Level.TRACE, "Init Start: Renderer");

        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.solid.default", new ModelLocomotiveSteamSolid(), new ModelLocomotiveSteamSolid(0.125F)));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:magic", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:electric", "locomotive.model.electric.default", new ModelLocomotiveElectric()));
//        LocomotiveRenderType.STEAM_MAGIC.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
        LocomotiveRenderType.DIESEL.registerRenderer(new LocomotiveRendererDiesel());
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

//        bindTESR(EnumMachineAlpha.TURBINE, TESRTurbineGauge::new);

        //TODO move to block classes
        bindTESR(TileSteamTurbine.class, TESRTurbineGauge::new);
        bindTESR(TileTank.class, TESRHollowTank::new);
        bindTESR(TileFluidManipulator.class, TESRManipulatorFluid::new);

//        bindTESR(EnumMachineBeta.ENGINE_STEAM_HOBBY, TESRPneumaticEngine::new);
//        bindTESR(EnumMachineBeta.ENGINE_STEAM_LOW, TESRPneumaticEngine::new);
//        bindTESR(EnumMachineBeta.ENGINE_STEAM_HIGH, TESRPneumaticEngine::new);

//        bindTESR(EnumMachineBeta.VOID_CHEST, TESRChest::new);
//        bindTESR(EnumMachineBeta.METALS_CHEST, TESRChest::new);

//        bindTESR(EnumMachineDelta.CAGE, TESRCagedEntity::new);

        bindTESR(TilePostEmblem.class, TESREmblemPost::new);

        bindTESR(TileRitual.class, TESRFirestone::new);

        bindTESR(TileBoxBase.class, TESRSignalBox::new);

        bindTESR(TileSignalBlock.class, TESRSignalLamp::new);
        bindTESR(TileSignalDistant.class, TESRSignalLamp::new);
        bindTESR(TileSignalToken.class, TESRSignalLamp::new);

        bindTESR(TileSignalBlockDual.class, TESRSignalLampDual::new);
        bindTESR(TileSignalDistantDual.class, TESRSignalLampDual::new);
        bindTESR(TileSignalTokenDual.class, TESRSignalLampDual::new);

//        registerBlockRenderer(new RenderBlockMachineBeta());
//        registerBlockRenderer(new RenderBlockMachineDelta());
//        registerBlockRenderer(new RenderBlockSignal());
//        registerBlockRenderer(RenderBlockPost.make());
//        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.post));
//        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.platform));
//        registerBlockRenderer(new RenderBlockFrame());
//        registerBlockRenderer(new RenderBlockStrengthGlass());
//        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockStone()));
//        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockMetal()));
//        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockAlpha()));
//        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockBeta()));
//        registerBlockRenderer(new RenderStair());
//        registerBlockRenderer(new RenderSlab());

        // TODO: these carts still need custom item models
//        stack = EnumCart.TANK.getCartItem();
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Tank));
//
//        stack = EnumCart.CARGO.getCartItem();
//        if (stack != null)
//            registerItemRenderer(stack.getItem(), new RenderCartItemFiltered(RenderCartItemFiltered.RendererType.Cargo));

        Game.log().msg(Level.TRACE, "Init Complete: Renderer");
    }

    private <T extends TileEntity> void bindTESR(Class<T> tileClass, Supplier<TileEntitySpecialRenderer<? super T>> factory) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileClass, factory.get());
    }

    @Deprecated
    private <T extends TileEntity> void bindTESR(IEnumMachine<?> machineType, Supplier<TileEntitySpecialRenderer<? super T>> factory) {
        if (machineType.isAvailable())
            ClientRegistry.bindTileEntitySpecialRenderer(machineType.getTileClass().asSubclass(TileEntity.class), factory.get());
    }

    @Deprecated
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

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (Game.DEVELOPMENT_VERSION && resourcePredicate.test(VanillaResourceType.TEXTURES))
            Game.log().msg(Game.DEBUG_REPORT, "Detected texture reload");
    }
}
