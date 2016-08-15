/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CollisionHandlerElectric extends CollisionHandler {
    private static CollisionHandler instance;

    public static CollisionHandler instance() {
        if (instance == null) {
            instance = new CollisionHandler();
        }
        return instance;
    }

    protected CollisionHandlerElectric() {
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (Game.isClient(world))
            return;

        if (!MiscTools.isKillableEntity(entity))
            return;

        ChargeNetwork.ChargeGraph graph = ChargeManager.getNetwork(world).getGraph(pos);
        if (graph.getCharge() > 2000)
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = ((EntityPlayer) entity);
                ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
                if (pants != null && RailcraftItems.OVERALLS.isInstance(pants)
                        && !((EntityPlayer) entity).capabilities.isCreativeMode
                        && MiscTools.RANDOM.nextInt(150) == 0) {
                    player.setItemStackToSlot(EntityEquipmentSlot.LEGS, InvTools.damageItem(pants, 1));
                }
            } else if (entity.attackEntityFrom(RailcraftDamageSource.TRACK_ELECTRIC, 2))
                graph.removeCharge(2000);
    }
}
