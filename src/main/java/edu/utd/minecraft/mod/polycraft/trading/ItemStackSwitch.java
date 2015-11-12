package edu.utd.minecraft.mod.polycraft.trading;

import java.lang.reflect.Type;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
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
			final String itemId,
			final int damage,
			final int stacksize,
			final NBTTagCompound enchantments)
	{
		this.player = player;
		//Item item = PolycraftRegistry.items.get(PolycraftRegistry.getRegistryNameFromId(itemId));
		//Block block = PolycraftRegistry.blocks.get(PolycraftRegistry.getRegistryNameFromId(itemId));
		boolean isBlock = false;

		//Note: you have to test items first because of the dual name mapping from base MC
		// thus if we find an item, we never have to look for the block...
		if (PolycraftRegistry.isIdItemId(itemId))
		{
			this.itemStack = new ItemStack(PolycraftRegistry.items.get(PolycraftRegistry.getRegistryNameFromId(itemId)),
					stacksize);
		}
		else if (PolycraftRegistry.isIdBlockId(itemId))
		{
			this.itemStack = new ItemStack(PolycraftRegistry.blocks.get(PolycraftRegistry.getRegistryNameFromId(itemId)),
					stacksize);
			isBlock = true;
		}
		else if (itemId.startsWith(PolycraftMod.MC_PREFIX + String.format("%04d", 112)))
		{
			this.itemStack = new ItemStack(PolycraftRegistry.blocks.get("Nether Brick"), stacksize);
			//hack because it is the exact same name as the item once no spaces/underscores
			isBlock = true;
		}
		else
		{
			this.itemStack = null;
			return;
		}

		if (this.itemStack != null)
		{
			if (!(itemId.startsWith(PolycraftMod.MC_PREFIX)))
				PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(this.itemStack); //add polycraft-recipe NBTTag

			if (damage > 0)
			{
				this.itemStack.setItemDamage(damage);
			}

			if ((enchantments != null) && (stacksize == 1))
				this.itemStack.setTagCompound(enchantments);

			return;
		}

	}

	public static class Deserializer implements JsonDeserializer<ItemStackSwitch> {

		private final EntityPlayer player;

		public Deserializer(final EntityPlayer player) {
			this.player = player;
		}

		@Override
		public ItemStackSwitch deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			JsonArray jarray = jobject.get("enchantments").getAsJsonArray();

			NBTTagCompound enchantments = new NBTTagCompound();
			NBTTagList enchList = new NBTTagList();

			Iterator i = jarray.iterator();
			while (i.hasNext())
			{
				JsonObject specificEnchantment = ((JsonElement) i.next()).getAsJsonObject();
				NBTTagCompound tag = new NBTTagCompound();
				tag.setShort("id", specificEnchantment.get("id").getAsShort());
				tag.setShort("lvl", specificEnchantment.get("level").getAsShort());
				enchList.appendTag(tag);
			}
			enchantments.setTag("ench", enchList);

			return new ItemStackSwitch(
					player,
					jobject.get("id").getAsString(),
					jobject.get("damage").getAsInt(),
					jobject.get("stacksize").getAsInt(),
					enchantments);
		}
	}

	public static class Serializer implements JsonSerializer<ItemStackSwitch> {

		@Override
		public JsonElement serialize(ItemStackSwitch src, Type typeOfSrc, JsonSerializationContext context) {

			JsonObject itemInfo = new JsonObject();
			itemInfo.addProperty("id", PolycraftRegistry.getRegistryIdFromItemStack(src.itemStack));
			itemInfo.addProperty("damage", src.itemStack.getItemDamage());
			itemInfo.addProperty("stacksize", src.itemStack.stackSize);

			NBTTagCompound list;
			if ((list = src.itemStack.getTagCompound()) != null)
			{
				JsonObject enchantArray = new JsonObject();

				for (int i = 0; i < list.getTagList("StoredEnchantments", 10).tagCount(); i++)
				{
					JsonObject specificEnchantment = new JsonObject();

					NBTTagCompound tag = list.getTagList("StoredEnchantments", 10).getCompoundTagAt(i);
					specificEnchantment.addProperty("level", tag.getShort("lvl"));
					specificEnchantment.addProperty("id", tag.getShort("id"));
					enchantArray.add("StoredEnchantments", specificEnchantment);
				}
				for (int i = 0; i < list.getTagList("ench", 10).tagCount(); i++)
				{
					JsonObject specificEnchantment = new JsonObject();

					NBTTagCompound tag = list.getTagList("ench", 10).getCompoundTagAt(i);
					specificEnchantment.addProperty("level", tag.getShort("lvl"));
					specificEnchantment.addProperty("id", tag.getShort("id"));
					enchantArray.add(String.valueOf(i), specificEnchantment);
				}

				itemInfo.add("enchantments", enchantArray);
			}
			else
				itemInfo.add("enchantments", new JsonArray());

			// TODO Auto-generated method stub
			return itemInfo;
		}
	}

}
