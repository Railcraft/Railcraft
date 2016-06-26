package uristqwerty.CraftGuide.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class LiquidSlot implements Slot
{
	private int x;
	private int y;
	private int width = 16;
	private int height = 16;
	private SlotType slotType = SlotType.INPUT_SLOT;

	private static NamedTexture containerTexture = null;

	public LiquidSlot(int x, int y)
	{
		this.x = x;
		this.y = y;

		if(containerTexture == null)
		{
			containerTexture = Util.instance.getTexture("liquidFilterContainer");
		}
	}

	@Override
	public void draw(Renderer renderer, int recipeX, int recipeY, Object[] data, int dataIndex, boolean isMouseOver)
	{
		int x = recipeX + this.x;
		int y = recipeY + this.y;

		if(data[dataIndex] instanceof FluidStack)
		{
			FluidStack liquid = (FluidStack)data[dataIndex];

			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

			Fluid fluid = liquid.getFluid();
			TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill(liquid).toString());

			if(icon != null)
			{
				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

                double u = icon.getInterpolatedU(3.0);
                double u2 = icon.getInterpolatedU(13.0);
                double v = icon.getInterpolatedV(1.0);
                double v2 = icon.getInterpolatedV(15.0);

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        		GL11.glBegin(GL11.GL_QUADS);
        	        GL11.glTexCoord2d(u, v);
        	        GL11.glVertex2i(x + 3, y + 1);

        	        GL11.glTexCoord2d(u, v2);
        	        GL11.glVertex2i(x + 3, y + 15);

        	        GL11.glTexCoord2d(u2, v2);
        	        GL11.glVertex2i(x + 13, y + 15);

        	        GL11.glTexCoord2d(u2, v);
        	        GL11.glVertex2i(x + 13, y + 1);
        		GL11.glEnd();
			}
		}

		renderer.renderRect(x - 1, y - 1, 18, 18, containerTexture);
	}

	@Override
	public ItemFilter getClickedFilter(int x, int y, Object[] data, int dataIndex)
	{
		if(data[dataIndex] instanceof FluidStack)
		{
			return new LiquidFilter((FluidStack)data[dataIndex]);
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean isPointInBounds(int x, int y, Object[] data, int dataIndex)
	{
		return x >= this.x && x < this.x + width
			&& y >= this.y && y < this.y + height;
	}

	@Override
	public List<String> getTooltip(int x, int y, Object[] data, int dataIndex)
	{
		List<String> tooltip = null;

		if(data[dataIndex] instanceof FluidStack)
		{
			tooltip = new ArrayList<String>(1);
			FluidStack stack = (FluidStack)data[dataIndex];
			tooltip.add(stack.getFluid().getLocalizedName(stack) + " (" + stack.amount + " milliBuckets)");
		}

		return tooltip;
	}

	//Check if the filter directly matches the contained LiquidStack, or
	// matches any liquid container containing the liquid.
	@Override
	public boolean matches(ItemFilter filter, Object[] data, int dataIndex, SlotType type)
	{
		if(!(data[dataIndex] instanceof FluidStack) ||
				(type != slotType && (
					type != SlotType.ANY_SLOT ||
					slotType == SlotType.DISPLAY_SLOT ||
					slotType == SlotType.HIDDEN_SLOT)))
		{
			return false;
		}

		FluidStack stack = (FluidStack)data[dataIndex];

		if(filter.matches(stack))
		{
			return true;
		}
		else
		{
			for(FluidContainerData liquidData: FluidContainerRegistry.getRegisteredFluidContainerData())
			{
				if(stack.isFluidEqual(liquidData.fluid) && filter.matches(liquidData.filledContainer))
				{
					return true;
				}
			}

			return false;
		}
	}

	public Slot setSlotType(SlotType type)
	{
		slotType = type;
		return this;
	}
}
