package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.inventory.courseblock.CHEM2323Inventory;

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

	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.clear();
		if (this.inventory instanceof CHEM2323Inventory)
		{
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;

			this.buttonList.add(new GuiButton(1, k + 156, l + 6, 12, 12, ">")); //this works, but do we keep adding buttons?
			this.buttonList.add(new GuiButton(1, k + 7, l + 6, 12, 12, "<"));
		}

	}

	@Override
	public void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
		if (this.inventory instanceof CHEM2323Inventory)
		{
			switch (button.id)
			{
			case 1:
				((CHEM2323Inventory) this.inventory).bookmark--;
				break;
			case 2:
				((CHEM2323Inventory) this.inventory).bookmark++;
				break;
			default:

			}
		}

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		if (this.inventory instanceof CHEM2323Inventory)
		{
			String s = I18n.format(inventory.getInventoryName(), new Object[0]);
			this.fontRendererObj.drawString(s, 6 * this.xSize / 7 - this.fontRendererObj.getStringWidth(s) / 2, 10, 4210752);
			this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		}
		else
		{
			String s = I18n.format(inventory.getInventoryName(), new Object[0]);
			this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
			this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		if (isChest)
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(inventory.getGuiResourceLocation());
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			//this.drawTexturedModalRect(k, 6, 0, 0, this.xSize, this.ySize);		
			this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
			if (this.inventory instanceof CHEM2323Inventory)
			{
				if (((CHEM2323Inventory) inventory).getOverlayResourceLocation() != null)
				{
					this.mc.getTextureManager().bindTexture(((CHEM2323Inventory) inventory).getOverlayResourceLocation());
					GL11.glPushMatrix();
					GL11.glScalef(0.492f, 0.365f, 1.0f);
					this.drawTexturedModalRect((int) (k / 0.492) + 51, (int) (l / 0.365) + 10, 0, 0, 256, 256);
					GL11.glPopMatrix();

					this.mc.getTextureManager().bindTexture(((CHEM2323Inventory) inventory).getOverlayResourceLocationLetters());
					GL11.glPushMatrix();
					this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
					GL11.glPopMatrix();

				}
			}

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
