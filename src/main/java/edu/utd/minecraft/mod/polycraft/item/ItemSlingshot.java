package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet;
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

	public enum SlingShotType{
		WOODEN, TACTICAL, SCATTER, BURST, GRAVITY, ICE;
	}
	
    private IIcon[] iconArray;
    public static final String[] slingPullIconNameArray = new String[] {PolycraftMod.getAssetName("slingpull1"), PolycraftMod.getAssetName("slingpull2"), PolycraftMod.getAssetName("slingpull3")};
    int holdCount;
    SlingShotType type;
    
	public ItemSlingshot(CustomObject config) {
		super(config);
		init();
		type = SlingShotType.WOODEN;
	}
	
	public ItemSlingshot(CustomObject config, SlingShotType type) {
		super(config);
		init();
		this.type = type;
	}
	
	private void init() {
		this.maxStackSize = 1;
		this.setMaxDamage(500);
//		this.setTextureName(PolycraftMod.getAssetName("Slingshot"));
		this.setCreativeTab(CreativeTabs.tabCombat);
	}
	
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count)
    {
        if(type == SlingShotType.WOODEN) {
        	fireWooden(stack, world, player, count);
        } else if(type == SlingShotType.TACTICAL) {
        	fireTactical(stack, world, player, count);
        } else if(type == SlingShotType.BURST) {
        	fireBurst(stack, world, player, count);
        } else if(type == SlingShotType.GRAVITY) {
        	fireGravity(stack, world, player, count);
        } else if(type == SlingShotType.ICE) {
        	fireIce(stack, world, player, count);
        }
    }
	
	private void fireWooden(ItemStack stack, World world, EntityPlayer player, int count) {
		int j = this.getMaxItemUseDuration(stack) - count;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()){
            return;
        }
        j = event.charge;

        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (flag || player.inventory.hasItem(ItemCustom.getItemById(6414))){
            float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D){
                return;
            }

            if (f > 1.0F) {
                f = 1.0F;
            }

            EntityPellet EntityPellet = new EntityPellet(world, player, f * 2.0F);

            if (f == 1.0F){
                EntityPellet.setIsCritical(true);
            }

            stack.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag){
                EntityPellet.canBePickedUp = 2;
            }
            else{
                player.inventory.consumeInventoryItem(ItemCustom.getItemById(6414));
            }

            if (!world.isRemote){
                world.spawnEntityInWorld(EntityPellet);
            }
        }
	}
	
	private void fireTactical(ItemStack stack, World world, EntityPlayer player, int count) {
		fireWooden(stack, world, player, count);
	}
	private void fireScatter(ItemStack stack, World world, EntityPlayer player, int count) {
		fireWooden(stack, world, player, count);
	}
	private void fireBurst(ItemStack stack, World world, EntityPlayer player, int count) {
		fireWooden(stack, world, player, count);
	}
	private void fireGravity(ItemStack stack, World world, EntityPlayer player, int count) {
		fireWooden(stack, world, player, count);
	}
	private void fireIce(ItemStack stack, World world, EntityPlayer player, int count) {
		fireWooden(stack, world, player, count);
	}
	
	public IIcon getItemIcon(ItemStack stack, int count){
		
		 if (holdCount >= 18){
             return this.iconArray[2];
         }

         if (holdCount > 13){
        	 return this.iconArray[1];
         }

         if (holdCount > 0){
             return this.iconArray[0];
         }
         return this.itemIcon;
    }
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int p_94599_1_){
        return this.iconArray[p_94599_1_];
    }
	
//	@Override
//	public boolean shouldRotateAroundWhenRendering() {
//		return true;
//	}
	
	@Override
	public String getItemStackDisplayName(ItemStack parItemstack){
		return "Wooden Slingshot";
	}
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	
        ArrowNockEvent event = new ArrowNockEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        if (player.capabilities.isCreativeMode || player.inventory.hasItem(ItemCustom.getItemById(6414)))
        {
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			if (world.isRemote) {
				this.holdCount = 72000 - player.getItemInUseCount();
			}
		}

        return stack;
    }
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack){
        return 72000;
    }
    

    public void registerIcons(IIconRegister register){
        this.itemIcon = register.registerIcon(PolycraftMod.getAssetName("Slingshot"));
        this.iconArray = new IIcon[slingPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = register.registerIcon(this.getIconString() + "_" + slingPullIconNameArray[i]);
        }
    }

    

//	public int getItemEnchantability() {
//		return 0;
//	}

}
