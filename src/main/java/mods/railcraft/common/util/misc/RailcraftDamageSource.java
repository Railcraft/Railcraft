/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RailcraftDamageSource extends DamageSource {

    public static final RailcraftDamageSource BORE = new RailcraftDamageSource("bore");
    public static final RailcraftDamageSource CRUSHER = new RailcraftDamageSource("crusher", 8);
    public static final RailcraftDamageSource STEAM = new RailcraftDamageSource("steam");
    public static final RailcraftDamageSource TRACK_ELECTRIC = new RailcraftDamageSource("track.electric");
    public static final RailcraftDamageSource TRAIN = new RailcraftDamageSource("train");

    static {
        BORE.setDamageBypassesArmor();
        TRACK_ELECTRIC.setDamageBypassesArmor();
        TRAIN.setDamageBypassesArmor();
    }

    private final int numMessages;

    private RailcraftDamageSource(String tag) {
        this(tag, 6);
    }

    private RailcraftDamageSource(String tag, int numMessages) {
        super(tag);
        this.numMessages = numMessages;
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entity) {
        String locTag = "death.railcraft." + damageType + "." + (MiscTools.getRand().nextInt(numMessages) + 1);
        return ChatPlugin.chatComp(locTag, entity.getCommandSenderName());
    }

    public static final EventHandler EVENT_HANDLER = new EventHandler();

    public static class EventHandler {

        @SubscribeEvent
        public void modifyDrops(LivingDropsEvent event) {
            if (event.source == STEAM)
                for (EntityItem entityItem : event.drops) {
                    ItemStack drop = entityItem.getEntityItem();
                    ItemStack cooked = FurnaceRecipes.smelting().getSmeltingResult(drop);
                    if (cooked != null && MiscTools.RANDOM.nextDouble() < 0.5) {
                        cooked = cooked.copy();
                        cooked.stackSize = drop.stackSize;
                        entityItem.setEntityItemStack(cooked);
                    }
                }
        }

    }

}
