package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class TreeTapRenderingHandler //extends PolycraftInventoryBlock.BasicRenderingHandler
{

	public TreeTapRenderingHandler(final Inventory config) {
		//super(config);
	}
/*
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderBlockTreeTapMetadata((PolycraftInventoryBlock) block, 0, 0, 0, 0, true, renderer);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		PolycraftInventoryBlock treeTap = (PolycraftInventoryBlock) block;
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(treeTap.getMixedBrightnessForBlock(world, x, y, z));
		int l = treeTap.colorMultiplier(world, x, y, z);
		float f = (l >> 16 & 255) / 255.0F;
		float f1 = (l >> 8 & 255) / 255.0F;
		float f2 = (l & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		tessellator.setColorOpaque_F(f, f1, f2);
		return renderBlockTreeTapMetadata(treeTap, x, y, z, world.getBlockMetadata(x, y, z), false, renderer);

	}

	public boolean renderBlockTreeTapMetadata(PolycraftInventoryBlock treeTap, int p_147799_2_, int p_147799_3_, int p_147799_4_, int p_147799_5_, boolean p_147799_6_, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		int i1 = TreeTapInventory.getDirectionFromMetadata(p_147799_5_);
		double d0 = 0.625D;
		renderer.setRenderBounds(0.0D, d0, 0.0D, 1.0D, 1.0D, 1.0D);

		if (p_147799_6_)
		{
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 0, p_147799_5_));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 1, p_147799_5_));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 2, p_147799_5_));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 3, p_147799_5_));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 4, p_147799_5_));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(treeTap, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(treeTap, 5, p_147799_5_));
			tessellator.draw();
		}
		else {
			renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
		}

		float f1;

		if (!p_147799_6_) {
			tessellator.setBrightness(treeTap.getMixedBrightnessForBlock(renderer.blockAccess, p_147799_2_, p_147799_3_, p_147799_4_));
			int j1 = treeTap.colorMultiplier(renderer.blockAccess, p_147799_2_, p_147799_3_, p_147799_4_);
			float f = (j1 >> 16 & 255) / 255.0F;
			f1 = (j1 >> 8 & 255) / 255.0F;
			float f2 = (j1 & 255) / 255.0F;

			if (EntityRenderer.anaglyphEnable) {
				float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
				float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
				float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
				f = f3;
				f1 = f4;
				f2 = f5;
			}

			tessellator.setColorOpaque_F(f, f1, f2);
		}

		final TreeTapBlock treeTapBlock = (TreeTapBlock) PolycraftRegistry.getBlock(config);
		IIcon iicon = treeTapBlock.iconOutside;
		IIcon iicon1 = treeTapBlock.iconInside;
		f1 = 0.125F;

		if (p_147799_6_) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(treeTap, -1.0F + f1, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(treeTap, 1.0F - f1, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(treeTap, 0.0D, 0.0D, -1.0F + f1, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(treeTap, 0.0D, 0.0D, 1.0F - f1, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(treeTap, 0.0D, -1.0D + d0, 0.0D, iicon1);
			tessellator.draw();
		}
		else {
			renderer.renderFaceXPos(treeTap, p_147799_2_ - 1.0F + f1, p_147799_3_, p_147799_4_, iicon);
			renderer.renderFaceXNeg(treeTap, p_147799_2_ + 1.0F - f1, p_147799_3_, p_147799_4_, iicon);
			renderer.renderFaceZPos(treeTap, p_147799_2_, p_147799_3_, p_147799_4_ - 1.0F + f1, iicon);
			renderer.renderFaceZNeg(treeTap, p_147799_2_, p_147799_3_, p_147799_4_ + 1.0F - f1, iicon);
			renderer.renderFaceYPos(treeTap, p_147799_2_, p_147799_3_ - 1.0F + d0, p_147799_4_, iicon1);
		}

		renderer.setOverrideBlockTexture(iicon);
		double d3 = 0.25D;
		double d4 = 0.25D;
		renderer.setRenderBounds(d3, d4, d3, 1.0D - d3, d0 - 0.002D, 1.0D - d3);

		if (p_147799_6_) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(treeTap, 0.0D, 0.0D, 0.0D, iicon);
			tessellator.draw();
		}
		else {
			renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
		}

		if (!p_147799_6_) {
			double d1 = 0.375D;
			double d2 = 0.25D;
			renderer.setOverrideBlockTexture(iicon);

			if (i1 == 0) {
				renderer.setRenderBounds(d1, 0.0D, d1, 1.0D - d1, 0.25D, 1.0D - d1);
				renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
			}

			if (i1 == 2) {
				renderer.setRenderBounds(d1, d4, 0.0D, 1.0D - d1, d4 + d2, d3);
				renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
			}

			if (i1 == 3) {
				renderer.setRenderBounds(d1, d4, 1.0D - d3, 1.0D - d1, d4 + d2, 1.0D);
				renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
			}

			if (i1 == 4) {
				renderer.setRenderBounds(0.0D, d4, d1, d3, d4 + d2, 1.0D - d1);
				renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
			}

			if (i1 == 5) {
				renderer.setRenderBounds(1.0D - d3, d4, d1, 1.0D, d4 + d2, 1.0D - d1);
				renderer.renderStandardBlock(treeTap, p_147799_2_, p_147799_3_, p_147799_4_);
			}
		}

		renderer.clearOverrideBlockTexture();
		return true;
	}*/
}
