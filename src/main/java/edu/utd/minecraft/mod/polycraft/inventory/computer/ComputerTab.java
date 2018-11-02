package edu.utd.minecraft.mod.polycraft.inventory.computer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;


//TODO:not yet finished
public class ComputerTab extends CreativeTabs {

/*	public final String label;
	
	public ComputerTab() {
		this("");
	}
*/	
	
	public ComputerTab(String label) {
		super("Computer " + label);
		//this.label=label;
		this.setBackgroundImageName("temp.png");
	}

	@Override
	public Item getTabIconItem() {
		return Items.apple;//TODO: icon on tab
	}
	
/*    public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc")
    {
        private static final String __OBFID = "CL_00000014";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.lava_bucket;
        }
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.all});
*/

}
