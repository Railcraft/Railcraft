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
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlock.MultiBlockStateReturn;
import mods.railcraft.common.blocks.signals.IDualHeadSignal;
import mods.railcraft.common.blocks.signals.TileSignalBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import java.util.EnumSet;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMagnifyingGlass extends ItemRailcraft implements IActivationBlockingItem {
    public static Item item;

    public ItemMagnifyingGlass() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setUnlocalizedName("railcraft.tool.magnifying.glass");
        setFull3D();

        MinecraftForge.EVENT_BUS.register(this);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static void register() {
        if (item == null) {
            String tag = "tool.magnifying.glass";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemMagnifyingGlass();
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item),
                        " G",
                        "S ",
                        'S', "stickWood",
                        'G', "paneGlassColorless"
                );

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
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
                ChatPlugin.sendLocalizedChatFromServer(thePlayer, "railcraft.gui.mag.glass.placedby", LocalizationPlugin.getEntityLocalizationTag(cart), CartTools.getCartOwner(cart));
                event.setCanceled(true);
            }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (Game.isNotHost(world))
            return false;
        TileEntity t = world.getTileEntity(x, y, z);
        boolean returnValue = false;
        if (t instanceof IOwnable) {
            IOwnable ownable = (IOwnable) t;
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.mag.glass.placedby", ownable.getLocalizationTag(), ownable.getOwner());
            returnValue = true;
        }
        if (t instanceof TileMultiBlock) {
            TileMultiBlock tile = (TileMultiBlock) t;
            if (tile.isStructureValid())
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.multiblock.state.valid");
            else
                for (MultiBlockStateReturn returnState : EnumSet.complementOf(EnumSet.of(MultiBlockStateReturn.VALID))) {
                    List<Integer> pats = tile.patternStates.get(returnState);
                    if (!pats.isEmpty())
                        ChatPlugin.sendLocalizedChatFromServer(player, returnState.message, pats.toString());
                }
            returnValue = true;
        }
        if (t instanceof IDualHeadSignal) {
            IDualHeadSignal signal = (IDualHeadSignal) t;
            SignalAspect top = signal.getTopAspect();
            SignalAspect bottom = signal.getBottomAspect();
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.mag.glass.aspect.dual", top.getLocalizationTag(), bottom.getLocalizationTag());
            returnValue = true;
        } else if (t instanceof TileSignalBase) {
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.mag.glass.aspect", ((TileSignalBase) t).getSignalAspect().getLocalizationTag());
            returnValue = true;
        }
        return returnValue;
    }
}
