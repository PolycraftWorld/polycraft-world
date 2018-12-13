package edu.utd.minecraft.mod.polycraft.minigame;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;

/**
 * Simple collision box for minigames.
 * 
 * @author Chris
 */
public class BoundingBox {

	private boolean yBound = false; // If the bounding box checks an entity's height for collisions.
	private Color color = Color.GREEN; // The color of the rendered bounding box.
	private float lineWidth = 4; // The width of rendered bounding box lines.
	private double x1, z1, x2, z2; // The (x, z) coordinates of the northwest and southeast corners.
	private float y1 = 0, y2 = 256; // The lowest and highest points of the bounding box.
	private boolean rendering = true; // True if the bounding box is visible to clients.
	
	/**
	 * <pre>
	 * BoundingBox(&lt;x1&gt;, &lt;z1&gt;, &lt;x2&gt;, &lt;z2&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with two nonadjacent corners specified as (x1, z1) and
	 * (x2, z2) in that order.
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 */
	public BoundingBox(double x1, double z1, double x2, double z2) {
		setBounds(x1, z1, x2, z2);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;x1&gt;, &lt;z1&gt;, &lt;x2&gt;, &lt;z2&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with two nonadjacent corners specified as (x1, z1) and
	 * (x2, z2) in that order alongside a color.
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 * @param color
	 */
	public BoundingBox(double x1, double z1, double x2, double z2, Color color) {
		setBounds(x1, z1, x2, z2);
		setColor(color);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;xc&gt;, &lt;zc&gt;, &lt;radius&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with a center specified as (xc, zc) and a radius.
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 */
	public BoundingBox(double xc, double zc, double radius) {
		setBounds(xc, zc, radius);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;xc&gt;, &lt;zc&gt;, &lt;radius&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with a center specified as (xc, zc), a radius, and a
	 * color.
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 * @param color
	 */
	public BoundingBox(double xc, double zc, double radius, Color color) {
		setBounds(xc, zc, radius);
		setColor(color);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;x1&gt;, &lt;z1&gt;, &lt;x2&gt;, &lt;z2&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with two nonadjacent corners specified as (x1, z1) and
	 * (x2, z2) in that order, a floor (y1), and a roof (y2).
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 * @param y1
	 * @param y2
	 */
	public BoundingBox(double x1, double z1, double x2, double z2, float y1, float y2) {
		setBounds(x1, z1, x2, z2, y1, y2);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;x1&gt;, &lt;z1&gt;, &lt;x2&gt;, &lt;z2&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with two nonadjacent corners specified as (x1, z1) and
	 * (x2, z2) in that order, a floor (y1), a roof (y2), and a color.
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 * @param y1
	 * @param y2
	 * @param color
	 */
	public BoundingBox(double x1, double z1, double x2, double z2, float y1, float y2, Color color) {
		setBounds(x1, z1, x2, z2, y1, y2);
		setColor(color);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;xc&gt;, &lt;zc&gt;, &lt;radius&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with a center specified as (xc, zc), a radius, a floor
	 * (y1), and a roof (y2).
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 * @param y1
	 * @param y2
	 */
	public BoundingBox(double xc, double zc, double radius, float y1, float y2) {
		setBounds(xc, zc, radius, y1, y2);
	}

	/**
	 * <pre>
	 * BoundingBox(&lt;xc&gt;, &lt;zc&gt;, &lt;radius&gt;, [y1], [y2], [{@link Color}])
	 * </pre>
	 * 
	 * Set up a bounding box with a center specified as (xc, zc), a radius, a floor
	 * (y1), a roof (y2), and a color.
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 * @param y1
	 * @param y2
	 * @param color
	 */
	public BoundingBox(double xc, double zc, double radius, float y1, float y2, Color color) {
		setBounds(xc, zc, radius, y1, y2);
		setColor(color);
	}

	/**
	 * Reshapes the bounding box with two nonadjacent corners specified as (x1, z1)
	 * and (x2, z2) in that order.
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 * @return this object
	 */
	public BoundingBox setBounds(double x1, double z1, double x2, double z2) {
		this.x1 = Math.min(x1, x2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.z2 = Math.max(z1, z2);
		calculateAESTHETICS();
		return this;
	}

	/**
	 * Reshapes the bounding box with a center specified as (xc, zc) and a radius.
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 * @return this object
	 */
	public BoundingBox setBounds(double xc, double zc, double radius) {
		this.x1 = xc - radius;
		this.z1 = zc - radius;
		this.x2 = xc + radius;
		this.z2 = zc + radius;
		calculateAESTHETICS();
		return this;
	}

	/**
	 * 
	 * Reshapes the bounding box with two nonadjacent corners specified as (x1, z1)
	 * and (x2, z2) in that order, a floor (y1), and a roof (y2).
	 * 
	 * @param x1
	 * @param z1
	 * @param x2
	 * @param z2
	 * @param y1
	 * @param y2
	 * @return this object
	 */
	public BoundingBox setBounds(double x1, double z1, double x2, double z2, float y1, float y2) {
		return setBounds(x1, z1, x2, z2).setYBounds(y1, y2);
	}

	/**
	 * Reshapes the bounding box with a center specified as (xc, zc), a radius, a
	 * floor (y1), and a roof (y2).
	 * 
	 * @param xc
	 * @param zc
	 * @param radius
	 * @param y1
	 * @param y2
	 * @return this object
	 */
	public BoundingBox setBounds(double xc, double zc, double radius, float y1, float y2) {
		return setBounds(xc, zc, radius).setYBounds(y1, y2);
	}

	/**
	 * Set to true if the collision of the bounding box should also check for
	 * height.
	 * 
	 * @param yBound
	 * @return this object
	 */
	public BoundingBox setYBound(boolean yBound) {
		this.yBound = yBound;
		return this;
	}

	/**
	 * Resizes the height of the bounding box based on the specified floor (y1) and
	 * roof (y2). <br>
	 * This also automatically sets if the box is yBound to true.
	 * 
	 * @param y1
	 * @param y2
	 * @return this object
	 */
	public BoundingBox setYBounds(float y1, float y2) {
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.yBound = true;
		calculateAESTHETICS();
		return this;
	}

	/**
	 * True if the specified entity is touching the bounding box in any capacity.
	 * 
	 * @param entity
	 * @return boolean
	 */
	public boolean isInBox(Entity entity) {
		if (yBound && (entity.boundingBox.maxY < y1 || entity.boundingBox.minY > y2))
			return false;
		return entity.posX >= x1 && entity.posX <= x2 && entity.posZ >= z1 && entity.posZ <= z2;
	}

	/**
	 * Get the {@link Color} of the bounding box.
	 * 
	 * @return Color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the {@link Color} of the bounding box if visible.
	 * 
	 * @param color
	 * @return this object
	 */
	public BoundingBox setColor(Color color) {
		this.color = color;
		return this;
	}

	/**
	 * The width of the rendered line.
	 * 
	 * @return float
	 */
	public float getLineWidth() {
		return lineWidth;
	}
	
	/**
	 * Gets the average radius of the bounding box (in case it's a rectangle)
	 * Divide by 4 to get the average and then to get the radius
	 * @return radius of the bounding box.
	 */
	public double getRadius() {
		return ((x2 - x1) + (z2 - z1))/4;
	}

	/**
	 * Set the width of the lines of the bounding box.
	 * 
	 * @param lineWidth
	 * @return this object
	 */
	public BoundingBox setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	/**
	 * True if the bounding box is visible on clients.
	 * 
	 * @return boolean
	 */
	public boolean isRendering() {
		return rendering;
	}

	/**
	 * Set to true if the bounding box should be visible on clients.
	 * 
	 * @param rendering
	 * @return this object
	 */
	public BoundingBox setRendering(boolean rendering) {
		this.rendering = rendering;
		return this;
	}

	// Rendering variables.
	private int xRange, yRange, zRange, hRange;
	private float xSpacing, ySpacing, zSpacing;

	/**
	 * Determines parameters for drawing the bounding box ahead of time.
	 * 
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

	/**
	 * This should be called by the minigame manager on the client's side when
	 * rendering.
	 */
	public void render(Entity entity) {
		if (entity.worldObj.isRemote && rendering) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glLineWidth(lineWidth);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
					color.getAlpha() / 255F); // Set color to specified color.

			float offset = (entity.ticksExisted % 20) / 20F; // For render animation.

			if (yBound) { // Top and bottom of bounding box render.
				for (int i = 0; i < hRange; i++) {
					float offset3 = i + offset;
					float offset4 = i + 1 - offset;
					double x1o3 = x1 + xSpacing * offset3;
					double x1o4 = x1 + xSpacing * offset4;
					double x2o3 = x2 - xSpacing * offset3;
					double x2o4 = x2 - xSpacing * offset4;
					double z1o3 = z1 + zSpacing * offset3;
					double z1o4 = z1 + zSpacing * offset4;
					double z2o3 = z2 - zSpacing * offset3;
					double z2o4 = z2 - zSpacing * offset4;
					GL11.glVertex3d(x1o4, y2, z1o4);
					GL11.glVertex3d(x2o4, y2, z1o4);
					GL11.glVertex3d(x2o4, y2, z1o4);
					GL11.glVertex3d(x2o4, y2, z2o4);
					GL11.glVertex3d(x2o4, y2, z2o4);
					GL11.glVertex3d(x1o4, y2, z2o4);
					GL11.glVertex3d(x1o4, y2, z2o4);
					GL11.glVertex3d(x1o4, y2, z1o4);

					GL11.glVertex3d(x1o3, y1, z1o3);
					GL11.glVertex3d(x2o3, y1, z1o3);
					GL11.glVertex3d(x2o3, y1, z1o3);
					GL11.glVertex3d(x2o3, y1, z2o3);
					GL11.glVertex3d(x2o3, y1, z2o3);
					GL11.glVertex3d(x1o3, y1, z2o3);
					GL11.glVertex3d(x1o3, y1, z2o3);
					GL11.glVertex3d(x1o3, y1, z1o3);
				}
			}

			// Sides of bounding box render.
			for (int i = 0; i < yRange; i++) {
				float height = y2 - ySpacing * (i + offset);
				GL11.glVertex3d(x1, height, z1);
				GL11.glVertex3d(x2, height, z1);

				GL11.glVertex3d(x2, height, z1);
				GL11.glVertex3d(x2, height, z2);

				GL11.glVertex3d(x2, height, z2);
				GL11.glVertex3d(x1, height, z2);

				GL11.glVertex3d(x1, height, z2);
				GL11.glVertex3d(x1, height, z1);
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}