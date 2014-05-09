package edu.utd.minecraft.mod.polycraft.config;

import java.util.Map;

import net.minecraft.item.Item;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Fuel extends Config {

	public static final ConfigRegistry<Fuel> registry = new ConfigRegistry<Fuel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Fuel.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Fuel(
						line[0], //name
						Config.find(line[1], line[2]), //source
						Integer.parseInt(line[3]), //heatIntensity
						Float.parseFloat(line[4]) //heatDuration
				));
	}

	private static class QuantifiedFuel {
		public final Fuel fuel;
		public final int quantity;

		public QuantifiedFuel(final Fuel fuel) {
			this(fuel, 1);
		}

		public QuantifiedFuel(final Fuel fuel, final int quantity) {
			this.fuel = fuel;
			this.quantity = quantity;
		}
	}

	private static final Map<Item, QuantifiedFuel> quantifiedFuelsByItem = Maps.newHashMap();

	public static void registerQuantifiedFuels() {
		for (final Fuel fuel : registry.values()) {
			if (fuel.source instanceof MinecraftItem)
				quantifiedFuelsByItem.put(PolycraftRegistry.getItem(fuel.source.name), new QuantifiedFuel(fuel));
			else if (fuel.source instanceof Compound) {
				for (final CompoundVessel compoundVessel : CompoundVessel.registry.values())
					if (compoundVessel.source == fuel.source)
						quantifiedFuelsByItem.put(PolycraftRegistry.getItem(compoundVessel.name), new QuantifiedFuel(fuel, compoundVessel.vesselType.getQuantityOfSmallestType()));
			}
		}
	}

	public static int getHeatIntensity(final Item item) {
		final QuantifiedFuel quantifiedFuel = quantifiedFuelsByItem.get(item);
		if (quantifiedFuel != null)
			return quantifiedFuel.fuel.heatIntensity;
		return 0;
	}

	public static float getHeatDurationSeconds(final Item item) {
		final QuantifiedFuel quantifiedFuel = quantifiedFuelsByItem.get(item);
		if (quantifiedFuel != null)
			return quantifiedFuel.fuel.heatDurationSeconds * quantifiedFuel.quantity;
		return 0;
	}

	public final Config source;
	public final int heatIntensity;
	public final float heatDurationSeconds;

	public Fuel(final String name, final Config source, final int heatIntensity, final float heatDurationSeconds) {
		super(name);
		this.source = source;
		this.heatIntensity = heatIntensity;
		this.heatDurationSeconds = heatDurationSeconds;
	}
}