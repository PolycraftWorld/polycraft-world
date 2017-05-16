package edu.utd.minecraft.mod.polycraft.config;

import java.util.Collection;
import java.util.LinkedList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Polymer extends Config {

	public enum Category {
		none,
		fluoropolymer,
		inorganicpolymer,
		inorganicorganicpolymer,
		naturalrubber,
		polyacrylate,
		polyaldehyde,
		polyalkenesulfide,
		polyamide,
		polycarbonate,
		polyepoxide,
		polyester,
		polyether,
		polyimide,
		polyol,
		polyolefin,
		polyphenylethers,
		polyphenol,
		polysaccharide,
		polyurethane,
		polyvinyl,
		polyvinylester,
		silicone,
		syntheticrubber,
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

	public static final ConfigRegistry<Polymer> registry = new ConfigRegistry<Polymer>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Polymer.class.getSimpleName().toLowerCase())) {
			if (line.length > 0) {
				int resinCodeValue = 0;
				if (line[5].length() > 0) {
					resinCodeValue = Integer.parseInt(line[5]);
					if (resinCodeValue > 7)
						resinCodeValue = 0;
				}

				Collection<Category> categories = null;
				for (int i = 8; i <= 10 && line.length > i; i++) {

					final String category = line[i].trim();
					if (!category.isEmpty()) {
						if (categories == null)
							categories = new LinkedList<Category>();
						categories.add(Polymer.Category.valueOf(line[i].replaceAll("[^A-Za-z0-9]", "").toLowerCase()));
					}
				}

				registry.register(new Polymer(
						PolycraftMod.getVersionNumeric(line[0]), //version
						line[1], // name
						line[2], // shortName
						Boolean.parseBoolean(line[3]), //thermoplastic
						Polymer.ResinCode.values()[resinCodeValue], // resinCode
						Boolean.parseBoolean(line[6]), // degradable
						categories // categories
				));
			}
		}
	}

	public final String shortName;
	public final boolean degradable;
	public final Collection<Category> categories;
	public final ResinCode resinCode;
	public final boolean thermoplastic;

	public Polymer(final int[] version, final String name, final String shortName, final boolean thermoplastic, final ResinCode resinCode, final boolean degradable, final Collection<Category> categories) {
		super(version, name);
		this.shortName = shortName;
		this.resinCode = resinCode;
		this.thermoplastic = thermoplastic;
		this.degradable = degradable;
		this.categories = categories;
	}
}