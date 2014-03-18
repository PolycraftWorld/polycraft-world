package edu.utd.minecraft.mod.polycraft.config;

import java.util.LinkedHashMap;

public class EntityRegistry<V extends Entity> extends LinkedHashMap<String, V> {

	public V register(final V entity) {
		put(entity.gameName, entity);
		return entity;
	}
}
