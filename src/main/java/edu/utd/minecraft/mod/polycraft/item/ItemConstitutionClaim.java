package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemConstitutionClaim extends ItemCustom{
	
	
	
	//private IIcon[] iconArray;
	
	public ItemConstitutionClaim(CustomObject config) {
		super(config);
		//this.setTextureName(PolycraftMod.getAssetNameString("constitution_0"));
		this.setCreativeTab(CreativeTabs.tabMisc); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		
	}
	
	
	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(IIconRegister register) {
//		// you cannot initialize iconArray when declared nor in the constructor, as it is client-side only, so do it here:
//		this.iconArray = new IIcon[5];
//
//		for (int i = 0; i < this.iconArray.length; ++i) {
//		// I use the integer ' i ' to differentiate my icons, so "icon1.png", "icon2.png", etc., because it fits a for loop nicely
//		// use whatever you want, of course
//		this.iconArray[i] = register.registerIcon(PolycraftMod.getAssetName("constitution_"+i));
//		}
//	}
//
//	// note that this isn't the actual bow code, it's from one of my own, similar items
//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
//		if(!stack.stackTagCompound.hasKey("Signs"))
//		{
//			return iconArray[0];
//		}
//		   return this.iconArray[stack.stackTagCompound.getInteger("Signs")];
//	}
//	@Override
//	@SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamage(int p_77617_1_)
//    {
//		return iconArray[0];
//    }
//
//    /**
//     * Returns the icon index of the stack given as argument.
//     */
//	@Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconIndex(ItemStack p_77650_1_)
//    {
//		if(p_77650_1_.stackTagCompound!=null)
//		{
//			if(!p_77650_1_.stackTagCompound.hasKey("Signs"))
//			{
//				return iconArray[0];
//			}
//			   return this.iconArray[p_77650_1_.stackTagCompound.getInteger("Signs")];
//		}
//		return iconArray[0];
//
//    }
//	@Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
//    {
//		return iconArray[0];
//    }
//	@Override
//    public IIcon getIcon(ItemStack stack, int pass)
//    {
//        /**
//         * Gets an icon index based on an item's damage value and the given render pass
//         */
//		if(!stack.stackTagCompound.hasKey("Signs"))
//		{
//			return iconArray[0];
//		}
//		   return this.iconArray[stack.stackTagCompound.getInteger("Signs")];
//    }

	
	
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
		 	if(p_77636_1_.getTagCompound()==null) {
	        	return (p_77636_1_.getTagCompound().getInteger("Signs")==4);
		 	}
		 	return false;
	    }

	 
	 @Override
	 public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
	 {
		 if(!p_77659_2_.isRemote)
		 {
			 
			 if(!p_77659_1_.getTagCompound().hasKey("Signs"))
			 {
				 NBTTagList nbtList = new NBTTagList();
				 p_77659_1_.getTagCompound().setTag("constitution", nbtList);
				 p_77659_1_.getTagCompound().setInteger("Signs", 0);
				
			 }
			
			 
			 switch (p_77659_1_.getTagCompound().getInteger("Signs")) {
	            case 0:  //

	            	//p_77659_1_.getTagCompound().setString("Leader", p_77659_3_.getCommandSenderName());
	            	NBTTagCompound nbt1 = new NBTTagCompound();
	            	nbt1.setLong("Player1", Enforcer.whitelist.get(p_77659_3_.getCommandSenderEntity().getName()));
	            	
	            	((NBTTagList) p_77659_1_.getTagCompound().getTag("constitution")).appendTag(nbt1);
	            	p_77659_1_.getTagCompound().setInteger("Signs", 1);
	            
	            	
	            	 ((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Player1 has signed the Constitution"));
	            	break;
	            case 1:  //
	            	if(isNewPlayer(p_77659_3_,p_77659_1_)){
		            	NBTTagCompound nbt2 = new NBTTagCompound();
		            	nbt2.setLong("Player1", Enforcer.whitelist.get(p_77659_3_.getCommandSenderEntity().getName()));
		            	((NBTTagList) p_77659_1_.getTagCompound().getTag("constitution")).appendTag(nbt2);
		            	p_77659_1_.getTagCompound().setInteger("Signs", 2);
		            	
		            	((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Player2 has signed the Constitution"));
			 		}
		            break;
	            case 2:  //
	            	if(isNewPlayer(p_77659_3_,p_77659_1_)){
		            	NBTTagCompound nbt3 = new NBTTagCompound();
		            	nbt3.setLong("Player1", Enforcer.whitelist.get(p_77659_3_.getCommandSenderEntity().getName()));
		            	((NBTTagList) p_77659_1_.getTagCompound().getTag("constitution")).appendTag(nbt3);
		            	p_77659_1_.getTagCompound().setInteger("Signs", 3);
		            	
		            	((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Player3 has signed the Constitution"));
	            	}	
		            break;
	            case 3:  //
	            	if(isNewPlayer(p_77659_3_,p_77659_1_)){
		            	NBTTagCompound nbt4 = new NBTTagCompound();
		            	nbt4.setLong("Player1", Enforcer.whitelist.get(p_77659_3_.getCommandSenderEntity().getName()));
		            	((NBTTagList) p_77659_1_.getTagCompound().getTag("constitution")).appendTag(nbt4);
		            	p_77659_1_.getTagCompound().setInteger("Signs", 4);
		            	
		            	((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Player4 has signed the Constitution"));
	            	}
		            break;
	            case 4:  //
	            	((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("No more players can sign the Constitution"));
	                
	                break;
	            default: //
                break;
			 }
		 }
		     return p_77659_1_;
	 }
	 

	 
		
	private boolean isNewPlayer(EntityPlayer player,ItemStack stack) {
		 NBTTagList nbtList = (NBTTagList)stack.getTagCompound().getTag("constitution");
		for(int i =1;i<=nbtList.tagCount();i++)
		{
			
			if(nbtList.getCompoundTagAt(i-1).getString("Player"+i).equals(player.getCommandSenderEntity().getName()))
			{
				((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("This player has already signed"));
                
				return false;
			}
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
