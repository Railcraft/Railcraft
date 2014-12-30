package mods.railcraft.client.gui;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.signals.TileBoxAnalogController;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiBoxAnalogController extends GuiBasic {

	private final TileBoxAnalogController tile;
	private final static int N_OF_ASPECTS = TileBoxAnalogController.N_OF_ASPECTS;
	private boolean enableAspect[][];
	private final static ResourceLocation loc = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_analog.png");
	
	public GuiBoxAnalogController(TileBoxAnalogController t)
	{
		super(t.getName(), loc, 240, 140);
		tile = t;
		enableAspect = t.enableAspect;
	}
	
	@Override
    public void initGui() {
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        
        for(int i = 0; i < N_OF_ASPECTS; i++)
        	for(int j = 0; j < 16; j++)
        	    buttonList.add(new GuiToggleButton(16*i + j, w + 65 + 15*j, h + 18 + i*21, 15, String.valueOf(j), enableAspect[i][j]));

	}
	
	@Override
    protected void drawExtras(int x, int y, float f) {
		for(int i = 0; i < N_OF_ASPECTS; i++)
		{
			int yPos = 23 + i*21;
			drawAlignedString(fontRendererObj, LocalizationPlugin.translate(SignalAspect.values()[i].getLocalizationTag()), 0, yPos, 64);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(tile == null)
			return;

		int aspect = button.id / 16;
		int strength = button.id % 16;
		
		//Simply toggle the button and its corresponding boolean value
		((GuiToggleButton)button).toggle();
		enableAspect[aspect][strength] ^= true;
	}
	
	@Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.enableAspect = enableAspect;
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }
	
	public static void drawAlignedString(FontRenderer fr, String s, int x, int y, int width)
	{
		fr.drawString(s, x + (width - fr.getStringWidth(s))/2, y, 0x404040);
	}
}
