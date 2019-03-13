package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
// import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;

import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.entity.Entity;

public class RenderBox {

	private static final int MIN_VIEWING_RANGE = 16;
	private static final int Y_TOP = 255;
	private static Gson gson = new Gson();

	private int warnTicks;
	private int maxWarnTicks;
	private Color color = Color.RED;
	private float lineWidth = 8;
	private double x1, z1, x2, z2, y, h, xRange, zRange, range, xRange2, zRange2, range2;

	public RenderBox(double x1, double z1, double x2, double z2, double y, double h, int warnTicks) {
		this.x1 = Math.min(x1, x2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.z2 = Math.max(z1, z2);
		init(y, h, warnTicks);
	}

	public RenderBox(double xc, double zc, double radius, double y, double h, int warnTicks) {
		this.x1 = xc - radius;
		this.z1 = zc - radius;
		this.x2 = xc + radius;
		this.z2 = zc + radius;
		init(y, h, warnTicks);
	}

	public RenderBox setColor(Color color) {
		this.color = color;
		return this;
	}

	private void init(double y, double h, int warnTicks) {
		this.y = y;
		this.h = h;
		this.warnTicks = warnTicks;
		this.maxWarnTicks = warnTicks;
		this.xRange = x2 - x1;
		this.zRange = z2 - z1;
		this.range = Math.max(xRange, zRange);
		this.range2 = Math.pow(range, 2);
		if (this.range < MIN_VIEWING_RANGE)
			this.range = MIN_VIEWING_RANGE;
		if (this.range2 < this.range * 2)
			this.range2 = this.range * 2;
		this.xRange2 = xRange / 2;
		this.zRange2 = zRange / 2;
	}

	public void render(Entity entity) {
		double xComp = Math.pow(entity.posX - x1 - xRange, 2);
		double yComp = Math.pow(entity.posY - y, 2);
		double zComp = Math.pow(entity.posZ - z1 - zRange, 2);
		double allDist = Math.sqrt(xComp + yComp + zComp) - range;
		double horizDist = Math.sqrt(xComp + (entity.posY < y ? yComp : 0) + zComp) - range;
		double offset = warnTicks / (double) maxWarnTicks;
		double xRange = this.xRange2 * offset;
		double zRange = this.zRange2 * offset;
		double xRange2 = this.xRange2 * (1 - offset);
		double zRange2 = this.zRange2 * (1 - offset);
		double x1o1 = x1 + xRange;
		double z1o1 = z1 + zRange;
		double x2o1 = x2 - xRange;
		double z2o1 = z2 - zRange;
		double x1o2 = x1 + xRange2;
		double z1o2 = z1 + zRange2;
		double x2o2 = x2 - xRange2;
		double z2o2 = z2 - zRange2;
		double y1 = y + 0.01;
		double y11 = y + 0.02;
		double y2 = y + entity.posY + Y_TOP;
		float horizWidth = lineWidth;
		float vertWidth = lineWidth;
		if (allDist > 0)
			horizWidth *= ((range2 - allDist) / range2);
		if (horizDist > 0)
			vertWidth *= ((range2 - horizDist) / range2);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.2F);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x2, y1, z2);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.2F);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glVertex3d(x1o1, y11, z1o1);
		GL11.glVertex3d(x2o1, y11, z1o1);
		GL11.glVertex3d(x1o1, y11, z2o1);
		GL11.glVertex3d(x2o1, y11, z2o1);

		GL11.glEnd();

		if (horizWidth > 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5F);
			GL11.glLineWidth(horizWidth);
			GL11.glBegin(GL11.GL_LINES);

			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z1);

			GL11.glEnd();
		}

		if (vertWidth > 0 && (warnTicks > 20 || warnTicks % 2 == 1)) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5F);
			GL11.glLineWidth(vertWidth);
			GL11.glBegin(GL11.GL_LINES);

			GL11.glVertex3d(x1o2, y1, z1o2);
			GL11.glVertex3d(x1o2, y2, z1o2);
			GL11.glVertex3d(x2o2, y1, z1o2);
			GL11.glVertex3d(x2o2, y2, z1o2);
			GL11.glVertex3d(x2o2, y1, z2o2);
			GL11.glVertex3d(x2o2, y2, z2o2);
			GL11.glVertex3d(x1o2, y1, z2o2);
			GL11.glVertex3d(x1o2, y2, z2o2);

			GL11.glEnd();
		}

		// GL11.glEnable(GL11.GL_CULL_FACE);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static boolean clientRender = false;

	private static int lastTicksExisted = 0;

}
