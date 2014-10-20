/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlock.MultiBlockStateReturn;
import mods.railcraft.common.blocks.signals.IDualHeadSignal;
import mods.railcraft.common.blocks.signals.TileSignalBase;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMagnifyingGlass extends ItemRailcraft implements IActivationBlockingItem {

    public static Item item;

    public static void register() {
        if (item == null) {
            String tag = "tool.magnifying.glass";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemMagnifyingGlass();
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), new Object[]{
                    " G",
                    "S ",
                    'S', new ItemStack(Items.stick),
                    'G', new ItemStack(Blocks.glass_pane)
                });

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, tag);
            }
//            CreeperPlugin.fixCreepers();
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public ItemMagnifyingGlass() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setUnlocalizedName("railcraft.tool.magnifying.glass");
        setFull3D();

        MinecraftForge.EVENT_BUS.register(this);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        EntityPlayer thePlayer = event.entityPlayer;

        Entity entity = event.target;

        ItemStack stack = thePlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof ItemMagnifyingGlass)
            thePlayer.swingItem();

        if (Game.isNotHost(thePlayer.worldObj))
            return;

        if (stack != null && stack.getItem() instanceof ItemMagnifyingGlass)
            if (entity instanceof EntityMinecart) {
                EntityMinecart cart = (EntityMinecart) entity;
                ChatPlugin.sendLocalizedChat(thePlayer, "railcraft.gui.mag.glass.placedby", Railcraft.getProxy().getItemDisplayName(cart.getCartItem()), PlayerPlugin.getUsername(thePlayer.worldObj, CartTools.getCartOwner(cart)));
                event.setCanceled(true);
            }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (Game.isNotHost(world))
            return false;
        TileEntity t = world.getTileEntity(x, y, z);
        boolean returnValue = false;
        if (t instanceof RailcraftTileEntity) {
            RailcraftTileEntity tile = (RailcraftTileEntity) t;
            ChatPlugin.sendLocalizedChat(player, "railcraft.gui.mag.glass.placedby", tile.getName(), PlayerPlugin.getUsername(world, tile.getOwner()));
            returnValue = true;
        }
        if (t instanceof TileMultiBlock) {
            TileMultiBlock tile = (TileMultiBlock) t;
            if (tile.isStructureValid())
                ChatPlugin.sendLocalizedChat(player, "railcraft.multiblock.state.valid");
            else
                for (MultiBlockStateReturn returnState : EnumSet.complementOf(EnumSet.of(MultiBlockStateReturn.VALID))) {
                    List<Integer> pats = tile.patternStates.get(returnState);
                    if (!pats.isEmpty())
                        ChatPlugin.sendLocalizedChat(player, returnState.message, pats.toString());
                }
            returnValue = true;
        }
        if (t instanceof IDualHeadSignal) {
            IDualHeadSignal signal = (IDualHeadSignal) t;
            SignalAspect top = signal.getTopAspect();
            SignalAspect bottom = signal.getBottomAspect();
            ChatPlugin.sendLocalizedChat(player, "railcraft.gui.mag.glass.aspect.dual", top.getLocalizationTag(), bottom.getLocalizationTag());
            returnValue = true;
        } else if (t instanceof TileSignalBase) {
            ChatPlugin.sendLocalizedChat(player, "railcraft.gui.mag.glass.aspect", ((TileSignalBase) t).getSignalAspect().getLocalizationTag());
            returnValue = true;
        }
        return returnValue;
    }

}
