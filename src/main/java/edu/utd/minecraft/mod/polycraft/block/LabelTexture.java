package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class LabelTexture {

	// private IIcon mainTexture;
	// private IIcon labelTexture;

	private IIcon topTexture;
	private IIcon bottomTexture; // flip
	private IIcon leftTexture;
	private IIcon rightTexture; // flip
	private IIcon frontTexture;
	private IIcon backTexture; // flip

	private final String textureName;
	private final int textureMode;
	private final int color;

	public LabelTexture(final String textureName) {
		this(textureName, -1);
	}

	public LabelTexture(final String textureName, final int textureMode) {
		this(textureName, textureMode, -1);
	}

	public LabelTexture(final String textureName, final int textureMode, final int color) {
		this.textureName = textureName;
		this.textureMode = textureMode;
		this.color = color;
	}

	public IIcon getIcon(int side, int p_149691_2_) {

		if (this.textureMode == 6) {

			switch (side) {
			case 0:
				// image needs to be flipped horizontally
				return bottomTexture;
			case 1:
				return topTexture;
			case 2:
				// image needs to be flipped horizontally
				return backTexture;
			case 3:
				return frontTexture;
			case 4:
				return leftTexture;
			case 5:
				// image needs to be flipped horizontally
				return rightTexture;
			}
			return topTexture;
		}
		// this is the case where just the bottom tile is different (for plastics)
		else if (this.textureMode == 5) {

			switch (side) {
			case 0:
				return bottomTexture;
			}
			return topTexture;
		}
		// this is the case where nothing is inverted with 2 textures
		else if (this.textureMode == 2) {
			switch (side) {
			case 0:
			case 2:
			case 5:
				return bottomTexture;
			}
			return topTexture;

		}

		else
			return topTexture;

		// if (labelSide == -1 || p_149691_1_ == labelSide)
		// return labelTexture;
		// return mainTexture;

	}

	public void registerBlockIcons(IIconRegister p_149651_1_) {
		if (this.textureMode == 6) {
			topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName));
			bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_bottom"));
			leftTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_left"));
			rightTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_right"));
			frontTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_front"));
			backTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_back"));
		} else if (this.textureMode == 5) {
			// removes the numbers from the textureName so we only need one top color PNG for plastics in general
			topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName.replaceAll("_[0-9][0-9]", "").replaceAll("_[0-9]", "") + "_top"));
			bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_bottom"));

		} else if (this.textureMode == 2) {
			topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName));
			bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_bottom"));

		} else {
			topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName.replaceAll("_[0-9][0-9]", "").replaceAll("_[0-9]", "") + "_top"));

		}

		// **These are test cases that were used with dummy icons to get #'s and flips correct**

		// topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("top"));
		// bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("bottom"));
		// leftTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("left"));
		// rightTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("right"));
		// frontTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("front"));
		// backTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("back"));

	}

	// public void registerBlockIcons(IIconRegister p_149651_1_, String color) {
	// if (this.textureMode == 6)
	// {
	// topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_" + color));
	// bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_bottom"+ "_" + color));
	// leftTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_left"+ "_" + color));
	// rightTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_right"+ "_" + color));
	// frontTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_front"+ "_" + color));
	// backTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_back"+ "_" + color));
	// }
	// else if (this.textureMode == 5 )
	// {
	// //all plastics looks the same on the top block
	// topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName("plastic_top"+ "_" + color));
	// bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName+ "_bottom"+ "_" + color));
	//
	//
	// }
	// else if (this.textureMode == 2)
	// {
	// topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName+ "_" + color));
	// bottomTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName+ "_bottom"+ "_" + color));
	//
	// }
	// else
	// {
	// topTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName+ "_" + color));
	//
	// }

	// }
}
