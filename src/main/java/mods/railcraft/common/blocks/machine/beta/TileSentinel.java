/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSentinel extends TileMachineBase {

    @Override
    public EnumMachineBeta getMachineType() {
        return EnumMachineBeta.SENTINEL;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                WorldCoordinate target = TileAnchorWorld.getTarget(player);
                if (target == null)
                    TileAnchorWorld.setTarget(this, player);
                else if (worldObj.provider.getDimension() != target.getDim())
                    ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.dimension", getLocalizationTag());
                else if (new WorldCoordinate(this).equals(target)) {
                    TileAnchorWorld.removeTarget(player);
                    ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.cancel", getLocalizationTag());
                } else {
                    TileEntity tile = TileAnchorWorld.getTargetAt(player, this, target);
                    if (tile instanceof TileAnchorWorld)
                        ((TileAnchorWorld) tile).setSentinel(player, new WorldCoordinate(this));
                    else if (tile != null)
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.invalid", getLocalizationTag());
                }
                crowbar.onWhack(player, hand, heldItem, getPos());
                return true;
            }
        }
        return super.blockActivated(player, hand, heldItem, side);
    }

    @Override
    public float getResistance(Entity exploder) {
        return 60f;
    }

    @Override
    public float getHardness() {
        return 20;
    }
}
