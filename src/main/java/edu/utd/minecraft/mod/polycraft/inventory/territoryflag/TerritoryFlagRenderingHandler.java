package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TerritoryFlagRenderingHandler //extends PolycraftInventoryBlock.BasicRenderingHandler
{

	public TerritoryFlagRenderingHandler(final Inventory config) {
		//super(config);
	}
	
	//	@Override
	//	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	//		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	//		renderBlockMetadata((PolycraftInventoryBlock) block, 0, 0, 0, 0, true, renderer);
	//		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	//	}
	//
	//	@Override
	//	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
	//		PolycraftInventoryBlock inventoryBlock = (PolycraftInventoryBlock) block;
	//		Tessellator tessellator = Tessellator.instance;
	//		tessellator.setBrightness(inventoryBlock.getMixedBrightnessForBlock(world, x, y, z));
	//		int l = inventoryBlock.colorMultiplier(world, x, y, z);
	//		float f = (l >> 16 & 255) / 255.0F;
	//		float f1 = (l >> 8 & 255) / 255.0F;
	//		float f2 = (l & 255) / 255.0F;
	//
	//		if (EntityRenderer.anaglyphEnable) {
	//			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
	//			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
	//			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
	//			f = f3;
	//			f1 = f4;
	//			f2 = f5;
	//		}
	//
	//		tessellator.setColorOpaque_F(f, f1, f2);
	//		return renderBlockMetadata(inventoryBlock, x, y, z, world.getBlockMetadata(x, y, z), false, renderer);
	//
	//	}
	//
	//	public boolean renderBlockMetadata(PolycraftInventoryBlock inventoryBlock, int p_147799_2_, int p_147799_3_, int p_147799_4_, int p_147799_5_, boolean p_147799_6_, RenderBlocks renderer) {
	//		Tessellator tessellator = Tessellator.instance;
	//		int i1 = CondenserInventory.getDirectionFromMetadata(p_147799_5_);
	//		double d0 = 0.625D;
	//		renderer.setRenderBounds(0.0D, d0, 0.0D, 1.0D, 1.0D, 1.0D);
	//
	//		if (p_147799_6_)
	//		{
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, -1.0F, 0.0F);
	//			renderer.renderFaceYNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 0, p_147799_5_));
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 1.0F, 0.0F);
	//			renderer.renderFaceYPos(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 1, p_147799_5_));
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, -1.0F);
	//			renderer.renderFaceZNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 2, p_147799_5_));
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, 1.0F);
	//			renderer.renderFaceZPos(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 3, p_147799_5_));
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 4, p_147799_5_));
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXPos(inventoryBlock, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(inventoryBlock, 5, p_147799_5_));
	//			tessellator.draw();
	//		}
	//		else {
	//			renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//		}
	//
	//		float f1;
	//
	//		if (!p_147799_6_) {
	//			tessellator.setBrightness(inventoryBlock.getMixedBrightnessForBlock(renderer.blockAccess, p_147799_2_, p_147799_3_, p_147799_4_));
	//			int j1 = inventoryBlock.colorMultiplier(renderer.blockAccess, p_147799_2_, p_147799_3_, p_147799_4_);
	//			float f = (j1 >> 16 & 255) / 255.0F;
	//			f1 = (j1 >> 8 & 255) / 255.0F;
	//			float f2 = (j1 & 255) / 255.0F;
	//
	//			if (EntityRenderer.anaglyphEnable) {
	//				float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
	//				float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
	//				float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
	//				f = f3;
	//				f1 = f4;
	//				f2 = f5;
	//			}
	//
	//			tessellator.setColorOpaque_F(f, f1, f2);
	//		}
	//
	//		final CondenserBlock inventoryBlockBlock = (CondenserBlock) PolycraftRegistry.getBlock(config);
	//		IIcon iicon = inventoryBlockBlock.iconOutside;
	//		IIcon iicon1 = inventoryBlockBlock.iconInside;
	//		f1 = 0.125F;
	//
	//		if (p_147799_6_) {
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXPos(inventoryBlock, -1.0F + f1, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXNeg(inventoryBlock, 1.0F - f1, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, 1.0F);
	//			renderer.renderFaceZPos(inventoryBlock, 0.0D, 0.0D, -1.0F + f1, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, -1.0F);
	//			renderer.renderFaceZNeg(inventoryBlock, 0.0D, 0.0D, 1.0F - f1, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 1.0F, 0.0F);
	//			renderer.renderFaceYPos(inventoryBlock, 0.0D, -1.0D + d0, 0.0D, iicon1);
	//			tessellator.draw();
	//		}
	//		else {
	//			renderer.renderFaceXPos(inventoryBlock, p_147799_2_ - 1.0F + f1, p_147799_3_, p_147799_4_, iicon);
	//			renderer.renderFaceXNeg(inventoryBlock, p_147799_2_ + 1.0F - f1, p_147799_3_, p_147799_4_, iicon);
	//			renderer.renderFaceZPos(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_ - 1.0F + f1, iicon);
	//			renderer.renderFaceZNeg(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_ + 1.0F - f1, iicon);
	//			renderer.renderFaceYPos(inventoryBlock, p_147799_2_, p_147799_3_ - 1.0F + d0, p_147799_4_, iicon1);
	//		}
	//
	//		renderer.setOverrideBlockTexture(iicon);
	//		double d3 = 0.25D;
	//		double d4 = 0.25D;
	//		renderer.setRenderBounds(d3, d4, d3, 1.0D - d3, d0 - 0.002D, 1.0D - d3);
	//
	//		if (p_147799_6_) {
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXPos(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	//			renderer.renderFaceXNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, 1.0F);
	//			renderer.renderFaceZPos(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 0.0F, -1.0F);
	//			renderer.renderFaceZNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, 1.0F, 0.0F);
	//			renderer.renderFaceYPos(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//			tessellator.startDrawingQuads();
	//			tessellator.setNormal(0.0F, -1.0F, 0.0F);
	//			renderer.renderFaceYNeg(inventoryBlock, 0.0D, 0.0D, 0.0D, iicon);
	//			tessellator.draw();
	//		}
	//		else {
	//			renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//		}
	//
	//		if (!p_147799_6_) {
	//			double d1 = 0.375D;
	//			double d2 = 0.25D;
	//			renderer.setOverrideBlockTexture(iicon);
	//
	//			if (i1 == 0) {
	//				renderer.setRenderBounds(d1, 0.0D, d1, 1.0D - d1, 0.25D, 1.0D - d1);
	//				renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//			}
	//
	//			if (i1 == 2) {
	//				renderer.setRenderBounds(d1, d4, 0.0D, 1.0D - d1, d4 + d2, d3);
	//				renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//			}
	//
	//			if (i1 == 3) {
	//				renderer.setRenderBounds(d1, d4, 1.0D - d3, 1.0D - d1, d4 + d2, 1.0D);
	//				renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//			}
	//
	//			if (i1 == 4) {
	//				renderer.setRenderBounds(0.0D, d4, d1, d3, d4 + d2, 1.0D - d1);
	//				renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//			}
	//
	//			if (i1 == 5) {
	//				renderer.setRenderBounds(1.0D - d3, d4, d1, 1.0D, d4 + d2, 1.0D - d1);
	//				renderer.renderStandardBlock(inventoryBlock, p_147799_2_, p_147799_3_, p_147799_4_);
	//			}
	//		}
	//
	//		renderer.clearOverrideBlockTexture();
	//		return true;
	//	}
}
