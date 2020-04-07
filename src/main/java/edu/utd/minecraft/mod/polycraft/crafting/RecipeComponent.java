package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.util.LogUtil;

/**
 * A recipe component consisting of an item stack and the container
 * slot it exists in.
 */
public class RecipeComponent implements Comparable<RecipeComponent> {
	public final ItemStack itemStack;
	public final ContainerSlot slot;
	
	public RecipeComponent(final int slotIndex, final Item item, final int stackCount) {
		this(new RecipeSlot(slotIndex), new ItemStack(item, stackCount));
	}
	
	public RecipeComponent(final int slotIndex, final ItemStack stack) {
		this(new RecipeSlot(slotIndex), stack);
	}
	
	public RecipeComponent(final ContainerSlot slot, final ItemStack stack) {
		Preconditions.checkNotNull(slot);
		Preconditions.checkNotNull(stack);
		Preconditions.checkNotNull(stack.getItem());
		this.itemStack = stack;
		this.slot = slot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemStack.getItem().getUnlocalizedName().hashCode();
		result = prime * result + slot.getSlotIndex();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RecipeComponent other = (RecipeComponent) obj;
		if (!other.itemStack.getItem().getUnlocalizedName().equals(
				this.itemStack.getItem().getUnlocalizedName())) {
			return false;
		}
		
		if (this.slot.getSlotIndex() != -1 && other.slot.getSlotIndex() != -1) {
			return this.slot.getSlotIndex() == other.slot.getSlotIndex();
		}
		return true;
	}

	@Override
	public int compareTo(RecipeComponent o) {
		if (slot.getSlotIndex() == o.slot.getSlotIndex()) {
			return this.itemStack.getItem().getUnlocalizedName().compareTo(
					o.itemStack.getItem().getUnlocalizedName());
		}
		return PolycraftMod.compareInt(slot.getSlotIndex(), o.slot.getSlotIndex());
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();	// define recipe input to store data
		nbt.setInteger("slotIndex", slot.getSlotIndex());
		NBTTagCompound itemStackConfig = new NBTTagCompound();	// define itemStackConfig to store itemStack to outputConfig
		itemStackConfig.setString("name", itemStack.getItem().getRegistryName());
		itemStackConfig.setInteger("stackSize", itemStack.stackSize);
		nbt.setTag("itemStack", itemStackConfig);
		return nbt;
	}

	public static RecipeComponent fromNBT(NBTTagCompound nbt) {
		NBTTagCompound itemStackConfig = nbt.getCompoundTag("itemStack");
		return new RecipeComponent(nbt.getInteger("slotIndex"), Item.getByNameOrId(itemStackConfig.getString("name")), itemStackConfig.getInteger("stackSize"));
	}
	
	public JsonObject toJson() {
		JsonObject jobj = new JsonObject();	// define jobj to return
		jobj.addProperty("slotIndex", slot.getSlotIndex());
		JsonObject itemStackConfig = new JsonObject();	// define itemStackConfig to store in Json
		itemStackConfig.addProperty("name", itemStack.getItem().getRegistryName());
		itemStackConfig.addProperty("stackSize", itemStack.stackSize);
		jobj.add("itemStack", itemStackConfig);
		return jobj;
	}
	
	public static RecipeComponent fromJson(JsonObject jobj) {
		JsonObject itemStackConfig = jobj.get("itemStack").getAsJsonObject();
		return new RecipeComponent(jobj.get("slotIndex").getAsInt(), Item.getByNameOrId(itemStackConfig.get("name").getAsString()), itemStackConfig.get("stackSize").getAsInt());
	}
	
	@Override
	public String toString() {
		return "slot=" + slot + ", itemStack=" + LogUtil.toString(itemStack);
	}
}
