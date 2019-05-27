package edu.utd.minecraft.mod.polycraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class TileEntityPolymerBrickRenderer extends TileEntitySpecialRenderer {

	private final ModelPolymerBrick modelPolymerBrick;
	private final ResourceLocation modelTexture = new ResourceLocation(PolycraftMod.MODID.toLowerCase(), "/textures/blocks/brick_top_black.png");

	//private IModelCustom chemicalProcessor;
	//private ResourceLocation cpObjFile;
	//private ResourceLocation cpTextureFile;
	//private String objFile = "/textures/models/inventories/teddy_bear2.obj";
	//private String textureFile = "/textures/models/inventories/ChemicalProcessor_Map.png";

	public TileEntityPolymerBrickRenderer() {
		this.modelPolymerBrick = new ModelPolymerBrick();
		//this.cpObjFile = new ResourceLocation(PolycraftMod.MODID, objFile);
		//this.chemicalProcessor = AdvancedModelLoader.loadModel(cpObjFile);
		//this.cpTextureFile = new ResourceLocation(PolycraftMod.MODID, textureFile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick, int destroyStage) {
		if (tileEntity instanceof TileEntityPolymerBrick) {
			TileEntityPolymerBrick tilePolymerBrick = (TileEntityPolymerBrick) tileEntity;
			EnumFacing direction = null;
			short angle = 0;

			if (tilePolymerBrick.getWorld() != null) {
				direction = tilePolymerBrick.getOrientation();
			}
			// System.out.println(direction + "|" + angle);
			GL11.glPushMatrix();
			scaleTranslateRotate(x, y, z, direction);

			//TODO: Walter to implement to fix 3D renderers
			Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
			this.modelPolymerBrick.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

			//Minecraft.getMinecraft().renderEngine.bindTexture(cpTextureFile);
			//this.chemicalProcessor.renderAll();
			GL11.glPopMatrix();
		}

	}

	private void scaleTranslateRotate(double x, double y, double z, EnumFacing orientation) {
		if (orientation == EnumFacing.NORTH) {
			// System.out.println("North");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(180, 0F, 0F, 1F);
			// GL11.glRotatef(-90F, 1F, 0F, 0F);
		} else if (orientation == EnumFacing.EAST) {
			// System.out.println("East");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(270, 0F, 1F, 0F);
			GL11.glRotatef(180, 0F, 0F, 1F);
		} else if (orientation == EnumFacing.SOUTH) {
			// System.out.println("South");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(180, 0F, 1F, 0F);
			GL11.glRotatef(180, 0F, 0F, 1F);
		} else if (orientation == EnumFacing.WEST) {
			// System.out.println("West");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(-180, 0F, 0F, 1F);
			GL11.glRotatef(-90, 0F, 1F, 0F);
		}
	}
}