package mods.railcraft.client.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.signals.TileBoxAnalogController;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.opengl.GL11;

public class GuiBoxAnalogController extends GuiBasic {

	private final TileBoxAnalogController tile;
	private final static int N_OF_ASPECTS = TileBoxAnalogController.N_OF_ASPECTS;
	private final static Pattern patternRange = Pattern.compile("(\\d+)-(\\d+)|(\\d+)");
	//When doing Pattern.matcher, these are the groups:           ^ 1    ^ 2    ^ 3
	
	private boolean enableAspect[][];
	private GuiTextField textbox[];
	
	public GuiBoxAnalogController(TileBoxAnalogController t) {
		super(t.getName());
		tile = t;
		enableAspect = t.enableAspect;
		textbox = new GuiTextField[N_OF_ASPECTS];
	}
	
	@Override 
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		for(GuiTextField t : textbox)
			t.mouseClicked(i, j, k);
	}
	
	@Override
	public void keyTyped(char c, int i) {
		super.keyTyped(c, i);
		//Disallow any PRINTABLE characters that are not digits, commas, or dashes
		if(c < ' ' || (c >= '0' && c <= '9') || c == '-' || c == ',' || c > '~')
			for(GuiTextField t : textbox)
				t.textboxKeyTyped(c, i);
	}
	
	private String rangeToString(boolean b[]) {
		String s = "";
		int start = -1;
		for(int i = 0; i < 16; i++) {
			if(b[i]) {
				if(start == -1) {
					s += i;
					start = i;
				}
			} else if(start != -1) {
				if(i - 1 == start)
					s += ",";
				else
					s += "-" + (i-1) + ",";
				start = -1;
			}
		}
		if(start != -1 && start != 15) {
			s += "-15";
			start = 15;
		}
		
		if(s.isEmpty() || start == 15)
			return s;
		else
			return s.substring(0, s.length()-1);	//Remove trailing comma
	}
	
	private boolean[] parseRegex(String regex) {
		boolean b[] = new boolean[16];
		Matcher m = patternRange.matcher(regex);
		while(m.find()) {
			if(m.groupCount() >= 3 && m.group(3) != null) {
				int i = Integer.parseInt(m.group(3));
				if(i >= 0 && i <= 15)
					b[i] = true;
			} else {
				int start = Integer.parseInt(m.group(1));
				int end = Integer.parseInt(m.group(2));
				if(start >= 0 && end >= 0 && start <= 15 && end <= 15 && start <= end)
					for(int i = start; i <= end; i++)
						b[i] = true;
			}
		}
		
		return b;
	}
	
	@Override
    public void initGui() {
        if (tile == null)
            return;
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        
        for(int i = 0; i < N_OF_ASPECTS; i++) {
        	textbox[i] = new GuiTextField(fontRendererObj, w + 72, h + getYPosFromIndex(i), 95, 10);
        	textbox[i].setMaxStringLength(37);
        	textbox[i].setText(rangeToString(enableAspect[i]));
        }

	}
	
	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		for(GuiTextField t : textbox)
			t.drawTextBox();
	}
	
	@Override
	protected void drawExtras(int x, int y, float f) {
		for(int i = 0; i < N_OF_ASPECTS; i++)
		{
			drawAlignedString(fontRendererObj, LocalizationPlugin.translate(SignalAspect.values()[i].getLocalizationTag()), 10, getYPosFromIndex(i) + 1, 50);
		}
	}
	
	@Override
	public void updateScreen() {
		for(GuiTextField t : textbox)
			t.updateCursorCounter();
		
		super.updateScreen();
	}
	
	@Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
        	for(int i = 0; i < N_OF_ASPECTS; i++)
        		enableAspect[i] = parseRegex(textbox[i].getText());
        	tile.enableAspect = enableAspect;
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }
	
	private static void drawAlignedString(FontRenderer fr, String s, int x, int y, int width) {
		fr.drawString(s, x + (width - fr.getStringWidth(s))/2, y, 0x404040);
	}
	
	private static int getYPosFromIndex(int i) {
		return 17 + i*14;
	}
}
