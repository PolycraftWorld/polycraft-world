package edu.utd.minecraft.mod.polycraft.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPolymerBrick extends ModelBase {

	ModelRenderer LeftLeg;
	ModelRenderer RightLeg;
	ModelRenderer Back;
	ModelRenderer Top;
	ModelRenderer LeftSupportTop;
	ModelRenderer RightSupportTop;
	ModelRenderer BackSupport;
	ModelRenderer Shelf;
	ModelRenderer Book1;
	ModelRenderer Book2;
	ModelRenderer Book3;
	ModelRenderer Book4;
	ModelRenderer Book5;
	ModelRenderer Book6;
	ModelRenderer RightShelfSupport;
	ModelRenderer BackShelfSupport;
	ModelRenderer LeftShelfSupport;

	public ModelPolymerBrick() {
		textureWidth = 64;
		textureHeight = 64;

		LeftLeg = new ModelRenderer(this, 30, 32);
		LeftLeg.addBox(0F, 0F, 0F, 1, 16, 16);
		LeftLeg.setRotationPoint(-8F, 8F, -8F);
		LeftLeg.setTextureSize(64, 64);
		LeftLeg.mirror = true;
		setRotation(LeftLeg, 0F, 0F, 0F);

		RightLeg = new ModelRenderer(this, 30, 32);
		RightLeg.addBox(0F, 0F, 0F, 1, 16, 16);
		RightLeg.setRotationPoint(7F, 8F, -8F);
		RightLeg.setTextureSize(64, 64);
		RightLeg.mirror = true;
		setRotation(RightLeg, 0F, 0F, 0F);

		Back = new ModelRenderer(this, 0, 47);
		Back.addBox(0F, 0F, 0F, 14, 16, 1);
		Back.setRotationPoint(-7F, 8F, 7F);
		Back.setTextureSize(64, 64);
		Back.mirror = true;
		setRotation(Back, 0F, 0F, 0F);

		Top = new ModelRenderer(this, 6, 16);
		Top.addBox(0F, 0F, 0F, 14, 1, 15);
		Top.setRotationPoint(-7F, 8F, -8F);
		Top.setTextureSize(64, 64);
		Top.mirror = true;
		setRotation(Top, 0F, 0F, 0F);

		LeftSupportTop = new ModelRenderer(this, 32, 0);
		LeftSupportTop.addBox(0F, 0F, 0F, 1, 1, 15);
		LeftSupportTop.setRotationPoint(-7F, 9F, -8F);
		LeftSupportTop.setTextureSize(64, 64);
		LeftSupportTop.mirror = true;
		setRotation(LeftSupportTop, 0F, 0F, 0F);

		RightSupportTop = new ModelRenderer(this, 32, 0);
		RightSupportTop.addBox(0F, 0F, 0F, 1, 1, 15);
		RightSupportTop.setRotationPoint(6F, 9F, -8F);
		RightSupportTop.setTextureSize(64, 64);
		RightSupportTop.mirror = true;
		setRotation(RightSupportTop, 0F, 0F, 0F);

		BackSupport = new ModelRenderer(this, 6, 14);
		BackSupport.addBox(0F, 0F, 0F, 12, 1, 1);
		BackSupport.setRotationPoint(-6F, 9F, 6F);
		BackSupport.setTextureSize(64, 64);
		BackSupport.mirror = true;
		setRotation(BackSupport, 0F, 0F, 0F);
		Shelf = new ModelRenderer(this, 6, 48);

		Shelf.addBox(0F, 0F, 0F, 14, 1, 15);
		Shelf.setRotationPoint(-7F, 16F, -8F);
		Shelf.setTextureSize(64, 64);
		Shelf.mirror = true;
		setRotation(Shelf, 0F, 0F, 0F);

		Book1 = new ModelRenderer(this, 0, 0);
		Book1.addBox(0F, 0F, -1F, 1, 6, 4);
		Book1.setRotationPoint(-6F, 10F, -7F);
		Book1.setTextureSize(64, 64);
		Book1.mirror = true;
		setRotation(Book1, 0F, 0F, 0F);

		Book2 = new ModelRenderer(this, 0, 0);
		Book2.addBox(0F, 0F, 0F, 1, 6, 4);
		Book2.setRotationPoint(-4F, 10F, -8F);
		Book2.setTextureSize(64, 64);
		Book2.mirror = true;
		setRotation(Book2, 0F, 0F, 0F);

		Book3 = new ModelRenderer(this, 0, 0);
		Book3.addBox(0F, 0F, 0F, 1, 6, 4);
		Book3.setRotationPoint(-6F, 10F, -2F);
		Book3.setTextureSize(64, 64);
		Book3.mirror = true;
		setRotation(Book3, 0F, 0F, 0F);

		Book4 = new ModelRenderer(this, 0, 0);
		Book4.addBox(0F, 0F, 0F, 1, 6, 4);
		Book4.setRotationPoint(-4F, 10F, -2F);
		Book4.setTextureSize(64, 64);
		Book4.mirror = true;
		setRotation(Book4, 0F, 0F, 0F);

		Book5 = new ModelRenderer(this, 0, 0);
		Book5.addBox(0F, 0F, -2F, 1, 6, 4);
		Book5.setRotationPoint(-1F, 10F, 0F);
		Book5.setTextureSize(64, 64);
		Book5.mirror = true;
		setRotation(Book5, 0F, 3.141593F, 0F);

		Book6 = new ModelRenderer(this, 10, 0);
		Book6.addBox(0F, 0F, 0F, 1, 6, 4);
		Book6.setRotationPoint(0F, 10F, 2F);
		Book6.setTextureSize(64, 64);
		Book6.mirror = true;
		setRotation(Book6, 0F, 3.141593F, 0.2230717F);

		RightShelfSupport = new ModelRenderer(this, 32, 0);
		RightShelfSupport.addBox(0F, 0F, -1F, 1, 1, 15);
		RightShelfSupport.setRotationPoint(6F, 17F, -7F);
		RightShelfSupport.setTextureSize(64, 64);
		RightShelfSupport.mirror = true;
		setRotation(RightShelfSupport, 0F, 0F, 0F);

		BackShelfSupport = new ModelRenderer(this, 6, 14);
		BackShelfSupport.addBox(0F, 0F, 13F, 12, 1, 1);
		BackShelfSupport.setRotationPoint(-6F, 17F, -7F);
		BackShelfSupport.setTextureSize(64, 64);
		BackShelfSupport.mirror = true;
		setRotation(BackShelfSupport, 0F, 0F, 0F);

		LeftShelfSupport = new ModelRenderer(this, 32, 0);
		LeftShelfSupport.addBox(0F, 0F, -1F, 1, 1, 15);
		LeftShelfSupport.setRotationPoint(-7F, 17F, -7F);
		LeftShelfSupport.setTextureSize(64, 64);
		LeftShelfSupport.mirror = true;
		setRotation(LeftShelfSupport, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		LeftLeg.render(f5);
		RightLeg.render(f5);
		Back.render(f5);
		Top.render(f5);
		LeftSupportTop.render(f5);
		RightSupportTop.render(f5);
		BackSupport.render(f5);
		Shelf.render(f5);
		Book1.render(f5);
		Book2.render(f5);
		Book3.render(f5);
		Book4.render(f5);
		Book5.render(f5);
		Book6.renderWithRotation(f5);
		RightShelfSupport.render(f5);
		BackShelfSupport.render(f5);
		LeftShelfSupport.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
