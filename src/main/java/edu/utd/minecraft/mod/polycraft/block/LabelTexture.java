package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class LabelTexture {

	private IIcon mainTexture;
	private IIcon labelTexture;
	private final String textureName;
	private final int labelSide;

	public LabelTexture(final String textureName) {
		this(textureName, -1);
	}

	public LabelTexture(final String textureName, final int labelSide) {
		this.textureName = textureName;
		this.labelSide = labelSide;
	}

	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		if (labelSide == -1 || p_149691_1_ == labelSide)
			return labelTexture;
		return mainTexture;
	}

	public void registerBlockIcons(IIconRegister p_149651_1_) {
		mainTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName));
		labelTexture = p_149651_1_.registerIcon(PolycraftMod.getTextureName(textureName + "_label"));
	}
}
