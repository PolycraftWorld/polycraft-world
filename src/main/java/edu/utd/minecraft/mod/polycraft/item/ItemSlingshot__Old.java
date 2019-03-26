package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityPaintBall__Old;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet__Old;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemSlingshot__Old extends ItemCustom {

	public enum SlingshotType{
		WOODEN, TACTICAL, SCATTER, BURST, GRAVITY, ICE;
	}
	
    private IIcon[] iconArray;
    public static final String[] slingPullIconNameArray = new String[] {PolycraftMod.getAssetName("slingpull1"), PolycraftMod.getAssetName("slingpull2"), PolycraftMod.getAssetName("slingpull3")};
    int holdCount;
    SlingshotType type;
    private static final int MIN_TICKS_TO_FIRE_TACTICAL = 48;
    private int numTicksSinceLastShot = 0;
    
	public ItemSlingshot__Old(CustomObject config) {
		super(config);
		init();
		type = SlingshotType.SCATTER;
	}
	
	public ItemSlingshot__Old(CustomObject config, SlingshotType type) {
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
        if(type == SlingshotType.WOODEN) {
        	fireWooden(stack, world, player, count);
        } else if(type == SlingshotType.TACTICAL) {
        	if(numTicksSinceLastShot < MIN_TICKS_TO_FIRE_TACTICAL) {
    			return;
    		}
        	fireTactical(stack, world, player, count);
        } else if(type == SlingshotType.SCATTER) {
        	fireScatter(stack, world, player, count);
        } else if(type == SlingshotType.BURST) {
        	fireBurst(stack, world, player, count);
        } else if(type == SlingshotType.GRAVITY) {
        	fireGravity(stack, world, player, count);
        } else if(type == SlingshotType.ICE) {
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

            EntityPellet__Old EntityPellet = new EntityPellet__Old(world, player, f * 2.0F);

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
		
		numTicksSinceLastShot = 0;
		
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

            EntityPaintBall__Old paintBall = new EntityPaintBall__Old(world, player, f * 2.0F, this.type);

            if (f == 1.0F){
                paintBall.setIsCritical(true);
            }

            stack.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag){
                paintBall.canBePickedUp = 2;
            }
            else{
                player.inventory.consumeInventoryItem(ItemCustom.getItemById(6414));
            }

            if (!world.isRemote){
                world.spawnEntityInWorld(paintBall);
            }
        }
	}
	private void fireScatter(ItemStack stack, World world, EntityPlayer player, int count) {
		fireTactical(stack, world, player, count);
	}
	private void fireBurst(ItemStack stack, World world, EntityPlayer player, int count) {
		fireTactical(stack, world, player, count);
	}
	private void fireGravity(ItemStack stack, World world, EntityPlayer player, int count) {
		fireTactical(stack, world, player, count);
	}
	private void fireIce(ItemStack stack, World world, EntityPlayer player, int count) {
		fireTactical(stack, world, player, count);
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

    public SlingshotType getType() {
    	return type;
    }
    
    @Override
    public void onUpdate(ItemStack stack, World _world, Entity entity, int par4, boolean par5) {
    	if(!_world.isRemote && numTicksSinceLastShot <= MIN_TICKS_TO_FIRE_TACTICAL) {
    		numTicksSinceLastShot++;
    	}
    	super.onUpdate(stack, _world, entity, par4, par5);
    }

//	public int getItemEnchantability() {
//		return 0;
//	}

}
