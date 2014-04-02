package edu.utd.minecraft.mod.polycraft.dynamiclights;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class DLFMLCorePlugin implements IFMLLoadingPlugin
{

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "edu.utd.minecraft.mod.polycraft.dynamiclights.DLTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

}
