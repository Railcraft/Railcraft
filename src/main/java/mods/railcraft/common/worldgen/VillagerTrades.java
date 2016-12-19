/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import com.google.common.base.Preconditions;

import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.IMerchant;
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
import java.util.Random;

public class VillagerTrades {

    public static void define(VillagerRegistry.VillagerCareer career) {
        career.addTrade(1, new GenericTrade(offer(Items.COAL, 16, 24), offer(Items.EMERALD)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(Items.COAL, 24, 32)));

        career.addTrade(1, new GenericTrade(offer(RailcraftItems.COKE, 8, 12), offer(Items.EMERALD)));
        career.addTrade(1, new GenericTrade(offer(Items.EMERALD), offer(RailcraftItems.COKE, 12, 16)));

        career.addTrade(1, new GenericTrade(offer(Blocks.RAIL, 30, 34), offer(Items.EMERALD, 2, 3)));


        career.addTrade(2, new GenericTrade(offer(Blocks.ACTIVATOR_RAIL, 14, 18), offer(Items.EMERALD, 2, 3)));
        career.addTrade(2, new GenericTrade(offer(Blocks.GOLDEN_RAIL, 14, 18), offer(Items.EMERALD, 2, 3)));
        career.addTrade(2, new GenericTrade(offer(Blocks.DETECTOR_RAIL, 14, 18), offer(Items.EMERALD, 2, 3)));

        for (TrackKits track : TrackKits.VALUES) {
            if (track.isEnabled())
                career.addTrade(2, new GenericTrade(offer(track.getStack(), track.recipeOutput - 2, track.recipeOutput + 2), offer(Items.EMERALD, 2, 3)));
        }

        career.addTrade(2, new GenericTrade(offer(Items.MINECART), offer(Items.EMERALD, 8, 10)));
        career.addTrade(2, new GenericTrade(offer(Items.EMERALD, 6, 8), offer(Items.MINECART)));

        career.addTrade(2, new GenericTrade(offer(RailcraftCarts.LOCO_STEAM_SOLID.getStack()), offer(Items.EMERALD, 32, 40)));

        career.addTrade(3, new GenericTrade(offer(RailcraftItems.CROWBAR_IRON), offer(Items.EMERALD, 7, 9)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.CROWBAR_STEEL), offer(Items.EMERALD, 14, 18)));

        career.addTrade(3, new GenericTrade(offer(RailcraftItems.WHISTLE_TUNER), offer(Items.EMERALD, 1, 2)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.MAG_GLASS), offer(Items.EMERALD, 1, 2)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_BLOCK_SURVEYOR), offer(Items.EMERALD, 6, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.SIGNAL_TUNER), offer(Items.EMERALD, 6, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.GOGGLES), offer(Items.EMERALD, 4, 8)));
        career.addTrade(3, new GenericTrade(offer(RailcraftItems.OVERALLS), offer(Items.EMERALD, 2, 4)));
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

    public static class GenericTrade implements EntityVillager.ITradeList {
        private final Offer sale;
        private final Offer[] offers;

        public GenericTrade(Offer sale, Offer... offers) {
            this.sale = sale;
            this.offers = offers;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random rand) {
            if (offers.length <= 0 || sale.obj == null)
                return;
            for (Offer offer : offers) {
                if (offer.obj == null)
                    return;
            }
            ItemStack sellStack = prepareStack(rand, sale);
            ItemStack buyStack1 = prepareStack(rand, offers[0]);
            ItemStack buyStack2 = ItemStack.EMPTY;
            if (offers.length >= 2)
                buyStack2 = prepareStack(rand, offers[1]);
            if (!sellStack.isEmpty() && !buyStack1.isEmpty()) {
                recipeList.add(new MerchantRecipe(buyStack1, buyStack2, sellStack));
            } else {
                Game.logTrace(Level.WARN, "Tried to define invalid trade offer for ({0},{1})->{2}, a necessary item was probably disabled. Skipping", buyStack1, buyStack2, sellStack);
            }
        }

        private ItemStack prepareStack(Random rand, Offer offer) throws IllegalArgumentException {
            if (offer.obj instanceof RailcraftItems) {
                return Preconditions.checkNotNull(((RailcraftItems) offer.obj).getStack(stackSize(rand, offer)));
            }
            if (offer.obj instanceof ItemStack) {
                ItemStack stack = (ItemStack) offer.obj;
                stack.setCount(stackSize(rand, offer));
                return stack;
            }
            if (offer.obj instanceof Item)
                return new ItemStack((Item) offer.obj, stackSize(rand, offer));
            if (offer.obj instanceof Block)
                return new ItemStack((Block) offer.obj, stackSize(rand, offer));
            return ItemStack.EMPTY;
        }

        private int stackSize(Random rand, Offer offer) {
            return MathHelper.getInt(rand, offer.min, offer.max);
        }
    }
}
