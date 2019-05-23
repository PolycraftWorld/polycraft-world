package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Exam;
import edu.utd.minecraft.mod.polycraft.config.Flashcard;

public class ItemExam extends Item implements PolycraftItem {
	public final Exam exam;
	public final ItemStack[] answers;
	public final ItemStack[] testBank;
	private JsonObject specificEnchantment;

	public ItemExam(final Exam exam) {
		Preconditions.checkNotNull(exam);
		this.setCreativeTab(CreativeTabs.tabMisc);
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(exam.name)));
		this.exam = exam;
		this.answers = new ItemStack[8];
		this.testBank = new ItemStack[12];
		this.addContentQ1ToExam();
	}

	private void addContentQ1ToExam() {
		this.testBank[0] = Flashcard.registry.get("flashcard_1").getItemStack();
		this.testBank[1] = Flashcard.registry.get("flashcard_2").getItemStack();
		this.testBank[2] = Flashcard.registry.get("flashcard_3").getItemStack();
		this.testBank[3] = Flashcard.registry.get("flashcard_4").getItemStack();
		this.testBank[4] = Flashcard.registry.get("flashcard_5").getItemStack();
		this.testBank[5] = Flashcard.registry.get("flashcard_a").getItemStack();
		this.testBank[6] = Flashcard.registry.get("flashcard_b").getItemStack();
		this.testBank[7] = Flashcard.registry.get("flashcard_cis").getItemStack();
		this.testBank[8] = Flashcard.registry.get("flashcard_trans").getItemStack();
		this.testBank[9] = Flashcard.registry.get("flashcard_sp").getItemStack();
		this.testBank[10] = Flashcard.registry.get("flashcard_sp2").getItemStack();
		this.testBank[11] = Flashcard.registry.get("flashcard_sp3").getItemStack();

	}

	public void answerExamQuestion(int testBankIndex, int answerIndex)
	{
		///ItemStack temp = this.answers[answerIndex];
		///this.answers[answerIndex] = this.testBank[testBankIndex];
		///this.testBank[testBankIndex] = temp;

	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.LEARNING_EXAM;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);
	}

}
