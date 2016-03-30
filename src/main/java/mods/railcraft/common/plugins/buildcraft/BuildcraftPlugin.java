package mods.railcraft.common.plugins.buildcraft;

import buildcraft.api.statements.StatementManager;

import mods.railcraft.common.plugins.buildcraft.actions.ActionProvider;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.TriggerProvider;
import mods.railcraft.common.plugins.buildcraft.triggers.Triggers;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BuildcraftPlugin {

    public static void init() {
        try {
            MinecraftForge.EVENT_BUS.register(new EventHook());
            StatementManager.registerTriggerProvider(new TriggerProvider());
            StatementManager.registerActionProvider(new ActionProvider());
        } catch (Throwable error) {
            Game.logErrorAPI("Buildcraft", error, StatementManager.class);
        }
    }

    public static void addFacade(Block block, int meta) {
        if (block == null) return;
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%s@%d", GameData.getBlockRegistry().getNameForObject(block), meta));
    }

    public static class EventHook {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void textureStitchPre(TextureStitchEvent.Pre event) {
            Actions.textureStitchPre(event.map);
            Triggers.textureStitchPre(event.map);
        }
    }
}
