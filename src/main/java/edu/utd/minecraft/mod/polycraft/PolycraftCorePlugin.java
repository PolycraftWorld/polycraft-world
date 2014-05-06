package edu.utd.minecraft.mod.polycraft;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class PolycraftCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
				"edu.utd.minecraft.mod.polycraft.dynamiclights.Transformer",
				"edu.utd.minecraft.mod.polycraft.fogclarity.Transformer",
				"edu.utd.minecraft.mod.polycraft.phaseshifter.Transformer"
		};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
