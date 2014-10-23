package mods.railcraft.client.gui;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.signals.TileBoxAnalogController;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiBoxAnalogController extends GuiBasic {

	private final TileBoxAnalogController tile;
	private boolean aspectMode[];
	private int signalLow[] ,signalHigh[];
	private final static int N_OF_ASPECTS = TileBoxAnalogController.N_OF_ASPECTS;
	private final static ResourceLocation loc = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_analog.png");
	
	public GuiBoxAnalogController(TileBoxAnalogController t)
	{
		super(t.getName(), loc, 240, 140);
		tile = t;
		aspectMode = t.aspectMode;
		signalLow = t.signalLow;
		signalHigh = t.signalHigh;
	}
	
	@Override
    public void initGui() {
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        
        for(int i = 0; i < N_OF_ASPECTS; i++)
        {
        	int y = h + 20 + i*23;
        	buttonList.add(new GuiToggleButton(i, w + 10, y, 70,
        			LocalizationPlugin.translate(SignalAspect.values()[i].getLocalizationTag()), aspectMode[i]));
        	buttonList.add(new GuiButton(N_OF_ASPECTS + i, w + 85, y, 20, 20, "<"));
        	buttonList.add(new GuiButton(N_OF_ASPECTS*2 + i, w + 125, y, 20, 20, ">"));
        	buttonList.add(new GuiButton(N_OF_ASPECTS*3 + i, w + 170, y, 20, 20, "<"));
        	buttonList.add(new GuiButton(N_OF_ASPECTS*4 + i, w + 210, y, 20, 20, ">"));
        }
	}
	
	@Override
    protected void drawExtras(int x, int y, float f) {
		for(int i = 0; i < N_OF_ASPECTS; i++)
		{
			int yPos = 25 + i*23;
			drawAlignedString(fontRendererObj, String.valueOf(signalLow[i]), 110, yPos, 10);
			drawAlignedString(fontRendererObj, "to", 150, yPos, 15);
			drawAlignedString(fontRendererObj, String.valueOf(signalHigh[i]), 195, yPos, 10);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(tile == null)
			return;
		
		int type = button.id / N_OF_ASPECTS;
		int id = button.id % N_OF_ASPECTS;
		//Aspect enable/disable
		if(type == 0)
		{
			((GuiToggleButton)button).toggle();
			aspectMode[id] = !aspectMode[id];
		}
		//Low signal decrease
		else if(type == 1)
			signalLow[id] = Math.max(PowerPlugin.NO_POWER, signalLow[id] - 1);
		//Low signal increase
		else if(type == 2)
			signalLow[id] = Math.min(signalLow[id] + 1, signalHigh[id]);
		//High signal decrease
		else if(type == 3)
			signalHigh[id] = Math.max(signalLow[id], signalHigh[id] - 1);
		//High signal increase
		else if(type == 4)
			signalHigh[id] = Math.min(signalHigh[id] + 1, PowerPlugin.FULL_POWER);
	}
	
	@Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.aspectMode = aspectMode;
            tile.signalLow = signalLow;
            tile.signalHigh = signalHigh;
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }
	
	public static void drawAlignedString(FontRenderer fr, String s, int x, int y, int width)
	{
		fr.drawString(s, x + (width - fr.getStringWidth(s))/2, y, 0x404040);
	}
}
