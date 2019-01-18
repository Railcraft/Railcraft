/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.ItemDust.EnumDust;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

public final class VillagerTrades {

    public static void addTradeForSteelForger(VillagerCareer career) {
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(new ItemStack(Items.COAL, 1, 1), 16, 24)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.COKE, 8, 12)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.IRON_INGOT, 7, 9)));

        career.addTrade(2, new GenericTrade(offer(RailcraftItems.INGOT.getStack(Metal.STEEL)), offer(Items.EMERALD, 1, 2), offer(Items.IRON_INGOT)));
        career.addTrade(2, new GenericTrade(offer(RailcraftItems.INGOT.getStack(Metal.STEEL)), offer(Items.EMERALD, 3, 4)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GEAR.getStack(EnumGear.STEEL)), offer(Items.EMERALD, 9, 16)));
        career.addTrade(2, new GenericTrade(offer(RailcraftItems.DUST.getStack(EnumDust.SLAG), 1, 2), offer(Items.EMERALD, 2, 4)));
    }

    public static void addTradeForAlloyer(VillagerCareer career) {
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.COAL, 16, 24)));
        career.addTrade(1, new GenericTrade(offer(RailcraftItems.COKE, 4, 6), offer(Items.EMERALD)));

        career.addTrade(2, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.INGOT.getStack(Metal.COPPER), 7, 9)));
        career.addTrade(2, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.INGOT.getStack(Metal.TIN), 7, 9)));
        career.addTrade(2, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.INGOT.getStack(Metal.ZINC), 7, 9)));
        career.addTrade(2, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.INGOT.getStack(Metal.NICKEL), 7, 9)));

        career.addTrade(3, new GenericTrade(offer(RailcraftItems.INGOT.getStack(Metal.BRASS)), offer(Items.EMERALD, 2, 3)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.INGOT.getStack(Metal.BRONZE)), offer(Items.EMERALD, 2, 3)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.INGOT.getStack(Metal.INVAR)), offer(Items.EMERALD, 2, 3)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GEAR.getStack(EnumGear.BRONZE)), offer(Items.EMERALD, 6, 12)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GEAR.getStack(EnumGear.BRASS)), offer(Items.EMERALD, 6, 12)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GEAR.getStack(EnumGear.INVAR)), offer(Items.EMERALD, 6, 12)));
    }

    public static void addTradeForCartman(VillagerCareer career) {
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.COAL, 16, 24)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.COKE, 8, 12)));

        career.addTrade(2, new CartTrade(false, 4, 7));
        career.addTrade(2, new CartTrade(false, 4, 7));

        career.addTrade(3, new CartTrade(false, 3, 5));
        career.addTrade(3, new CartTrade(true, 30, 40));
        career.addTrade(3, new CartTrade(true, 30, 40));
    }

    public static void addTradeForTrackman(VillagerCareer career) {
        BiFunction<ItemStack, Random, ItemStack> enchanter = (stack, rand) -> {
            EnchantmentHelper.addRandomEnchantment(rand, stack, 15 + rand.nextInt(16), true);
            return stack;
        };
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.COAL, 16, 24)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.COKE, 8, 12)));

        career.addTrade(1, new GenericTrade(offer(Blocks.RAIL, 30, 34), offer(Items.EMERALD, 2, 3)));

        career.addTrade(2, new TrackKitTrade());
        career.addTrade(2, new TrackKitTrade());

        career.addTrade(3, new TrackKitTrade());
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.CROWBAR_STEEL), offer(Items.EMERALD, 24, 54)).setEnchanter(enchanter).setUse((t) -> 3));
//        career.addTrade(3, new GenericTrade(offer(RailcraftItems.WHISTLE_TUNER), offer(Items.EMERALD, 1, 2)));
//        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_BLOCK_SURVEYOR), offer(Items.EMERALD, 6, 8)));
//        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_TUNER), offer(Items.EMERALD, 6, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GOGGLES), offer(Items.EMERALD, 4, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.OVERALLS), offer(Items.EMERALD, 19, 32)).setEnchanter(enchanter).setUse((t) -> 3));
    }

    private VillagerTrades() {
    }

    //    private float baseChance;
    public static Offer offer(@Nullable Object obj) {
        return offer(obj, 1);
    }

    public static Offer offer(@Nullable Object obj, int amount) {
        return offer(obj, amount, amount);
    }

    public static Offer offer(@Nullable Object obj, int min, int max) {
        return new Offer(obj, min, max);
    }

    private static class Offer {

        public final @Nullable Object obj;
        public final int min, max;

        public Offer(@Nullable Object obj, int min, int max) {
            this.obj = obj;
            this.min = min;
            this.max = max;
        }
    }

    private static class GenericTrade implements EntityVillager.ITradeList {

        private static final BiFunction<ItemStack, Random, ItemStack> DEFAULT_ENCHANTER = (stack, rand) -> stack;
        private static final ToIntFunction<GenericTrade> USE_SETTER = (t) -> 7;

        private final Offer sale;
        private final Offer[] offers;
        private ToIntFunction<GenericTrade> maxUseSetter;
        private BiFunction<ItemStack, Random, ItemStack> enchanter;

        public GenericTrade(Offer sale, Offer... offers) {
            this.sale = sale;
            this.offers = offers;
            this.maxUseSetter = USE_SETTER;
            this.enchanter = DEFAULT_ENCHANTER;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random rand) {
            if (offers.length <= 0 || sale.obj == null) {
                return;
            }
            for (Offer offer : offers) {
                if (offer.obj == null) {
                    return;
                }
            }
            ItemStack sellStack = prepareStack(rand, sale);
            ItemStack buyStack1 = prepareStack(rand, offers[0]);
            ItemStack buyStack2 = InvTools.emptyStack();
            if (offers.length >= 2) {
                buyStack2 = prepareStack(rand, offers[1]);
            }
            if (!InvTools.isEmpty(sellStack) && !InvTools.isEmpty(buyStack1)) {
                recipeList.add(new MerchantRecipe(buyStack1, buyStack2, enchanter.apply(sellStack, rand), 0, maxUseSetter.applyAsInt(this)));
            } else {
                Game.log().trace(Level.WARN, "Tried to define invalid trade offer for ({0},{1})->{2}, a necessary item was probably disabled. Skipping",
                        buyStack1, buyStack2, sellStack);
            }
        }

        private ItemStack prepareStack(Random rand, Offer offer) throws IllegalArgumentException {
            if (offer.obj instanceof RailcraftItems) {
                return ((RailcraftItems) offer.obj).getStack(stackSize(rand, offer));
            }
            if (offer.obj instanceof ItemStack) {
                ItemStack stack = (ItemStack) offer.obj;
                setSize(stack, stackSize(rand, offer));
                return stack;
            }
            if (offer.obj instanceof Item) {
                return new ItemStack((Item) offer.obj, stackSize(rand, offer));
            }
            if (offer.obj instanceof Block) {
                return new ItemStack((Block) offer.obj, stackSize(rand, offer));
            }
            return ItemStack.EMPTY;
        }

        GenericTrade setUse(ToIntFunction<GenericTrade> f) {
            this.maxUseSetter = f;
            return this;
        }

        GenericTrade setEnchanter(BiFunction<ItemStack, Random, ItemStack> enchanter) {
            this.enchanter = enchanter;
            return this;
        }

        private int stackSize(Random rand, Offer offer) {
            return MathHelper.getInt(rand, offer.min, offer.max);
        }
    }

    private static class CartTrade implements ITradeList {

        private static final List<RailcraftCarts> cheap = new ArrayList<>();
        private static final List<RailcraftCarts> expensive = new ArrayList<>();
        private final List<RailcraftCarts> ref;
        private final int priceLow;
        private final int priceHigh;

        static {
            cheap.add(RailcraftCarts.BASIC);
            cheap.add(RailcraftCarts.CHEST);
            cheap.add(RailcraftCarts.HOPPER);
            cheap.add(RailcraftCarts.TNT);
            cheap.add(RailcraftCarts.CARGO);
            cheap.add(RailcraftCarts.JUKEBOX);
            cheap.add(RailcraftCarts.BED);
            cheap.add(RailcraftCarts.TANK);
            cheap.add(RailcraftCarts.TNT_WOOD);
            cheap.add(RailcraftCarts.WORK);
            expensive.add(RailcraftCarts.LOCO_ELECTRIC);
            expensive.add(RailcraftCarts.LOCO_STEAM_SOLID);
            expensive.add(RailcraftCarts.WORLDSPIKE_PERSONAL);
            expensive.add(RailcraftCarts.WORLDSPIKE_STANDARD);
            expensive.add(RailcraftCarts.CHEST_METALS);
            expensive.add(RailcraftCarts.CHEST_VOID);
            expensive.add(RailcraftCarts.MOW_TRACK_RELAYER);
            expensive.add(RailcraftCarts.MOW_TRACK_LAYER);
            expensive.add(RailcraftCarts.MOW_TRACK_REMOVER);
            expensive.add(RailcraftCarts.MOW_UNDERCUTTER);
            expensive.add(RailcraftCarts.BORE);
            cheap.removeIf(cart -> !cart.isLoaded());
            expensive.removeIf(cart -> !cart.isLoaded());
        }

        CartTrade(boolean expensive, int priceLow, int priceHigh) {
            this.ref = expensive ? CartTrade.expensive : cheap;
            this.priceHigh = priceHigh;
            this.priceLow = priceLow;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            if (ref.isEmpty()) {
                return;
            }
            ItemStack stack = ref.get(random.nextInt(ref.size())).getStack();
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, MathHelper.getInt(random, priceLow, priceHigh)), ItemStack.EMPTY, stack));
        }
    }

    private static class TrackKitTrade implements EntityVillager.ITradeList {

        private static final List<TrackKit> trackKits;

        static {
            trackKits = TrackRegistry.TRACK_KIT.stream()
                    .filter(kit -> kit.isEnabled() && kit.isVisible())
                    .collect(Collectors.toList());
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            if (trackKits.isEmpty())
                return;
            ItemStack stack = trackKits.get(random.nextInt(trackKits.size())).getTrackKitItem();
            if (stack.isEmpty())
                return;
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, MathHelper.getInt(random, 2, 6)), ItemStack.EMPTY, stack));
        }
    }
}
