/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.world;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by CovertJaguar on 5/16/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class GoggleAuraWorldRenderer {

    public static GoggleAuraWorldRenderer INSTANCE = new GoggleAuraWorldRenderer();
    public Collection<CartInfo> cartInfos = new LinkedList<>();

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (cartInfos.isEmpty()) {
            return;
        }

        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (ItemGoggles.isPlayerWearing(player)) {
            ItemStack goggles = ItemGoggles.getGoggles(player);
            ItemGoggles.GoggleAura aura = ItemGoggles.getCurrentAura(goggles);
            if (aura == ItemGoggles.GoggleAura.SHUNTING) {
                OpenGL.glPushMatrix();
                final double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
                final double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
                final double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

                GlStateManager.translate(-px, -py, -pz);

                GL11.glNormal3f(0.0F, 0.0F, 1.0F);

                OpenGL.glDisable(GL11.GL_LIGHTING);
                OpenGL.glEnable(GL11.GL_BLEND);
                OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                OpenGL.glDisable(GL11.GL_TEXTURE_2D);

                OpenGL.glEnable(GL11.GL_LINE_SMOOTH);
                OpenGL.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                OpenGL.glLineWidth(4F);

                OpenGL.glBegin(GL11.GL_LINES);

                World world = Minecraft.getMinecraft().theWorld;
                for (CartInfo cartInfo : cartInfos) {
                    EntityMinecart cart = CartTools.getCartFromUUID(world, cartInfo.id);
                    if (cart == null) {
                        continue;
                    }
                    setColor(cartInfo.train.hashCode());
                    OpenGL.glVertex(cart.getPositionVector());
                    Vec3d top = cart.getPositionVector().addVector(0.0, 2.0, 0.0);
                    OpenGL.glVertex(top);

                    renderLink(world, top, cartInfo.linkA);
                    renderLink(world, top, cartInfo.linkB);
                }
                OpenGL.glEnd();

                OpenGL.glLineWidth(2F);
                OpenGL.glEnable(GL11.GL_TEXTURE_2D);
                OpenGL.glPopMatrix();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (Game.isClient(event.getWorld())) {
            cartInfos.clear();
        }
    }

    private void setColor(int color) {
        float c1 = (float) (color >> 16 & 255) / 255.0F;
        float c2 = (float) (color >> 8 & 255) / 255.0F;
        float c3 = (float) (color & 255) / 255.0F;
        OpenGL.glColor4f(c1, c2, c3, 0.8F);
    }

    private void renderLink(World world, Vec3d top, UUID linkId) {
        EntityMinecart link = CartTools.getCartFromUUID(world, linkId);
        if (link != null) {
            OpenGL.glVertex(top);
            OpenGL.glVertex(link.getPositionVector().addVector(0.0, 1.5, 0.0));
        }
    }

    public static class CartInfo {

        public final UUID id;
        public final UUID train;
        public final UUID linkA;
        public final UUID linkB;

        public CartInfo(UUID id, UUID train, UUID linkA, UUID linkB) {
            this.id = id;
            this.train = train;
            this.linkA = linkA;
            this.linkB = linkB;
        }

    }
}
