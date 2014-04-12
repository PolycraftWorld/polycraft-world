package edu.utd.minecraft.mod.polycraft.config;

public class Polymer extends Compound {

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

	public final String shortName;
	public final String pelletName;
	public final String fiberName;
	public final boolean degradable;
	public final Category category;
	public final ResinCode resinCode;
	public final int craftingPelletsPerBlock;

	public Polymer(final String name, final String shortName, final String pelletName, final String fiberName,
			final boolean degradable, final Category category, final ResinCode resinCode, final int craftingPelletsPerBlock) {
		super(name, false);
		this.shortName = shortName;
		this.pelletName = pelletName;
		this.fiberName = fiberName;
		this.degradable = degradable;
		this.category = category;
		this.resinCode = resinCode;
		this.craftingPelletsPerBlock = craftingPelletsPerBlock;
	}
}