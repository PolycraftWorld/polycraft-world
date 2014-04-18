package edu.utd.minecraft.mod.polycraft.config;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Element.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Element(
					line[0], //name
					line[1], //symbol
					Integer.parseInt(line[2]), //atomicNumber
					Integer.parseInt(line[3]), //group
					Integer.parseInt(line[4]), //period
					Double.parseDouble(line[5]), //weight
					Double.parseDouble(line[6]), //density
					Double.parseDouble(line[7]), //melt
					Double.parseDouble(line[8]), //boil
					Double.parseDouble(line[9]), //heat
					Double.parseDouble(line[10]), //electronegativity
					Double.parseDouble(line[11]) //abundance
			));
	}

	public final String symbol;
	public final int atomicNumber;
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