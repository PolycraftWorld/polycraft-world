package edu.utd.minecraft.mod.polycraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class TileEntityPolymerBrickRenderer extends TileEntitySpecialRenderer {

	private final ModelPolymerBrick modelPolymerBrick;
	private final ResourceLocation modelTexture = new ResourceLocation(PolycraftMod.MODID.toLowerCase(), "/textures/blocks/brick_top_black.png");

	public TileEntityPolymerBrickRenderer() {
		this.modelPolymerBrick = new ModelPolymerBrick();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
		if (tileEntity instanceof TileEntityPolymerBrick) {
			TileEntityPolymerBrick tilePolymerBrick = (TileEntityPolymerBrick) tileEntity;
			ForgeDirection direction = null;
			short angle = 0;

			if (tilePolymerBrick.getWorldObj() != null) {
				direction = tilePolymerBrick.getOrientation();
			}
			// System.out.println(direction + "|" + angle);
			GL11.glPushMatrix();
			scaleTranslateRotate(x, y, z, direction);
			Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
			this.modelPolymerBrick.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}

	}

	private void scaleTranslateRotate(double x, double y, double z, ForgeDirection orientation) {
		if (orientation == ForgeDirection.NORTH) {
			// System.out.println("North");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(180, 0F, 0F, 1F);
			// GL11.glRotatef(-90F, 1F, 0F, 0F);
		} else if (orientation == ForgeDirection.EAST) {
			// System.out.println("East");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(270, 0F, 1F, 0F);
			GL11.glRotatef(180, 0F, 0F, 1F);
		} else if (orientation == ForgeDirection.SOUTH) {
			// System.out.println("South");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(180, 0F, 1F, 0F);
			GL11.glRotatef(180, 0F, 0F, 1F);
		} else if (orientation == ForgeDirection.WEST) {
			// System.out.println("West");
			GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
			GL11.glRotatef(-180, 0F, 0F, 1F);
			GL11.glRotatef(-90, 0F, 1F, 0F);
		}
	}
}