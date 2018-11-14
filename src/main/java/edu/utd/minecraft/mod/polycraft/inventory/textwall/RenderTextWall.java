package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderTextWall extends RenderPainting {

	@Override
	protected ResourceLocation getEntityTexture(Entity textwall) {
		return new ResourceLocation(PolycraftMod.getAssetName("textures/gui/consent_background.png"));
	}
	
	 /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
	@Override
    public void doRender(EntityPainting textWall, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
		
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindEntityTexture(textWall);
        //this.mc.getTextureManager().bindTexture(background_image);
        this.getFontRendererFromRenderManager().drawString("Test123", textWall.getWidthPixels()/2, textWall.getHeightPixels()/2, 0);
        //int i = (this.width - 248) / 2;
        //int j = (this.height - 200) / 2;
        //this.drawTexturedModalRect(i, j, 0, 0, 248, 250);
		
//        GL11.glPushMatrix();
//        GL11.glTranslated(p_76986_2_, p_76986_4_, p_76986_6_);
//        GL11.glRotatef(p_76986_8_, 0.0F, 1.0F, 0.0F);
//        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
//        this.bindEntityTexture(p_76986_1_);
//        EntityPainting.EnumArt enumart = p_76986_1_.art;
//        float f2 = 0.0625F;
//        GL11.glScalef(f2, f2, f2);
//        this.func_77010_a(p_76986_1_, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
//        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//        GL11.glPopMatrix();
    }

}
