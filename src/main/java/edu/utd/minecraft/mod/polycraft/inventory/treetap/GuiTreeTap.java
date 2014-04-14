package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

@SideOnly(Side.CLIENT)
public class GuiTreeTap extends GuiContainer
{
	private static final String TEXTURE_NAME = "textures/gui/container/tree_tap.png";
	private static final ResourceLocation treeTapGuiTextures = new ResourceLocation(PolycraftMod.getAssetName(TEXTURE_NAME));

	private final IInventory field_147084_v;
	private final IInventory field_147083_w;

	public GuiTreeTap(InventoryPlayer par1InventoryPlayer, IInventory par2IInventory)
	{
		super(new ContainerTreeTap(par1InventoryPlayer, par2IInventory));
		this.field_147084_v = par1InventoryPlayer;
		this.field_147083_w = par2IInventory;
		this.allowUserInput = false;
		this.ySize = 133;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		this.fontRendererObj.drawString(this.field_147083_w.hasCustomInventoryName() ? this.field_147083_w.getInventoryName() : I18n.format(this.field_147083_w.getInventoryName(), new Object[0]), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.field_147084_v.hasCustomInventoryName() ? this.field_147084_v.getInventoryName() : I18n.format(this.field_147084_v.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(treeTapGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}