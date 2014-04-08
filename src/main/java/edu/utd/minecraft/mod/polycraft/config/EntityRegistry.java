package edu.utd.minecraft.mod.polycraft.config;

import java.util.LinkedHashMap;

public class EntityRegistry<V extends Entity> extends LinkedHashMap<String, V> {

	public V register(final V entity) {
		if (containsKey(entity))
			throw new RuntimeException("Registry already contains entity for " + entity.name);
		put(entity.name, entity);
		return entity;
	}
}
