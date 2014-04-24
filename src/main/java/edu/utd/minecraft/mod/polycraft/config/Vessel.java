package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Vessel extends SourcedConfig<Compound> {

	public static final ConfigRegistry<Vessel> registry = new ConfigRegistry<Vessel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Vessel.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Vessel(
						line[0], //gameID
						line[1], //name
						(Compound) Config.find(line[2], line[3]), //source
						Type.valueOf(line[4]) //type
				));
	}

	public static Vessel findVessel(final Compound compound, final Type type) {
		for (final Vessel vessel : registry.values())
			if (vessel.source == compound && vessel.type == type)
				return vessel;
		return null;
	}

	public enum Type {
		Vial(Matter.State.Solid),
		Beaker(Matter.State.Liquid),
		Flask(Matter.State.Gas),
		Jar(Matter.State.Solid, Vial),
		Pale(Matter.State.Liquid, Beaker),
		Cartridge(Matter.State.Gas, Flask),
		Gaylord(Matter.State.Solid, Jar),
		Drum(Matter.State.Liquid, Pale),
		Canister(Matter.State.Gas, Drum);

		public final Matter.State matterState;
		public final Type smallerType;

		Type(final Matter.State matterState) {
			this(matterState, null);
		}

		Type(final Matter.State matterState, final Type smallerType) {
			this.matterState = matterState;
			this.smallerType = smallerType;
		}
	};

	public final Type type;

	public Vessel(final String gameID, final String name, final Compound source, final Type type) {
		super(gameID, name, source);
		this.type = type;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}