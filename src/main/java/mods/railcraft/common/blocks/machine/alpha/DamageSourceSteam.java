/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.DamageSource;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DamageSourceSteam extends DamageSource {

    public static final DamageSourceSteam INSTANCE = new DamageSourceSteam();

    private DamageSourceSteam() {
        super("steam");
//        setDamageBypassesArmor();
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entity) {
        String format = LocalizationPlugin.translate("damage.steam." + (MiscTools.getRand().nextInt(5) + 1));
        return ChatPlugin.getMessage(String.format(format, entity.getCommandSenderName()));
    }

    @SubscribeEvent
    public void modifyDrops(LivingDropsEvent event) {
        if (event.source == this)
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
