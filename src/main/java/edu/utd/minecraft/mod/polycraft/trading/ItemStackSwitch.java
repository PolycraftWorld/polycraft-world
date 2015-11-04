package edu.utd.minecraft.mod.polycraft.trading;

import java.lang.reflect.Type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;

public class ItemStackSwitch {

	public final EntityPlayer player;
	public final ItemStack itemStack;

	public ItemStackSwitch(final EntityPlayer player, final ItemStack itemStack)
	{
		this.player = player;
		this.itemStack = itemStack;
	}

	public ItemStackSwitch(final EntityPlayer player,
			final JsonElement id,
			final JsonElement stacksize,
			final JsonElement damage,
			final JsonElement enchantments)
	{
		this.player = player;
		this.itemStack = new ItemStack(PolycraftRegistry.items.get(Integer.parseInt(id.toString())), Integer.parseInt(stacksize.toString())); //create ItemStack here		
		PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(this.itemStack); //add polycraft-recipe NBTTag
		this.itemStack.setItemDamage(Integer.parseInt(damage.toString()));

		//parse these out of the enchantment string
		//		 NBTTagList nbttaglist = this.itemStack.stackTagCompound.getTagList("ench", 10);
		//	        NBTTagCompound nbttagcompound = new NBTTagCompound();
		//	        nbttagcompound.setShort("id", (short)1);
		//	        nbttagcompound.setShort("lvl", (short)1);
		//	        
		//	        nbttaglist.appendTag(nbttagcompound);
		//		
		//		
		//		
		//		this.itemStack.stackTagCompound.setTag(p_74782_1_, p_74782_2_);
	}

	public static class Deserializer implements JsonDeserializer<ItemStackSwitch> {

		private final EntityPlayer player;
		private final ItemStack itemStack;

		public Deserializer(final EntityPlayer player, final ItemStack itemStack) {
			this.player = player;
			this.itemStack = itemStack;
		}

		@Override
		public ItemStackSwitch deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new ItemStackSwitch(
					player,
					jobject.get("id"),
					jobject.get("stacksize"),
					jobject.get("damage"),
					jobject.get("enchantments"));
		}
	}

}
