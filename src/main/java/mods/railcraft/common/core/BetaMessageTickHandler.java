/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Created by CovertJaguar on 3/22/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum BetaMessageTickHandler {
    INSTANCE;
    private static final String[] lines = {
            "You are using a development version of Railcraft.",
            "There is no guarantee that your server or worlds are safe.",
            "There is no guarantee that game breaking bugs will be fixed prior to the next stable build.",
            "There is no guarantee when the next build will be available or where it will be uploaded.",
            "You use this at your own risk, as is. Bugs and all.",
            "This build should not be used in modpacks.",
            "Have a nice day and enjoy the mod!",
            "- CovertJaguar"
    };
    private static final int DELAY = 2;
    private static final int INTERVAL = 64;
    private static final float CHANCE = 0.25F;
    private int lineCounter;
    private int startCounter;

    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent event) {
        lineCounter = 0;
        startCounter = 0;
    }

    @SubscribeEvent
    public void tick(LivingEvent.LivingUpdateEvent event) {
        if (Game.DEVELOPMENT_VERSION
                && lineCounter < lines.length
                && event.getEntity().world.getWorldTime() % INTERVAL == 0
                && Game.isClient(event.getEntity().world)
                && event.getEntity() instanceof EntityPlayer) {
            startCounter++;
            if (startCounter > DELAY && (lineCounter > 0 || MiscTools.RANDOM.nextFloat() < CHANCE)) {
                sendMessage((EntityPlayer) event.getEntity(), lines[lineCounter]);
                lineCounter++;
            }
        }
    }

    private void sendMessage(EntityPlayer player, String msg) {
        player.sendMessage(ChatPlugin.makeMessage(msg).setStyle(new Style().setColor(TextFormatting.RED)));
    }
}
