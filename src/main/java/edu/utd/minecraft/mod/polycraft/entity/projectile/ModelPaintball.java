package edu.utd.minecraft.mod.polycraft.entity.projectile;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModelCustom;
//import net.minecraftforge.client.model.obj.ObjModelLoader;

@SideOnly(Side.CLIENT)
public class ModelPaintball extends ModelBase
{
	boolean inColor;
	int color;
    public ModelRenderer part1;
    public ModelRenderer part2;
    public ModelRenderer part3;
    public ModelRenderer part4;
    public ModelRenderer part5;
    
    public ModelRenderer part6;
    public ModelRenderer part7;
    public ModelRenderer part8;
    public ModelRenderer part9;
    public ModelRenderer part10;
    
    public ModelRenderer part11;
    public ModelRenderer part12;
    public ModelRenderer part13;
    public ModelRenderer part14;
    public ModelRenderer part15;
    
    public ModelRenderer part16;
    public ModelRenderer part17;
    public ModelRenderer part18;
    public ModelRenderer part19;
    public ModelRenderer part20;
	//private IModelCustom inventoryModel;
	public ResourceLocation objFile;
	public ResourceLocation textureFile;

    private static final String __OBFID = "CL_00000859";

    public ModelPaintball()
    {
    	this.objFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/ironcannonball.obj");
		//this.inventoryModel = AdvancedModelLoader.loadModel(this.objFile);
		//this.inventoryModel = new ObjModelLoader().loadInstance(this.objFile);
		this.textureFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/cannonball.png");
	
//        float f = 4.0F;
//        float f1 = 0.0F;
//        
//        this.part1 = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
//        this.part1.addBox(-8.0F, -6.9F, -8.0F, 16, 7, 16, 0.0F);
//        this.part1.setRotationPoint(0.0F, 0.0F + f + 20.0F, 0.0F);
//        
//        this.part2 = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
//        this.part2.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part2.setRotationPoint(0.0F, 0.0F + f + 9.0F, 0.0F);
//
//        this.part3 = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
//        this.part3.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part3.setRotationPoint(0.0F, 0.0F + f, 0.0F);
//        this.part4 = (new ModelRenderer(this, 0, 56)).setTextureSize(64, 64);
//        this.part4.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part4.setRotationPoint(0.0F, 0.0F + f - 10.0F, 0.0F);
//        this.part5 = (new ModelRenderer(this, 0, 76)).setTextureSize(64, 64);
//        this.part5.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part5.setRotationPoint(0.0F, 0.0F + f - 20.0F, 0.0F);
//        
//        this.part6 = (new ModelRenderer(this, 0, 96)).setTextureSize(64, 64);
//        this.part6.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part6.setRotationPoint(0.0F, 0.0F + f - 30.0F, 0.0F);
//        this.part7 = (new ModelRenderer(this, 0, 116)).setTextureSize(64, 64);
//        this.part7.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part7.setRotationPoint(0.0F, 0.0F + f - 40.0F, 0.0F);
//        this.part8 = (new ModelRenderer(this, 0, 136)).setTextureSize(64, 64);
//        this.part8.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part8.setRotationPoint(0.0F, 0.0F + f - 50.0F, 0.0F);
//        this.part9 = (new ModelRenderer(this, 0, 156)).setTextureSize(64, 64);
//        this.part9.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part9.setRotationPoint(0.0F, 0.0F + f - 60.0F, 0.0F);
//        this.part10 = (new ModelRenderer(this, 0, 176)).setTextureSize(64, 64);
//        this.part10.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part10.setRotationPoint(0.0F, 0.0F + f - 70.0F, 0.0F);
//        
//        this.part11 = (new ModelRenderer(this, 0, 196)).setTextureSize(64, 64);
//        this.part11.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part11.setRotationPoint(0.0F, 0.0F + f - 80.0F, 0.0F);
//        this.part12 = (new ModelRenderer(this, 0, 216)).setTextureSize(64, 64);
//        this.part12.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part12.setRotationPoint(0.0F, 0.0F + f - 90.0F, 0.0F);
//        this.part13 = (new ModelRenderer(this, 0, 236)).setTextureSize(64, 64);
//        this.part13.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part13.setRotationPoint(0.0F, 0.0F + f - 100.0F, 0.0F);
//        this.part14 = (new ModelRenderer(this, 0, 256)).setTextureSize(64, 64);
//        this.part14.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part14.setRotationPoint(0.0F, 0.0F + f - 110.0F, 0.0F);
//        this.part15 = (new ModelRenderer(this, 0, 276)).setTextureSize(64, 64);
//        this.part15.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part15.setRotationPoint(0.0F, 0.0F + f - 120.0F, 0.0F);
//        
//        this.part16 = (new ModelRenderer(this, 0, 296)).setTextureSize(64, 64);
//        this.part16.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part16.setRotationPoint(0.0F, 0.0F + f - 130.0F, 0.0F);
//        this.part17 = (new ModelRenderer(this, 0, 316)).setTextureSize(64, 64);
//        this.part17.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part17.setRotationPoint(0.0F, 0.0F + f - 140.0F, 0.0F);
//        this.part18 = (new ModelRenderer(this, 0, 336)).setTextureSize(64, 64);
//        this.part18.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part18.setRotationPoint(0.0F, 0.0F + f - 150.0F, 0.0F);
//        this.part19 = (new ModelRenderer(this, 0, 356)).setTextureSize(64, 64);
//        this.part19.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.part19.setRotationPoint(0.0F, 0.0F + f - 160.0F, 0.0F);
//        this.part20 = (new ModelRenderer(this, 0, 376)).setTextureSize(64, 64);
//        this.part20.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F);
//        this.part20.setRotationPoint(0.0F, 0.0F + f - 170.0F, 0.0F);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
    {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
        this.part3.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
        this.part3.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
        this.part1.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI) * 0.25F;
        float f6 = MathHelper.sin(this.part1.rotateAngleY);
        float f7 = MathHelper.cos(this.part1.rotateAngleY);

    }
    
    public void setColor(int color)
    {
    	inColor=true;
    	this.color=color;
    }
    

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
    {
    	GL11.glRotatef(180.0F, 1.0F, 1.0F, 0.0F);
		GL11.glTranslatef(+1.3F, 2.5F, 3.75F);
    	GL11.glScalef(0.2F, 0.2F, 0.2F);
		GL11.glTranslatef(-12.45F, -25.0F, -37.5F);
    	Minecraft.getMinecraft().renderEngine.bindTexture(this.textureFile);
		if(inColor)
        {
            float r = (float)(color >> 16 & 0xff) / 255F;
            float g = (float)(color >> 8 & 0xff) / 255F;
            float b = (float)(color & 0xff) / 255F;
            GL11.glColor4f(r, g, b, 1.0F);
        }
		
		//this.inventoryModel.renderAll();
		
//        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
//        this.part1.render(p_78088_7_);
//        this.part2.render(p_78088_7_);
//        this.part3.render(p_78088_7_);
//        this.part4.render(p_78088_7_);
//        this.part5.render(p_78088_7_);
//        this.part6.render(p_78088_7_);
//        this.part7.render(p_78088_7_);
//        this.part8.render(p_78088_7_);
//        this.part9.render(p_78088_7_);
//        this.part10.render(p_78088_7_);
//        this.part11.render(p_78088_7_);
//        this.part12.render(p_78088_7_);
//        this.part13.render(p_78088_7_);
//        this.part14.render(p_78088_7_);
//        this.part15.render(p_78088_7_);
//        this.part16.render(p_78088_7_);
//        this.part17.render(p_78088_7_);
//        this.part18.render(p_78088_7_);
//        this.part19.render(p_78088_7_);
//        this.part20.render(p_78088_7_);
		

    }
}