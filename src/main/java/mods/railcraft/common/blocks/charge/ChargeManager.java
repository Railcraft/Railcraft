/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.charge.ChargeApiAccess;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeManager {
    private static final Map<World, ChargeDimension> chargeNetworks = new MapMaker().weakKeys().makeMap();

    {
        ChargeApiAccess.setDimensionHook(ChargeManager::getDimension);
    }

    public static ChargeDimension getDimension(World world) {
        return chargeNetworks.computeIfAbsent(world, ChargeDimension::new);
    }

    public static ChargeManager getEventListener() {
        return new ChargeManager();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
            getDimension(event.world).tick();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        chargeNetworks.put(event.getWorld(), new ChargeDimension(event.getWorld()));
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        chargeNetworks.remove(event.getWorld());
    }

    //TODO these util methods need more general purposes or move to a util class?

    public static void zapEntity(World world, BlockPos pos, IBlockState state, Entity entity, DamageSource damageSource, float damage, double chargeCost) {
        if (Game.isClient(world))
            return;

        if (!MiscTools.isKillableEntity(entity))
            return;

        ChargeNode node = ChargeManager.getDimension(world).getNode(pos);
        if (node.getChargeRegion().getCharge() > chargeCost) {
            boolean shock = true;
            ItemStack overalls = getOveralls(entity);
            ItemStack boots = getRubberBoots(entity);
            if (!InvTools.isEmpty(overalls) && !InvTools.isEmpty(boots)) {
                shock = false;
                if (MiscTools.RANDOM.nextInt(300) == 0)
                    entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, InvTools.damageItem(overalls, 1));
                else if (MiscTools.RANDOM.nextInt(300) == 150)
                    entity.setItemStackToSlot(EntityEquipmentSlot.FEET, InvTools.damageItem(boots, 1));
            }
            if (!InvTools.isEmpty(overalls)) {
                shock = false;
                if (MiscTools.RANDOM.nextInt(150) == 0)
                    entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, InvTools.damageItem(overalls, 1));
            }
            if (!InvTools.isEmpty(boots)) {
                shock = false;
                if (MiscTools.RANDOM.nextInt(150) == 0)
                    entity.setItemStackToSlot(EntityEquipmentSlot.FEET, InvTools.damageItem(boots, 1));
            }
            if (shock && entity.attackEntityFrom(damageSource, damage)) {
                node.removeCharge(chargeCost);
                EffectManager.instance.zapEffectDeath(world, entity);
            }
        }
    }

    private static ItemStack getOveralls(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entity);
            ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            if (!InvTools.isEmpty(pants) && RailcraftItems.OVERALLS.isInstance(pants) && !((EntityPlayer) entity).capabilities.isCreativeMode)
                return pants;
        }
        return InvTools.emptyStack();
    }

    private static ItemStack getRubberBoots(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entity);
            ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (!InvTools.isEmpty(feet) && (ModItems.RUBBER_BOOTS.isEqual(feet, false, false) || ModItems.STATIC_BOOTS.isEqual(feet, false, false)) && !((EntityPlayer) entity).capabilities.isCreativeMode)
                return feet;
        }
        return InvTools.emptyStack();
    }
}
