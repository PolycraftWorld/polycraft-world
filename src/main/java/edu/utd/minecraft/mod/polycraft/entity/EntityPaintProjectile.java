package edu.utd.minecraft.mod.polycraft.entity;

import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class EntityPaintProjectile extends Entity{
	
	ItemSlingshot.SlingshotType type;
	
	public EntityPaintProjectile(World world) {
		super(world);
	}

	public EntityPaintProjectile(World world, Entity shootingEntity, float f) {
		super(world);
	}
}
