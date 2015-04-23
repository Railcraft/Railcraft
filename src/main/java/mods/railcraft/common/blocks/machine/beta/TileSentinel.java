/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSentinel extends TileMachineBase {

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.SENTINEL;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, xCoord, yCoord, zCoord)) {
                WorldCoordinate target = TileAnchorWorld.getTarget(player);
                if (target == null)
                    TileAnchorWorld.setTarget(this, player);
                else if (worldObj.provider.dimensionId != target.dimension)
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
                crowbar.onWhack(player, current, xCoord, yCoord, zCoord);
                return true;
            }
        }
        return super.blockActivated(player, side);
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
