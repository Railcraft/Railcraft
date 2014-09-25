/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.util.DamageSource;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IChatComponent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DamageSourceTrain extends DamageSource {

    public static final DamageSourceTrain INSTANCE = new DamageSourceTrain();

    public DamageSourceTrain() {
        super("train");
        setDamageBypassesArmor();
//        setDamageAllowedInCreativeMode();
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entity) {
        String format = LocalizationPlugin.translate("railcraft.damage.train." + (MiscTools.getRand().nextInt(5) + 1));
        return ChatPlugin.getMessage(String.format(format, entity.getCommandSenderName()));
    }

}
