package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Element extends Config {

	public static final ConfigRegistry<Element> registry = new ConfigRegistry<Element>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Element.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Element(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //name
						line[index++], //symbol
						Matter.State.valueOf(line[index++]), //state
						Integer.parseInt(line[index++]), //atomicNumber
						Integer.parseInt(line[index++]), //group
						Integer.parseInt(line[index++]), //period
						Double.parseDouble(line[index++]), //weight
						Double.parseDouble(line[index++]), //density
						Double.parseDouble(line[index++]), //melt
						Double.parseDouble(line[index++]), //boil
						Double.parseDouble(line[index++]), //heat
						Double.parseDouble(line[index++]), //electronegativity
						Double.parseDouble(line[index++]) //abundance
				));
			}
	}

	public final String symbol;
	public final int atomicNumber;
	public final Matter.State state;
	public final int group;
	public final int period;
	public final double weight;
	public final double density;
	public final double melt;
	public final double boil;
	public final double heat;
	public final double electronegativity;
	public final double abundance;

	public Element(final int[] version, final String name, final String symbol, final Matter.State state, final int atomicNumber,
			final int group, final int period, final double weight, final double density,
			final double melt, final double boil, final double heat,
			final double electronegativity, final double abundance) {
		super(version, name);
		this.symbol = symbol;
		this.state = state;
		this.atomicNumber = atomicNumber;
		this.group = group;
		this.period = period;
		this.weight = weight;
		this.density = density;
		this.melt = melt;
		this.boil = boil;
		this.heat = heat;
		this.electronegativity = electronegativity;
		this.abundance = abundance;
	}
}