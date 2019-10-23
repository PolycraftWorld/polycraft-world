package edu.utd.minecraft.mod.polycraft.client.gui.exp.creation;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import scala.swing.event.KeyTyped;

public class GuiExpSteps extends GuiListExtended {
	private final GuiScreen gui;
	private final Minecraft minecraft;
	private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(
			PolycraftMod.getAssetNameString("textures/gui/consent_background.png"));
	
	private final ArrayList<GuiListExtended.IGuiListEntry> devStep;
	private int maxStringLength = 0; //used during rendering to "left-justify" all parameter names
	
	public static Color HEADER_TEXT_COLOR = new Color(155, 155, 155);
	private static int SLOT_HEIGHT = 24;
	
    /**
     * What to multiply the amount you moved your mouse by (used for slowing down scrolling when over the items and not
     * on the scroll bar)
     */
    private float scrollMultiplier;
	private float initialClickY = -2.0F;
	public float amountScrolled;
	private int selectedElement = -1;
	private long lastClicked;
	private ItemDevTool devTool;
	
	public GuiExpSteps(GuiScreen gui, Minecraft mc, ItemDevTool devTool) {
		//see below for the names of those variables.
		super(mc, gui.width - 248, gui.height - 100, 50, gui.height - 50,SLOT_HEIGHT);
	//	super(Minecraft minecraft, int width, int height, int top, int bottomOffset, int slotHeight);
		
		int x_start = (gui.width - 240) / 2;
		int y_start = (gui.height - 184) / 2;
		
		this.devTool = devTool;
		this.left = x_start;
		this.right = gui.width - this.left;
		//System.out.println("Width: , Height, Top, Bottom: " + this.width + " " + this.height + " " + this.top + " " + this.bottom);
		//System.out.println("Left: Right: " + this.left + " " + this.right);
		this.minecraft = mc;
		this.gui = gui;
		this.devStep = new ArrayList<>();
		//this.headerPadding = 0;
		
		if(gui instanceof GuiExpCreator) {
			updateSteps();
		}
		
		
	}
	
	public ArrayList<? extends IGuiListEntry> getChangedItems(){
		ArrayList<GuiListExtended.IGuiListEntry> newItems = new ArrayList<>();
		for(GuiListExtended.IGuiListEntry entry : this.devStep) {
			if(entry instanceof ConfigSlider) {
				ConfigSlider temp = (ConfigSlider) entry;
				if(temp.hasChanged) {
					newItems.add(new ConfigSlider(temp.parameterName, temp.slider.getValue(), 0, 0));
				}
			}
		}
		return newItems;
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
	public GuiExpSteps(Minecraft minecraft, int width, int height, int top, int bottomOffset, int slotHeight) {
		super(minecraft, width, height, top, bottomOffset, slotHeight);
		this.minecraft = minecraft;
		this.gui = null;
		this.devStep = new ArrayList<>();
	}
	
	public void updateSteps() {
		this.devStep.clear();
		//TODO: Make a more sustainable params list.
		this.devStep.add(new ConfigHeader("Stage One"));
		int counter = 0;
		int featureListSize = devTool.getFeatures().size();
		for(TutorialFeature feature: devTool.getFeatures()) {
			this.devStep.add(new ConfigStep(feature, counter, counter==featureListSize-1?true:false));
			counter++;
		}
    	
		
		this.devStep.add(new ConfigHeader("End"));
	}

	@Override
	public IGuiListEntry getListEntry(int index) {
		
		return this.devStep.get(index);
	}

	@Override
	protected int getSize() {
	
		return this.devStep.size();
	}
	
	/**
	 * The default one in GuiSlot uses CONSTANTS!! WHYYYY
	 */
	@Override
	protected int getScrollBarX() {
		// TODO Auto-generated method stub
		return this.right - 10;
	}
	
	/**
	 * The Default one just returns 220... WHY??
	 */
	@Override
	public int getListWidth() {
		// TODO Auto-generated method stub
		return this.right;
	}
	
	@Override
	protected void drawSelectionBox(int xPos, int yPos, int mouseX, int mouseY) {
		int totalRows = this.getSize();
		Tessellator tessellator = Tessellator.getInstance();
		
		for (int rowIterator = 0; rowIterator < totalRows; rowIterator++) {
			int curRow = yPos + rowIterator * this.slotHeight + this.headerPadding +10;
			
			if(curRow < this.bottom - this.slotHeight && curRow > this.top) {
				this.drawSlot(rowIterator, xPos, curRow, 0, mouseX, mouseY);
			}
		}
	}
	
	/**
     * Overlays the background to hide scrolled items
     */
	protected void overlayBackground(int p_148136_1_, int p_148136_2_, int p_148136_3_, int p_148136_4_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        this.minecraft.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        //TODO: update to 1.8
//
//        tessellator.startDrawingQuads();
//        tessellator.setColorRGBA_I(4210752, p_148136_4_);
//        tessellator.addVertexWithUV((double)this.left, (double)p_148136_2_, 0.0D, 0.0D, (double)((float)p_148136_2_ / f));
//        tessellator.addVertexWithUV((double)(this.left + this.width), (double)p_148136_2_, 0.0D, (double)((float)this.width / f), (double)((float)p_148136_2_ / f));
//        tessellator.setColorRGBA_I(4210752, p_148136_3_);
//        tessellator.addVertexWithUV((double)(this.left + this.width), (double)p_148136_1_, 0.0D, (double)((float)this.width / f), (double)((float)p_148136_1_ / f));
//        tessellator.addVertexWithUV((double)this.left, (double)p_148136_1_, 0.0D, 0.0D, (double)((float)p_148136_1_ / f));
        tessellator.draw();
    }
    
//    @Override
//    public void drawBackground() {
//    	
//    }
    
    /**
     * Updated Draw Screen Function that uses the Scroll Handler in {@link GuiExperimentList}
     * to draw items on the screen. Double-click functionality IS enabled in this handler
     * @param mouseX
     * @param mouseY
     * @param mouseAction
     * @param scrollPos value from 0.0 to 1.0 that indicates the current scroll position
     */
    public void drawScreenHandler(int mouseX, int mouseY, float mouseAction, float scrollPos) {
    	this.mouseX = mouseX;
    	this.mouseY = mouseY;
    	
    	//lets handle scrolling from the above function.
    	this.amountScrolled = scrollPos * this.getContentHeight();
    	this.bindAmountScrolled();
    	
    	int k = this.getSize();
    	int l1;
        int i2;
        int k2;
        int i3;
    	
        //Handle Mouse Input by the user.
    	if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom)
        {
    		//check to see if the mouse has been clicked.
            if (Mouse.isButtonDown(0) && this.getEnabled())
            {
            	//check to see where the mouse is.
            	 if (mouseY >= this.top && mouseY <= this.bottom)
                 {
            		 //get specific parameters to determine the element the mouse is hovering over,
            		 //based on the current scroll amount and the slot heights
                     int k1 = this.width / 2 - this.getListWidth() / 2;
                     l1 = this.width / 2 + this.getListWidth() / 2;
                     i2 = mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                     int j2 = i2 / this.slotHeight;
                     
                     //If the mouse is over a given slot element, then register the click
                     if (mouseX >= k1 && mouseX <= l1 && j2 >= 0 && i2 >= 0 && j2 < k)
                     {
                    	 
                    	 //check to see if the user doubled clicked (flag is True if it's a double click)
                         boolean flag = j2 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                         this.elementClicked(j2, flag, mouseX, mouseY); //click the element.
                         
                       //set up parameters to determine if it's a double click
                         this.selectedElement = j2; 
                         this.lastClicked = Minecraft.getSystemTime();
                     }
                     
                     //not really sure what this does, of if it's even needed. This may be a deprecated function
                     //from DrawScreen (see below).
                     else if (mouseX >= k1 && mouseX <= l1 && i2 < 0)
                     {
                         this.func_148132_a(mouseX - k1, mouseY - this.top + (int)this.amountScrolled - 4);
                         //flag1 = false;
                     }
                 }
            }
        }
    	
    	
    	GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        l1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
        i2 = this.top + 4 - (int)this.amountScrolled;


        //draw the elements based on the screen
        this.drawSelectionBox(l1, i2, mouseX, mouseY);
        
        //Handle mouse-over on Elements on the screen
        //TODO: implemment tool-tips that pop-up when a user hovers their mouse here!
        this.func_148142_b(mouseX, mouseY);
    	
    	
    }
    
	
    /**
     * TODO: Safely delete this function after ensuring we don't need ANY of the information here
     */
	@Deprecated
	public void drawScreen(int mouseX, int mouseY, float p_148128_3_)
    {
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

        //TODO: use GuiExperimentList's scrolling handler instead of GuiSlot to determine which slots to draw.
        //NOTE: this also handles click and drag, and element double-click. Maybe this should be handled in the parent class (i.e., GuiExperimentList?)
        //Would we want a double-click action? If so, we would need to utilize this logic.
        
        if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom)
        {
            if (Mouse.isButtonDown(0) && this.getEnabled())
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
            	try {
					for (; !this.minecraft.gameSettings.touchscreen && Mouse.next(); this.minecraft.currentScreen.handleMouseInput())
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
				}catch (IOException e){
            		e.printStackTrace();
				}

            }
        }

        this.bindAmountScrolled();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.getInstance();
        //drawContainerBackground(tessellator);
        l1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
        i2 = this.top + 4 - (int)this.amountScrolled;


        this.drawSelectionBox(l1, i2, mouseX, mouseY);
       // GL11.glDisable(GL11.GL_DEPTH_TEST);
       // byte b0 = 4;
       // this.drawBackground();
//        GL11.glEnable(GL11.GL_BLEND);
//        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
//        GL11.glDisable(GL11.GL_ALPHA_TEST);
//        GL11.glShadeModel(GL11.GL_SMOOTH);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);

        this.func_148142_b(mouseX, mouseY);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glShadeModel(GL11.GL_FLAT);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
    }
	
		
	@Override
	protected void drawBackground() {
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(this.BACKGROUND_IMAGE);
	}
	
	
	
	@Override
	public int getSlotIndexFromScreenCoords(int mouseX, int mouseY)
    {
        int k = this.left + this.width / 2 - this.getListWidth() / 2;
        int l = this.left + this.width / 2 + this.getListWidth() / 2;
        int i1 = mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int j1 = i1 / this.slotHeight;
        return mouseX < this.getScrollBarX() && mouseX >= k && mouseX <= l && j1 >= 0 && i1 >= 0 && j1 < this.getSize() ? j1 : -1;
    }
	
	/**
     * Returns the amountScrolled field as an integer.
     */
	@Override
    public int getAmountScrolled()
    {
        return (int)this.amountScrolled;
    }
	
	/**
	 * Stop the thing from scrolling out of bounds
	 */
	protected void bindAmountScrolled()
	{
	    int i = this.func_148135_f();
	
	    if (i < 0)
	    {
	        i /= 2;
	    }
	
	    if (!this.field_148163_i && i < 0)
	    {
	        i = 0;
	    }
	
	    if (this.amountScrolled < 0.0F)
	    {
	        this.amountScrolled = 0.0F;
	    }
	
	    if (this.amountScrolled > (float)i)
	    {
	        this.amountScrolled = (float)i;
	    }
	}

    /**
     * Return the height of the content being scrolled
     */
	@Override
    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }
	
	
	/**
	 * Get the amount of scrolling needed (passed to GuiExperimentList)
	 * @return
	 */
	public int getExtraScrollSpace() {
		int test;
		test = this.getContentHeight();
		test = (int) Math.ceil((test - this.height)/this.getSlotHeight());
		
		return test;
	}
	
	public int getNumSteps() {
		return devStep.size();
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char c, int p) {
		for(IGuiListEntry entry: devStep) {
			if(entry instanceof ConfigStep) {
				((ConfigStep)entry).keyTyped(c, p);
			}
		}
	}
	

	/**
	 * This is a ConfigHeader that draws centered text on the screen that describes a section
	 * of Configurator commands.
	 * @author dxn140130
	 *
	 */
	@SideOnly(Side.CLIENT)
	public class ConfigHeader implements GuiListExtended.IGuiListEntry {
		
		private final String headerName;
		private final int width;
		
		public ConfigHeader(String name) {
			this.headerName = I18n.format(name, new Object[0]);
			this.width = GuiExpSteps.this.minecraft.fontRendererObj.getStringWidth(headerName);
		}

		@Override
		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

		}

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int yStart, int p_148279_4_, int p_148279_5_,
				int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			//Your guess is as good as mine - I am not patient in figuring out those variables.
			//I think p_.._3 and p_.._5 have to do with the y height and scroll position.
			GuiExpSteps.this.minecraft.fontRendererObj.drawString(this.headerName,
					GuiExpSteps.this.minecraft.currentScreen.width / 2 - this.width / 2,  
					yStart + GuiExpSteps.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2, 
					Format.getIntegerFromColor(new Color(0,0,0)));
			
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
	
	/**
	 * This describes a generic ConfigSlider object that contains 3 elements: 
	 * A String {@link #parameterName}, a Slider with Associated Bounds {@link #slider}, and a 
	 * button to reset the slider {@link #reset}
	 * 
	 * This object is created from the {link ExperimentParameter} object that is passed into this
	 * object by GuiExperimentList, depending on what experiment the user selects.
	 * @author dxn140130
	 *
	 */
	@SideOnly(Side.CLIENT)
	public class ConfigSlider implements GuiListExtended.IGuiListEntry {
		
		public boolean hasChanged = false;
		
		private int SLIDER_WIDTH = 90;
		private int RESET_WIDTH = 20;
		private int HEIGHT = GuiExpSteps.SLOT_HEIGHT - 2;
		
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
			this.reset = new GuiButton(0, 0, 0, RESET_WIDTH, HEIGHT, "\u23ce");
			this.parameterName = GuiExpSteps.this.minecraft.fontRendererObj.trimStringToWidth(this.parameterName, GuiExpSteps.this.width/2 + 5);
		}

		@Override
		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
			
		}

		@Override
		public void drawEntry(int p_148279_1_, int what, int yStart, int p_148279_4_, int p_148279_5_,
				int mouseX, int mouseY, boolean p_148279_9_) {
			// draw each ConfigSlider entity on a row.
			int xStart = GuiExpSteps.this.left;
			GuiExpSteps.this.minecraft.fontRendererObj.drawString(this.parameterName, xStart,
					yStart + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2, Format.getIntegerFromColor(new Color(90, 90, 90)));
			
			
			//GuiExperimentConfig.this.minecraft.fontRendererObj.drawString(this.parameterName, xStart + 120 - GuiExperimentConfig.this.maxStringLength,
			//		yStart + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRendererObj.FONT_HEIGHT / 2, Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
			int x_offset = 95;
			
			this.slider.xPosition = xStart + x_offset;
			this.slider.yPosition = yStart - GuiExpSteps.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.slider.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			this.reset.enabled = this.slider.getValue() != this.defaultValue; //disable reset if the slider is at its default.
			this.reset.xPosition = xStart + x_offset + SLIDER_WIDTH + 5;
			this.reset.yPosition = yStart - GuiExpSteps.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.reset.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			
		}

		@Override
		public boolean mousePressed(int startingSlotID, int mouseX, int mouseY, int b, int c,
				int d) {
			//System.out.println("Slider Changed: " + this.parameterName);
			if(this.reset.mousePressed(GuiExpSteps.this.minecraft, mouseX, mouseY)) {
				this.slider.setValue(this.defaultValue);
				//this.slider.dispString = String.format("%d", (int)this.defaultValue);
				this.slider.displayString = String.format("%d", (int)this.defaultValue);
				this.reset.enabled = false;
				this.hasChanged = false;
				//reset will be disabled on drawEntry()
				return true;
			} else if(this.slider.mousePressed(GuiExpSteps.this.minecraft, mouseX, mouseY)) {
				//don't need to do any additional functionaity, I don't think?
				this.hasChanged = true;
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
		
		public String getName() {
			return this.parameterName;
		}
		
		public double getSelectedValue() {
			return this.defaultValue;
		}
		
	}
	
	/**
	 * This describes a generic ConfigSlider object that contains 3 elements: 
	 * A String {link #parameterName}, a Slider with Associated Bounds {link #slider}, and a
	 * button to reset the slider {link #reset}
	 * 
	 * This object is created from the {link ExperimentParameter} object that is passed into this
	 * object by GuiExperimentList, depending on what experiment the user selects.
	 * @author dxn140130
	 *
	 */
	@SideOnly(Side.CLIENT)
	public class ConfigStep implements GuiListExtended.IGuiListEntry {
		
		public boolean hasChanged = false;
		
		private int SLIDER_WIDTH = 90;
		private int B_WIDTH = 20;
		private int HEIGHT = GuiExpSteps.SLOT_HEIGHT - 4;
		private int index;
		private TutorialFeature feature;
		private GuiTextField text;
		private String stepName;
		private GuiButton config, moveUp, moveDown, delete;
		private BlockPos pos;
		private boolean isFirst, isLast, editName;
		
		/**
		 * Create a Config Slider GuiSlot
		 */
		public ConfigStep(TutorialFeature feature, int index, boolean isLast) {
			this.feature = feature;
			this.stepName = feature.getName();
			//new GuiSlider(paramID++, x_pos, y_pos, (int) (this.X_WIDTH*0.75), buttonheight, 
			//key, "", vals[1], vals[2], vals[0], true, true, null); //I18n.format(name, new Object[0])
			this.pos = feature.getPos();

			this.config = new GuiButton(1, 0, 0, B_WIDTH, HEIGHT, "\u2699");
			this.moveUp = new GuiButton(1, 0, 0, B_WIDTH, HEIGHT, "\u21e7");
			this.moveDown = new GuiButton(2, 0, 0, B_WIDTH, HEIGHT, "\u21e9");
			this.delete = new GuiButton(3, 0, 0, B_WIDTH, HEIGHT, "\u00A74X");
			this.isFirst = index == 0? true:false;
			this.isLast = isLast;
			this.index = index;
			text =  new GuiTextField( 400, GuiExpSteps.this.minecraft.fontRendererObj, GuiExpSteps.this.left + 2, 8, SLIDER_WIDTH, GuiExpSteps.SLOT_HEIGHT - 12);
			text.setMaxStringLength(32);
			text.setText(feature.getName());
			text.setTextColor(16777215);
			text.setVisible(true);
			text.setCanLoseFocus(false);
			text.setFocused(true);
			this.editName = false;
		}

		@Override
		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

		}

		@Override
		public void drawEntry(int p_148279_1_, int what, int yStart, int p_148279_4_, int p_148279_5_,
				int mouseX, int mouseY, boolean p_148279_9_) {
			// draw each ConfigSlider entity on a row.
			int xStart = GuiExpSteps.this.left;
			GuiExpSteps.this.gui.drawRect(xStart, yStart - 10, xStart + SLIDER_WIDTH + B_WIDTH * 4 + 48, yStart + GuiExpSteps.SLOT_HEIGHT - 12, 0x50303030);
			
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			GuiExpSteps.this.minecraft.fontRendererObj.drawString(Integer.toString((int)this.pos.getX()) + ", " + (int)this.pos.getY() + ", " + (int)this.pos.getZ(), xStart*2 + 4,
					(yStart + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2 + 10) *2, Format.getIntegerFromColor(new Color(90, 90, 90)));
			GL11.glScalef(2F, 2F, 2F);
			//GuiExperimentConfig.this.minecraft.fontRendererObj.drawString(this.parameterName, xStart + 120 - GuiExperimentConfig.this.maxStringLength,
			//		yStart + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRendererObj.FONT_HEIGHT / 2, Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
			int x_offset = 35;
			
			if(isFirst)
				this.moveUp.enabled = false;
			if(isLast)
				this.moveDown.enabled = false;
			
			this.config.xPosition = xStart + x_offset + SLIDER_WIDTH + 5;
			this.config.yPosition = yStart - (GuiExpSteps.this.SLOT_HEIGHT-2)/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.config.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			this.moveUp.xPosition = this.config.xPosition + B_WIDTH + 2;
			this.moveUp.yPosition = yStart - (GuiExpSteps.this.SLOT_HEIGHT-2)/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.moveUp.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			this.moveDown.xPosition = this.moveUp.xPosition + B_WIDTH + 2;
			this.moveDown.yPosition = yStart - (GuiExpSteps.this.SLOT_HEIGHT-2)/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.moveDown.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			this.delete.xPosition = this.moveDown.xPosition + B_WIDTH + 2;
			this.delete.yPosition = yStart - (GuiExpSteps.this.SLOT_HEIGHT-2)/4 + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2;
			this.delete.drawButton(GuiExpSteps.this.minecraft, mouseX, mouseY);
			if(!editName) {
				GuiExpSteps.this.minecraft.fontRendererObj.drawString(feature.getName(), xStart + 2,
						yStart + p_148279_5_ / 2 - GuiExpSteps.this.minecraft.fontRendererObj.FONT_HEIGHT / 2 - 2, Format.getIntegerFromColor(new Color(90, 90, 90)));
			}else {
				text.yPosition = yStart - 8;
				
				text.drawTextBox();
			}
		}
		
		
		/**
		 * Fired when a key is typed. This is the equivalent of
		 * KeyListener.keyTyped(KeyEvent e).
		 */
		protected void keyTyped(char c, int p) {
			if (editName)
				text.textboxKeyTyped(c, p);
		}

		@Override
		public boolean mousePressed(int startingSlotID, int mouseX, int mouseY, int b, int c,
				int d) {
			//System.out.println("Slider Changed: " + this.parameterName);
			if(this.delete.mousePressed(GuiExpSteps.this.minecraft, mouseX, mouseY)) {
				GuiExpSteps.this.devTool.removeFeatures(this.index);
				GuiExpSteps.this.updateSteps();
				this.hasChanged = true;
				return true;
			} else if(this.moveUp.mousePressed(GuiExpSteps.this.minecraft, mouseX, mouseY)) {
				GuiExpSteps.this.devTool.swapFeatures(this.index, this.index - 1);
				GuiExpSteps.this.updateSteps();
				this.hasChanged = true;
				return true;
				//return this.slider.mousePressed(GuiExperimentConfig.this.mc, mouseX, mouseY);
			} else if(this.moveDown.mousePressed(GuiExpSteps.this.minecraft,  mouseX,  mouseY)){
				GuiExpSteps.this.devTool.swapFeatures(this.index, this.index + 1);
				GuiExpSteps.this.updateSteps();
				this.hasChanged = true;
				return true;
			}else if(this.config.mousePressed(GuiExpSteps.this.minecraft,  mouseX,  mouseY)){
				if(GuiExpSteps.this.gui instanceof GuiExpCreator)
					((GuiExpCreator)GuiExpSteps.this.gui).editFeature(feature);
				
				return true;
			}
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int mouseX, int mouseY, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			this.delete.mouseReleased(mouseX, mouseY);
			this.moveUp.mouseReleased(mouseX, mouseY);
			this.moveDown.mouseReleased(mouseX, mouseY);
		}
		
		public String getName() {
			return this.stepName;
		}
		
	}
		
}
