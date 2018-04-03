/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

public class VillagerTrades {

    public static void define(VillagerRegistry.VillagerCareer career) {
        BiFunction<ItemStack, Random, ItemStack> enchanter = (stack, rand) -> {
            EnchantmentHelper.addRandomEnchantment(rand, stack, 15 + rand.nextInt(16), true);
            return stack;
        };
        career.addTrade(1, new GenericTrade(offer(Items.COAL, 12, 16), offer(Items.EMERALD)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.COAL, 16, 24)));

        career.addTrade(1, new GenericTrade(offer(RailcraftItems.COKE, 6, 8), offer(Items.EMERALD)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.COKE, 8, 12)));

        career.addTrade(1, new GenericTrade(offer(Blocks.RAIL, 30, 34), offer(Items.EMERALD, 2, 3)));

        career.addTrade(2, new TrackKitTrade());
        career.addTrade(2, new TrackKitTrade());
        career.addTrade(2, new TrackKitTrade());

        career.addTrade(2, new GenericTrade(offer(Items.MINECART), offer(Items.EMERALD, 2, 5)));

        career.addTrade(2, new GenericTrade(offer(RailcraftCarts.LOCO_STEAM_SOLID.getStack()), offer(Items.EMERALD, 32, 40)));

        career.addTrade(3, new TrackKitTrade());

        career.addTrade(3, new GenericTrade(offer(RailcraftItems.CROWBAR_IRON), offer(Items.EMERALD, 7, 9)));
        career.addTrade(3,
                new GenericTrade(offer(RailcraftItems.CROWBAR_STEEL), offer(Items.EMERALD, 24, 54)).setEnchanter(enchanter).setUse((t) -> 1));

        career.addTrade(3, new GenericTrade(offer(RailcraftItems.WHISTLE_TUNER), offer(Items.EMERALD, 1, 2)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.MAG_GLASS), offer(Items.EMERALD, 1, 2)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_BLOCK_SURVEYOR), offer(Items.EMERALD, 6, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_TUNER), offer(Items.EMERALD, 6, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GOGGLES), offer(Items.EMERALD, 4, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.OVERALLS), offer(Items.EMERALD, 19, 32)).setEnchanter(enchanter).setUse((t) -> 1));
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

        @Nullable
        public final Object obj;
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
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random rand) {
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
                Game.logTrace(Level.WARN, "Tried to define invalid trade offer for ({0},{1})->{2}, a necessary item was probably disabled. Skipping",
                        buyStack1, buyStack2, sellStack);
            }
        }

        @Nullable
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
            return null;
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

    private static class TrackKitTrade implements EntityVillager.ITradeList {

        private static final List<TrackKit> trackKits;

        static {
            trackKits = TrackRegistry.TRACK_KIT.stream()
                    .filter(kit -> kit.isEnabled() && kit.isVisible())
                    .collect(Collectors.toList());
        }

        @Override
        public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
            if (trackKits.size() == 0)
                return;
            ItemStack stack = trackKits.get(random.nextInt(trackKits.size())).getTrackKitItem(2);
            if (stack == null)
                return;
            recipeList.add(new MerchantRecipe(stack, null,
                    new ItemStack(Items.EMERALD, MathHelper.getInt(random, 1, 3))));
        }
    }
}
