package edu.utd.minecraft.mod.polycraft.client.gui;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.sun.prism.paint.Color;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiConsent extends GuiScreen {

	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(
			PolycraftMod.getAssetName("textures/gui/consent_background.png"));
	private static final ResourceLocation BACKGROUND_SCROLL = new ResourceLocation(
			PolycraftMod.getAssetName("textures/gui/consent_background_scroll.png"));
	private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
	private static final String __OBFID = "CL_00000691";
	private EntityPlayer player;
	private int x, y, z;
	/**
	 * Amount scrolled from 0.0 to 1.0 inclusive.
	 */
	private float scroll = 0.0F;
	/**
	 * True if the scrollbar is being dragged.
	 */
	private boolean scrolling;
	/**
	 * True if the scrollbar is needed.
	 */
	private boolean scrollNeeded = false;
	/**
	 * True if the left mouse button was held down last time drawScreen was called.
	 */
	private boolean wasClicking;
	private int screenID; // current screen

	public GuiConsent(EntityPlayer player, int x, int y, int z) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
		this.screenID = 0;
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
		this.buttonList.add(new GuiButton(4, this.width / 2 - 116, this.height / 2 + 56, 114, 20, "< Back"));
		this.buttonList.add(new GuiButton(5, this.width / 2 + 2, this.height / 2 + 56, 114, 20, "Next >"));
		/*
		 * for (int i = 0; i < 4; i++) this.buttonList.add(new GuiButton(i, this.width /
		 * 2 - 116, this.height / 2 + 88 - i * 24, 232, 20,
		 * I18n.format("gui.consent.answer1" + i)));
		 */
	}

	protected void actionPerformed(GuiButton button) {

		((GuiButton) this.buttonList.get(0)).enabled = true;
		switch (button.id) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			screenID--;
			break;
		case 5:
			screenID++;
			if (screenID <= 9)
				break;
		default:
			this.mc.displayGuiScreen((GuiScreen) null);
			break;
		}

		switch (button.id) {
		case 1:
			button.enabled = false;

			try {
				Class oclass = Class.forName("java.awt.Desktop");
				Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
				oclass.getMethod("browse", new Class[] { URI.class }).invoke(object,
						new Object[] { new URI("https://www.polycraftworld.com") });
			} catch (Throwable throwable) {
				logger.error("Couldn\'t open link", throwable);
			}

			break;
		case 2:
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		}
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
		this.mc.getTextureManager().bindTexture(scrollNeeded ? BACKGROUND_SCROLL : BACKGROUND_IMAGE);
		int i = (this.width - 248) / 2;
		int j = (this.height - 184) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 248, 248);
	}

	private static final int X_PAD = 10;
	private static final int X_WIDTH = 248 - 10 * 2;
	private static final int X_WIDTH_SCROLL = X_WIDTH - 22;
	private static final int Y_PAD = 8;

	/**
	 * Draws the screen and all the components in it. Called repeatedly so be
	 * careful.
	 */
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		((GuiButton) this.buttonList.get(0)).enabled = screenID != 0;
		((GuiButton) this.buttonList.get(1)).displayString = screenID == 9 ? "Play!" : "Next >";
		this.drawDefaultBackground();
		int x_start = (this.width - 248) / 2;
		int y_start = (this.height - 184) / 2;
		int x_pos = x_start + X_PAD;
		int y_pos = y_start + Y_PAD;
		this.fontRendererObj.drawSplitString(I18n.format("gui.consent.title"), x_pos, y_pos, X_WIDTH, 0xFFFFFFFF);
		y_pos += this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.consent.title"), X_WIDTH).size() * 9
				+ 9;
		switch (screenID) {
		case 0:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.longassparagraph1"), x_pos, y_pos, X_WIDTH,
					0);
			break;
		case 1:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.longassparagraph2"), x_pos, y_pos, X_WIDTH,
					0);
			break;
		case 2:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.longassparagraph3"), x_pos, y_pos, X_WIDTH,
					0);
			break;
		case 3:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.longassparagraph4"), x_pos, y_pos, X_WIDTH,
					0);
			break;
		case 4:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.question1"), x_pos, y_pos, X_WIDTH, 0);
			break;
		case 5:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.question2"), x_pos, y_pos, X_WIDTH, 0);
			break;
		case 6:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.question3"), x_pos, y_pos, X_WIDTH, 0);
			break;
		case 7:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.question4"), x_pos, y_pos, X_WIDTH, 0);
			break;
		case 8:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.question5"), x_pos, y_pos, X_WIDTH, 0);
			break;
		case 9:
			this.fontRendererObj.drawSplitString(I18n.format("gui.consent.finished"), x_pos, y_pos, X_WIDTH, 0);
			break;
		}

		if (scrollNeeded) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(SCROLL_TAB);
			// The scroll bar sits at (226, 8) but the border is 1 wide.
			this.drawTexturedModalRect(x_start + 227, y_start + 9, 232, 0, 12, 15);
		}

		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}

}
