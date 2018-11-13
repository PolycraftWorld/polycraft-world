package edu.utd.minecraft.mod.polycraft.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.world.World;

public class PolycraftEntityHanging extends EntityHanging {

	public static void register(Class entityClass, int id, String entityName){
		//EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityRegistry.registerModEntity(entityClass, entityName, id, PolycraftMod.instance, 0, 0, false);
		
	}
	
	public PolycraftEntityHanging(World p_i1588_1_) {
		super(p_i1588_1_);
		// TODO Auto-generated constructor stub
	}

	public PolycraftEntityHanging(World p_i1589_1_, int p_i1589_2_, int p_i1589_3_, int p_i1589_4_, int p_i1589_5_) {
		super(p_i1589_1_, p_i1589_2_, p_i1589_3_, p_i1589_4_, p_i1589_5_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getWidthPixels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightPixels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onBroken(Entity p_110128_1_) {
		// TODO Auto-generated method stub

	}

}
