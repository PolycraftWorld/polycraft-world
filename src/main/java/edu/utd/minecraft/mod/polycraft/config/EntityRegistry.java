package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;

public class EntityRegistry<V extends Entity> extends HashMap<String, V> {

	public V register(final V entity) {
		put(entity.gameName, entity);
		return entity;
	}
}
