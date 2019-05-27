package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Flashcard;

public class ItemFlashcard extends Item implements PolycraftItem {
	public final Flashcard flashcard;

	public ItemFlashcard(final Flashcard flashcard) {
		Preconditions.checkNotNull(flashcard);
		this.setCreativeTab(CreativeTabs.tabMisc);
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(flashcard.name)));
		this.flashcard = flashcard;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.LEARNING_FLASHCARD;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);
	}
}
