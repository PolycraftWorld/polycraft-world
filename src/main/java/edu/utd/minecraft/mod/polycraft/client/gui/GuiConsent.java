package edu.utd.minecraft.mod.polycraft.client.gui;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiConsent extends GuiScreen {

	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(
			PolycraftMod.getAssetName("textures/gui/consent_background.png"));
	private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");

	private ArrayList<String> lines; // The text to show, broken up by line.
	private int ylines; // The number of lines the text space can accommodate.
	private int extraLines; // How many lines are overflowing from the alloted text space.
	private int titleHeight; // How many relative pixels the title text takes up.
	private GuiTextField answer;
	// Progress variables.
	private float scroll = 0.0F; // Amount of scroll, from 0.0 to 1.0 inclusive.
	private boolean scrolling; // True if the scroll bar is being dragged.
	private boolean wasClicking; // True if the left mouse button was held down last time drawScreen was called.
	private int screenID; // Current screen
	private boolean[] completed = new boolean[13];
	public boolean consent = false;
	// Not sure what these ones below are for.
	private static final String __OBFID = "CL_00000691";
	private EntityPlayer player;
	private int x, y, z;

	public GuiConsent(EntityPlayer player, int x, int y, int z) {
		lines = new ArrayList<String>();
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
		this.completed[0] = true;
		this.completed[11] = true;
		this.completed[12] = true;
		this.screenID = 1;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		this.buttonList.clear();
		/*
		 * NUMBER RATIONALE:
		 * 
		 * 56: Since button height is 20 and padding is 16, add 56 to 184 / 2.
		 * 
		 * Button width and x positioning were taken from the GuiScreenDemo class. This
		 * pads 8 on left and right and 4 in between buttons.
		 */
		// this.buttonList.add(new GuiButton(4, this.width / 2 - 116, this.height / 2 +
		// 56, 114, 20, "< Back"));
		// this.buttonList.add(new GuiButton(5, this.width / 2 + 2, this.height / 2 +
		// 56, 114, 20, "Next >"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 116, this.height / 2 + 56, 103, 20, "< Back"));
		this.buttonList.add(new GuiButton(5, this.width / 2 - 9, this.height / 2 + 56, 103, 20, "Next >"));
		answer = new GuiTextField(this.fontRendererObj, this.width / 2 - 116, this.height / 2 - 12, 210,
				this.fontRendererObj.FONT_HEIGHT);
		answer.setMaxStringLength(16);
		answer.setTextColor(16777215);
		answer.setVisible(true);
		answer.setCanLoseFocus(false);
		answer.setFocused(true);
		Keyboard.enableRepeatEvents(true); // Allow the keyboard to hold buttons.
		switchScreen(); // Set up the first screen.
	}

	// Eventually I should make a modular GUI framework. - Chris
	private static final int SCROLL_HEIGHT = 151;
	private static final int X_PAD = 10;
	// private static final int X_WIDTH = 248 - 10 * 2;
	// private static final int X_WIDTH_SCROLL = X_WIDTH - 22;
	private static final int X_WIDTH = 206; // X_WIDTH_SCROLL
	private static final int Y_HEIGHT = 126;
	private static final int Y_PAD = 8;

	private void switchScreen() {
		answer.setText("");
		lines.clear();
		scroll = 0.0F;
		titleHeight = this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.title"), X_WIDTH).size()
				* this.fontRendererObj.FONT_HEIGHT + this.fontRendererObj.FONT_HEIGHT;
		for (int i = this.buttonList.size() - 1; i > 1; i--)
			this.buttonList.remove(i);
		GuiButton yes;
		GuiButton no;
		switch (screenID) {
		case 0: // If the player is not 18+.
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.minor"), X_WIDTH));
			break;
		case 1: // Start of terms.
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.longassparagraph1"),
					X_WIDTH));
			break;
		case 2:
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.longassparagraph2"),
					X_WIDTH));
			break;
		case 3:
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.longassparagraph3"),
					X_WIDTH));
			break;
		case 4: // Contact information.
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.longassparagraph4"),
					X_WIDTH));
			lines.add("");
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.contact1"), X_WIDTH));
			lines.add("");
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.contact2"), X_WIDTH));
			lines.add("");
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.contact3"), X_WIDTH));
			break;
		case 5: // Question #3
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question3"), X_WIDTH));
			yes = new GuiButton(0, this.width / 2 - 116, this.height / 2 - 12, 210, 20, I18n.format("gui.yes"));
			no = new GuiButton(1, this.width / 2 - 116, this.height / 2 + 10, 210, 20,
					completed[5] ? "" : I18n.format("gui.no"));
			yes.enabled = !completed[5];
			no.enabled = !completed[5];
			this.buttonList.add(yes);
			this.buttonList.add(no);
			break;
		case 6: // Question #1
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question1"), X_WIDTH));
			this.buttonList.add(new GuiButton(0, this.width / 2 - 116, this.height / 2 - 34, 210, 20,
					I18n.format("gui.consent.question10")));
			this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 - 12, 210, 20,
					I18n.format("gui.consent.question11")));
			this.buttonList.add(new GuiButton(2, this.width / 2 - 116, this.height / 2 + 10, 210, 20,
					I18n.format("gui.consent.question12")));
			this.buttonList.add(new GuiButton(3, this.width / 2 - 116, this.height / 2 + 32, 210, 20,
					I18n.format("gui.consent.question13")));
			break;
		case 7: // Question #2
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question2"), X_WIDTH));
			break;
		case 8: // Question #4
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question4"), X_WIDTH));
			break;
		case 9: // Question #5
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question5"), X_WIDTH));
			this.buttonList.add(new GuiButton(0, this.width / 2 - 116, this.height / 2 - 34, 210, 20,
					I18n.format("gui.consent.question50")));
			this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 - 12, 210, 20,
					I18n.format("gui.consent.question51")));
			this.buttonList.add(new GuiButton(2, this.width / 2 - 116, this.height / 2 + 10, 210, 20,
					I18n.format("gui.consent.question52")));
			this.buttonList.add(new GuiButton(3, this.width / 2 - 116, this.height / 2 + 32, 210, 20,
					I18n.format("gui.consent.question53")));
			break;
		case 10: // Question #6
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.question6"), X_WIDTH));
			yes = new GuiButton(0, this.width / 2 - 116, this.height / 2 - 12, 210, 20, I18n.format("gui.yes"));
			no = new GuiButton(1, this.width / 2 - 116, this.height / 2 + 10, 210, 20,
					completed[10] ? "" : I18n.format("gui.no"));
			yes.enabled = !completed[10];
			no.enabled = !completed[10];
			this.buttonList.add(yes);
			this.buttonList.add(no);
			break;
		case 11: // Finishing screen for no consent.
			lines.addAll(
					this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.nonegiven"), X_WIDTH));
			break;
		case 12: // Finishing screen for consent given.
			lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.finished"), X_WIDTH));
			break;
		}
		// Calculate how many lines the title text will need.
		ylines = Math.min(lines.size(), (Y_HEIGHT - titleHeight) / this.fontRendererObj.FONT_HEIGHT);
		extraLines = lines.size() - ylines;
	}

	/**
	 * Disables other answer choices except for the specified button.
	 */
	private void disableOthers(GuiButton button) {
		for (int i = this.buttonList.size() - 1; i > 1; i--) {
			GuiButton other = (GuiButton) this.buttonList.get(i);
			if (button != other) {
				other.displayString = "";
				other.enabled = false;
			}
		}
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
		case 0: // Button choice A or Yes
			if (screenID == 5) { // Is 18+
				completed[5] = true;
				button.enabled = false;
				((GuiButton) this.buttonList.get(3)).displayString = "";
				((GuiButton) this.buttonList.get(3)).enabled = false;
			} else if (screenID == 10) { // Consent given.
				completed[10] = true;
				button.enabled = false;
				consent = true;
				screenID = 12;
				switchScreen();
			} else {
				// Mark button as incorrect.
				button.displayString = screenID == 6 ? I18n.format("gui.consent.tryagain")
						: I18n.format("gui.consent.more");
				button.enabled = false;
			}
			break;
		case 1: // Button choice B or No
			if (screenID == 5) { // Not 18+
				screenID = 0;
				switchScreen();
			} else if (screenID == 10) { // Consent not given.
				screenID = 11;
				completed[10] = true;
				switchScreen();
			} else {
				// Mark button as incorrect.
				button.displayString = screenID == 6 ? I18n.format("gui.consent.tryagain")
						: I18n.format("gui.consent.more");
				button.enabled = false;
			}
			break;
		case 2: // Button choice C
			if (screenID == 6) { // Question #1
				completed[6] = true;
				button.enabled = false;
				disableOthers(button);
			} else {
				// Mark button as incorrect.
				button.displayString = I18n.format("gui.consent.more");
				button.enabled = false;
			}
			break;
		case 3: // Button choice D
			if (screenID == 9) { // Question #5
				completed[9] = true;
				button.enabled = false;
				disableOthers(button);
			} else {
				// Mark button as incorrect.
				button.displayString = I18n.format("gui.consent.tryagain");
				button.enabled = false;
			}
			break;
		case 4: // Back button.
			if (screenID > 10) // Jump back to consent page.
				screenID = 10;
			else // Jump back a single page.
				screenID--;
			switchScreen();
			break;
		case 5: // Next button.
			if (screenID == 10) { // Jump to consent given or not given result screens.
				screenID = consent ? 12 : 11;
				switchScreen();
				break;
			} else if (screenID != 0 && screenID < 11) { // Jump to next screen.
				screenID++;
				switchScreen();
				break;
			}
		default: // Should not occur outside of case 5 falling through.
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
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
		if (screenID == 7 || screenID == 8)
			answer.textboxKeyTyped(c, p);
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
		if (this.buttonList.size() < 2)
			return; // Buttons are not ready yet.
		((GuiButton) this.buttonList.get(0)).enabled = screenID > 1 && screenID != 11;
		((GuiButton) this.buttonList.get(1)).displayString = screenID == 0 || screenID > 10 ? "Play!" : "Next >";
		((GuiButton) this.buttonList.get(1)).enabled = completed[screenID];

		// Set the marker for drawing text.
		int x_pos = x_start + X_PAD;
		int y_pos = y_start + Y_PAD;
		// Render the title.
		this.fontRendererObj.drawSplitString(I18n.format("gui.consent.title"), x_pos, y_pos, X_WIDTH, 0xFFFFFFFF);
		y_pos += titleHeight;

		// Set which line to start displaying text box to simulate scrolling.
		int lineStart = Math.round(this.scroll * extraLines);
		for (int i = 0; i < ylines; i++, y_pos += this.fontRendererObj.FONT_HEIGHT)
			this.fontRendererObj.drawString(lines.get(lineStart + i), x_pos, y_pos, 0);
		// Allow user to proceed if the bottom-most text line has been displayed.
		// This is for reading the information before the questions.
		if (screenID < 5 && lineStart == extraLines && this.buttonList.size() == 2)
			completed[screenID] = true;
		else if (screenID < 5 && !completed[screenID] && extraLines > 0)
			((GuiButton) this.buttonList.get(1)).displayString = "Scroll";

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SCROLL_TAB);
		// The scroll bar sits at (226, 8) but the border is 1 wide so the scroll
		// indicator really starts at (227, 9).
		this.drawTexturedModalRect(x_start + 227, y_start + 9 + (int) (this.scroll * SCROLL_HEIGHT),
				232 + (extraLines > 0 ? 0 : 12), 0, 12, 15);

		// Check text input for questions 2 and 4.
		boolean match;
		if (screenID == 7) { // Question #2
			match = answer.getText().equals("/exit");
			if (match)
				completed[7] = true;
			answer.setTextColor(match ? 43520 : 16777215); // Color text green.
			answer.drawTextBox();
		} else if (screenID == 8) { // Question #4
			// Remove phone number meta characters '+' and '-' to check the number.
			String number = answer.getText().replaceAll("[+-]", "");
			// Allow U.S. code.
			match = number.equals("9728834579") || number.equals("19728834579");
			if (match)
				completed[8] = true;
			answer.setTextColor(match ? 43520 : 16777215); // Color text green.
			answer.drawTextBox();
		}
		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
}
