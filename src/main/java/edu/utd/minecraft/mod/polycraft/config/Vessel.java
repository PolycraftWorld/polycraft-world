package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Vessel {

	public enum Type {
		Bag(Matter.State.Solid),
		Vial(Matter.State.Liquid),
		Flask(Matter.State.Gas),
		Sack(Matter.State.Solid, Bag),
		Beaker(Matter.State.Liquid, Vial),
		Cartridge(Matter.State.Gas, Flask),
		PowderKeg(Matter.State.Solid, Sack),
		Drum(Matter.State.Liquid, Beaker),
		Canister(Matter.State.Gas, Cartridge);

		public final Matter.State matterState;
		public final Type smallerType;
		public final int quantityOfSmallerType;
		public Type largerType;

		Type(final Matter.State matterState) {
			this(matterState, null, 0);
		}

		Type(final Matter.State matterState, final Type smallerType) {
			this(matterState, smallerType, PolycraftMod.recipeSmallerVesselsPerLargerVessel);
		}

		Type(final Matter.State matterState, final Type smallerType, final int quantityOfSmallerType) {
			this.matterState = matterState;
			this.smallerType = smallerType;
			if (smallerType != null)
				this.smallerType.largerType = this;
			this.quantityOfSmallerType = quantityOfSmallerType;
		}

		public int getQuantityOfSmallestType() {
			if (smallerType == null)
				return 1;
			return quantityOfSmallerType * smallerType.getQuantityOfSmallestType();
		}

		public static Type readFromConfig(final String config) {
			return Type.valueOf(config.replaceAll(" ", ""));
		}
	}
}