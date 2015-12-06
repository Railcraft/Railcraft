/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.gui.buttons.GuiButtonRoutingTableNextPage;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketCurrentItemNBT;
import mods.railcraft.common.util.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@SideOnly(Side.CLIENT)
public class GuiRoutingTable extends GuiScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "routing_table.png");
    public static final int MAX_PAGES = 50;
    public static final int WRAP_WIDTH = 226;
    public static final String TABLE_LOC_TAG = "railcraft.gui.routing.table.";
    public static final String TABLE_MANUAL_LOC_TAG = TABLE_LOC_TAG + "manual.";
    /**
     * The player editing the book
     */
    private final EntityPlayer player;
    private final ItemStack bookStack;
    /**
     * Whether the book is signed or can still be edited
     */
    private final boolean editable = true;
    private boolean bookModified;
    private boolean editingTitle;
    private boolean readingManual;
    /**
     * Update ticks since the gui was opened
     */
    private int updateCount;
    private final int bookImageWidth = 256;
    private final int bookImageHeight = 192;
    private final int numManualPages;
    private int currPage, currLine, currChar;
    private final LinkedList<LinkedList<String>> bookPages;
    private String bookTitle = "";
    private GuiButtonRoutingTableNextPage buttonNextPage;
    private GuiButtonRoutingTableNextPage buttonPreviousPage;
    private GuiBetterButton buttonDone;
    private GuiBetterButton buttonSign;
    private GuiBetterButton buttonHelp;

    public GuiRoutingTable(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.bookStack = stack;

        LinkedList<LinkedList<String>> pages = ItemRoutingTable.getPages(stack);
        if (pages == null) {
            bookPages = new LinkedList<LinkedList<String>>();
            initPages();
        } else {
            bookPages = pages;
            if (bookPages.isEmpty())
                initPages();
        }

        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            bookTitle = nbt.getString("title");
        }

        String locTag = TABLE_MANUAL_LOC_TAG + "numPages";
        int manualPages = 16;
        if (LocalizationPlugin.hasTag(locTag))
            try {
                manualPages = Integer.valueOf(LocalizationPlugin.translate(locTag));
            } catch (NumberFormatException ex) {
            }
        numManualPages = manualPages;
    }

    private void initPages() {
        LinkedList<String> page = new LinkedList<String>();
        bookPages.add(page);
        page.add("");
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        if (editable) {
            List<GuiBetterButton> buttons = new ArrayList<GuiBetterButton>();
            buttons.add(buttonSign = new GuiBetterButton(3, 0, 4 + bookImageHeight, 65, LocalizationPlugin.translate(TABLE_LOC_TAG + "name")));
            buttons.add(buttonHelp = new GuiBetterButton(4, 0, 4 + bookImageHeight, 65, LocalizationPlugin.translate("railcraft.gui.help")));
            buttons.add(buttonDone = new GuiBetterButton(0, 0, 4 + bookImageHeight, 65, StatCollector.translateToLocal("gui.done")));
            GuiTools.newButtonRowAuto(buttonList, width / 2 - 100, 200, buttons);
        } else
            buttonList.add(buttonDone = new GuiBetterButton(0, width / 2 - 100, 4 + bookImageHeight, 200, StatCollector.translateToLocal("gui.done")));

        int xOffset = (width - bookImageWidth) / 2;
        byte yOffset = 2;
        buttonList.add(buttonNextPage = new GuiButtonRoutingTableNextPage(1, xOffset + 200, yOffset + 154, true));
        buttonList.add(buttonPreviousPage = new GuiButtonRoutingTableNextPage(2, xOffset + 30, yOffset + 154, false));
        updateButtons();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private int getMaxPages() {
        if (readingManual)
            return numManualPages;
        if (editingTitle)
            return 0;
        return MAX_PAGES;
    }

    private void updateButtons() {
        buttonNextPage.visible = !editingTitle && (currPage < getMaxPages() - 1);
        buttonPreviousPage.visible = !editingTitle && currPage > 0;

        buttonHelp.displayString = readingManual ? StatCollector.translateToLocal("gui.back") : LocalizationPlugin.translate("railcraft.gui.help");

        if (editable)
            buttonSign.displayString = editingTitle ? StatCollector.translateToLocal("gui.back") : LocalizationPlugin.translate(TABLE_LOC_TAG + "name");
    }

    private void sendBookToServer() {
        if (this.editable && this.bookModified) {
            ItemRoutingTable.setPages(bookStack, bookPages);

            NBTTagCompound nbt = InvTools.getItemData(bookStack);

            nbt.setString("author", Railcraft.proxy.getPlayerUsername(player));
            if (!bookTitle.equals(""))
                nbt.setString("title", bookTitle);

            PacketCurrentItemNBT pkt = new PacketCurrentItemNBT(player, bookStack);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     *
     * @param button
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button == buttonDone) {
                this.mc.displayGuiScreen((GuiScreen) null);
                this.sendBookToServer();
            } else if (button == buttonSign) {
                editingTitle = !editingTitle;
                readingManual = false;
                currPage = 0;
                currLine = 0;
                currChar = 0;
            } else if (button == buttonHelp) {
                readingManual = !readingManual;
                editingTitle = false;
                currPage = 0;
                currLine = 0;
                currChar = 0;
            } else if (button == buttonNextPage) {
                if (readingManual) {
                    if (currPage < numManualPages - 1) {
                        currPage++;
                        currLine = 0;
                        currChar = 0;
                    }
                } else if (currPage < bookPages.size() - 1) {
                    currPage++;
                    currLine = 0;
                    currChar = 0;
                } else if (editable) {
                    addNewPage();

                    if (currPage < bookPages.size() - 1) {
                        currPage++;
                        currLine = 0;
                        currChar = 0;
                    }
                }
            } else if (button == buttonPreviousPage)
                if (currPage > 0) {
                    currPage--;
                    currLine = 0;
                    currChar = 0;
                }

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if (bookPages.size() < 50) {
            LinkedList<String> page = new LinkedList<String>();
            page.add("");
            bookPages.add(page);
            bookModified = true;
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char c, int key) {
        super.keyTyped(c, key);

        if (editable && !readingManual)
            if (this.editingTitle)
                this.keyTypedInTitle(c, key);
            else
                this.keyTypedInBook(c, key);
    }

    /**
     * Processes keystrokes when editing the text of a book
     */
    private void keyTypedInBook(char c, int key) {
        switch (c) {
            case 22:
                this.addToBook(GuiScreen.getClipboardString());
                return;
        }
        switch (key) {
            case Keyboard.KEY_BACK: {
                String currentLine = getLine(currLine);
                if (currentLine.length() > 0 && currChar > 0)
                    setLine(currPage, currLine, currentLine.substring(0, currChar - 1) + currentLine.substring(currChar--));
                else if (currLine > 0 && currChar == 0 && getLine(currLine - 1).length() == 0) {
                    List<String> page = getPage(currPage);
                    page.remove(--currLine);
                } else if (currLine > 0 && currChar == 0 && currentLine.length() == 0) {
                    List<String> page = getPage(currPage);
                    page.remove(currLine--);
                    currChar = getLine(currLine).length();
                }
                return;
            }
            case Keyboard.KEY_DELETE: {
                String text = getLine(currLine);
                if (currChar < text.length()) {
                    setLine(currPage, currLine, text.substring(0, currChar) + text.substring(currChar + 1));
                    return;
                }
                List<String> page = getPage(currPage);
                if (currLine < page.size() - 1) {
                    if (text.length() == 0) {
                        page.remove(currLine);
                        currChar = 0;
                        return;
                    }
                    text = getLine(currLine + 1);
                    if (text.length() == 0) {
                        page.remove(currLine + 1);
                        return;
                    }
                }
                return;
            }
            case Keyboard.KEY_RETURN: {
                List<String> page = getPage(currPage);
                if (page.size() < ItemRoutingTable.LINES_PER_PAGE) {
                    String line = getLine(currLine);
                    setLine(currPage, currLine, line.substring(0, currChar));
                    page.add(++currLine, line.substring(currChar));
                    currChar = 0;
                }
                return;
            }
            case Keyboard.KEY_END: {
                currChar = getLine(currLine).length();
                return;
            }
            case Keyboard.KEY_HOME: {
                currChar = 0;
                return;
            }
            case Keyboard.KEY_NEXT: {
                currLine = getPage(currPage).size() - 1;
                currChar = Math.min(currChar, getLine(currLine).length());
                return;
            }
            case Keyboard.KEY_PRIOR: {
                currLine = 0;
                currChar = Math.min(currChar, getLine(currLine).length());
                return;
            }
            case Keyboard.KEY_DOWN: {
                List<String> page = getPage(currPage);
                if (currLine < page.size() - 1)
                    currChar = getLine(++currLine).length();
                return;
            }
            case Keyboard.KEY_UP: {
                if (currLine > 0)
                    currChar = getLine(--currLine).length();
                return;
            }
            case Keyboard.KEY_RIGHT: {
                String line = getLine(currLine);
                if (currChar < line.length())
                    currChar++;
                return;
            }
            case Keyboard.KEY_LEFT: {
                if (currChar > 0)
                    currChar--;
                return;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            String text = getLine(currLine);
            if (text.length() < ItemRoutingTable.LINE_LENGTH) {
                StringBuilder builder = new StringBuilder(text);
                setLine(currPage, currLine, builder.insert(currChar++, c).toString());
            }
        }
    }

    private void keyTypedInTitle(char c, int key) {
        switch (key) {
            case Keyboard.KEY_BACK:
                if (bookTitle.length() > 0) {
                    bookTitle = bookTitle.substring(0, bookTitle.length() - 1);
                    updateButtons();
                }

                return;
            case Keyboard.KEY_RETURN:
                if (bookTitle.length() > 0) {
                    sendBookToServer();
                    mc.displayGuiScreen((GuiScreen) null);
                }

                return;
            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(c)) {
                    this.bookTitle = this.bookTitle + Character.toString(c);
                    this.updateButtons();
                    this.bookModified = true;
                }
        }
    }

    private List<String> getPage(int page) {
        if (bookPages.isEmpty())
            initPages();
        return bookPages.get(page);
    }

    private String getLine(int line) {
        List<String> page = getPage(currPage);
        return page.get(line);
    }

    private void setLine(int pageNum, int lineNum, String string) {
        if (pageNum >= 0 && pageNum < bookPages.size()) {
            List<String> page = bookPages.get(pageNum);
            page.set(lineNum, string);
            this.bookModified = true;
        }
    }

    private void addToBook(String string) {
        String currentText = getLine(currLine);
        String newText = currentText + string;

        if (newText.length() < ItemRoutingTable.LINE_LENGTH) {
            this.setLine(currPage, currLine, newText);
            currChar = getLine(currLine).length();
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        int xOffset = (this.width - this.bookImageWidth) / 2;
        byte yOffset = 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.bookImageWidth, this.bookImageHeight);

        if (this.editingTitle) {
            String title = this.bookTitle;

            if (this.editable)
                if (this.updateCount / 6 % 2 == 0)
                    title = title + "" + EnumChatFormatting.BLACK + "_";
                else
                    title = title + "" + EnumChatFormatting.GRAY + "_";

            String s1 = StatCollector.translateToLocal("book.editTitle");
            int l = this.fontRendererObj.getStringWidth(s1);
            this.fontRendererObj.drawString(s1, xOffset + 36 + (116 - l) / 2, yOffset + 16 + 16, 0);
            int i1 = this.fontRendererObj.getStringWidth(title);
            this.fontRendererObj.drawString(title, xOffset + 36 + (116 - i1) / 2, yOffset + 48, 0);
            String s2 = String.format(StatCollector.translateToLocal("book.byAuthor"), Railcraft.proxy.getPlayerUsername(player));
            int j1 = this.fontRendererObj.getStringWidth(s2);
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + s2, xOffset + 36 + (116 - j1) / 2, yOffset + 48 + 10, 0);
//            String s3 = StatCollector.translateToLocal("book.finalizeWarning");
//            this.fontRendererObj.drawSplitString(s3, xOffset + 36, yOffset + 80, 116, 0);
        } else if (readingManual) {
//            GuiTools.drawCenteredString(fontRendererObj, RailcraftLanguage.translate("routing.table.manual.title"), yOffset + 16, width);
            fontRendererObj.drawString(LocalizationPlugin.translate(TABLE_MANUAL_LOC_TAG + "title"), xOffset + 45, yOffset + 16, 0);

            String pageNumString = String.format(StatCollector.translateToLocal("book.pageIndicator"), currPage + 1, numManualPages);
            int pageNumStringWidth = this.fontRendererObj.getStringWidth(pageNumString);
            this.fontRendererObj.drawString(pageNumString, xOffset - pageNumStringWidth + this.bookImageWidth - 44, yOffset + 16, 0);

            if (currPage < 0 || currPage >= numManualPages)
                return;

            String pageTag = TABLE_MANUAL_LOC_TAG + "page" + (currPage + 1);

            if (LocalizationPlugin.hasTag(pageTag)) {
                String text = LocalizationPlugin.translate(pageTag);
                this.fontRendererObj.drawSplitString(text, xOffset + 16, yOffset + 16 + 16, WRAP_WIDTH, 0);
            }
        } else {
            String pageNumString = String.format(StatCollector.translateToLocal("book.pageIndicator"), currPage + 1, bookPages.size());
            int pageNumStringWidth = this.fontRendererObj.getStringWidth(pageNumString);
            this.fontRendererObj.drawString(pageNumString, xOffset - pageNumStringWidth + this.bookImageWidth - 44, yOffset + 16, 0);

            if (currPage < 0 || currPage >= bookPages.size())
                return;

            StringBuilder text = new StringBuilder();

            List<String> page = bookPages.get(currPage);
            ListIterator<String> it = page.listIterator();
            while (it.hasNext()) {
                String line = it.next();
                text.append(EnumChatFormatting.BLACK);
                int start = text.length();
                text.append(line).append(" ");

                if (editable && it.previousIndex() == currLine)
                    if (updateCount / 6 % 2 == 0) {
                        text.insert(start + currChar, EnumChatFormatting.UNDERLINE.toString());
                        text.insert(start + currChar + 3, EnumChatFormatting.BLACK.toString());
                    }

                text.append("\n");
            }
            this.fontRendererObj.drawSplitString(text.toString(), xOffset + 16, yOffset + 16 + 16, WRAP_WIDTH, 0);
        }

        super.drawScreen(par1, par2, par3);
    }

}
