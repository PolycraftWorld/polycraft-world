package edu.utd.minecraft.mod.polycraft.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;

public class Alloy extends Entity {

	public static final EntityRegistry<Alloy> registry = new EntityRegistry<Alloy>();

	static {
		Map<Object, Integer> craftingInputs = new HashMap<Object, Integer>();
		craftingInputs.put(Items.iron_ingot, 8);
		craftingInputs.put(Ingot.registry.get("ingot_element_carbon"), 1);
		registry.register(new Alloy("Steel", 8, craftingInputs));
	}

	public final String name;
	public final int craftingAmount;
	public final Map<Object, Integer> craftingInputs;

	public Alloy(final String name, final int craftingAmount, final Map<Object, Integer> craftingInputs) {
		super("alloy_" + name.toLowerCase().replaceAll(" ", "_"), name);
		this.name = name;
		this.craftingAmount = craftingAmount;
		this.craftingInputs = craftingInputs;
	}
}