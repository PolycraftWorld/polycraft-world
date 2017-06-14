package edu.utd.minecraft.mod.polycraft.entity.npc;

import java.util.ArrayList;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.EntityList;
import net.minecraft.world.biome.BiomeGenBase;

public class PolycraftNPCs {

	public static void mainRegistry() {
		registerEntity();
	}

	public static void registerEntity() {
		createNPC(EntityMadScientist.class, "Mad Scientist", 0x00AACC, 0x001010);
	}

	public static void createNPC(Class entityClass, String entityName, int solidColor, int spotColor) {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityRegistry.registerModEntity(entityClass, entityName, solidColor, PolycraftMod.instance, 64, 1, true);
		ArrayList<BiomeGenBase> test = new ArrayList();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			if (biome != null)
				test.add(biome);
		// EntityRegistry.addSpawn(entityClass, 32, 2, 8,
		// EnumCreatureType.creature,
		// test.toArray(new BiomeGenBase[test.size()]));
		addEgg(id, solidColor, spotColor);
	}

	private static void addEgg(int id, int solidColor, int spotColor) {
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, solidColor, spotColor));
	}

	public static void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityMadScientist.class,
				new RenderMadScientist(new ModelMadScientist(), 0));
	}

}
