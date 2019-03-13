package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
// import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;

import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class RenderBox {

	private static final int MIN_VIEWING_RANGE = 16;
	private static final int Y_TOP = 255;
	private static Gson gson = new Gson();

	private int warnTicks;
	private int maxWarnTicks;
	private Color color = Color.RED;
	private float lineWidth = 8;
	private int maxBreathingTicks = 0; //Breathing effect cycle time (0 for no effect)
	private int breathingTicks = 0;
	private int breathingDir = 1; //1 for fading in , -1 for fading out
	private double x1, z1, x2, z2, y, h, xRange, zRange, range, xRange2, zRange2, range2;

	public RenderBox(double x1, double z1, double x2, double z2, double y, double h, int warnTicks) {
		this.x1 = Math.min(x1, x2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1 +1, x2+1);
		this.z2 = Math.max(z1 +1, z2 +1);
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
		this.maxBreathingTicks = 160;
		
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
		//double y2 = y + entity.posY + Y_TOP;
		double y2 = y + h + 0.02;
		float horizWidth = lineWidth;
		float vertWidth = lineWidth;
		if (allDist > 0)
			horizWidth *= ((range2 - allDist) / range2);
		if (horizDist > 0)
			vertWidth *= ((range2 - horizDist) / range2);
		
		float alphaLines, alphaFace;
		if(maxBreathingTicks == 0) {
			alphaFace = 0.2F;
			alphaLines = 0.5F;
		}else {
			float alphaMult = (float) ((breathingTicks * 1.0)/maxBreathingTicks);
			alphaFace = 0.2F * alphaMult + 0.1F;
			alphaLines = 0.5F * alphaMult  + 0.15F;
			if(breathingTicks == maxBreathingTicks) {
				breathingDir = -1;
			}else if(breathingTicks == 0) {
				breathingDir = 1;
			}
			breathingTicks += breathingDir;
		}
		
		
		
		

		if (horizWidth > 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alphaLines);
			GL11.glLineWidth(horizWidth);
			GL11.glBegin(GL11.GL_LINES);
			
			//bottom box lines
			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z1);
			
			//top box lines
			GL11.glVertex3d(x1, y2, z1);
			GL11.glVertex3d(x2, y2, z1);
			GL11.glVertex3d(x2, y2, z1);
			GL11.glVertex3d(x2, y2, z2);
			GL11.glVertex3d(x2, y2, z2);
			GL11.glVertex3d(x1, y2, z2);
			GL11.glVertex3d(x1, y2, z2);
			GL11.glVertex3d(x1, y2, z1);

			GL11.glEnd();
		}

		if (vertWidth > 0 && (warnTicks > 20 || warnTicks % 2 == 1)) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alphaLines);
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
		
		Vec3 v1, v2, v3, v4;

		//bottom box face
		v1 = Vec3.createVectorHelper(x1, y1, z1);
		v2 = Vec3.createVectorHelper(x2, y1, z1);
		v3 = Vec3.createVectorHelper(x1, y1, z2);
		v4 = Vec3.createVectorHelper(x2, y1, z2);
		drawFace(v1, v2, v3, v4, alphaFace);
		
		//top box face
		v1 = Vec3.createVectorHelper(x1, y2, z1);
		v2 = Vec3.createVectorHelper(x2, y2, z1);
		v3 = Vec3.createVectorHelper(x1, y2, z2);
		v4 = Vec3.createVectorHelper(x2, y2, z2);
		drawFace(v1, v2, v3, v4, alphaFace);

		// GL11.glEnable(GL11.GL_CULL_FACE);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void drawFace(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, float alpha) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		//top box face
		GL11.glVertex3d(v1.xCoord, v1.yCoord, v1.zCoord);
		GL11.glVertex3d(v2.xCoord, v2.yCoord, v2.zCoord);
		GL11.glVertex3d(v3.xCoord, v3.yCoord, v3.zCoord);
		GL11.glVertex3d(v4.xCoord, v4.yCoord, v4.zCoord);

		GL11.glEnd();
	}
	
	private void drawFace(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		drawFace(v1, v2, v3, v4, 0.2F);
	}

	public static boolean clientRender = false;

	private static int lastTicksExisted = 0;

}
