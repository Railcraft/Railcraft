package mods.railcraft.common.plugins.buildcraft;

import buildcraft.api.gates.ActionManager;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import mods.railcraft.common.plugins.buildcraft.actions.ActionProvider;
import mods.railcraft.common.plugins.buildcraft.triggers.TriggerProvider;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BuildcraftPlugin {

    public static void init() {
        try {
            ActionManager.registerTriggerProvider(new TriggerProvider());
            ActionManager.registerActionProvider(new ActionProvider());
        } catch (Throwable error) {
            Game.logErrorAPI("Buildcraft", error, ActionManager.class);
        }
    }

    public static void addFacade(Block block, int meta) {
        if (block == null) return;
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%s@%d", GameData.blockRegistry.getNameForObject(block), meta));
    }
}
