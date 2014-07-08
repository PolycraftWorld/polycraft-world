package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class PolycraftInventoryGui<I extends PolycraftInventory> extends GuiContainer {

	protected final I inventory;
	private boolean isChest;

	public PolycraftInventoryGui(I inventory, InventoryPlayer playerInventory) {
		super(inventory.getCraftingContainer(playerInventory));
		this.inventory = inventory;
	}

	public PolycraftInventoryGui(I inventory, InventoryPlayer playerInventory, int ySize) {
		this(inventory, playerInventory);
		this.ySize = ySize;
	}
	
	public PolycraftInventoryGui(I inventory, InventoryPlayer playerInventory, int xSize, int ySize, boolean isChest) {
		this(inventory, playerInventory);
		this.xSize = xSize;
		this.ySize = ySize;
		this.isChest = true;
	}

	public PolycraftInventoryGui(I inventory, InventoryPlayer playerInventory, int ySize, boolean allowUserInput) {
		this(inventory, playerInventory, ySize);
		this.allowUserInput = allowUserInput;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
			String s = I18n.format(inventory.getInventoryName(), new Object[0]);
			this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
			this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96+2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		if (isChest)
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(inventory.getGuiResourceLocation());
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.drawTexturedModalRect(k, 6, 0, 0, this.xSize, this.ySize);			
		}
		else
		{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(inventory.getGuiResourceLocation());
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		}
	}
}
