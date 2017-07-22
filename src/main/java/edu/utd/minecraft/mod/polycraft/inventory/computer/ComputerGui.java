package edu.utd.minecraft.mod.polycraft.inventory.computer;

import static org.lwjgl.opengl.GL11.glColor4f;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ComputerGui extends /* GuiContainer */ PolycraftInventoryGui<ComputerInventory> {

	public static final ResourceLocation computerBaseGui = new ResourceLocation(PolycraftMod.MODID,
			"textures/gui/computer_stuff/large_computer.png");
	public static final ResourceLocation computerTabIcons = new ResourceLocation(PolycraftMod.MODID,
			"textures/gui/computer_stuff/icons.png");

	private ComputerInventory computer;
	private final GuiRectangle[] tabs;
	private GuiRectangle activeTab;

	public ComputerGui(ComputerInventory inventory, InventoryPlayer playerInventory, int xSize, int ySize) {
		// public ComputerGui(InventoryPlayer invPlayer, ComputerInventory
		// computer) {

		super(inventory, playerInventory);
		this.xSize = xSize;
		this.ySize = ySize;
		// super(new ComputerContainer( invPlayer, computer));
		// this.xSize=342;
		// this.ySize=330;
		// this.computer=computer;
		tabs = new GuiRectangle[4];
		tabs[0] = new GuiRectangle(336, 23, 52, 60);
		tabs[1] = new GuiRectangle(336, 84, 52, 60);
		tabs[2] = new GuiRectangle(336, 145, 52, 60);
		tabs[3] = new GuiRectangle(336, 206, 52, 60);
		activeTab = tabs[0];
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
		glColor4f(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(computerBaseGui);

		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		// region Draw Basic Tabs
		int srcX, srcY;
		for (int i = 0; i < tabs.length; i++) {
			int side = 2;
			GuiRectangle rectangle = tabs[i];
			srcX = 460 / 2;
			srcY = 145 / 2;
			if ((rectangle.inRect(this, mouseX, mouseY, side) && tabs[i] != activeTab))
				srcY = 84 / 2; // darken tab when hovering over it
			if (tabs[i] == activeTab)
				srcY = 23 / 2;
			rectangle.draw(this, srcX, srcY, side);
		}
		// endregion Draw Basic Tabs

		// region icons on tabs
		// 41:00 on youtube
		Minecraft.getMinecraft().getTextureManager().bindTexture(computerTabIcons);
		drawTexturedModalRect(guiLeft + xSize + 2, guiTop + 22, 7/2, 38/2, 25, 12);//games
		drawTexturedModalRect(guiLeft + xSize + 5, guiTop + 55, 6/2, 116/2, 15, 10);//camera
		drawTexturedModalRect(guiLeft + xSize + 6, guiTop + 82, 14/2, 65/2, 9, 12);//auction
		drawTexturedModalRect(guiLeft + xSize + 4, guiTop + 114, 3/2, 95/2, 19, 7);//email
		// endregion icons on tabs

		// region Tab Contents
		if (activeTab == tabs[0]) {
//			System.out.println("first tab");
			// draw this tab's stuff
			// Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
			// drawTexturedModalRect(guiLeft + 3, guiTop + 3,0,0,170,90);
		}
		if (activeTab == tabs[1]) {
//			System.out.println("second tab");
			// draw this tab's stuff
		}
		if (activeTab == tabs[2]) {
//			System.out.println("third tab");
			// draw this tab's stuff
		}
		if (activeTab == tabs[3]) {
//			System.out.println("fourth tab");
			// draw this tab's stuff
		}
		// endregion Tab Contents

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = I18n.format(inventory.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
//		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		
		String[] tabText = {"Mini Games", "Security Cameras", "Auction House", "Email"};
		int side = 2;
		int index = 0;
		String text;
		GuiRectangle rectangle;
		for (int i=0; i<tabs.length; i++){
			if (activeTab == tabs[i])
				index = i;
			rectangle = tabs[i];
			text = tabText[i];
			rectangle.drawString(this, mouseX, mouseY, side, text);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {

		super.mouseClicked(mouseX, mouseY, mouseButton);
		GuiRectangle rectangle;
		for (int i = 0; i < tabs.length; i++) {
			rectangle = tabs[i];
			int side = 2;
			// if (i>2)
			// side =2;
			if (rectangle.inRect(this, mouseX, mouseY, side)) {
				activeTab = tabs[i];
				break;
			}
		}
	}

	public int getLeft() {
		return guiLeft;
	}

	public int getTop() {
		return guiTop;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public void drawHoverString(List texts, int x, int y) {
		
		drawHoveringText(texts, x, y, fontRendererObj);
		
	}
}
