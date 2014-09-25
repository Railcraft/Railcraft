/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.ItemMap;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartAnchorPersonal extends EntityCartAnchor {

    private static final int MINUTES_BEFORE_DISABLE = 5;
    private long ticksSincePlayerLogged;

    public EntityCartAnchorPersonal(World world) {
        super(world);
    }

    public EntityCartAnchorPersonal(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void onUpdate() {
        if (ticket != null) {
            if (PlayerPlugin.isPlayerConnected(CartTools.getCartOwner(this)))
                ticksSincePlayerLogged = 0;
            else
                ticksSincePlayerLogged++;
            if (ticksSincePlayerLogged > RailcraftConstants.TICKS_PER_MIN * MINUTES_BEFORE_DISABLE)
                releaseTicket();
        }
        super.onUpdate();
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == EnumCart.ANCHOR_PERSONAL;
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), CartTools.getCartOwner(this).getName(), worldObj, Type.ENTITY);
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelPersonal;
    }

    @Override
    protected boolean meetsTicketRequirements() {
        return PlayerPlugin.isPlayerConnected(CartTools.getCartOwner(this)) && super.meetsTicketRequirements();
    }

    @Override
    public int getDisplayTileData() {
        return EnumMachineAlpha.PERSONAL_ANCHOR.ordinal();
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ANCHOR_PERSONAL.getTag());
    }

    @Override
    public IIcon getBlockTextureOnSide(int side) {
        if (side < 2 && !getFlag(TICKET_FLAG))
            return EnumMachineAlpha.PERSONAL_ANCHOR.getTexture(6);
        return EnumMachineAlpha.PERSONAL_ANCHOR.getTexture(side);
    }

}
