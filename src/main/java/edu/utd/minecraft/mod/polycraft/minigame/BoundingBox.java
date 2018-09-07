package edu.utd.minecraft.mod.polycraft.minigame;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class BoundingBox {

	private boolean yBound = false;
	private Color color;
	private float lineWidth = 4;
	private double x1, z1, x2, z2;
	private float y1 = 0, y2 = 256;
	private boolean rendering = true;

	public BoundingBox(double x1, double z1, double x2, double z2) {
		setBounds(x1, z1, x2, z2);
		setColor(Color.GREEN);
	}

	public BoundingBox(double x1, double z1, double x2, double z2, Color color) {
		setBounds(x1, z1, x2, z2);
		setColor(color);
	}

	public BoundingBox(double xc, double zc, double radius) {
		setBounds(xc, zc, radius);
		setColor(Color.GREEN);
	}

	public BoundingBox(double xc, double zc, double radius, Color color) {
		setBounds(xc, zc, radius);
		setColor(color);
	}

	public BoundingBox setBounds(double x1, double z1, double x2, double z2) {
		this.x1 = Math.min(x1, x2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.z2 = Math.max(z1, z2);
		calculateAESTHETICS();
		return this;
	}

	public BoundingBox setBounds(double xc, double zc, double radius) {
		this.x1 = xc - radius;
		this.z1 = zc - radius;
		this.x2 = xc + radius;
		this.z2 = zc + radius;
		calculateAESTHETICS();
		return this;
	}

	public BoundingBox setYBound(boolean yBound) {
		this.yBound = yBound;
		return this;
	}

	public BoundingBox setYBounds(float y1, float y2) {
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.yBound = true;
		calculateAESTHETICS();
		return this;
	}

	public boolean isInBox(Entity entity) {
		if (yBound && (entity.boundingBox.maxY < y1 || entity.boundingBox.minY > y2))
			return false;
		return entity.posX >= x1 && entity.posX <= x2 && entity.posZ >= z1 && entity.posZ <= z2;
	}

	public Color getColor() {
		return color;
	}

	public BoundingBox setColor(Color color) {
		this.color = color;
		return this;
	}

	public boolean isRendering() {
		return rendering;
	}

	public BoundingBox setRendering(boolean rendering) {
		this.rendering = rendering;
		return this;
	}

	private int xRange, yRange, zRange, hRange;
	private float xSpacing, ySpacing, zSpacing;

	/**
	 * I'm sorry.
	 */
	private void calculateAESTHETICS() {
		xRange = (int) (x2 - x1);
		yRange = (int) (y2 - y1);
		zRange = (int) (z2 - z1);
		hRange = Math.min(xRange, zRange);
		xSpacing = (float) (hRange / (x2 - x1));
		ySpacing = yRange / (y2 - y1);
		zSpacing = (float) (hRange / (z2 - z1));
		hRange /= 2;
	}

	public void render(Entity entity) {
		if (entity.worldObj.isRemote && rendering) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			// GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glLineWidth(lineWidth);
			GL11.glBegin(GL11.GL_LINES);

			if (isInBox(entity))
				GL11.glColor4f(1 - color.getRed() / 255F, 1 - color.getGreen() / 255F, 1 - color.getBlue() / 255F,
						color.getAlpha() / 255F);

			else
				GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
						color.getAlpha() / 255F);

			float offset = (entity.ticksExisted % 20) / 20F;
			float offset2 = 1 - offset;

			if (yBound) {
				for (int i = 0; i < hRange; i++) {
					float offset3 = i + offset;
					float offset4 = i + offset2;
					GL11.glVertex3d(x1 + xSpacing * offset4, y2, z1 + zSpacing * offset4);
					GL11.glVertex3d(x2 - xSpacing * offset4, y2, z1 + zSpacing * offset4);
					GL11.glVertex3d(x2 - xSpacing * offset4, y2, z1 + zSpacing * offset4);
					GL11.glVertex3d(x2 - xSpacing * offset4, y2, z2 - zSpacing * offset4);
					GL11.glVertex3d(x2 - xSpacing * offset4, y2, z2 - zSpacing * offset4);
					GL11.glVertex3d(x1 + xSpacing * offset4, y2, z2 - zSpacing * offset4);
					GL11.glVertex3d(x1 + xSpacing * offset4, y2, z2 - zSpacing * offset4);
					GL11.glVertex3d(x1 + xSpacing * offset4, y2, z1 + zSpacing * offset4);

					GL11.glVertex3d(x1 + xSpacing * offset3, y1, z1 + zSpacing * offset3);
					GL11.glVertex3d(x2 - xSpacing * offset3, y1, z1 + zSpacing * offset3);
					GL11.glVertex3d(x2 - xSpacing * offset3, y1, z1 + zSpacing * offset3);
					GL11.glVertex3d(x2 - xSpacing * offset3, y1, z2 - zSpacing * offset3);
					GL11.glVertex3d(x2 - xSpacing * offset3, y1, z2 - zSpacing * offset3);
					GL11.glVertex3d(x1 + xSpacing * offset3, y1, z2 - zSpacing * offset3);
					GL11.glVertex3d(x1 + xSpacing * offset3, y1, z2 - zSpacing * offset3);
					GL11.glVertex3d(x1 + xSpacing * offset3, y1, z1 + zSpacing * offset3);
				}
			}

			for (int i = 0; i < yRange; i++) {
				GL11.glVertex3d(x1, y2 - ySpacing * (i + offset), z1);
				GL11.glVertex3d(x2, y2 - ySpacing * (i + offset), z1);

				GL11.glVertex3d(x2, y2 - ySpacing * (i + offset), z1);
				GL11.glVertex3d(x2, y2 - ySpacing * (i + offset), z2);

				GL11.glVertex3d(x2, y2 - ySpacing * (i + offset), z2);
				GL11.glVertex3d(x1, y2 - ySpacing * (i + offset), z2);

				GL11.glVertex3d(x1, y2 - ySpacing * (i + offset), z2);
				GL11.glVertex3d(x1, y2 - ySpacing * (i + offset), z1);
			}

			// GL11.glColor4f(1, 0, 0, 0.25F);
			// if (yBound) {
			// GL11.glVertex3d(x1, y1, z1); // A
			// GL11.glVertex3d(x1, y1, z2); // B
			// }
			// GL11.glVertex3d(x2, y1, z1); // C
			// GL11.glVertex3d(x2, y1, z2); // D
			// GL11.glColor4f(0, 1, 0, 0.25F);
			// GL11.glVertex3d(x2, y2, z2); // E
			// GL11.glColor4f(0, 0, 1, 0.25F);
			// GL11.glVertex3d(x1, y1, z2); // F (B)
			// GL11.glVertex3d(x1, y2, z2); // G
			// GL11.glColor4f(0, 1, 0, 0.25F);
			// GL11.glVertex3d(x1, y1, z1); // H (A)
			// GL11.glVertex3d(x1, y2, z1); // I
			// GL11.glColor4f(0, 0, 1, 0.25F);
			// GL11.glVertex3d(x2, y1, z1); // J (C)
			// GL11.glVertex3d(x2, y2, z1); // K
			// GL11.glColor4f(0, 1, 0, 0.25F);
			// GL11.glVertex3d(x2, y2, z2); // L (E)
			// if (yBound) {
			// GL11.glColor4f(1, 0, 0, 0.25F);
			// GL11.glVertex3d(x1, y2, z1); // M (I)
			// GL11.glVertex3d(x1, y2, z2); // N (G)
			// }

			GL11.glEnd();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
