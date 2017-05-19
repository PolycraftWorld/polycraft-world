package edu.utd.minecraft.mod.polycraft.entity.npc;

import cpw.mods.fml.common.registry.EntityRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class PolycraftNPCs {
	
	public static void mainRegistry() {
		registerEntity();
	}
	
	public static void registerEntity() {
		
	}

	public static void createNPC(Class entityClass, String entityName, int solidColor, int spotColor) {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityRegistry.registerModEntity(entityClass, entityName, solidColor, PolycraftMod.instance, 16, 10, true);
		EntityRegistry.addSpawn(entityClass, 2, 0, 1, EnumCreatureType.creature, BiomeGenBase.getBiomeGenArray());
		addEgg(id, solidColor, spotColor);
	}
	
	private static void addEgg(int id, int solidColor, int spotColor) {
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, solidColor, spotColor));
	}

}
