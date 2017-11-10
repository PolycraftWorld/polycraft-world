package edu.utd.minecraft.mod.polycraft.entity.npc;

import java.util.ArrayList;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.EntityList;
import net.minecraft.world.biome.BiomeGenBase;

public class PolycraftNPCs {

	public static void mainRegistry() {
		// TODO: Make this better as we add more entities.
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityResearchAssistant.class, "researchAssistant", id);
		EntityRegistry.registerModEntity(EntityResearchAssistant.class, "researchAssistant", id, PolycraftMod.instance,
				64, 1, true);
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, 0xEEEEEE, 0x111111));
		
		id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityTerritoryFlag.class, "TerritoryFlag", id);
		EntityRegistry.registerModEntity(EntityTerritoryFlag.class, "TerritoryFlag", id, PolycraftMod.instance,
				64, 1, true);
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, 0x000000, 0x000000));
		

	}
}
