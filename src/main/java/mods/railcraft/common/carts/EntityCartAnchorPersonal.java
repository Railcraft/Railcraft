/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nonnull;

/**
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
            if (PlayerPlugin.isPlayerConnected(CartToolsAPI.getCartOwner(this)))
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
        return RailcraftCarts.getCartType(stack) == RailcraftCarts.ANCHOR_PERSONAL;
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), CartToolsAPI.getCartOwner(this).getName(), worldObj, ForgeChunkManager.Type.ENTITY);
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelPersonal;
    }

    @Override
    protected boolean meetsTicketRequirements() {
        return PlayerPlugin.isPlayerConnected(CartToolsAPI.getCartOwner(this)) && super.meetsTicketRequirements();
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return EnumMachineAlpha.ANCHOR_PERSONAL.getDefaultState().withProperty(TileAnchorWorld.DISABLED, !hasTicketFlag());
    }
}
