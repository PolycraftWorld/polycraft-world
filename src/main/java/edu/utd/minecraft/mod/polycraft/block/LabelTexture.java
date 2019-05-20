package edu.utd.minecraft.mod.polycraft.block;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class LabelTexture {

	public static final int SIDE_BOTTOM = 0;
	public static final int SIDE_TOP = 1;
	public static final int SIDE_BACK = 2;
	public static final int SIDE_FRONT = 3;
	public static final int SIDE_LEFT = 4;
	public static final int SIDE_RIGHT = 5;

//	private final IIcon[] icons = new IIcon[6];
	private final String mainTextureName;
	private final String mainFlippedTextureName;
	private final String bottomTextureName;
	private final String topTextureName;

	public LabelTexture(final String mainTextureName) {
		this(mainTextureName, null);
	}

	public LabelTexture(final String mainTextureName, final String mainFlippedTextureName) {
		this(mainTextureName, mainFlippedTextureName, mainFlippedTextureName);
	}

	public LabelTexture(final String mainTextureName, final String mainFlippedTextureName, final String bottomTextureName) {
		this(mainTextureName, mainFlippedTextureName, bottomTextureName, null);
	}

	public LabelTexture(final String mainTextureName, final String mainFlippedTextureName, final String bottomTextureName, final String topTextureName) {
		this.mainTextureName = mainTextureName;
		this.mainFlippedTextureName = mainFlippedTextureName == null ? mainTextureName : mainFlippedTextureName;
		this.bottomTextureName = bottomTextureName == null ? mainTextureName : bottomTextureName;
		this.topTextureName = topTextureName == null ? mainTextureName : topTextureName;
	}

//	public IIcon getIcon(int side, int metadata) {
//		switch (metadata)
//		{
//		case SIDE_BOTTOM:
//			return icons[side];
//		default:
//			return icons[side];
//		}
//	}

//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		icons[SIDE_BOTTOM] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(bottomTextureName));
//		icons[SIDE_TOP] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(topTextureName));
//		icons[SIDE_BACK] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(mainFlippedTextureName));
//		icons[SIDE_FRONT] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(mainTextureName));
//		icons[SIDE_LEFT] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(mainTextureName));
//		icons[SIDE_RIGHT] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(mainFlippedTextureName));
//	}
}
