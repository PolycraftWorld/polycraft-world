package edu.utd.minecraft.mod.polycraft.client.gui;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTutorialMenu extends GuiScreen{
	
	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(
			PolycraftMod.getAssetName("textures/gui/consent_background.png"));
	private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
	
	private ArrayList<String> lines; // The text to show, broken up by line.
	private int ylines; // The number of lines the text space can accommodate.
	private int extraLines; // How many lines are overflowing from the alloted text space.
	private int titleHeight; // How many relative pixels the title text takes up.
	
	// Progress variables.
	private float scroll = 0.0F; // Amount of scroll, from 0.0 to 1.0 inclusive.
	private boolean scrolling; // True if the scroll bar is being dragged.
	private boolean wasClicking; // True if the left mouse button was held down last time drawScreen was called.
	
	// Not sure what these ones below are for.
	private static final String __OBFID = "CL_00000691";
	private EntityPlayer player;
	private int x, y, z;
	
	private static final int SCROLL_HEIGHT = 151;
	private static final int X_PAD = 10;
	private static final int X_WIDTH = 206; // X_WIDTH_SCROLL
	private static final int Y_HEIGHT = 126;
	private static final int Y_PAD = 8;

	
	public GuiTutorialMenu(EntityPlayer player) {
		lines = new ArrayList<String>();
        this.player = player;
    }
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 116, this.height / 2 + 56, 103, 20, "Close"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 9, this.height / 2 + 56, 103, 20, "Play !"));
		lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.tutorial.body"), X_WIDTH));
		
		scroll = 0.0F;
		titleHeight = this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.title"), X_WIDTH).size()
				* this.fontRendererObj.FONT_HEIGHT + this.fontRendererObj.FONT_HEIGHT;
		// Calculate how many lines the title text will need.
		ylines = Math.min(lines.size(), (Y_HEIGHT - titleHeight) / this.fontRendererObj.FONT_HEIGHT);
		extraLines = lines.size() - ylines;
	}

	
	/**
	 * Fired when a button is pressed.<br>
	 * Current setup: <br>
	 * <ul>
	 * <li>0 to 3 are answer choices A to D.</li>
	 * <li>4 is the back button.</li>
	 * <li>5 is the next button.</li>
	 * <li>Nothing else is possible and will result in the GUI closing.</li>
	 * </ul>
	 */
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0: // Button choice Close
			this.closeGUI();
			break;
		case 1: // Button choice Play Tutorial
			this.closeGUI();
			ClientEnforcer.INSTANCE.sendTutorialRequest();
			break;
		default: // Should not occur
			this.closeGUI();
			break;
		}
	}
	/**
	 * Handles mouse wheel scrolling.
	 */
	public void handleMouseInput() {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();
		if (i != 0 && extraLines > 0) {
			scroll -= Math.signum(i) / (float) extraLines;
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}
	}
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char c, int p) {
//			if (screenID == 7 || screenID == 8)
//				answer.textboxKeyTyped(c, p);
	}

	/**
	 * Close GUI after halftime
	 */
	public void closeGUI() {
		this.mc.displayGuiScreen((GuiScreen) null);
		this.mc.setIngameFocus();
	}
	
	/**
	 * Same as creative mode search menu to set repeat events.
	 */
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();
	}


	/**
	 * Draws either a gradient over the background screen (when it exists) or a flat
	 * gradient over background.png
	 */
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND_IMAGE);
		int i = (this.width - 248) / 2; // This GUI is 248 x 184.
		int j = (this.height - 184) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 248, 184);
	}

	/**
	 * Draws the screen and all the components in it. Called repeatedly so be
	 * careful.
	 */
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		// Get the position of the top left pixel.
		int x_start = (this.width - 248) / 2;
		int y_start = (this.height - 184) / 2;
		// Operate the scroll bar.
		boolean flag = Mouse.isButtonDown(0);
		int i1 = x_start + 226; // x and y of scroll bar.
		int j1 = y_start + 8;
		int k1 = i1 + 14;
		int l1 = j1 + SCROLL_HEIGHT;

		// Start scrolling check.
		if (!this.wasClicking && flag && mouseX >= i1 && mouseY >= j1 && mouseX < k1 && mouseY < l1 + 17)
			this.scrolling = extraLines > 0;
		// Stop scrolling check.
		if (!flag)
			this.scrolling = false;
		// Set history of mouse clicking.
		this.wasClicking = flag;
		// Set scroll in progress.
		if (this.scrolling) {
			this.scroll = ((float) (mouseY - j1) - 7.5F) / ((float) (l1 - j1));
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}

		// Draw the background image.
		this.drawDefaultBackground();

		// Set the marker for drawing text.
		int x_pos = x_start + X_PAD;
		int y_pos = y_start + Y_PAD;
		// Render the title.
		this.fontRendererObj.drawSplitString(I18n.format("gui.tutorial.title"), x_pos, y_pos, X_WIDTH, 0xFFFFFFFF);
		y_pos += titleHeight;

		// Set which line to start displaying text box to simulate scrolling.
		int lineStart = Math.round(this.scroll * extraLines);
		for (int i = 0; i < ylines; i++, y_pos += this.fontRendererObj.FONT_HEIGHT)
			this.fontRendererObj.drawString(lines.get(lineStart + i), x_pos, y_pos, 0);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SCROLL_TAB);
		// The scroll bar sits at (226, 8) but the border is 1 wide so the scroll
		// indicator really starts at (227, 9).
		this.drawTexturedModalRect(x_start + 227, y_start + 9 + (int) (this.scroll * SCROLL_HEIGHT),
				232 + (extraLines > 0 ? 0 : 12), 0, 12, 15);
		
		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
	
	
}
