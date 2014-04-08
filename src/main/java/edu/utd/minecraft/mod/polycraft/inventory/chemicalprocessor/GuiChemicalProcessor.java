package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

@SideOnly(Side.CLIENT)
public class GuiChemicalProcessor extends GuiContainer {
	private static final String TEXTURE_NAME = "textures/gui/container/chemical_processor.png";
	private static final ResourceLocation chemicalProcessorGuiTextures
		= new ResourceLocation(PolycraftMod.getAssetName(TEXTURE_NAME));
	private final TileEntityChemicalProcessor tileChemicalProcessor;

	public GuiChemicalProcessor(InventoryPlayer par1InventoryPlayer, TileEntityChemicalProcessor par2TileEntityChemicalProcessor) {
		super(new ContainerChemicalProcessor(par1InventoryPlayer, par2TileEntityChemicalProcessor));
		this.tileChemicalProcessor = par2TileEntityChemicalProcessor;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		String s = this.tileChemicalProcessor.hasCustomInventoryName()
				? this.tileChemicalProcessor.getInventoryName()
			    : I18n.format(this.tileChemicalProcessor.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(chemicalProcessorGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;

		if (this.tileChemicalProcessor.isBurning()) {
			i1 = this.tileChemicalProcessor.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(k + 26, l + 49 - i1, 176, 12 - i1, 14, i1 + 2);
		}

		i1 = this.tileChemicalProcessor.getCookProgressScaled(24);
		this.drawTexturedModalRect(k + 89, l + 36, 176, 14, i1 + 1, 16);
	}
}