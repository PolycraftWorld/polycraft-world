package edu.utd.minecraft.mod.polycraft.entity.entityliving;

import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;

public class EntityDummy extends EntityIronGolem {

	
	private static PolycraftEntity config;
	
	public EntityDummy(World p_i1595_1_) {
		super(p_i1595_1_);
		// TODO Auto-generated constructor stub
	}

	public static final void register(final PolycraftEntity polycraftEntity) {
		EntityDummy.config = polycraftEntity;
		PolycraftEntityLiving.register(EntityDummy.class, config.entityID, config.name, 0xFFDB00, 0xCCAC00);
	}
}
