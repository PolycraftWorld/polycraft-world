package edu.utd.minecraft.mod.polycraft.client.gui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class GuiExperimentConfig extends GuiListExtended {
	private final GuiScreen gui;
	private final Minecraft mc;
	private final ArrayList<GuiListExtended.IGuiListEntry> configList;
	private int maxStringLength = 0; //used during rendering to "left-justify" all parameter names
	
	private ExperimentParameters params;
	public static Color HEADER_TEXT_COLOR = new Color(155, 155, 155);
	private static int SLOT_HEIGHT = 22;
	private float initialClickY = -2.0F;
	protected boolean isMousePressed = true; //??
	/** How far down this slot has been scrolled */
    private float amountScrolled;
    /** The element in the list that was selected */
    private int selectedElement = -1;
    /** The time when this button was last clicked. */
    private long lastClicked;
    //public int headerPadding;
    /**
     * What to multiply the amount you moved your mouse by (used for slowing down scrolling when over the items and not
     * on the scroll bar)
     */
    private float scrollMultiplier;
	
	public GuiExperimentConfig(GuiScreen gui, Minecraft mc) {
		//see below for the names of those variables.
		super(mc, ((GuiExperimentList)gui).X_WIDTH, ((GuiExperimentList)gui).SCROLL_HEIGHT, 50, 200, SLOT_HEIGHT);
		this.mc = mc;
		this.gui = gui;
		this.configList = new ArrayList<>();
		this.headerPadding = 0;
		
		if(gui instanceof GuiExperimentList) {
			params = ((GuiExperimentList)gui).currentParameters;
			
			//TODO: Make a more sustainable params list.
			this.configList.add(new ConfigHeader("Timing"));
			
			for(String key : params.timingParameters.keySet()) {
	    		Integer[] vals = params.timingParameters.get(key);
	    		
	    		int l = mc.fontRenderer.getStringWidth(key);
	    		if(l > this.maxStringLength)
	    			maxStringLength = l;
	    		
	    		this.configList.add(new ConfigSlider(key, vals[0], vals[1], vals[2]));
	    		
	    	}
			
			this.configList.add(new ConfigHeader("Scoring"));
			
			for(String key : params.scoringParameters.keySet()) {
	    		Number[] vals = params.scoringParameters.get(key);
	    		int l = mc.fontRenderer.getStringWidth(key);
	    		if(l > this.maxStringLength)
	    			maxStringLength = l;
	    		this.configList.add(new ConfigSlider(key, Double.parseDouble(vals[0].toString()), Double.parseDouble(vals[1].toString()), Double.parseDouble(vals[2].toString())));
	    	}
			
			this.configList.add(new ConfigHeader("Items"));
		}
		
		
	}

	/**
	 * Don't delete this - it's a good reference for future you!
	 * @param minecraft
	 * @param width
	 * @param height
	 * @param top
	 * @param bottomOffset
	 * @param slotHeight
	 */
	@Deprecated
	public GuiExperimentConfig(Minecraft minecraft, int width, int height, int top, int bottomOffset, int slotHeight) {
		super(minecraft, width, height, top, bottomOffset, slotHeight);
		this.mc = minecraft;
		this.gui = null;
		this.configList = new ArrayList<>();
	}

	@Override
	public IGuiListEntry getListEntry(int index) {
		
		return this.configList.get(index);
	}

	@Override
	protected int getSize() {
	
		return this.configList.size();
	}
	
	@Override
	protected void drawBackground() {
		//System.out.println("LOL");
	}
	
	@Override
    public int getAmountScrolled()
    {
        return (int)this.amountScrolled;
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float mouseEvent) {
		// TODO Auto-generated method stub
		//super.drawScreen(p_148128_1_, p_148128_2_, p_148128_3_);
		this.mouseX = mouseX;
        this.mouseY = mouseY;
        //this.drawBackground();
        int k = this.getSize();
        int l = this.getScrollBarX();
        int i1 = l + 6;
        int l1;
        int i2;
        int k2;
        int i3;

        if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom)
        {
            if (Mouse.isButtonDown(0) && this.func_148125_i())
            {
                if (this.initialClickY == -1.0F)
                {
                    boolean flag1 = true;

                    if (mouseY >= this.top && mouseY <= this.bottom)
                    {
                        int k1 = this.width / 2 - this.getListWidth() / 2;
                        l1 = this.width / 2 + this.getListWidth() / 2;
                        i2 = mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                        int j2 = i2 / this.slotHeight;

                        if (mouseX >= k1 && mouseX <= l1 && j2 >= 0 && i2 >= 0 && j2 < k)
                        {
                            boolean flag = j2 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(j2, flag, mouseX, mouseY);
                            this.selectedElement = j2;
                            this.lastClicked = Minecraft.getSystemTime();
                        }
                        else if (mouseX >= k1 && mouseX <= l1 && i2 < 0)
                        {
                            this.func_148132_a(mouseX - k1, mouseY - this.top + (int)this.amountScrolled - 4);
                            flag1 = false;
                        }

                        if (mouseX >= l && mouseX <= i1)
                        {
                            this.scrollMultiplier = -1.0F;
                            i3 = this.func_148135_f();

                            if (i3 < 1)
                            {
                                i3 = 1;
                            }

                            k2 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());

                            if (k2 < 32)
                            {
                                k2 = 32;
                            }

                            if (k2 > this.bottom - this.top - 8)
                            {
                                k2 = this.bottom - this.top - 8;
                            }

                            this.scrollMultiplier /= (float)(this.bottom - this.top - k2) / (float)i3;
                        }
                        else
                        {
                            this.scrollMultiplier = 1.0F;
                        }

                        if (flag1)
                        {
                            this.initialClickY = (float)mouseY;
                        }
                        else
                        {
                            this.initialClickY = -2.0F;
                        }
                    }
                    else
                    {
                        this.initialClickY = -2.0F;
                    }
                }
                else if (this.initialClickY >= 0.0F)
                {
                    this.amountScrolled -= ((float)mouseY - this.initialClickY) * this.scrollMultiplier;
                    this.initialClickY = (float)mouseY;
                }
            }
            else
            {
                for (; !this.mc.gameSettings.touchscreen && Mouse.next(); this.mc.currentScreen.handleMouseInput())
                {
                    int j1 = Mouse.getEventDWheel();

                    if (j1 != 0)
                    {
                        if (j1 > 0)
                        {
                            j1 = -1;
                        }
                        else if (j1 < 0)
                        {
                            j1 = 1;
                        }

                        this.amountScrolled += (float)(j1 * this.slotHeight / 2);
                    }
                }

                this.initialClickY = -1.0F;
            }
        }

        this.bindAmountScrolled();
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_FOG);
//        Tessellator tessellator = Tessellator.instance;
//        drawContainerBackground(tessellator);
        l1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
        i2 = this.top + 4 - (int)this.amountScrolled + this.headerPadding;

//        if (this.hasListHeader)
//        {
//            this.drawListHeader(l1, i2, tessellator);
//        }

        this.drawSelectionBox(l1, i2, mouseX, mouseY);
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte b0 = 4;

        i3 = this.func_148135_f();

        if (i3 > 0)
        {
            k2 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();

            if (k2 < 32)
            {
                k2 = 32;
            }

            if (k2 > this.bottom - this.top - 8)
            {
                k2 = this.bottom - this.top - 8;
            }

            int l2 = (int)this.amountScrolled * (this.bottom - this.top - k2) / i3 + this.top;

            if (l2 < this.top)
            {
                l2 = this.top;
            }


        }

      //  this.func_148142_b(p_148128_1_, p_148128_2_);
		
		
		
	}
	
	/**
     * Stop the thing from scrolling out of bounds
     */
    private void bindAmountScrolled()
    {
        int i = this.func_148135_f();
        if (i < 0){
            i /= 2;
        }
        if (!this.field_148163_i && i < 0){
            i = 0;
        }
        if (this.amountScrolled < 0.0F)
        	this.amountScrolled = 0.0F;

        if (this.amountScrolled > (float)i)
            this.amountScrolled = (float)i;
    }
	
	
//	@Override
//	public void drawScreen(int mouseX, int mouseY, float depth) {
//		
//	}
	
	@SideOnly(Side.CLIENT)
	public class ConfigHeader implements GuiListExtended.IGuiListEntry {
		
		private final String headerName;
		private final int width;
		
		public ConfigHeader(String name) {
			this.headerName = I18n.format(name, new Object[0]);
			this.width = GuiExperimentConfig.this.mc.fontRenderer.getStringWidth(headerName);
		}

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,
				Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			//Your guess is as good as mine - I am not patient in figuring out those variables.
			//I think p_.._3 and p_.._5 have to do with the y height and scroll position.
			GuiExperimentConfig.this.mc.fontRenderer.drawString(this.headerName,
					GuiExperimentConfig.this.mc.currentScreen.width / 2 - this.width / 2,  
					p_148279_3_ + p_148279_5_ - GuiExperimentConfig.this.mc.fontRenderer.FONT_HEIGHT - 1, 
					Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
		}

		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
				int p_148278_6_) {
			//don't do anything on mouse press.
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	public class ConfigSlider implements GuiListExtended.IGuiListEntry {
		
		private int SLIDER_WIDTH = 100;
		private int RESET_WIDTH = 25;
		private int HEIGHT = GuiExperimentConfig.SLOT_HEIGHT - 2;
		
		private String parameterName;
		private GuiSlider slider;
		private GuiButton reset;
		private double defaultValue;
		
		/**
		 * Create a Config Slider GuiSlot
		 * @param name 			Name of Parameter to be varied
		 * @param defaultValue 	default (server-saved) value of the parameter
		 * @param minVal		minimum Slider Value
		 * @param maxVal		maximum Slider Value
		 */
		public ConfigSlider(String name, double defaultValue, double minVal, double maxVal) {
			this.parameterName = name;
			//new GuiSlider(paramID++, x_pos, y_pos, (int) (this.X_WIDTH*0.75), buttonheight, 
			//key, "", vals[1], vals[2], vals[0], true, true, null); //I18n.format(name, new Object[0])
			this.defaultValue = defaultValue;
			this.slider = new GuiSlider(0, 0, 0, SLIDER_WIDTH, HEIGHT, "", "", minVal, maxVal, defaultValue, false, true, null);
			this.reset = new GuiButton(0, 0, 0, RESET_WIDTH, HEIGHT, "X");
		}

		@Override
		public void drawEntry(int p_148279_1_, int xStart, int yStart, int p_148279_4_, int p_148279_5_,
				Tessellator p_148279_6_, int mouseX, int mouseY, boolean p_148279_9_) {
			// draw each ConfigSlider entity on a row.
			GuiExperimentConfig.this.mc.fontRenderer.drawString(this.parameterName, xStart + 120 - GuiExperimentConfig.this.maxStringLength,
					yStart + p_148279_5_ / 2 - GuiExperimentConfig.this.mc.fontRenderer.FONT_HEIGHT / 2, Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
			this.slider.xPosition = xStart + 105;
			this.slider.yPosition = yStart;
			this.slider.drawButton(GuiExperimentConfig.this.mc, mouseX, mouseY);
			this.reset.enabled = this.slider.getValue() != this.defaultValue; //disable reset if the slider is at its default.
			this.reset.xPosition = xStart + 105 + SLIDER_WIDTH + 5;
			this.reset.yPosition = yStart;
			this.reset.drawButton(GuiExperimentConfig.this.mc, mouseX, mouseY);
			
		}

		@Override
		public boolean mousePressed(int startingSlotID, int mouseX, int mouseY, int b, int c,
				int d) {
			System.out.println("a, b, c, d" + startingSlotID + " " + b + " " + c + " " + d);
			if(this.reset.mousePressed(GuiExperimentConfig.this.mc, mouseX, mouseY)) {
				this.slider.setValue(this.defaultValue);
				this.slider.dispString = String.format("%d", (int)this.defaultValue);
				//reset will be disabled on drawEntry()
				return true;
			} else if(this.slider.mousePressed(GuiExperimentConfig.this.mc, mouseX, mouseY)) {
				//don't need to do any additional functionaity, I don't think?
				return true;
				//return this.slider.mousePressed(GuiExperimentConfig.this.mc, mouseX, mouseY);
			} else {
				return false;
			}
		}

		@Override
		public void mouseReleased(int p_148277_1_, int mouseX, int mouseY, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			this.slider.mouseReleased(mouseX, mouseY);
			this.reset.mouseReleased(mouseX, mouseY);
			
		}
		
	}
		
}
