package edu.utd.minecraft.mod.polycraft.inventory.courseblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Flashcard;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.CoinOperatedInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;
import edu.utd.minecraft.mod.polycraft.item.ItemExam;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashcard;

public class CHEM2323Inventory extends CoinOperatedInventory {

	public static final int[] slotIndexInput;
	public static final int slotIndexItem;
	public static final int slotIndexInputFee;
	public static final int slotIndexOutput;
	public static final int[] slotIndexBank;

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		slotIndexInput = new int[8];
		slotIndexBank = new int[12];
		int i = 0;
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 0, 0, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 0, 1, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 0, 2, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 0, 3, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 8, 0, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 8, 1, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 8, 2, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput[i++] = guiSlots.size(), 8, 3, 8, 8));

		i = 0;

		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 10, 0, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 10, 1, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 10, 2, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 10, 3, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 11, 0, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 11, 1, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 11, 2, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 11, 3, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 12, 0, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 12, 1, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 12, 2, 8, 8));
		guiSlots.add(GuiContainerSlot.createMisc(slotIndexBank[i++] = guiSlots.size(), 12, 3, 8, 8));

		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputFee = guiSlots.size(), 1, 4, 8, 8));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexItem = guiSlots.size(), 4, 4, 8, 8));

		guiSlots.add(GuiContainerSlot.createOutput(slotIndexOutput = guiSlots.size(), 7, 4, 8, 8));

	}

	private static Inventory config;
	public int bookmark = 0;

	public ResourceLocation getOverlayResourceLocation() {
		if (this.slotHasValidItem(guiSlots.get(slotIndexItem)))
		{
			return new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s-%s.png",
					PolycraftMod.getFileSafeName(config.name), PolycraftMod.getFileSafeName(this.getStackInSlot(slotIndexItem).getDisplayName()))));
		}
		//return new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s-p1.png", PolycraftMod.getFileSafeName(config.name))));
		return null;
	}

	public ResourceLocation getOverlayResourceLocationLetters() {
		if (this.slotHasValidItem(guiSlots.get(slotIndexItem)))
		{
			return new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s_letters_only.png",
					PolycraftMod.getFileSafeName(config.name))));
		}
		//return new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s-p1.png", PolycraftMod.getFileSafeName(config.name))));
		return null;
	}

	private boolean slotHasValidItem(GuiContainerSlot guiContainerSlot) {

		if (this.slotHasItem(guiContainerSlot))
		{
			Item itemToTest = this.getStackInSlot(guiSlots.get(slotIndexItem)).getItem();

			if ((Block.getBlockFromItem(itemToTest) == Blocks.dirt) ||
					(Block.getBlockFromItem(itemToTest) == Blocks.grass) ||
					(Block.getBlockFromItem(itemToTest) == Blocks.sand))
				return true;
			if (itemToTest instanceof ItemExam)
				return true;
		}

		return false;
	}

	public static final void register(final Inventory config) {
		CHEM2323Inventory.config = config;
		config.containerType = PolycraftContainerType.CHEM_2323;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, CHEM2323Inventory.class));
	}

	public CHEM2323Inventory() {
		super(PolycraftContainerType.CHEM_2323, config, 134, -1);
		this.addBehavior(new CraftingBehavior<CHEM2323Inventory>());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, playerInventoryOffset, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 250, 216, true);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack stack) {
		super.setInventorySlotContents(slotIndex, stack);
		if (slotIndexItem == slotIndex)
		{
			if (this.slotHasItem(guiSlots.get(slotIndex)))
			{
				if (stack.getItem() instanceof ItemExam)
				{

					NBTTagCompound list = stack.getTagCompound();
					int questionListCount = list.getTagList("Questions", 10).tagCount();

					NBTTagCompound question = list.getTagList("Questions", 10).getCompoundTagAt(this.bookmark);
					String countMinusOne = "";

					for (int i = 0; i < 8; i++)
					{
						if (Flashcard.registry.get(question.getString(String.format("A%s", i + 1))) != null)
							this.setStackInSlot(guiSlots.get(slotIndexInput[i]), Flashcard.registry.get(question.getString(String.format("A%s", i + 1))).getItemStack());
					}
					for (int i = 0; i < 12; i++)
					{
						if (Flashcard.registry.get(question.getString(String.format("TB%s", i + 1))) != null)
							this.setStackInSlot(guiSlots.get(slotIndexBank[i]), Flashcard.registry.get(question.getString(String.format("TB%s", i + 1))).getItemStack());
					}

				}
			}
			else
			{
				for (int i = 0; i < 8; i++)
				{
					if (this.slotHasItem(guiSlots.get(slotIndexInput[i])))
					{
						if (this.getStackInSlot(guiSlots.get(slotIndexInput[i])).getItem() instanceof ItemFlashcard)
							this.clearSlotContents(guiSlots.get(slotIndexInput[i]));
					}
				}
				for (int i = 0; i < 12; i++)
				{
					if (this.slotHasItem(guiSlots.get(slotIndexBank[i])))
					{
						if (this.getStackInSlot(guiSlots.get(slotIndexBank[i])).getItem() instanceof ItemFlashcard)
							this.clearSlotContents(guiSlots.get(slotIndexBank[i]));
					}
				}

			}
		}
		else if (slotIndex == slotIndexInputFee)
		{
			//submit exam after coin check			
		}

		else if (guiSlots.get(slotIndex).getSlotType() == SlotType.INPUT)
		{
			//change the answers of the specific itemStack, not item

		}
		else if (guiSlots.get(slotIndex).getSlotType() == SlotType.MISC)
		{

		}

	}
	//	@Override
	//	public void onPickupFromSlot(EntityPlayer player, ContainerSlot slot, ItemStack itemStack) {
	//		if (this.slotHasItem(slot))
	//		{
	//			if (itemStack.getItem() instanceof ItemExam)
	//			{
	//				for (int i = 0; i < 8; i++)
	//				{
	//					if (this.getStackInSlot(guiSlots.get(slotIndexInput[i])).getItem() instanceof ItemFlashcard)
	//						//this.setStackInSlot(guiSlots.get(slotIndexInput[i]), null);
	//						this.clearSlotContents(guiSlots.get(slotIndexInput[i]));
	//				}
	//				for (int i = 0; i < 12; i++)
	//				{
	//					if (this.getStackInSlot(guiSlots.get(slotIndexBank[i])).getItem() instanceof ItemFlashcard)
	//						this.clearSlotContents(guiSlots.get(slotIndexBank[i]));
	//				}
	//
	//			}
	//
	//		}
	//		this.markDirty();
	//		super.onPickupFromSlot(player, slot, itemStack);
	//
	//	}

}
