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

	public final int[] version;
	public final String name;
	public final ConfigParams params;

	public Config(final int[] version, final String name) {
		this(version, name, null, 0);
	}

	public Config(final int[] version, final String name, final String[] params, final int paramsOffset) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("name");
		}
		this.version = version;
		this.name = name;
		this.params = (params == null || params.length <= paramsOffset) ? null : new ConfigParams(params, paramsOffset);
	}

	public static void registerFromResources(final String directory) {
		MinecraftItem.registerFromResource(directory);
		MinecraftBlock.registerFromResource(directory);
		Element.registerFromResource(directory);
		Mineral.registerFromResource(directory);
		Alloy.registerFromResource(directory);
		Compound.registerFromResource(directory);
		Polymer.registerFromResource(directory);
		PolymerObject.registerFromResource(directory);
		Fuel.registerFromResource(directory);

		Ore.registerFromResource(directory);
		Ingot.registerFromResource(directory);
		CompressedBlock.registerFromResource(directory);
		Catalyst.registerFromResource(directory);
		ElementVessel.registerFromResource(directory);
		CompoundVessel.registerFromResource(directory);
		PolymerPellets.registerFromResource(directory);
		PolymerFibers.registerFromResource(directory);
		PolymerBlock.registerFromResource(directory);
		PolymerSlab.registerFromResource(directory);
		PolymerStairs.registerFromResource(directory);
		PolymerWall.registerFromResource(directory);
		Mold.registerFromResource(directory);
		MoldedItem.registerFromResource(directory);
		GrippedTool.registerFromResource(directory);
		PogoStick.registerFromResource(directory);
		Inventory.registerFromResource(directory);
		CustomObject.registerFromResource(directory);
		InternalObject.registerFromResource(directory);
	}

	private static Map<String, ConfigRegistry<? extends Config>> registriesByType = new HashMap<String, ConfigRegistry<? extends Config>>();
	static {
		registriesByType.put(MinecraftItem.class.getSimpleName(), MinecraftItem.registry);
		registriesByType.put(MinecraftBlock.class.getSimpleName(), MinecraftBlock.registry);
		registriesByType.put(Element.class.getSimpleName(), Element.registry);
		registriesByType.put(Mineral.class.getSimpleName(), Mineral.registry);
		registriesByType.put(Alloy.class.getSimpleName(), Alloy.registry);
		registriesByType.put(Compound.class.getSimpleName(), Compound.registry);
		registriesByType.put(Polymer.class.getSimpleName(), Polymer.registry);
		registriesByType.put(PolymerObject.class.getSimpleName(), PolymerObject.registry);
		registriesByType.put(Fuel.class.getSimpleName(), Fuel.registry);

		registriesByType.put(Ore.class.getSimpleName(), Ore.registry);
		registriesByType.put(Ingot.class.getSimpleName(), Ingot.registry);
		registriesByType.put(Catalyst.class.getSimpleName(), Catalyst.registry);
		registriesByType.put(ElementVessel.class.getSimpleName(), ElementVessel.registry);
		registriesByType.put(CompoundVessel.class.getSimpleName(), CompoundVessel.registry);
		registriesByType.put(PolymerPellets.class.getSimpleName(), PolymerPellets.registry);
		registriesByType.put(PolymerFibers.class.getSimpleName(), PolymerFibers.registry);
		registriesByType.put(PolymerBlock.class.getSimpleName(), PolymerBlock.registry);
		registriesByType.put(PolymerSlab.class.getSimpleName(), PolymerSlab.registry);
		registriesByType.put(PolymerStairs.class.getSimpleName(), PolymerStairs.registry);
		registriesByType.put(PolymerWall.class.getSimpleName(), PolymerWall.registry);
		registriesByType.put(Mold.class.getSimpleName(), Mold.registry);
		registriesByType.put(MoldedItem.class.getSimpleName(), MoldedItem.registry);
		registriesByType.put(GrippedTool.class.getSimpleName(), GrippedTool.registry);
		registriesByType.put(PogoStick.class.getSimpleName(), PogoStick.registry);
		registriesByType.put(Inventory.class.getSimpleName(), Inventory.registry);
		registriesByType.put(CustomObject.class.getSimpleName(), CustomObject.registry);
		registriesByType.put(InternalObject.class.getSimpleName(), InternalObject.registry);
	}

	public static Config find(final String type, final String name) {
		final ConfigRegistry<? extends Config> registry = registriesByType.get(type.replaceAll(" ", ""));
		if (registry != null) {
			return registry.get(name);
		}
		return null;
	}
}