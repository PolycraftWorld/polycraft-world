package edu.utd.minecraft.mod.polycraft.config;

import java.util.Map;

import net.minecraft.item.Item;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Fuel extends Config {

	public static final ConfigRegistry<Fuel> registry = new ConfigRegistry<Fuel>();
	private static final Map<Integer, Fuel> fuelsByIndex = Maps.newHashMap();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Fuel.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				//System.out.println("Custom object: "+line[3]);
				final int index = Integer.parseInt(line[4]);
				fuelsByIndex.put(index, registry.register(new Fuel(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //name
						Config.find(line[2], line[3]), //source
						index, //index
						Integer.parseInt(line[5]), //heatIntensity
						Float.parseFloat(line[6]) //heatDuration
						)));
			}
	}

	public static Fuel getFuel(final int index) {
		return fuelsByIndex.get(index);
	}

	public static class QuantifiedFuel {
		public final Fuel fuel;
		public final int quantity;

		public QuantifiedFuel(final Fuel fuel) {
			this(fuel, 1);
		}

		public QuantifiedFuel(final Fuel fuel, final int quantity) {
			this.fuel = fuel;
			this.quantity = quantity;
		}

		public float getHeatDuration() {
			return fuel.heatDurationSeconds * quantity;
		}
	}

	public static final Map<Item, QuantifiedFuel> quantifiedFuelsByItem = Maps.newLinkedHashMap();

	public static void registerQuantifiedFuels() {
		for (final Fuel fuel : registry.values()) {
			if (fuel.source instanceof MinecraftItem)
				quantifiedFuelsByItem.put(PolycraftRegistry.getItem(fuel.source.name), new QuantifiedFuel(fuel));
			else if (fuel.source instanceof MinecraftBlock)
				quantifiedFuelsByItem.put(PolycraftRegistry.getItem(fuel.source.name), new QuantifiedFuel(fuel));
			else if (fuel.source instanceof CustomObject)
				quantifiedFuelsByItem.put(PolycraftRegistry.getItem(fuel.source.name), new QuantifiedFuel(fuel));
			else if (fuel.source instanceof Element) {
				for (final ElementVessel elementVessel : ElementVessel.registry.values())
					if (elementVessel.source == fuel.source)
						quantifiedFuelsByItem.put(PolycraftRegistry.getItem(elementVessel.name), new QuantifiedFuel(fuel, elementVessel.vesselType.getQuantityOfSmallestType()));
			}
			else if (fuel.source instanceof Compound) {
				for (final CompoundVessel compoundVessel : CompoundVessel.registry.values())
				{
					if (compoundVessel.source == fuel.source)
					{
						quantifiedFuelsByItem.put(PolycraftRegistry.getItem(compoundVessel.name), new QuantifiedFuel(fuel, compoundVessel.vesselType.getQuantityOfSmallestType()));
						//beaker of crude oil (from compound vessels, polycraft materials)
						if ("kS".equals(compoundVessel.gameID))
							//FIXME hard coded for now, the recipe for a bucket of crude currently says you get 16 beakers for one bucket
							quantifiedFuelsByItem.put(PolycraftMod.itemOilBucket, new QuantifiedFuel(fuel, compoundVessel.vesselType.getQuantityOfSmallestType() * 16));
					}
				}
			}
			else
			{
				throw new Error("Fuel source not found: " + fuel.name);
			}
		}
	}

	public static Fuel getFuel(final Item item) {
		final QuantifiedFuel quantifiedFuel = quantifiedFuelsByItem.get(item);
		if (quantifiedFuel != null)
			return quantifiedFuel.fuel;
		return null;
	}

	public static int getHeatIntensity(final Item item) {
		final Fuel fuel = getFuel(item);
		if (fuel != null)
			return fuel.heatIntensity;
		return 0;
	}

	public static float getHeatDurationSeconds(final Item item) {
		final QuantifiedFuel quantifiedFuel = quantifiedFuelsByItem.get(item);
		if (quantifiedFuel != null)
			return quantifiedFuel.getHeatDuration();
		return 0;
	}

	public final Config source;
	public final int index;
	public final int heatIntensity;
	public final float heatDurationSeconds;

	public Fuel(final int[] version, final String name, final Config source, final int index, final int heatIntensity, final float heatDurationSeconds) {
		super(version, name);
		this.source = source;
		this.index = index;
		this.heatIntensity = heatIntensity;
		this.heatDurationSeconds = heatDurationSeconds;
	}
}