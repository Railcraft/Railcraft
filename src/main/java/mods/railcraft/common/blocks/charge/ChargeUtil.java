/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.charge.IChargeProtectionItem;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum ChargeUtil implements IChargeUtil {
    INSTANCE;

    static {
        Charge.util = INSTANCE;
    }

    private final Map<World, ChargeNetwork> chargeNetworks = new MapMaker().weakKeys().makeMap();

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
            Charge.util.getNetwork(event.world).tick();
    }

    @Override
    public ChargeNetwork getNetwork(World world) {
        return chargeNetworks.computeIfAbsent(world, ChargeNetwork::new);
    }

    @Override
    public void zapEntity(World world, BlockPos pos, Entity entity, DamageSource damageSource, float damage) {
        if (Game.isClient(world))
            return;

        if (!MiscTools.isKillableEntity(entity))
            return;

        double chargeCost = damage * Charge.CHARGE_PER_DAMAGE;

        ChargeNetwork.ChargeNode node = getNetwork(world).getNode(pos);
        if (node.getChargeGraph().getCharge() > chargeCost) {
            float remainingDamage = damage;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) entity;
                EnumMap<EntityEquipmentSlot, IChargeProtectionItem> protections = new EnumMap<>(EntityEquipmentSlot.class);
                EnumSet.allOf(EntityEquipmentSlot.class).forEach(slot -> {
                            IChargeProtectionItem protection = getChargeProtection(livingEntity, slot);
                            if (protection != null)
                                protections.put(slot, protection);
                        }
                );
                for (Map.Entry<EntityEquipmentSlot, IChargeProtectionItem> e : protections.entrySet()) {
                    if (remainingDamage > 0.1) {
                        IChargeProtectionItem.ZepResult result = e.getValue().zap(livingEntity.getItemStackFromSlot(e.getKey()), livingEntity, remainingDamage);
                        entity.setItemStackToSlot(e.getKey(), result.stack);
                        remainingDamage -= result.damagePrevented;
                    } else break;
                }
            }
            if (remainingDamage > 0.1 && entity.attackEntityFrom(damageSource, remainingDamage)) {
                node.removeCharge(chargeCost);
                EffectManager.instance.zapEffectDeath(world, entity);
            }
        }
    }

    private @Nullable IChargeProtectionItem getChargeProtection(EntityLivingBase entity, EntityEquipmentSlot slot) {
        ItemStack stack = entity.getItemStackFromSlot(slot);
        Item item = stack.getItem();
        if (item instanceof IChargeProtectionItem && ((IChargeProtectionItem) item).isZapProtectionActive(stack, entity)) {
            return (IChargeProtectionItem) item;
        }
        if (ModItems.RUBBER_BOOTS.isEqual(stack, false, false)
                || ModItems.STATIC_BOOTS.isEqual(stack, false, false)) {
            return new IChargeProtectionItem() {
            };
        }
        return null;
    }
}
