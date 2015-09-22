/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import mods.railcraft.client.render.FluidRenderer;
import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.carts.EntityCartTank;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererTank extends CartContentRendererFiltered {
    private final RenderInfo fillBlock = new RenderInfo(0.4f, 0.0f, 0.4f, 0.6f, 0.999f, 0.6f);

    public CartContentRendererTank() {
        fillBlock.texture = new IIcon[6];
    }

    @Override
    public void renderOther(RenderCart renderer, EntityMinecart cart, float light, float time, int x, int y, int z) {
        EntityCartTank cartTank = (EntityCartTank) cart;
        StandardTank tank = cartTank.getTankManager().get(0);
        if (tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            if (displayLists != null) {
                GL11.glPushMatrix();

                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                GL11.glTranslatef(0, 0.0625f, 0);

                float cap = tank.getCapacity();
                float level = Math.min(tank.renderData.amount, cap) / cap;

                renderer.bindTex(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                FluidRenderer.setColorForTank(tank);
                GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

                if (cartTank.isFilling()) {
                    ResourceLocation texSheet = FluidRenderer.setupFlowingLiquidTexture(tank.renderData.fluid, fillBlock.texture);
                    if (texSheet != null) {
                        renderer.bindTex(texSheet);
                        RenderFakeBlock.renderBlockForEntity(fillBlock, cart.worldObj, x, y, z, false, true);
                    }
                }

                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }
}
