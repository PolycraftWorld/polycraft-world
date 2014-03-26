package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class LabelTexture {

	private final IIcon[] icons = new IIcon[6];
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

	public IIcon getIcon(int side, int p_149691_2_) {
		return icons[side];
	}

	public void registerBlockIcons(IIconRegister p_149651_1_) {
		icons[0] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(bottomTextureName));
		icons[1] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(topTextureName));
		icons[2] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(mainFlippedTextureName)); // back
		icons[3] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(mainTextureName)); // front
		icons[4] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(mainTextureName)); // left
		icons[5] = p_149651_1_.registerIcon(PolycraftMod.getTextureName(mainFlippedTextureName)); // right
	}
}
