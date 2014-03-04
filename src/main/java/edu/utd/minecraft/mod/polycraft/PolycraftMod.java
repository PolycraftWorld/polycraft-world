package edu.utd.minecraft.mod.polycraft;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod
{
    public static final String MODID = "polycraft";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//Dirt to diamonds!
        ItemStack dirtStack = new ItemStack(Blocks.dirt);
        ItemStack diamondsStack = new ItemStack(Items.diamond, 64);
        GameRegistry.addShapelessRecipe(diamondsStack, dirtStack);
    }
}
