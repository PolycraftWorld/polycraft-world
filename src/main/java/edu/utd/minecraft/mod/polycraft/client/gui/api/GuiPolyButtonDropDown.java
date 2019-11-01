package edu.utd.minecraft.mod.polycraft.client.gui.api;

import java.util.List;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.item.ItemAITool.BlockType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyButtonDropDown<E extends Enum<E>> extends GuiButton{
	
	public static final ResourceLocation DropDownTab = new ResourceLocation(PolycraftMod.MODID,
			"textures/gui/Button.png");
	
	//private E[] options; //current opt set to options[0]
	//private String currentOpt; 
	private E currentOpt;
	
	public int x, y, w, h;
	public GuiButton[] buttonList;
	public GuiButton[] fullButtonList;
	
	private boolean open=false;
	
	public GuiPolyButtonDropDown(int id, int xStart, int yStart, int width, int height, final E option) {
		super(id, xStart, yStart, width, height, option.name());
		this.x=xStart;
		this.y=yStart;
		this.w=width;
		this.h=height;
		this.currentOpt=option;
		this.buttonList = new GuiButton[ option.getDeclaringClass().getEnumConstants().length];
		this.fullButtonList = new GuiButton[ option.getDeclaringClass().getEnumConstants().length];
		setFullButtons();
		setButtons();
	
		//this.options = options;

	}
	
	public void setCurrentOpt(E option)
	{
		this.currentOpt= option;
		this.displayString=option.name();
	}

	

	public E getCurrentOpt() {
		return currentOpt;
	}
	
	public void setButtons()
	{
		int yOffset=0;
		for(int c=0;c< currentOpt.getDeclaringClass().getEnumConstants().length;c++)
		{
			if(currentOpt.getDeclaringClass().getEnumConstants()[c].name()==this.currentOpt.name())
				continue;
			this.buttonList[c]= new GuiButton(c+501, this.x, this.y+((yOffset+1)*20), width, height, currentOpt.getDeclaringClass().getEnumConstants()[c].name());
			yOffset++;
		}
	}
	
	public void setFullButtons()
	{
		int yOffset=0;
		for(int c=0;c< currentOpt.getDeclaringClass().getEnumConstants().length;c++)
		{
//			if(currentOpt.getDeclaringClass().getEnumConstants()[c].name()==this.currentOpt.name())
//				continue;
			this.fullButtonList[c]= new GuiButton(c+501, this.x, this.y+((yOffset+1)*20), width, height, currentOpt.getDeclaringClass().getEnumConstants()[c].name());
			yOffset++;
		}
	}
	
	public void addButtons(List<GuiButton> buttonList)
	{
		int yOffset=0;
		for(int c=0;c< currentOpt.getDeclaringClass().getEnumConstants().length;c++)
		{
			if(currentOpt.getDeclaringClass().getEnumConstants()[c].name()==this.currentOpt.name())
				continue;
			this.buttonList[c]= new GuiButton(c+501, this.x, this.y+((yOffset+1)*20), width, height, currentOpt.getDeclaringClass().getEnumConstants()[c].name());
			buttonList.add(this.buttonList[c]);
			yOffset++;
		}
	}
	
	public void removeButtons(List<GuiButton> buttonList)
	{
		for(GuiButton btn:this.buttonList)
		{
			buttonList.remove(btn);
		}
	}
	
	 public boolean actionPerformed(GuiButton button) {
		 for(GuiButton btn: buttonList)
		 {
			 if(btn!=null)
			 {
				 if(btn==button)
				 {
					 this.currentOpt= Enum.valueOf(currentOpt.getDeclaringClass(), button.displayString);
					 this.displayString=button.displayString;
					 return true;
				 }
			 }
		 }
		 return false;
		 
	 }
	 
	 
	 public boolean actionPerformed(GuiButton button,PolycraftGuiScreenBase gui) {
		 
		 if(open)
		 {
			 for(GuiButton btnTest: this.buttonList)
			 {
				 if(btnTest!=null)
				 {
					 if(btnTest==button || button==this)
					 {
						 actionPerformed(button);
						 this.removeButtons(gui.getButtonList());
						 open=false;
						 
						 for(GuiButton btn: gui.getButtonList())
						 {
							 btn.enabled=true;
						 }
						 return false;
					 }
					 else 
					 {
//						 for(GuiButton btnTest2: this.fullButtonList)
//						 {
//							 if(btnTest2.displayString==button.displayString)
//							 {
//								 actionPerformed(button);
//								 this.removeButtons(gui.getButtonList());
//								 gui.open=false;
//								 
//								 for(GuiButton btn: gui.getButtonList())
//								 {
//									 btn.enabled=true;
//								 }
//								 return false;
//							 }
//						 }
					 }
				 }
			 }
			 return false;
		 }
		 else
		 {
			 if(button==this)
			 {
				 for(GuiButton btn: gui.getButtonList())
				 {
					 if(btn!=this)
						 btn.enabled=false;
				 }
				 
				 addButtons(gui.getButtonList());
				 open=true;
				 return true;
			 }
			 return false;
		 }
//		 if(button!=this)
//		 {
//			 
//		 }
//		 actionPerformed(button);
//		 this.removeButtons(gui.getButtonList());
//		 gui.open=false;
//		 
//		 for(GuiButton btn: gui.getButtonList())
//		 {
//			 btn.enabled=true;
//		 }
//		 return true;
//		 else
//		 {
//		 
//			 for(GuiButton btn: buttonList)
//			 {
//				 if(btn==button)
//				 {
//					 this.currentOpt= Enum.valueOf(currentOpt.getDeclaringClass(), button.displayString);
//					 this.displayString=button.displayString;
//					 return true;
//				 }
//			 }
//			 return false;
//		 }
		 
	 }
	
	public GuiButton[] getButtons()
	{
		return buttonList;
	}
	
}
