package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	public final String symbol;
	public final int atomicNumber;
	public final boolean fluid;
	public final int group;
	public final int period;
	public final double weight;
	public final double density;
	public final double melt;
	public final double boil;
	public final double heat;
	public final double electronegativity;
	public final double abundance;

	public Element(final String name, final String symbol, final int atomicNumber,
			final int group, final int period, final double weight, final double density,
			final double melt, final double boil, final double heat,
			final double electronegativity, final double abundance) {
		super(name);
		this.symbol = symbol;
		this.atomicNumber = atomicNumber;
		this.fluid = PolycraftMod.worldTemperatureKelvin >= melt;
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