package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;

public class Alloy extends Entity {

	public static final EntityRegistry<Alloy> registry = new EntityRegistry<Alloy>();

	static {
		// TODO need to fix this to be ordered (since the chemical processor is order matters)
		Map<Object, Integer> processingMaterials = new HashMap<Object, Integer>();
		processingMaterials.put(Blocks.iron_block, 9);
		processingMaterials.put(Ingot.registry.get("ingot_element_carbon"), 1);
		registry.register(new Alloy("Steel", processingMaterials, 9));
	}

	public final String name;
	public final int processingOutputBlocks;
	public final Map<Object, Integer> processingInputMaterials;

	public Alloy(final String name, final Map<Object, Integer> processingInputMaterials, final int processingOutputBlocks) {
		super("alloy_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.name = name;
		this.processingInputMaterials = processingInputMaterials;
		this.processingOutputBlocks = processingOutputBlocks;
	}
}