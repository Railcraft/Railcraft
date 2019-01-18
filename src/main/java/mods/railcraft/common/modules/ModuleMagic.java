/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.items.firestone.EntityItemFirestone;
import mods.railcraft.common.items.firestone.FirestoneTickHandler;
import mods.railcraft.common.items.potion.RailcraftPotionTypes;
import mods.railcraft.common.items.potion.RailcraftPotions;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:magic", description = "firestone, waterstone, icestone")
public class ModuleMagic extends RailcraftModulePayload {
    public ModuleMagic() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.FIRESTONE_CRACKED,
                        RailcraftItems.FIRESTONE_CUT,
                        RailcraftItems.FIRESTONE_RAW,
                        RailcraftItems.FIRESTONE_REFINED,

                        RailcraftBlocks.RITUAL,
                        RailcraftPotions.CREOSOTE,
                        RailcraftPotionTypes.CREOSOTE,
                        RailcraftPotionTypes.LONG_CREOSOTE,
                        RailcraftPotionTypes.STRONG_CREOSOTE
                );
            }

            @Override
            public void preInit() {
                EntityItemFirestone.register();

                RailcraftEnchantments.registerEnchantments();

                MinecraftForge.EVENT_BUS.register(new FirestoneTickHandler());
            }
        });
    }

}
