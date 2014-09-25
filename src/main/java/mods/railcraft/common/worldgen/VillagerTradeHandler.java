/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import java.util.Random;
import mods.railcraft.common.blocks.signals.ItemSignalBlockSurveyor;
import mods.railcraft.common.blocks.signals.ItemSignalTuner;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.items.*;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillagerTradeHandler implements IVillageTradeHandler {

    private float baseChance;

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random rand) {
        //this is where the custom villager trades are specified        
        baseChance = ObfuscationReflectionHelper.<Float, EntityVillager>getPrivateValue(EntityVillager.class, villager, "field_82191_bN");

        addTrade(recipeList, rand, 0.7F, new Offer(Items.coal, 16, 24), new Offer(Items.emerald));
        addTrade(recipeList, rand, 0.7F, new Offer(Items.emerald), new Offer(Items.coal, 24, 32));

        addTrade(recipeList, rand, 0.4F, new Offer(RailcraftToolItems.getCoalCoke(), 8, 12), new Offer(Items.emerald));
        addTrade(recipeList, rand, 0.4F, new Offer(Items.emerald), new Offer(RailcraftToolItems.getCoalCoke(), 12, 16));

        addTrade(recipeList, rand, 0.7F, new Offer(Blocks.rail, 30, 34), new Offer(Items.emerald, 2, 3));
        addTrade(recipeList, rand, 0.1F, new Offer(Blocks.activator_rail, 14, 18), new Offer(Items.emerald, 2, 3));
        addTrade(recipeList, rand, 0.1F, new Offer(Blocks.golden_rail, 14, 18), new Offer(Items.emerald, 2, 3));
        addTrade(recipeList, rand, 0.1F, new Offer(Blocks.detector_rail, 14, 18), new Offer(Items.emerald, 2, 3));

        for (EnumTrack track : EnumTrack.VALUES) {
            if (track.isEnabled())
                addTrade(recipeList, rand, 0.1F, new Offer(track.getItem(), track.recipeOutput - 2, track.recipeOutput + 2), new Offer(Items.emerald, 2, 3));
        }

        addTrade(recipeList, rand, 0.3F, new Offer(Items.minecart), new Offer(Items.emerald, 8, 10));
        addTrade(recipeList, rand, 0.3F, new Offer(Items.emerald, 6, 8), new Offer(Items.minecart));

        addTrade(recipeList, rand, 0.1F, new Offer(EnumCart.LOCO_STEAM_SOLID.getCartItem()), new Offer(Items.emerald, 32, 40));

        addTrade(recipeList, rand, 0.3F, new Offer(ItemCrowbar.getItem()), new Offer(Items.emerald, 7, 9));
        addTrade(recipeList, rand, 0.1F, new Offer(ItemCrowbarReinforced.getItem()), new Offer(Items.emerald, 14, 18));

        addTrade(recipeList, rand, 0.3F, new Offer(ItemWhistleTuner.getItem()), new Offer(Items.emerald, 1, 2));
        addTrade(recipeList, rand, 0.3F, new Offer(ItemMagnifyingGlass.getItem()), new Offer(Items.emerald, 1, 2));
        addTrade(recipeList, rand, 0.3F, new Offer(ItemSignalBlockSurveyor.getItem()), new Offer(Items.emerald, 6, 8));
        addTrade(recipeList, rand, 0.3F, new Offer(ItemSignalTuner.getItem()), new Offer(Items.emerald, 6, 8));
        addTrade(recipeList, rand, 0.4F, new Offer(ItemGoggles.getItem()), new Offer(Items.emerald, 4, 8));
        addTrade(recipeList, rand, 0.5F, new Offer(RailcraftToolItems.getOveralls()), new Offer(Items.emerald, 2, 4));
    }

    private class Offer {

        public final Object obj;
        public final int min, max;

        public Offer(Object obj, int min, int max) {
            this.obj = obj;
            this.min = min;
            this.max = max;
        }

        public Offer(Object obj, int amount) {
            this(obj, amount, amount);
        }

        public Offer(Object obj) {
            this(obj, 1);
        }

    }

    private void addTrade(MerchantRecipeList recipeList, Random rand, float chance, Offer sale, Offer... offers) {
        if (offers.length <= 0 || sale.obj == null)
            return;
        for (Offer offer : offers) {
            if (offer.obj == null)
                return;
        }
        if (rand.nextFloat() < adjustProbability(chance)) {
            ItemStack sellStack = prepareStack(rand, sale);
            ItemStack buyStack1 = prepareStack(rand, offers[0]);
            ItemStack buyStack2 = null;
            if (offers.length >= 2)
                buyStack2 = prepareStack(rand, offers[1]);
            recipeList.add(new MerchantRecipe(buyStack1, buyStack2, sellStack));
        }
    }

    private ItemStack prepareStack(Random rand, Offer offer) throws IllegalArgumentException {
        if (offer.obj instanceof ItemStack) {
            ItemStack stack = (ItemStack) offer.obj;
            stack.stackSize = stackSize(rand, offer.min, offer.max);
            return stack;
        }
        if (offer.obj instanceof Item)
            return new ItemStack((Item) offer.obj, stackSize(rand, offer.min, offer.max));
        if (offer.obj instanceof Block)
            return new ItemStack((Block) offer.obj, stackSize(rand, offer.min, offer.max));
        throw new IllegalArgumentException("Unrecongnized object passed to villager trade setup");
    }

    private int stackSize(Random rand, int min, int max) {
        return MathHelper.getRandomIntegerInRange(rand, min, max);
    }

    private float adjustProbability(float chance) {
        float adjustedChance = chance + baseChance;
        return adjustedChance > 0.9F ? 0.9F - (adjustedChance - 0.9F) : adjustedChance;
    }

}
