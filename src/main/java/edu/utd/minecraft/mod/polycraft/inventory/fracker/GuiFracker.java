package edu.utd.minecraft.mod.polycraft.inventory.fracker;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

@SideOnly(Side.CLIENT)
public class GuiFracker extends GuiContainer {
	private static final ResourceLocation frackerGuiTextures = new ResourceLocation(PolycraftMod.getTextureName("textures/gui/container/fracker.png"));
	private final TileEntityFracker tileFracker;

	public GuiFracker(InventoryPlayer par1InventoryPlayer, TileEntityFracker par2TileEntityFracker) {
		super(new ContainerFracker(par1InventoryPlayer, par2TileEntityFracker));
		this.tileFracker = par2TileEntityFracker;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		String s = this.tileFracker.hasCustomInventoryName() ? this.tileFracker.getInventoryName() : I18n.format(this.tileFracker.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(frackerGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;

		if (this.tileFracker.isBurning()) {
			i1 = this.tileFracker.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(k + 19, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
		}

		i1 = this.tileFracker.getCookProgressScaled(24);
		this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
	}
}