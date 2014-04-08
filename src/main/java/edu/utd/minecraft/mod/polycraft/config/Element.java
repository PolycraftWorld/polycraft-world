package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Element extends Entity {

	public static final EntityRegistry<Element> registry = new EntityRegistry<Element>();

	public static final Element hydrogen = registry.register(new Element("Hydrogen", "H", 1, 1, 1, 1.0082, 0.00008988, 14.01, 20.28, 14.304, 2.2, 1400));
	public static final Element helium = registry.register(new Element("Helium", "He", 2, 18, 1, 4.002602, 0.0001785, 0.956, 4.22, 5.193, 0, 0.008));
	public static final Element lithium = registry.register(new Element("Lithium", "Li", 3, 1, 2, 6.942, 0.534, 453.69, 1560, 3.582, 0.98, 20));
	public static final Element beryllium = registry.register(new Element("Beryllium", "Be", 4, 2, 2, 9.012182, 1.85, 1560, 2742, 1.825, 1.57, 2.8));
	public static final Element boron = registry.register(new Element("Boron", "B", 5, 13, 2, 10.812, 2.34, 2349, 4200, 1.026, 2.04, 10));
	public static final Element carbon = registry.register(new Element("Carbon", "C", 6, 14, 2, 12.011, 2.267, 3800, 4300, 0.709, 2.55, 200));
	public static final Element nitrogen = registry.register(new Element("Nitrogen", "N", 7, 15, 2, 14.0072, 0.0012506, 63.15, 77.36, 1.04, 3.04, 19));
	public static final Element oxygen = registry.register(new Element("Oxygen", "O", 8, 16, 2, 15.9992, 0.001429, 54.36, 90.2, 0.918, 3.44, 461000));
	public static final Element fluorine = registry.register(new Element("Fluorine", "F", 9, 17, 2, 18.9984, 0.001696, 53.53, 85.03, 0.824, 3.98, 585));
	public static final Element neon = registry.register(new Element("Neon", "Ne", 10, 18, 2, 20.1797, 0.0008999, 24.56, 27.07, 1.03, 0, 0.005));
	public static final Element sodium = registry.register(new Element("Sodium", "Na", 11, 1, 3, 22.9897692, 0.971, 370.87, 1156, 1.228, 0.93, 23600));
	public static final Element magnesium = registry.register(new Element("Magnesium", "Mg", 12, 2, 3, 24.3059, 1.738, 923, 1363, 1.023, 1.31, 23300));
	public static final Element aluminium = registry.register(new Element("Aluminium", "Al", 13, 13, 3, 26.9815386, 2.698, 933.47, 2792, 0.897, 1.61, 82300));
	public static final Element silicon = registry.register(new Element("Silicon", "Si", 14, 14, 3, 28.0854, 2.3296, 1687, 3538, 0.705, 1.9, 282000));
	public static final Element phosphorus = registry.register(new Element("Phosphorus", "P", 15, 15, 3, 30.973762, 1.82, 317.3, 550, 0.769, 2.19, 1050));
	public static final Element sulfur = registry.register(new Element("Sulfur", "S", 16, 16, 3, 32.062, 2.067, 388.36, 717.87, 0.71, 2.58, 350));
	public static final Element chlorine = registry.register(new Element("Chlorine", "Cl", 17, 17, 3, 35.452, 0.003214, 171.6, 239.11, 0.479, 3.16, 145));
	public static final Element argon = registry.register(new Element("Argon", "Ar", 18, 18, 3, 39.948, 0.0017837, 83.8, 87.3, 0.52, 0, 3.5));
	public static final Element potassium = registry.register(new Element("Potassium", "K", 19, 1, 4, 39.0983, 0.862, 336.53, 1032, 0.757, 0.82, 20900));
	public static final Element calcium = registry.register(new Element("Calcium", "Ca", 20, 2, 4, 40.078, 1.54, 1115, 1757, 0.647, 1, 41500));
	public static final Element scandium = registry.register(new Element("Scandium", "Sc", 21, 3, 4, 44.95591, 2.989, 1814, 3109, 0.568, 1.36, 22));
	public static final Element titanium = registry.register(new Element("Titanium", "Ti", 22, 4, 4, 47.867, 4.54, 1941, 3560, 0.523, 1.54, 5650));
	public static final Element vanadium = registry.register(new Element("Vanadium", "V", 23, 5, 4, 50.9415, 6.11, 2183, 3680, 0.489, 1.63, 120));
	public static final Element chromium = registry.register(new Element("Chromium", "Cr", 24, 6, 4, 51.9961, 7.15, 2180, 2944, 0.449, 1.66, 102));
	public static final Element manganese = registry.register(new Element("Manganese", "Mn", 25, 7, 4, 54.938045, 7.44, 1519, 2334, 0.479, 1.55, 950));
	public static final Element iron = registry.register(new Element("Iron", "Fe", 26, 8, 4, 55.845, 7.874, 1811, 3134, 0.449, 1.83, 56300));
	public static final Element cobalt = registry.register(new Element("Cobalt", "Co", 27, 9, 4, 58.93319, 8.86, 1768, 3200, 0.421, 1.88, 25));
	public static final Element nickel = registry.register(new Element("Nickel", "Ni", 28, 10, 4, 58.6934, 8.912, 1728, 3186, 0.444, 1.91, 84));
	public static final Element copper = registry.register(new Element("Copper", "Cu", 29, 11, 4, 63.546, 8.96, 1357.77, 2835, 0.385, 1.9, 60));
	public static final Element zinc = registry.register(new Element("Zinc", "Zn", 30, 12, 4, 65.38, 7.134, 692.88, 1180, 0.388, 1.65, 70));
	public static final Element gallium = registry.register(new Element("Gallium", "Ga", 31, 13, 4, 69.723, 5.907, 302.9146, 2477, 0.371, 1.81, 19));
	public static final Element germanium = registry.register(new Element("Germanium", "Ge", 32, 14, 4, 72.63, 5.323, 1211.4, 3106, 0.32, 2.01, 1.5));
	public static final Element arsenic = registry.register(new Element("Arsenic", "As", 33, 15, 4, 74.9216, 5.776, 1090, 887, 0.329, 2.18, 1.8));
	public static final Element selenium = registry.register(new Element("Selenium", "Se", 34, 16, 4, 78.96, 4.809, 453, 958, 0.321, 2.55, 0.05));
	public static final Element bromine = registry.register(new Element("Bromine", "Br", 35, 17, 4, 79.9049, 3.122, 265.8, 332, 0.474, 2.96, 2.4));
	public static final Element krypton = registry.register(new Element("Krypton", "Kr", 36, 18, 4, 83.798, 0.003733, 115.79, 119.93, 0.248, 3, 0.001));
	public static final Element rubidium = registry.register(new Element("Rubidium", "Rb", 37, 1, 5, 85, 1.532, 312.46, 961, 0.363, 0.82, 90));
	public static final Element strontium = registry.register(new Element("Strontium", "Sr", 38, 2, 5, 87.62, 2.64, 1050, 1655, 0.301, 0.95, 370));
	public static final Element yttrium = registry.register(new Element("Yttrium", "Y", 39, 3, 5, 88.90585, 4.469, 1799, 3609, 0.298, 1.22, 33));
	public static final Element zirconium = registry.register(new Element("Zirconium", "Zr", 40, 4, 5, 91.224, 6.506, 2128, 4682, 0.278, 1.33, 165));
	public static final Element niobium = registry.register(new Element("Niobium", "Nb", 41, 5, 5, 92.90638, 8.57, 2750, 5017, 0.265, 1.6, 20));
	public static final Element molybdenum = registry.register(new Element("Molybdenum", "Mo", 42, 6, 5, 95.96, 10.22, 2896, 4912, 0.251, 2.16, 1.2));
	public static final Element technetium = registry.register(new Element("Technetium", "Tc", 43, 7, 5, 981, 11.5, 2430, 4538, 0, 1.9, 0.001));
	public static final Element ruthenium = registry.register(new Element("Ruthenium", "Ru", 44, 8, 5, 101.072, 12.37, 2607, 4423, 0.238, 2.2, 0.001));
	public static final Element rhodium = registry.register(new Element("Rhodium", "Rh", 45, 9, 5, 102.905502, 12.41, 2237, 3968, 0.243, 2.28, 0.001));
	public static final Element palladium = registry.register(new Element("Palladium", "Pd", 46, 10, 5, 106.421, 12.02, 1828.05, 3236, 0.244, 2.2, 0.015));
	public static final Element silver = registry.register(new Element("Silver", "Ag", 47, 11, 5, 107.86822, 10.501, 1234.93, 2435, 0.235, 1.93, 0.075));
	public static final Element cadmium = registry.register(new Element("Cadmium", "Cd", 48, 12, 5, 112.4118, 8.69, 594.22, 1040, 0.232, 1.69, 0.159));
	public static final Element indium = registry.register(new Element("Indium", "In", 49, 13, 5, 114.8181, 7.31, 429.75, 2345, 0.233, 1.78, 0.25));
	public static final Element tin = registry.register(new Element("Tin", "Sn", 50, 14, 5, 118.7107, 7.287, 505.08, 2875, 0.228, 1.96, 2.3));
	public static final Element antimony = registry.register(new Element("Antimony", "Sb", 51, 15, 5, 121.7601, 6.685, 903.78, 1860, 0.207, 2.05, 0.2));
	public static final Element tellurium = registry.register(new Element("Tellurium", "Te", 52, 16, 5, 127.603, 6.232, 722.66, 1261, 0.202, 2.1, 0.001));
	public static final Element iodine = registry.register(new Element("Iodine", "I", 53, 17, 5, 126.904473, 4.93, 386.85, 457.4, 0.214, 2.66, 0.45));
	public static final Element xenon = registry.register(new Element("Xenon", "Xe", 54, 18, 5, 131.2936, 0.005887, 161.4, 165.03, 0.158, 2.6, 0.001));
	public static final Element caesium = registry.register(new Element("Caesium", "Cs", 55, 1, 6, 132.90545192, 1.873, 301.59, 944, 0.242, 0.79, 3));
	public static final Element barium = registry.register(new Element("Barium", "Ba", 56, 2, 6, 137.3277, 3.594, 1000, 2170, 0.204, 0.89, 425));
	public static final Element lanthanum = registry.register(new Element("Lanthanum", "La", 57, 0, 6, 138.905477, 6.145, 1193, 3737, 0.195, 1.1, 39));
	public static final Element cerium = registry.register(new Element("Cerium", "Ce", 58, 0, 6, 140.1161, 6.77, 1068, 3716, 0.192, 1.12, 66.5));
	public static final Element praseodymium = registry.register(new Element("Praseodymium", "Pr", 59, 0, 6, 140.907652, 6.773, 1208, 3793, 0.193, 1.13, 9.2));
	public static final Element neodymium = registry.register(new Element("Neodymium", "Nd", 60, 0, 6, 144.2423, 7.007, 1297, 3347, 0.19, 1.14, 41.5));
	public static final Element promethium = registry.register(new Element("Promethium", "Pm", 61, 0, 6, 145, 7.26, 1315, 3273, 0, 1.13, 0.001));
	public static final Element samarium = registry.register(new Element("Samarium", "Sm", 62, 0, 6, 150.362, 7.52, 1345, 2067, 0.197, 1.17, 7.05));
	public static final Element europium = registry.register(new Element("Europium", "Eu", 63, 0, 6, 151.9641, 5.243, 1099, 1802, 0.182, 1.2, 2));
	public static final Element gadolinium = registry.register(new Element("Gadolinium", "Gd", 64, 0, 6, 157.253, 7.895, 1585, 3546, 0.236, 1.2, 6.2));
	public static final Element terbium = registry.register(new Element("Terbium", "Tb", 65, 0, 6, 158.925352, 8.229, 1629, 3503, 0.182, 1.2, 1.2));
	public static final Element dysprosium = registry.register(new Element("Dysprosium", "Dy", 66, 0, 6, 162.5001, 8.55, 1680, 2840, 0.17, 1.22, 5.2));
	public static final Element holmium = registry.register(new Element("Holmium", "Ho", 67, 0, 6, 164.930322, 8.795, 1734, 2993, 0.165, 1.23, 1.3));
	public static final Element erbium = registry.register(new Element("Erbium", "Er", 68, 0, 6, 167.2593, 9.066, 1802, 3141, 0.168, 1.24, 3.5));
	public static final Element thulium = registry.register(new Element("Thulium", "Tm", 69, 0, 6, 168.934212, 9.321, 1818, 2223, 0.16, 1.25, 0.52));
	public static final Element ytterbium = registry.register(new Element("Ytterbium", "Yb", 70, 0, 6, 173.0545, 6.965, 1097, 1469, 0.155, 1.1, 3.2));
	public static final Element lutetium = registry.register(new Element("Lutetium", "Lu", 71, 3, 6, 174.96681, 9.84, 1925, 3675, 0.154, 1.27, 0.8));
	public static final Element hafnium = registry.register(new Element("Hafnium", "Hf", 72, 4, 6, 178.492, 13.31, 2506, 4876, 0.144, 1.3, 3));
	public static final Element tantalum = registry.register(new Element("Tantalum", "Ta", 73, 5, 6, 180.947882, 16.654, 3290, 5731, 0.14, 1.5, 2));
	public static final Element tungsten = registry.register(new Element("Tungsten", "W", 74, 6, 6, 183.841, 19.25, 3695, 5828, 0.132, 2.36, 1.3));
	public static final Element rhenium = registry.register(new Element("Rhenium", "Re", 75, 7, 6, 186.2071, 21.02, 3459, 5869, 0.137, 1.9, 0.001));
	public static final Element osmium = registry.register(new Element("Osmium", "Os", 76, 8, 6, 190.233, 22.61, 3306, 5285, 0.13, 2.2, 0.002));
	public static final Element iridium = registry.register(new Element("Iridium", "Ir", 77, 9, 6, 192.2173, 22.56, 2719, 4701, 0.131, 2.2, 0.001));
	public static final Element platinum = registry.register(new Element("Platinum", "Pt", 78, 10, 6, 195.0849, 21.46, 2041.4, 4098, 0.133, 2.28, 0.005));
	public static final Element gold = registry.register(new Element("Gold", "Au", 79, 11, 6, 196.9665694, 19.282, 1337.33, 3129, 0.129, 2.54, 0.004));
	public static final Element mercury = registry.register(new Element("Mercury", "Hg", 80, 12, 6, 200.5923, 13.5336, 234.43, 629.88, 0.14, 2, 0.085));
	public static final Element thallium = registry.register(new Element("Thallium", "Tl", 81, 13, 6, 204.389, 11.85, 577, 1746, 0.129, 1.62, 0.85));
	public static final Element lead = registry.register(new Element("Lead", "Pb", 82, 14, 6, 207.21, 11.342, 600.61, 2022, 0.129, 1.87, 14));
	public static final Element bismuth = registry.register(new Element("Bismuth", "Bi", 83, 15, 6, 208.980401, 9.807, 544.7, 1837, 0.122, 2.02, 0.009));
	public static final Element polonium = registry.register(new Element("Polonium", "Po", 84, 16, 6, 209, 9.32, 527, 1235, 0, 2, 0.001));
	public static final Element astatine = registry.register(new Element("Astatine", "At", 85, 17, 6, 210, 7, 575, 610, 0, 2.2, 0.001));
	public static final Element radon = registry.register(new Element("Radon", "Rn", 86, 18, 6, 222, 0.00973, 202, 211.3, 0.094, 2.2, 0.001));
	public static final Element francium = registry.register(new Element("Francium", "Fr", 87, 1, 7, 223, 1.87, 300, 950, 0, 0.7, 0.001));
	public static final Element radium = registry.register(new Element("Radium", "Ra", 88, 2, 7, 226, 5.5, 973, 2010, 0.094, 0.9, 0.001));
	public static final Element actinium = registry.register(new Element("Actinium", "Ac", 89, 0, 7, 227, 10.07, 1323, 3471, 0.12, 1.1, 0.001));
	public static final Element thorium = registry.register(new Element("Thorium", "Th", 90, 0, 7, 232.038062, 11.72, 2115, 5061, 0.113, 1.3, 9.6));
	public static final Element protactinium = registry.register(new Element("Protactinium", "Pa", 91, 0, 7, 231.035882, 15.37, 1841, 4300, 0, 1.5, 0.001));
	public static final Element uranium = registry.register(new Element("Uranium", "U", 92, 0, 7, 238.028913, 18.95, 1405.3, 4404, 0.116, 1.38, 2.7));
	public static final Element neptunium = registry.register(new Element("Neptunium", "Np", 93, 0, 7, 237, 20.45, 917, 4273, 0, 1.36, 0.001));
	public static final Element plutonium = registry.register(new Element("Plutonium", "Pu", 94, 0, 7, 244, 19.84, 912.5, 3501, 0, 1.28, 0.001));
	public static final Element americium = registry.register(new Element("Americium", "Am", 95, 0, 7, 243, 13.69, 1449, 2880, 0, 1.13, 0.001));
	public static final Element curium = registry.register(new Element("Curium", "Cm", 96, 0, 7, 247, 13.51, 1613, 3383, 0, 1.28, 0.001));
	public static final Element berkelium = registry.register(new Element("Berkelium", "Bk", 97, 0, 7, 247, 14.79, 1259, 2900, 0, 1.3, 0.001));
	public static final Element californium = registry.register(new Element("Californium", "Cf", 98, 0, 7, 251, 15.1, 1173, 1743, 0, 1.3, 0.001));
	public static final Element einsteinium = registry.register(new Element("Einsteinium", "Es", 99, 0, 7, 252, 8.84, 1133, 1269, 0, 1.3, 0));
	public static final Element fermium = registry.register(new Element("Fermium", "Fm", 100, 0, 7, 257, 0, 1125, 0, 0, 1.3, 0));
	public static final Element mendelevium = registry.register(new Element("Mendelevium", "Md", 101, 0, 7, 258, 0, 1100, 0, 0, 1.3, 0));
	public static final Element nobelium = registry.register(new Element("Nobelium", "No", 102, 0, 7, 259, 0, 1100, 0, 0, 1.3, 0));
	public static final Element lawrencium = registry.register(new Element("Lawrencium", "Lr", 103, 3, 7, 262, 0, 1900, 0, 0, 1.3, 0));
	public static final Element rutherfordium = registry.register(new Element("Rutherfordium", "Rf", 104, 4, 7, 267, 23.2, 2400, 5800, 0, 0, 0));
	public static final Element dubnium = registry.register(new Element("Dubnium", "Db", 105, 5, 7, 268, 29.3, 0, 0, 0, 0, 0));
	public static final Element seaborgium = registry.register(new Element("Seaborgium", "Sg", 106, 6, 7, 269, 35, 0, 0, 0, 0, 0));
	public static final Element bohrium = registry.register(new Element("Bohrium", "Bh", 107, 7, 7, 270, 37.1, 0, 0, 0, 0, 0));
	public static final Element hassium = registry.register(new Element("Hassium", "Hs", 108, 8, 7, 269, 40.7, 0, 0, 0, 0, 0));
	public static final Element meitnerium = registry.register(new Element("Meitnerium", "Mt", 109, 9, 7, 278, 37.4, 0, 0, 0, 0, 0));
	public static final Element darmstadtium = registry.register(new Element("Darmstadtium", "Ds", 110, 10, 7, 281, 34.8, 0, 0, 0, 0, 0));
	public static final Element roentgenium = registry.register(new Element("Roentgenium", "Rg", 111, 11, 7, 281, 28.7, 0, 0, 0, 0, 0));
	public static final Element copernicium = registry.register(new Element("Copernicium", "Cn", 112, 12, 7, 285, 23.7, 0, 357, 0, 0, 0));
	public static final Element ununtrium = registry.register(new Element("Ununtrium", "Uut", 113, 13, 7, 286, 16, 700, 1400, 0, 0, 0));
	public static final Element flerovium = registry.register(new Element("Flerovium", "Fl", 114, 14, 7, 289, 14, 340, 420, 0, 0, 0));
	public static final Element ununpentium = registry.register(new Element("Ununpentium", "Uup", 115, 15, 7, 288, 13.5, 700, 1400, 0, 0, 0));
	public static final Element livermorium = registry.register(new Element("Livermorium", "Lv", 116, 16, 7, 293, 12.9, 708.5, 1085, 0, 0, 0));
	public static final Element ununseptium = registry.register(new Element("Ununseptium", "Uus", 117, 17, 7, 294, 7.2, 673, 823, 0, 0, 0));
	public static final Element ununoctium = registry.register(new Element("Ununoctium", "Uuo", 118, 18, 7, 294, 5, 258, 263, 0, 0, 0));

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

	@Override
	public String export(final String delimiter) {
		return String.format("%2$s%1$s%3$s%1$s%4$s%1$s%5$s%1$s%6$s%1$s%7$s%1$s%8$s%1$s%9$s%1$s%10$s%1$s%11$s%1$s%12$s%1$s%13$s",
				delimiter, super.export(delimiter), symbol, atomicNumber, group, period, weight, density, melt, boil, heat, electronegativity, abundance);
	}

	public static String generate(final String[] entity) {
		final String[] params = new String[] {
				"\"" + entity[0] + "\"",
				"\"" + entity[1] + "\"",
				entity[2], entity[3], entity[4], entity[5], entity[6], entity[7], entity[8], entity[9], entity[10], entity[11]
		};
		return Entity.generate(Element.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}