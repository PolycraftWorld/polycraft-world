package edu.utd.minecraft.mod.polycraft.entity.projectile;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.EntityPaintBall__Old;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet__Old;
import edu.utd.minecraft.mod.polycraft.entity.projectile.ModelPaintball;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderPaintball extends Render {

	private static final ResourceLocation PBTextures = new ResourceLocation(PolycraftMod.MODID,
			"textures/models/inventories/paintball.png");
	private static final String __OBFID = "CL_00000978";

	protected ModelPaintball ModelPaintball;
	private float shadowSize;
	public int pbcolor;
	public RenderPaintball(RenderManager renderManager) {
		super(renderManager);
		this.shadowSize = 0.5F;
		this.ModelPaintball = new ModelPaintball();
		this.ModelPaintball.setColor(0xFF660000);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker function
	 * which does the actual work. In all probability, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void
	 * func_76986_a(T entity, double d, double d1, double d2, float f, float f1).
	 * But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(EntityPaintBall__Old paintball, double p_76986_2_, double p_76986_4_, double p_76986_6_,
			float p_76986_8_, float p_76986_9_) {
	
		this.ModelPaintball.setColor(pbcolor);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) p_76986_2_+.6F, (float) p_76986_4_+.5F, (float) p_76986_6_-.9F);
		// GL11.glRotatef(180.0F - p_76986_8_, 0.0F, 1.0F, 0.0F);
//        float f2 = (float)p_76986_1_.getTimeSinceHit() - p_76986_9_;
//        float f3 = p_76986_1_.getDamageTaken() - p_76986_9_;
//
//        if (f3 < 0.0F)
//        {
//            f3 = 0.0F;
//        }
//
//        if (f2 > 0.0F)
//        {
//            GL11.glRotatef(MathHelper.sin(f2) * f2 * f3 / 10.0F * (float)p_76986_1_.getForwardDirection(), 1.0F, 0.0F, 0.0F);
//        }

		float f4 = .25F;
		GL11.glScalef(f4, f4, f4);
		//GL11.glScalef(1.0F / f4, 1.0F / f4, 1.0F / f4);
		this.bindTexture(PBTextures);
		//GL11.glScalef(-2.0F, -2.0F, 2.0F);
		this.ModelPaintball.render(paintball, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		

	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless
	 * you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityPaintBall__Old p_110775_1_) {
		return PBTextures;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless
	 * you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return this.getEntityTexture((EntityPaintBall__Old) p_110775_1_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker function
	 * which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void
	 * func_76986_a(T entity, double d, double d1, double d2, float f, float f1).
	 * But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		this.doRender((EntityPaintBall__Old) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
}