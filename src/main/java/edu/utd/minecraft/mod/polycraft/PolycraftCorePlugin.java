package edu.utd.minecraft.mod.polycraft;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class PolycraftCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
			edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.Transformer.class.getCanonicalName(),
			edu.utd.minecraft.mod.polycraft.transformer.fogclarity.Transformer.class.getCanonicalName(),
			edu.utd.minecraft.mod.polycraft.transformer.phaseshifter.Transformer.class.getCanonicalName()
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
