package edu.utd.minecraft.mod.polycraft.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.ModClassLoader;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.block.LabelTexture;
import edu.utd.minecraft.mod.polycraft.client.RenderIDs;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;

public class BlockPipeRenderingHandler implements ISimpleBlockRenderingHandler {
	
	protected final InternalObject config;

	public BlockPipeRenderingHandler(final InternalObject config) {
		super();
		this.config = config;
	}
	
	@Override
	public int getRenderId() {
		return config.renderID;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		//TODO: this does not seem to do anything yet...
		
		int meta = 3;
		Tessellator tessellator = Tessellator.instance;
		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess worldBlockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		return renderBlockPipeMetadata(worldBlockAccess, (BlockPipe) block, x, y, z, worldBlockAccess.getBlockMetadata(x, y, z), false, renderer); //changed this to true

	}

	public boolean renderBlockPipeMetadata(IBlockAccess worldBlockAccess, BlockPipe blockPipe, int xCoord, int yCoord, int zCoord, int metaData, boolean p_147799_6_, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		int directionOut = metaData;
		TileEntityBlockPipe bte = blockPipe.getTileEntityBlockPipeAtXYZ(worldBlockAccess, xCoord, yCoord, zCoord);
		
		int directionIn = bte.directionIn; //TileEntityBlockPipe.getConnectedDirection(worldBlockAccess, xCoord, yCoord, zCoord);
		
		double maxBound = 0.75D;
		double minBound = 0.25D;
		
		double lowMiddle = 0.25D;
		double highMiddle = 0.75D;
		double lowEdge = 0D;
		double highEdge = 1D;
		
		boolean SW = false;
		boolean SE = false;
		boolean NE = false;
		boolean NW = false;
		
		
//		double d1 = 0.375D;
//		double d2 = 0.25D;		
//		double d3 = 0.25D;
//		double d4 = 0.25D;
		
		float f1;
		
		//***************************************************
		//this renders the top part of the tree tap
		
		//renderer.setRenderBounds(minBound, minBound, 0.0D, maxBound, maxBound, 1.0D);
		//renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		
		
//		public static final int SIDE_BOTTOM = 0;
//		public static final int SIDE_TOP = 1;
//		public static final int SIDE_BACK = 2;
//		public static final int SIDE_FRONT = 3;
//		public static final int SIDE_LEFT = 4;
//		public static final int SIDE_RIGHT = 5;
		
//		renderer.renderFaceXPos(blockPipe, xCoord - 1.0F + f1, yCoord, zCoord, blockPipe.iconLeft);
//		renderer.renderFaceXNeg(blockPipe, xCoord + 1.0F - f1, yCoord, zCoord, blockPipe.iconRight);
//		renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord - 1.0F + f1, blockPipe.iconFront);
//		renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord + 1.0F - f1, blockPipe.iconBack);
//		renderer.renderFaceYPos(blockPipe, xCoord, yCoord - 1.0F + d0, zCoord, blockPipe.iconTop);
//		renderer.renderFaceYNeg(blockPipe, xCoord, yCoord + 1.0F - d0, zCoord, blockPipe.iconBottom);
		
		if (directionOut == ForgeDirection.DOWN.ordinal()) {	
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//		
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = lowEdge;
//			renderer.renderMinZ = minBound;
//			
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = lowMiddle;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(minBound, lowEdge, minBound, maxBound, lowMiddle, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
		
		if (directionOut == ForgeDirection.UP.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = highMiddle;
//			renderer.renderMinZ = minBound;			
//			
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = highEdge;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(minBound, highMiddle, minBound, maxBound, highEdge, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionOut == ForgeDirection.NORTH.ordinal()) {
			
		
	
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//					
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = lowEdge;				
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = lowMiddle;
			
			renderer.setRenderBounds(minBound, minBound, lowEdge, maxBound, maxBound, lowMiddle);			
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionOut == ForgeDirection.SOUTH.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//						
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = highMiddle;
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = highEdge;
//			
			renderer.setRenderBounds(minBound, minBound, highMiddle, maxBound, maxBound, highEdge);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionOut == ForgeDirection.WEST.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = lowEdge;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = minBound;
//			renderer.renderMaxX = lowMiddle;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(lowEdge, minBound, minBound, lowMiddle, maxBound, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionOut == ForgeDirection.EAST.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = highMiddle;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = minBound;
//			renderer.renderMaxX = highEdge;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(highMiddle, minBound, minBound, highEdge, maxBound, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
		
		
		if (directionIn == ForgeDirection.DOWN.ordinal()) {	
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//		
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = lowEdge;
//			renderer.renderMinZ = minBound;
//			
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = lowMiddle;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(minBound, lowEdge, minBound, maxBound, lowMiddle, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
		
		if (directionIn == ForgeDirection.UP.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = highMiddle;
//			renderer.renderMinZ = minBound;			
//			
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = highEdge;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(minBound, highMiddle, minBound, maxBound, highEdge, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionIn == ForgeDirection.NORTH.ordinal()) {
			
	
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//					
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = lowEdge;				
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = lowMiddle;
			
			renderer.setRenderBounds(minBound, minBound, lowEdge, maxBound, maxBound, lowMiddle);			
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionIn == ForgeDirection.SOUTH.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//						
//			renderer.renderMinX = minBound;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = highMiddle;
//			renderer.renderMaxX = maxBound;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = highEdge;
			
			renderer.setRenderBounds(minBound, minBound, highMiddle, maxBound, maxBound, highEdge);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionIn == ForgeDirection.WEST.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = lowEdge;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = minBound;
//			renderer.renderMaxX = lowMiddle;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(lowEdge, minBound, minBound, lowMiddle, maxBound, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
	
		if (directionIn == ForgeDirection.EAST.ordinal()) {
			
//			renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconBack);
//			renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconFront);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconHorizontal);
//			
//			renderer.renderMinX = highMiddle;
//			renderer.renderMinY = minBound;
//			renderer.renderMinZ = minBound;
//			renderer.renderMaxX = highEdge;
//			renderer.renderMaxY = maxBound;
//			renderer.renderMaxZ = maxBound;
			
			renderer.setRenderBounds(highMiddle, minBound, minBound, highEdge, maxBound, maxBound);
			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		}
		
		lowMiddle = 0.75D;
		highMiddle = 0.25D;
		lowEdge = 0.25D;
		highEdge = 0.75D;
		
			
//		renderer.renderFaceXPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		renderer.renderFaceXNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		renderer.renderFaceYPos(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		renderer.renderFaceYNeg(blockPipe, xCoord, yCoord, zCoord, blockPipe.iconSolid);
//		
//		renderer.renderMinX = highMiddle;
//		renderer.renderMinY = highMiddle;
//		renderer.renderMinZ = highMiddle;
//		renderer.renderMaxX = highEdge;
//		renderer.renderMaxY = highEdge;
//		renderer.renderMaxZ = highEdge;
		
		renderer.setOverrideBlockTexture(blockPipe.iconSolid);
		
		renderer.setRenderBounds(highMiddle, highMiddle, highMiddle, highEdge, highEdge, highEdge);
		renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
		
	
		

		tessellator.setBrightness(blockPipe.getMixedBrightnessForBlock(renderer.blockAccess, xCoord, yCoord, zCoord));
		int j1 = blockPipe.colorMultiplier(renderer.blockAccess, xCoord, yCoord, zCoord);
		float f = (j1 >> 16 & 255) / 255.0F;
		f1 = (j1 >> 8 & 255) / 255.0F;
		float f2 = (j1 & 255) / 255.0F;

//		if (EntityRenderer.anaglyphEnable) {
//			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
//			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
//			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
//			f = f3;
//			f1 = f4;
//			f2 = f5;
//		}

		tessellator.setColorOpaque_F(f, f1, f2);
		
		//***************************************************

//		f1 = 0.125F;
//
//			renderer.renderFaceXPos(blockPipe, xCoord - 1.0F + f1, yCoord, zCoord, blockPipe.iconLeft);
//			renderer.renderFaceXNeg(blockPipe, xCoord + 1.0F - f1, yCoord, zCoord, blockPipe.iconRight);
//			renderer.renderFaceZPos(blockPipe, xCoord, yCoord, zCoord - 1.0F + f1, blockPipe.iconFront);
//			renderer.renderFaceZNeg(blockPipe, xCoord, yCoord, zCoord + 1.0F - f1, blockPipe.iconBack);
//			renderer.renderFaceYPos(blockPipe, xCoord, yCoord - 1.0F + d0, zCoord, blockPipe.iconTop);
//			renderer.renderFaceYNeg(blockPipe, xCoord, yCoord + 1.0F - d0, zCoord, blockPipe.iconBottom);
//
//
//		renderer.setOverrideBlockTexture(blockPipe.iconLeft);
//		double d3 = 0.25D;
//		double d4 = 0.25D;
//		renderer.setRenderBounds(d3, d4, d3, 1.0D - d3, d0 - 0.002D, 1.0D - d3);
//
//			renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//
//			double d1 = 0.375D;
//			double d2 = 0.25D;
//			renderer.setOverrideBlockTexture(blockPipe.iconLeft);
//
//			if (directionOut == 0) {
//				renderer.setRenderBounds(d1, 0.0D, d1, 1.0D - d1, 0.25D, 1.0D - d1);
//				renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//			}
//
//			if (directionOut == 2) {
//				renderer.setRenderBounds(d1, d4, 0.0D, 1.0D - d1, d4 + d2, d3);
//				renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//			}
//
//			if (directionOut == 3) {
//				renderer.setRenderBounds(d1, d4, 1.0D - d3, 1.0D - d1, d4 + d2, 1.0D);
//				renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//			}
//
//			if (directionOut == 4) {
//				renderer.setRenderBounds(0.0D, d4, d1, d3, d4 + d2, 1.0D - d1);
//				renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//			}
//
//			if (directionOut == 5) {
//				renderer.setRenderBounds(1.0D - d3, d4, d1, 1.0D, d4 + d2, 1.0D - d1);
//				renderer.renderStandardBlock(blockPipe, xCoord, yCoord, zCoord);
//			}


		renderer.clearOverrideBlockTexture();
		return true;
	}
}
