/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.items.firestone.EntityItemFirestone;
import mods.railcraft.common.items.firestone.FirestoneTickHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule("railcraft:magic")
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

                        RailcraftBlocks.RITUAL
//                        RailcraftBlocks.ore
                );
            }

            @Override
            public void preInit() {
                EntityItemFirestone.register();

                FMLCommonHandler.instance().bus().register(new FirestoneTickHandler());
            }

        });
    }

}
