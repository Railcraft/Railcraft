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
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeManager {
    private static final Map<World, ChargeNetwork> chargeNetworks = new MapMaker().weakKeys().makeMap();

    public static ChargeNetwork getNetwork(World world) {
        return chargeNetworks.computeIfAbsent(world, ChargeNetwork::new);
    }

    public static ChargeManager getEventListener() {
        return new ChargeManager();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
            getNetwork(event.world).tick();
    }

    public static void forConnections(World world, BlockPos pos, BiConsumer<BlockPos, IChargeBlock.ChargeDef> action) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (state.getBlock() instanceof IChargeBlock) {
            IChargeBlock block = (IChargeBlock) state.getBlock();
            IChargeBlock.ChargeDef chargeDef = block.getChargeDef(state, world, pos);
            if (chargeDef != null) {
                Map<BlockPos, EnumSet<IChargeBlock.ConnectType>> possibleConnections = chargeDef.getConnectType().getPossibleConnectionLocations(pos);
                for (Map.Entry<BlockPos, EnumSet<IChargeBlock.ConnectType>> connection : possibleConnections.entrySet()) {
                    IBlockState otherState = WorldPlugin.getBlockState(world, connection.getKey());
                    if (otherState.getBlock() instanceof IChargeBlock) {
                        IChargeBlock.ChargeDef other = ((IChargeBlock) otherState.getBlock()).getChargeDef(WorldPlugin.getBlockState(world, connection.getKey()), world, connection.getKey());
                        if (other != null && other.getConnectType().getPossibleConnectionLocations(connection.getKey()).get(pos).contains(chargeDef.getConnectType())) {
                            action.accept(connection.getKey(), other);
                        }
                    }
                }
            }
        }
    }

    public static void zapEntity(World world, BlockPos pos, IBlockState state, Entity entity, DamageSource damageSource, float damage, double chargeCost) {
        if (Game.isClient(world))
            return;

        if (!MiscTools.isKillableEntity(entity))
            return;

        ChargeNetwork.ChargeNode node = ChargeManager.getNetwork(world).getNode(pos);
        if (node.getChargeGraph().getCharge() > chargeCost) {
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

    @Nullable
    private static ItemStack getOveralls(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entity);
            ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            if (!InvTools.isEmpty(pants) && RailcraftItems.OVERALLS.isInstance(pants) && !((EntityPlayer) entity).capabilities.isCreativeMode)
                return pants;
        }
        return InvTools.emptyStack();
    }

    @Nullable
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
