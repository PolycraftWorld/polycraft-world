package edu.utd.minecraft.mod.polycraft.entity.entityliving;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.entity.PolycraftSpawnEgg;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;

public class PolycraftEntityLiving {
	
	public static void register(Class entityClass, int id, String entityName, int solidColor, int spotColor){
		//EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityRegistry.registerModEntity(entityClass, entityName, id, PolycraftMod.instance, 64, 1, true);
		
		createEgg(entityName, solidColor, spotColor);
		
	}
	
	public static void register(Class entityClass, int id, String entityName){
		//EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
		EntityRegistry.registerModEntity(entityClass, entityName, id, PolycraftMod.instance, 64, 1, true);
		
		//createEgg(entityName, solidColor, spotColor);
		
	}
	
	private static void createEgg(String entityName, int solidColor, int spotColor){
		//EntityList.entityEggs.put(id, new EntityList.EntityEggInfo(id, solidColor, spotColor));
		Item itemSpawnEgg = new PolycraftSpawnEgg(entityName, solidColor, spotColor)
			      .setUnlocalizedName("spawn_egg_"+entityName.toLowerCase())
			      .setTextureName(PolycraftMod.getAssetName("textures/entity/SpawnEgg.png"));
			GameRegistry.registerItem(itemSpawnEgg, "spawnEgg"+entityName);
	}

}
