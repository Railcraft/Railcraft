/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mods.railcraft.common.advancements.criterion.RailcraftAdvancementTriggers;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.PotionPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public abstract class CartBaseSurprise extends EntityCartTNTWood {

    protected static final ISurprise COAL = new SurpriseItem(new ItemStack(Items.COAL), 100);
    private static final byte SPAWN_DIST = 2;
    private static final Multimap<Class<? extends CartBaseSurprise>, SurpriseCategory> SURPRISES = HashMultimap.create();

    protected CartBaseSurprise(World world) {
        super(world);
    }

    protected CartBaseSurprise(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    protected static SurpriseCategory createSurpriseCategory(Class<? extends CartBaseSurprise> clazz, int chance) {
        SurpriseCategory category = new SurpriseCategory(chance);
        SURPRISES.put(clazz, category);
        return category;
    }

    public Random getRandom() {
        return rand;
    }

    @Override
    public final void explode(float blastRadius) {
        super.explode(blastRadius);
        if (Game.isHost(world)) {
            spawnSurprises();
            RailcraftAdvancementTriggers.getInstance().onSurpriseExplode((EntityPlayerMP) CartTools.getCartOwnerEntity(this), this);
        }
    }

    protected void spawnSurprises() {
        List<SurpriseCategory> categories = new ArrayList<>(SURPRISES.get(getClass()));
        while (true) {
            int index = rand.nextInt(categories.size());
            SurpriseCategory category = categories.get(index);
            int weight = rand.nextInt(100);
            if (category.chance >= weight) {
                category.spawnSurprises(this);
                return;
            }
        }
    }

    protected interface ISurprise {

        void spawn(CartBaseSurprise cart);

        int getWeight();

    }

    protected static final class SurpriseCategory {
        private final List<ISurprise> surprises = new ArrayList<>();
        final int chance;
        private int numberToSpawn = 1;

        SurpriseCategory(int chance) {
            this.chance = chance;
        }

        protected void setNumberToSpawn(int amount) {
            this.numberToSpawn = amount;
        }

        protected ISurprise getWeightedSurprise(Random rand) {
            if (surprises.isEmpty())
                return COAL;
            while (true) {
                int index = rand.nextInt(surprises.size());
                ISurprise surprise = surprises.get(index);
                int weight = rand.nextInt(100);
                if (surprise.getWeight() >= weight)
                    return surprise;
            }
        }

        protected void spawnSurprises(CartBaseSurprise cart) {
            for (int i = 0; i < numberToSpawn; i++) {
                getWeightedSurprise(cart.rand).spawn(cart);
            }
        }

        protected void add(ISurprise surprise) {
            surprises.add(surprise);
        }

        protected void add(@Nullable ItemStack gift, int chance) {
            if (!InvTools.isEmpty(gift))
                surprises.add(new SurpriseItem(gift, chance));
        }

        protected void add(@Nullable Item gift, int chance) {
            if (gift != null)
                surprises.add(new SurpriseItem(new ItemStack(gift), chance));
        }

        protected void add(@Nullable Item gift, int stackSize, int chance) {
            if (gift != null)
                surprises.add(new SurpriseItem(new ItemStack(gift, stackSize), chance));
        }

        protected void add(@Nullable Block gift, int chance) {
            if (gift != null)
                surprises.add(new SurpriseItem(new ItemStack(gift), chance));
        }

        protected void add(RailcraftItems gift, int chance) {
            add(gift.getStack(), chance);
        }
    }

    protected abstract static class SurpriseItemStack implements ISurprise {
        public final int weight;

        protected SurpriseItemStack(int weight) {
            this.weight = weight;
        }

        public abstract ItemStack getStack(Random rand);

        @Override
        public void spawn(CartBaseSurprise cart) {
            Random rand = cart.rand;
            double x = cart.posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
            double y = cart.posY + 1 + rand.nextInt(3) - 1;
            double z = cart.posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
            InvTools.dropItem(getStack(rand), cart.world, x, y, z);
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

    protected static class SurpriseItem extends SurpriseItemStack {

        public final ItemStack stack;

        protected SurpriseItem(ItemStack stack, int weight) {
            super(weight);
            this.stack = stack;
        }

        @Override
        public ItemStack getStack(Random rand) {
            return stack.copy();
        }
    }

    protected static class SurprisePotion extends SurpriseItemStack {

        protected SurprisePotion(int weight) {
            super(weight);
        }

        @Override
        public ItemStack getStack(Random rand) {
            float type = rand.nextFloat();
            List<ItemStack> choices;
            if (type > 0.8F)
                choices = PotionPlugin.getPotionsLingering();
            else if (type > 0.5F)
                choices = PotionPlugin.getPotionsSplash();
            else
                choices = PotionPlugin.getPotions();
            ItemStack potion = choices.get(rand.nextInt(choices.size()));
            return potion.copy();
        }
    }

    protected static class SurpriseEntity<T extends EntityLiving> implements ISurprise {
        public final Class<T> entityType;
        public final int weight;
        public final int numToSpawn;
        private final BiConsumer<CartBaseSurprise, T> setup;
        private final @Nullable BiConsumer<CartBaseSurprise, T> postSpawn;

        protected SurpriseEntity(Class<T> entityType, int weight, int numToSpawn, BiConsumer<CartBaseSurprise, T> setup, @Nullable BiConsumer<CartBaseSurprise, T> postSpawn) {
            this.entityType = entityType;
            this.weight = weight;
            this.numToSpawn = numToSpawn;
            this.setup = setup;
            this.postSpawn = postSpawn;
        }

        public static <T extends EntityLiving> SurpriseEntity<T> create(Class<T> entityType, int weight, int numToSpawn) {
            return new SurpriseEntity<>(entityType, weight, numToSpawn, (cart, entity) -> entity.onInitialSpawn(cart.world.getDifficultyForLocation(new BlockPos(entity)), null), null);
        }

        public static <T extends EntityLiving> SurpriseEntity<T> create(Class<T> entityType, int weight, int numToSpawn, BiConsumer<CartBaseSurprise, T> setup) {
            return new SurpriseEntity<>(entityType, weight, numToSpawn, setup, null);
        }

        public static <T extends EntityLiving> SurpriseEntity<T> create(Class<T> entityType, int weight, int numToSpawn, BiConsumer<CartBaseSurprise, T> setup, BiConsumer<CartBaseSurprise, T> postSpawn) {
            return new SurpriseEntity<>(entityType, weight, numToSpawn, setup, postSpawn);
        }

        @Override
        public void spawn(CartBaseSurprise cart) {
            Random rand = cart.rand;
            World world = cart.world;

            for (int i = 0; i < numToSpawn; i++) {
                Entity entity = EntityList.createEntityByIDFromName(EntityList.getKey(entityType), world);

                if (entityType.isInstance(entity)) {
                    T living = entityType.cast(entity);
                    double x = cart.posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
                    double y = cart.posY + living.height + rand.nextInt(3);
                    double z = cart.posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
                    living.setLocationAndAngles(x, y, z, rand.nextFloat() * 360.0F, 0.0F);

                    if (world.checkNoEntityCollision(living.getEntityBoundingBox())
                            && world.getCollisionBoxes(living, living.getEntityBoundingBox()).isEmpty()
                            && !world.containsAnyLiquid(living.getEntityBoundingBox())) {

                        setup.accept(cart, living);

                        world.spawnEntity(living);
                        world.playEvent(2004, new BlockPos(x, y, z), 0);

                        living.spawnExplosionParticle();

                        if (postSpawn != null)
                            postSpawn.accept(cart, living);
                    }
                }
            }
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

}
