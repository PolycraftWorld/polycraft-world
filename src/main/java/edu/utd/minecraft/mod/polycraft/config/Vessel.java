package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Vessel {

	public enum Type {
		Vial(Matter.State.Solid),
		Beaker(Matter.State.Liquid),
		Flask(Matter.State.Gas),
		Jar(Matter.State.Solid, Vial),
		Pail(Matter.State.Liquid, Beaker),
		Cartridge(Matter.State.Gas, Flask),
		PowderKeg(Matter.State.Solid, Jar),
		Drum(Matter.State.Liquid, Pail),
		Canister(Matter.State.Gas, Drum);

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
	}
}