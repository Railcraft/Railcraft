/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.util.entity.ai.EntityAIHalloweenKnights;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;

public class EntityCartPumpkin extends CartBaseSurprise {
    private static final CartBaseSurprise.SurpriseCategory MOBS = createSurpriseCategory(EntityCartPumpkin.class, 100);
    private static final CartBaseSurprise.SurpriseCategory POTIONS = createSurpriseCategory(EntityCartPumpkin.class, 100);

    static {
        POTIONS.add(new SurprisePotion(100));
        POTIONS.add(RailcraftItems.CROWBAR_SEASONS, 1);

        MOBS.add(SurpriseEntity.create(EntityBat.class, 75, 3));
        MOBS.add(SurpriseEntity.create(EntityWitch.class, 25, 1));
        MOBS.add(SurpriseEntity.create(EntityGhast.class, 25, 1));
        MOBS.add(SurpriseEntity.create(EntityPigZombie.class, 25, 1));
        MOBS.add(SurpriseEntity.create(EntityWither.class, 5, 1, (cart, wither) -> {
            wither.setDropItemsWhenDead(false); // Uhh no free nether stars
        }));

        MOBS.add(SurpriseEntity.create(EntityWitherSkeleton.class, 50, 1, (cart, skeleton) -> {
            Random rand = cart.getRandom();
            if (rand.nextInt(4) == 0) {
                skeleton.tasks.addTask(4, new EntityAIAttackMelee(skeleton, 0.25F, false));
                skeleton.setItemStackToSlot(MAINHAND, new ItemStack(Items.STONE_SWORD));
            } else {
                skeleton.tasks.addTask(4, new EntityAIAttackRanged(skeleton, 0.25F, 60, 10.0F));
                skeleton.setItemStackToSlot(MAINHAND, new ItemStack(Items.BOW));
            }

            skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(rand.nextFloat() < 0.25F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
        }));

        MOBS.add(SurpriseEntity.create(EntitySkeletonHorse.class, 10, 1,
                (cart, horse) -> {
                    horse.setGrowingAge(0);
                    horse.tasks.addTask(1, new EntityAIHalloweenKnights(horse));
                },
                (cart, horse) -> cart.world.addWeatherEffect(new EntityLightningBolt(horse.world, horse.posX, horse.posY, horse.posZ, true))
        ));

        MOBS.add(SurpriseEntity.create(EntityZombieHorse.class, 20, 1,
                (cart, horse) -> horse.setGrowingAge(0)
        ));

        if (ModuleWorld.villagerTrackman != null) {
            MOBS.add(SurpriseEntity.create(EntityZombieVillager.class, 200, 1,
                    (cart, villager) -> villager.setForgeProfession(ModuleWorld.villagerTrackman)
            ));
        }
    }

    public EntityCartPumpkin(World world) {
        super(world);
    }

    public EntityCartPumpkin(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.PUMPKIN;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        setBlastRadius(1.5f);
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.PUMPKIN.getDefaultState();
    }

    @Override
    protected float getMinBlastRadius() {
        return 0.5f;
    }

    @Override
    protected float getMaxBlastRadius() {
        return 4;
    }

    @Override
    protected void spawnSurprises() {
        MOBS.spawnSurprises(this);
        POTIONS.spawnSurprises(this);
    }
}
