package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

@SideOnly(Side.CLIENT)
public class GuiMachiningMill extends GuiContainer {
	private static final String TEXTURE_NAME = "textures/gui/container/machining_mill.png";
	private static final ResourceLocation machiningMillGuiTextures = new ResourceLocation(PolycraftMod.getAssetName(TEXTURE_NAME));

	private final TileEntityMachiningMill tileMachiningMill;

	public GuiMachiningMill(InventoryPlayer par1InventoryPlayer, TileEntityMachiningMill par2TileEntityMachiningMill) {
		super(new ContainerMachiningMill(par1InventoryPlayer, par2TileEntityMachiningMill));
		this.tileMachiningMill = par2TileEntityMachiningMill;
		this.ySize = 203;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		String s = this.tileMachiningMill.hasCustomInventoryName()
				? this.tileMachiningMill.getInventoryName()
				: I18n.format(this.tileMachiningMill.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(machiningMillGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}