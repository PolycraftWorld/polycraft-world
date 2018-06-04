package edu.utd.minecraft.mod.polycraft.entity.entityliving.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.entity.Entity;

public class ModelPolySlime extends ModelSlime {

	public ModelPolySlime()
	{
		this(16);
	}
	
	public ModelPolySlime(int p_i1157_1_) {
		super(p_i1157_1_);
		// TODO Auto-generated constructor stub
	}


	

	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
			float p_78088_6_, float p_78088_7_) {
		// Rotation angles are called by the superclass method.
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
	}
}
