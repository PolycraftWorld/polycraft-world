package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Config {
	private static Logger logger = LogManager.getLogger();

	protected static final String getVariableName(String name) {
		name = name.replaceAll("[^A-Za-z0-9]", "");
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public final String name;

	public Config(final String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("name");
		}
		this.name = name;
	}

	public static void registerFromResources(final String directory, final String extension, final String delimeter) {
		Element.registerFromResource(directory, extension, delimeter);
		Mineral.registerFromResource(directory, extension, delimeter);
		Alloy.registerFromResource(directory, extension, delimeter);
		Compound.registerFromResource(directory, extension, delimeter);
		Polymer.registerFromResource(directory, extension, delimeter);

		Ore.registerFromResource(directory, extension, delimeter);
		Ingot.registerFromResource(directory, extension, delimeter);
		CompressedBlock.registerFromResource(directory, extension, delimeter);
		Catalyst.registerFromResource(directory, extension, delimeter);
		//TODO Vessels
		PolymerPellets.registerFromResource(directory, extension, delimeter);
		PolymerFibers.registerFromResource(directory, extension, delimeter);
		PolymerSlab.registerFromResource(directory, extension, delimeter);
		PolymerBlock.registerFromResource(directory, extension, delimeter);
		Mold.registerFromResource(directory, extension, delimeter);
		MoldedItem.registerFromResource(directory, extension, delimeter);
		Inventory.registerFromResource(directory, extension, delimeter);
		CustomObject.registerFromResource(directory, extension, delimeter);
		InternalObject.registerFromResource(directory, extension, delimeter);
	}

	private static Map<String, ConfigRegistry<? extends Config>> registriesByType = new HashMap<String, ConfigRegistry<? extends Config>>();
	static
	{
		registriesByType.put(Element.class.getSimpleName(), Element.registry);
		registriesByType.put(Mineral.class.getSimpleName(), Mineral.registry);
		registriesByType.put(Alloy.class.getSimpleName(), Alloy.registry);
		registriesByType.put(Compound.class.getSimpleName(), Compound.registry);
		registriesByType.put(Polymer.class.getSimpleName(), Polymer.registry);

		registriesByType.put(Ore.class.getSimpleName(), Ore.registry);
		registriesByType.put(Ingot.class.getSimpleName(), Ingot.registry);
		registriesByType.put(Catalyst.class.getSimpleName(), Catalyst.registry);
		//TODO Vessels
		registriesByType.put(Catalyst.class.getSimpleName(), Catalyst.registry);
		registriesByType.put(PolymerPellets.class.getSimpleName(), PolymerPellets.registry);
		registriesByType.put(PolymerFibers.class.getSimpleName(), PolymerFibers.registry);
		registriesByType.put(PolymerSlab.class.getSimpleName(), PolymerSlab.registry);
		registriesByType.put(PolymerBlock.class.getSimpleName(), PolymerBlock.registry);
		registriesByType.put(Mold.class.getSimpleName(), Mold.registry);
		registriesByType.put(MoldedItem.class.getSimpleName(), MoldedItem.registry);
		registriesByType.put(Inventory.class.getSimpleName(), Inventory.registry);
		registriesByType.put(CustomObject.class.getSimpleName(), CustomObject.registry);
		registriesByType.put(InternalObject.class.getSimpleName(), InternalObject.registry);
	}

	public static Config find(final String type, final String name) {
		final ConfigRegistry<? extends Config> registry = registriesByType.get(type);
		if (registry != null) {
			return registry.get(name);
		}
		return null;
	}
}