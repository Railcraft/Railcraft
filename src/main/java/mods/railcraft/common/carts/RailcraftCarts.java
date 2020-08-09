/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.ItemWrapper;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.modules.ModuleCarts;
import mods.railcraft.common.modules.ModuleCharge;
import mods.railcraft.common.modules.ModuleLocomotives;
import mods.railcraft.common.modules.ModuleSteam;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.CartDisassemblyRecipe;
import mods.railcraft.common.util.entity.EntityIDs;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static mods.railcraft.common.carts.CartTools.initCartPos;

public enum RailcraftCarts implements IRailcraftCartContainer {

    // Vanilla Carts
    BASIC(0, "cart_basic", EntityCartBasic.class, EntityCartBasic::new, (c) -> Items.MINECART),
    CHEST(0, "cart_chest", EntityCartChest.class, EntityCartChest::new, (c) -> Items.CHEST_MINECART, from(Blocks.CHEST)),
    COMMAND_BLOCK(3, "cart_command_block", EntityCartCommand.class, EntityCartCommand::new, (c) -> Items.COMMAND_BLOCK_MINECART, null),
    FURNACE(0, "cart_furnace", EntityCartFurnace.class, EntityCartFurnace::new, (c) -> Items.FURNACE_MINECART, from(Blocks.FURNACE)),
    HOPPER(0, "cart_hopper", EntityCartHopper.class, EntityCartHopper::new, (c) -> Items.HOPPER_MINECART, from(Blocks.HOPPER)),
    TNT(0, "cart_tnt", EntityCartTNT.class, EntityCartTNT::new, (c) -> Items.TNT_MINECART, from(Blocks.TNT)),
    // Item form added by Railcraft
    SPAWNER(0, "cart_spawner", EntityCartSpawner.class, EntityCartSpawner::new, ItemCartSpawner::new, null),

    // Railcraft Carts
    BORE(1, "bore", EntityTunnelBore.class, EntityTunnelBore::new, ItemTunnelBore::new),
    CARGO(0, "cart_cargo", EntityCartCargo.class, EntityCartCargo::new, ItemCartCargo::new, from(Blocks.TRAPPED_CHEST)),
    CHEST_METALS(0, "cart_chest_metals", EntityCartChestMetals.class, EntityCartChestMetals::new, ItemCartChestMetals::new, RailcraftBlocks.CHEST_METALS::getStack) {
        {
            conditions().add(RailcraftBlocks.CHEST_METALS);
        }
    },
    CHEST_VOID(0, "cart_chest_void", EntityCartChestVoid.class, EntityCartChestVoid::new, ItemCartChestVoid::new, RailcraftBlocks.CHEST_VOID::getStack) {
        {
            conditions().add(RailcraftBlocks.CHEST_VOID);
        }
    },
    ENERGY_BATBOX(0, "cart_ic2_batbox", EntityCartEnergyBatBox.class, EntityCartEnergyBatBox::new, ItemCart::new, ModItems.BAT_BOX::getStack),
    ENERGY_CESU(0, "cart_ic2_cesu", EntityCartEnergyCESU.class, EntityCartEnergyCESU::new, ItemCart::new, ModItems.CESU::getStack),
    ENERGY_MFE(0, "cart_ic2_mfe", EntityCartEnergyMFE.class, EntityCartEnergyMFE::new, ItemCart::new, ModItems.MFE::getStack),
    ENERGY_MFSU(1, "cart_ic2_mfsu", EntityCartEnergyMFSU.class, EntityCartEnergyMFSU::new, ItemCart::new, ModItems.MFSU::getStack),
    GIFT(3, "cart_gift", EntityCartGift.class, EntityCartGift::new, ItemCartGift::new),
    JUKEBOX(0, "cart_jukebox", EntityCartJukebox.class, EntityCartJukebox::new, ItemCartJukebox::new, from(Blocks.JUKEBOX)),
    BED(0, "cart_bed", EntityCartBed.class, EntityCartBed::new, ItemCartBed::new, () -> new ItemStack(Items.BED)),
    MOW_TRACK_LAYER(1, "mow_track_layer", EntityCartTrackLayer.class, EntityCartTrackLayer::new, ItemCartMOWTrackLayer::new),
    MOW_TRACK_RELAYER(1, "mow_track_relayer", EntityCartTrackRelayer.class, EntityCartTrackRelayer::new, ItemCartMOWTrackRelayer::new),
    MOW_TRACK_REMOVER(1, "mow_track_remover", EntityCartTrackRemover.class, EntityCartTrackRemover::new, ItemCartMOWTrackRemover::new),
    MOW_UNDERCUTTER(1, "mow_undercutter", EntityCartUndercutter.class, EntityCartUndercutter::new, ItemCartMOWUndercutter::new),
    PUMPKIN(3, "cart_pumpkin", EntityCartPumpkin.class, EntityCartPumpkin::new, ItemCartPumpkin::new),
    REDSTONE_FLUX(0, "cart_redstone_flux", EntityCartRF.class, EntityCartRF::new, ItemCartRF::new),
    TANK(0, "cart_tank", EntityCartTank.class, EntityCartTank::new, ItemCartTank::new, () -> {
        ItemStack stack = RailcraftBlocks.GLASS.getStack();
        return !InvTools.isEmpty(stack) ? stack : new ItemStack(Blocks.GLASS, 8);
    }),
    TNT_WOOD(0, "cart_tnt_wood", EntityCartTNTWood.class, EntityCartTNTWood::new, ItemCartTNTWood::new),
    TRADE_STATION(0, "cart_trade_station", EntityCartTradeStation.class, EntityCartTradeStation::new, ItemCartTradeStation::new, RailcraftBlocks.TRADE_STATION::getStack) {
        {
            conditions().add(RailcraftBlocks.TRADE_STATION);
        }
    },
    WORK(0, "cart_work", EntityCartWork.class, EntityCartWork::new, ItemCartWork::new, from(Blocks.CRAFTING_TABLE)),

    // Railcraft Locomotives
    LOCO_STEAM_SOLID(1, "locomotive_steam_solid", EntityLocomotiveSteamSolid.class, EntityLocomotiveSteamSolid::new, ItemLocoSteamSolid::new) {
        {
            conditions().add(ModuleLocomotives.class);
            conditions().add(ModuleSteam.class);
        }
    },
    //    LOCO_STEAM_MAGIC(1, "locomotive_steam_magic", EntityLocomotiveSteamMagic.class, (c) -> new ItemLocomotive(c, LocomotiveRenderType.STEAM_MAGIC, EnumColor.PURPLE, EnumColor.SILVER)) {
//        {
//            conditions().add(ModuleLocomotives.class);
//            conditions().add(ModuleThaumcraft.class);
//        }
//    },
    LOCO_ELECTRIC(1, "locomotive_electric", EntityLocomotiveElectric.class, EntityLocomotiveElectric::new, ItemLocoElectric::new) {
        {
            conditions().add(ModuleLocomotives.class);
            conditions().add(ModuleCharge.class);
        }
    },
    LOCO_CREATIVE(3, "locomotive_creative", EntityLocomotiveCreative.class, EntityLocomotiveCreative::new, (c) -> new ItemLocomotive(c, LocomotiveRenderType.ELECTRIC, EnumColor.BLACK, EnumColor.MAGENTA)) {
        {
            conditions().add(ModuleLocomotives.class);
        }
    },
    WORLDSPIKE_STANDARD(0, "cart_worldspike_standard", EntityCartWorldspikeStandard.class, EntityCartWorldspikeStandard::new, ItemCartWorldspikeStandard::new, WorldspikeVariant.STANDARD::getStack) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.STANDARD);
        }
    },
    WORLDSPIKE_ADMIN(3, "cart_worldspike_admin", EntityCartWorldspikeAdmin.class, EntityCartWorldspikeAdmin::new, ItemCartWorldspike::new) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.ADMIN);
        }
    },
    WORLDSPIKE_PERSONAL(0, "cart_worldspike_personal", EntityCartWorldspikePersonal.class, EntityCartWorldspikePersonal::new, ItemCartWorldspikePersonal::new, WorldspikeVariant.PERSONAL::getStack) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.PERSONAL);
        }
    },
    ;
    @SuppressWarnings("WeakerAccess")
    public static final RailcraftCarts[] VALUES = values();
    private static final Map<Class<? extends EntityMinecart>, IRailcraftCartContainer> classToContainer = new HashMap<>();
    private final Class<? extends EntityMinecart> type;
    private final Function<World, Entity> factory;
    private EntityEntry entry;
    private final int id;
    private final byte rarity;
    private final Function<RailcraftCarts, Item> itemSupplier;
    private final Definition def;
    private final @Nullable Supplier<ItemStack> contentsSupplier;
    //    private Item item;
    private boolean isSetup;
    private Optional<IRailcraftItemSimple> railcraftObject = Optional.empty();

    private static Supplier<ItemStack> from(Block block) {
        return () -> new ItemStack(block);
    }

    RailcraftCarts(int rarity, String tag, Class<? extends EntityMinecart> type, Function<World, Entity> factory, Function<RailcraftCarts, Item> itemSupplier) {
        this(rarity, tag, type, factory, itemSupplier, null);
    }

    RailcraftCarts(int rarity, String tag, Class<? extends EntityMinecart> type, Function<World, Entity> factory, Function<RailcraftCarts, Item> itemSupplier, @Nullable Supplier<ItemStack> contentsSupplier) {
        int entityId;
        try {
            entityId = (byte) EntityIDs.class.getField(tag.toUpperCase(Locale.ROOT)).getInt(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.def = new Definition(tag);
        this.itemSupplier = itemSupplier;
        this.contentsSupplier = contentsSupplier;
        this.id = entityId;
        this.rarity = (byte) rarity;
        this.type = type;
        this.factory = factory;
        conditions().add(ModuleCarts.class);
        conditions().add(RailcraftConfig::isCartEnabled, () -> "disabled via config");
    }

    @Override
    public Definition getDef() {
        return def;
    }

    public static IRailcraftCartContainer fromClass(Class<? extends EntityMinecart> clazz) {
        IRailcraftCartContainer result = classToContainer.get(clazz);
        return result == null ? BASIC : result;
    }

    public static IRailcraftCartContainer fromCart(EntityMinecart cart) {
        return fromClass(cart.getClass());
    }

    public static @Nullable IRailcraftCartContainer getCartType(@Nullable ItemStack cart) {
        if (cart == null)
            return null;
        if (cart.getItem() == Items.MINECART)
            return RailcraftCarts.BASIC;
        if (cart.getItem() == Items.CHEST_MINECART)
            return RailcraftCarts.CHEST;
        if (cart.getItem() == Items.TNT_MINECART)
            return RailcraftCarts.TNT;
        if (cart.getItem() == Items.FURNACE_MINECART)
            return RailcraftCarts.FURNACE;
        if (cart.getItem() == Items.HOPPER_MINECART)
            return RailcraftCarts.HOPPER;
        if (cart.getItem() == Items.COMMAND_BLOCK_MINECART)
            return RailcraftCarts.COMMAND_BLOCK;
        if (cart.getItem() instanceof ItemCart)
            return ((ItemCart) cart.getItem()).getCartType();
        return null;
    }

    @Override
    public String getEntityLocalizationTag() {
        return "entity.railcraft." + getBaseTag() + ".name";
    }

    public Item getItem() {
        return getObject().map(IRailcraftObject::getObject).orElse(Items.AIR);
    }

    @Override
    public Optional<IRailcraftItemSimple> getObject() {
        return railcraftObject;
    }

    @Override
    public Class<? extends EntityMinecart> getCartClass() {
        return type;
    }

    @Override
    public EntityEntry getRegistration() {
        return entry;
    }

    @Override
    public ItemStack getContents() {
        if (contentsSupplier == null)
            return ItemStack.EMPTY;
        return contentsSupplier.get();
    }

    @Override
    public EntityMinecart makeCart(ItemStack stack, World world, double i, double j, double k) {
        EntityMinecart entity = (EntityMinecart) entry.newInstance(world);
        initCartPos(entity, i, j, k);
        if (entity instanceof IRailcraftCart)
            ((IRailcraftCart) entity).initEntityFromItem(stack);
        return entity;
    }

    private void registerEntity() {
        if (id < 0)
            return;
        entry = EntityEntryBuilder.create()
                .id(def.registryName, id)
                .entity(type)
                .name(getBaseTag())
                .tracker(256, 2, true)
                .factory(factory)
                .build();
        classToContainer.put(type, this);
        ForgeRegistries.ENTITIES.register(entry);
    }

    @Override
    public void register() {
        if (!isSetup) {
            isSetup = true;
            if (isEnabled()) {
                registerEntity();
                Item item = itemSupplier.apply(this);
                if (item instanceof ItemCart) {
                    ItemCart itemCart = (ItemCart) item;
                    railcraftObject = Optional.of(itemCart);
                    itemCart.setRegistryName(getRegistryName());
                    itemCart.setTranslationKey("railcraft.entity." + getBaseTag().replace("_", "."));
                    itemCart.setRarity(rarity);
                    RailcraftRegistry.register(itemCart);

                    itemCart.initializeDefinition();
                } else if (item != null) {
                    railcraftObject = Optional.of(new ItemWrapper(item));
                }
            } else {
                conditions().printFailureReason(this);
            }
        } else {
            Game.log().msg(Level.INFO, "{0} has been registered twice", this, new Throwable("Stacktrace"));
        }
    }

    @Override
    public void defineRecipes() {
        IRailcraftCartContainer.super.defineRecipes();
        if (contentsSupplier != null)
            CraftingPlugin.addRecipe(new CartDisassemblyRecipe.RailcraftVariant(this));
    }

    @Override
    public boolean isEnabled() {
        if (isVanillaCart())
            return true;
        return conditions().test(this);
    }

    public Function<World, Entity> getFactory() {
        return factory;
    }

    public boolean isVanillaCart() {
        // Note: Spawner minecarts are from vanilla but the item form is from Railcraft.
        switch (this) {
            case CHEST:
            case HOPPER:
            case COMMAND_BLOCK:
            case BASIC:
            case FURNACE:
            case TNT:
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "RailcraftCarts{" + getBaseTag() + "}";
    }
}
