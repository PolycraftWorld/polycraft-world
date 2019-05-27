package edu.utd.minecraft.mod.polycraft.item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public interface PolycraftItem {
	// Gets the item's category.  This value is used to populate the wiki.
	public ItemCategory getCategory();

}
