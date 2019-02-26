package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityPaintball;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemSlingshot extends ItemCustom {

    private IIcon[] iconArray;
    public static final String[] slingPullIconNameArray = new String[] {PolycraftMod.getAssetName("slingpull1"), PolycraftMod.getAssetName("slingpull2"), PolycraftMod.getAssetName("slingpull3")};
    int jeff;
	public ItemSlingshot(CustomObject config) {
		super(config);
		this.maxStackSize = 1;
		this.setMaxDamage(500);
//		this.setTextureName(PolycraftMod.getAssetName("Slingshot"));
		this.setCreativeTab(CreativeTabs.tabCombat);
	}

	
	public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityPlayer p_77615_3_, int p_77615_4_)
    {
        int j = this.getMaxItemUseDuration(p_77615_1_) - p_77615_4_;

        ArrowLooseEvent event = new ArrowLooseEvent(p_77615_3_, p_77615_1_, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        j = event.charge;

        boolean flag = p_77615_3_.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, p_77615_1_) > 0;

        if (flag || p_77615_3_.inventory.hasItem(ItemCustom.getItemById(6414)))
        {
            float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F) 
            {
                f = 1.0F;
            }

            EntityPaintball EntityPaintball = new EntityPaintball(p_77615_2_, p_77615_3_, f * 2.0F);

            if (f == 1.0F)
            {
                EntityPaintball.setIsCritical(true);
            }

            p_77615_1_.damageItem(1, p_77615_3_);
            p_77615_2_.playSoundAtEntity(p_77615_3_, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                EntityPaintball.canBePickedUp = 2;
            }
            else
            {
                p_77615_3_.inventory.consumeInventoryItem(ItemCustom.getItemById(6414));
            }

            if (!p_77615_2_.isRemote)
            {
                p_77615_2_.spawnEntityInWorld(EntityPaintball);
            }
        }
    }
	
	
	public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_)
    {
		
		 if (jeff >= 18)
         {
             return this.iconArray[2];
         }

         if (jeff > 13)
         {
        	 return this.iconArray[1];
         }

         if (jeff > 0)
         {
             return this.iconArray[0];
         }
         return this.itemIcon;
    }
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int p_94599_1_)
    {
        return this.iconArray[p_94599_1_];
    }
	
//	@Override
//	public boolean shouldRotateAroundWhenRendering() {
//		return true;
//	}
	
	@Override
	public String getItemStackDisplayName(ItemStack parItemstack)
	{
		return "Wooden Slingshot";
	}
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player)
    {
    	
        ArrowNockEvent event = new ArrowNockEvent(player, p_77659_1_);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        if (player.capabilities.isCreativeMode || player.inventory.hasItem(ItemCustom.getItemById(6414)))
        {
            player.setItemInUse(p_77659_1_, this.getMaxItemUseDuration(p_77659_1_));
            this.jeff = 72000 - player.getItemInUseCount();
        }

        return p_77659_1_;
    }
    public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_)
    {
        return p_77654_1_;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return 72000;
    }
    

    public void registerIcons(IIconRegister p_94581_1_)
    {
        this.itemIcon = p_94581_1_.registerIcon(PolycraftMod.getAssetName("Slingshot"));
        this.iconArray = new IIcon[slingPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = p_94581_1_.registerIcon(this.getIconString() + "_" + slingPullIconNameArray[i]);
        }
    }

    

//	public int getItemEnchantability() {
//		return 0;
//	}

}
