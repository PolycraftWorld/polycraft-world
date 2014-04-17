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
	public final String itemNamePellet;
	public final String itemNameFiber;
	public final String itemNameSlab;
	public final String itemNameDoubleSlab;
	public final String blockNameSlab;
	public final String blockNameDoubleSlab;
	public final boolean degradable;
	public final Category category;
	public final ResinCode resinCode;
	public final int pelletsPerBlock;
	public final boolean slabable;
	public final int slabBounceHeight;

	public Polymer(final String name, final String shortName, final String pelletName, final String fiberName,
			final boolean degradable, final Category category, final ResinCode resinCode, final int pelletsPerBlock,
			final boolean slabable, final int slabBounceHeight) {
		super(name, false);
		this.shortName = shortName;
		this.itemNamePellet = pelletName;
		this.itemNameFiber = fiberName;
		this.blockNameSlab = name + "_slab";
		this.blockNameDoubleSlab = "double_" + blockNameSlab;
		this.itemNameSlab = blockNameSlab + "_item";
		this.itemNameDoubleSlab = blockNameDoubleSlab + "_item";
		this.degradable = degradable;
		this.category = category;
		this.resinCode = resinCode;
		this.pelletsPerBlock = pelletsPerBlock;
		this.slabable = slabable;
		this.slabBounceHeight = slabBounceHeight;
	}
}