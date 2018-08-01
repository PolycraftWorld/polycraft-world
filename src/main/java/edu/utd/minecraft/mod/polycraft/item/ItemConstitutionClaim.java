package edu.utd.minecraft.mod.polycraft.item;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemConstitutionClaim extends ItemCustom{
	
	protected  GsonBuilder gsonBuilderPull;// were changed to not final
	protected  GsonBuilder gsonBuilderPush;// were changed to not final
	
	private IIcon[] iconArray;
	private int maxSigners;
	
	public ItemConstitutionClaim(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("constitution_0"));
		this.setCreativeTab(CreativeTabs.tabMisc); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		
		gsonBuilderPull= new GsonBuilder();
		gsonBuilderPush= new GsonBuilder();
		
		this.maxSigners=1;
		
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		// you cannot initialize iconArray when declared nor in the constructor, as it is client-side only, so do it here:
		this.iconArray = new IIcon[5];
	
		for (int i = 0; i < this.iconArray.length; ++i) {
		// I use the integer ' i ' to differentiate my icons, so "icon1.png", "icon2.png", etc., because it fits a for loop nicely
		// use whatever you want, of course
		this.iconArray[i] = register.registerIcon(PolycraftMod.getAssetName("constitution_"+i));
		}
	}
	
	// note that this isn't the actual bow code, it's from one of my own, similar items
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if(!stack.stackTagCompound.hasKey("signers"))
		{
			return iconArray[0];
		}
		   return this.iconArray[stack.stackTagCompound.getTagList("signers", 10).tagCount()];
	}
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
		return iconArray[0];
    }

    /**
     * Returns the icon index of the stack given as argument.
     */
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack p_77650_1_)
    {
		if(p_77650_1_.stackTagCompound!=null)
		{
			if(!p_77650_1_.stackTagCompound.hasKey("signers"))
			{
				return iconArray[0];
			}
			return this.iconArray[p_77650_1_.stackTagCompound.getTagList("signers", 10).tagCount()];
		}
		return iconArray[0];
		   
    }
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
    {
		return iconArray[0];
    }
	@Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        /**
         * Gets an icon index based on an item's damage value and the given render pass
         */
		if(!stack.stackTagCompound.hasKey("signers"))
		{
			return iconArray[0];
		}
		   return this.iconArray[stack.stackTagCompound.getTagList("signers", 10).tagCount()];
    }

	
	
	 @Override
		// Doing this override means that there is no localization for language
		// unless you specifically check for localization here and convert
		public String getItemStackDisplayName(ItemStack par1ItemStack)
		{
		    return "Constitution Claim";
		}
	 

	 @SideOnly(Side.CLIENT)
	    public boolean hasEffect(ItemStack p_77636_1_)
	    {
		 	return false;
	    }

	 
	 @Override
	 public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
	 {
		 if(!p_77659_2_.isRemote)
		 {
			 
			 if(!p_77659_1_.stackTagCompound.hasKey("signers"))
			 {
				 NBTTagList nbtList = new NBTTagList();
				 p_77659_1_.stackTagCompound.setTag("signers", nbtList);
				
				
			 }
			
			 if( ((NBTTagList)p_77659_1_.stackTagCompound.getTagList("signers", 10)).tagCount()<maxSigners)
			 {
				 if(isNewPlayer(p_77659_3_,p_77659_1_)){
					 NBTTagCompound nbt = new NBTTagCompound();
					 nbt.setLong("id", Enforcer.whitelist.get(p_77659_3_.getCommandSenderName().toLowerCase()));
					 ((NBTTagList) p_77659_1_.stackTagCompound.getTag("signers")).appendTag(nbt);
					 ((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Player Signed"));
				 }
			 }else if(((NBTTagList)p_77659_1_.stackTagCompound.getTagList("signers", 10)).tagCount()==maxSigners)
			 {
				 if(!isNewPlayer(p_77659_3_,p_77659_1_)){
	            		try {

							if (ServerEnforcer.portalRestUrl != null)
							{
								
								
								JsonObject info = new JsonObject();
								JsonArray arr = new JsonArray();
								for(int c=0;c<((NBTTagList)p_77659_1_.stackTagCompound.getTag("signers")).tagCount();c++)
								{
									arr.add(new JsonPrimitive(((NBTTagList)p_77659_1_.stackTagCompound.getTag("signers")).getCompoundTagAt(c).getLong("id")));
								}
								
								info.add("signers", arr);
								
								String jsonToSend = gsonBuilderPush.create().toJson(info, new TypeToken<JsonObject>() {
								}.getType());
								String sendString = String.format("%s/players/%s/create_constitution/",
										ServerEnforcer.portalRestUrl,
										Enforcer.whitelist.get(p_77659_3_.getCommandSenderName().toLowerCase()));
								
					            System.out.println(jsonToSend);

								
								String contentFromPortal = NetUtil.postInventory(sendString, jsonToSend);

								if (contentFromPortal == null)
								{
									//return false
								}//did not get a confirm string from the portal - don't sync

								//return true;
							}

						} catch (final IOException e) {
							PolycraftMod.logger.error("Unable to send constitution info", e);
							//return false;
						}
	            		
	            		
	            		
	            		
	            		((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Sending to Portal"));
	            	}
			 }else
			 {
				 ((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("no"));
			 }
			 
		 }
		     return p_77659_1_;
	 }
	 

	 
		
	private boolean isNewPlayer(EntityPlayer player,ItemStack stack) {
		if(stack.stackTagCompound.hasKey("signers")) {
			NBTTagList nbtList = (NBTTagList)stack.stackTagCompound.getTag("signers");
			for(int i =1;i<=nbtList.tagCount();i++)
			{
				
				if(nbtList.getCompoundTagAt(i-1).getLong("id")==Enforcer.whitelist.get(player.getCommandSenderName().toLowerCase()))
				{
					((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("This player has already signed"));
	                
					return false;
				}
			}
			
			
			return true;
		}
		return true;
	}



	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
	{
		if(!p_77648_3_.isRemote)
		{
			
		}
	    return false;
	}

}
