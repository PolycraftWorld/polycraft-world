package edu.utd.minecraft.mod.polycraft.config;

public class Polymer extends Entity {

	public enum Category {
		None,
		Cellulosic,
		Fluoropolymer,
		Polyacrylate,
		Polyamide,
		Polyaryletherketone,
		Polycarbonate,
		Polyester,
		Polyether,
		Polyimide,
		Polyurethane,
		PolymerComposite,
		Polyol,
		Polyolefin,
		Polyoxazole,
		Rubber,
	}

	public enum ResinCode {
		NONE(0, ""),
		PET(1, "PolyEthylene Terephthalate"),
		HDPE(2, "High-Density PolyEthylene"),
		PVC(3, "PolyVinyl Chloride"),
		LDPE(4, "Low-Density PolyEthylene"),
		PP(5, "PolyPropylene"),
		PS(6, "PolyStyrene"),
		O(7, "Other");

		public final int recyclingNumber;
		public final String name;

		private ResinCode(final int recyclingNumber, final String name) {
			this.recyclingNumber = recyclingNumber;
			this.name = name;
		}
	}

	public static final EntityRegistry<Polymer> registry = new EntityRegistry<Polymer>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Polymer.class.getSimpleName().toLowerCase(), extension, delimeter)) {
			int resinCodeValue = 0;
			if (line[3].length() > 0) {
				resinCodeValue = Integer.parseInt(line[3]);
				if (resinCodeValue > 7)
					resinCodeValue = 0;
			}
			registry.register(new Polymer(
					line[0], //name
					line[1], //shortName
					Polymer.ResinCode.values()[resinCodeValue], //resinCode
					Boolean.parseBoolean(line[4]), //degradable
					Polymer.Category.valueOf(line[6].replaceAll(" ", "").trim()) //category
			));
		}
	}

	public final String shortName;
	public final boolean degradable;
	public final Category category;
	public final ResinCode resinCode;

	public Polymer(final String name, final String shortName, final ResinCode resinCode, final boolean degradable, final Category category) {
		super(name);
		this.shortName = shortName;
		this.resinCode = resinCode;
		this.degradable = degradable;
		this.category = category;
	}
}