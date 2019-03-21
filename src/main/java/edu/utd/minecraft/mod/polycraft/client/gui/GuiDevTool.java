package edu.utd.minecraft.mod.polycraft.client.gui;

import java.awt.Color;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiSlider;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentConfig.ConfigSlider;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.ExperimentsPacketType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import scala.swing.event.MouseReleased;

public class GuiDevTool extends PolycraftGuiScreenBase {
	private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation background_image = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/consent_background.png"));
    private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
    
    private static final String __OBFID = "CL_00000691";
    private EntityPlayer player;
    private ItemDevTool devTool;	//devTool is where all working data is stored
    private int x, y, z;
    private int screenID; //current screen
    private static final String TITLE = "Polycraft DEV TOOL";
    private final int screenContainerWidth = 230;
    private final int screenContainerHeight = 130;
    private boolean wasClicking; 
    private int buttonCount = 1;
    private int buttonheight = 20;
    private int button_padding_y = 4;
    private float scroll = 0.0F; // Amount of scroll, from 0.0 to 1.0 inclusive.
	private boolean scrolling; // True if the scroll bar is being dragged.
	private int ylines; // The number of buttons/lines the text space can accommodate.
	private int extraLines; // How many buttons/lines are overflowing from the alloted text space.
	private long lastEventNanoseconds = 0;	//lastEventNanoseconds used to prevent the same mouse click causing multiple actions
	private TutorialFeature featureToAdd;
	private TutorialFeature.TutorialFeatureType featureToAddType;
	
	public final int SCROLL_HEIGHT = 151;
	private final int X_PAD = 10;
	// private static final int X_WIDTH = 248 - 10 * 2;
	// private static final int X_WIDTH_SCROLL = X_WIDTH - 22;
	public final int X_WIDTH = 206; // X_WIDTH_SCROLL
	public final int Y_HEIGHT = 126;
	private final int Y_PAD = 8;
	private int titleHeight;
	
	//Navigation buttons
	GuiButton btnBack, btnNext;
    private ArrayList<GuiButton> experimentsListButton = new ArrayList<GuiButton>();
    private ArrayList<GuiButton> configButtons = new ArrayList<GuiButton>();
    //Add Step buttons
    GuiButton btnAddStepType;
    public ArrayList<GuiTextField> textFields = new ArrayList<GuiTextField>();
    public ArrayList<GuiPolyLabel> labels = new ArrayList<GuiPolyLabel>();
    public ArrayList<GuiPolyButtonCycle> options = new ArrayList<GuiPolyButtonCycle>();
	private GuiDevToolStep guiSteps;
	
    
    private enum WhichScreen {
    		DEV_MAIN,
    		DEV_STEPS,
    		DEV_DETAIL,
    		DEV_STEP_EDIT,
    		DEV_ADD_STEP
    }
    
    private String userFeedbackText = "";
    private WhichScreen screenSwitcher = WhichScreen.DEV_MAIN;
    
    public GuiDevTool(EntityPlayer player, int x, int y, int z) {
        System.out.print("gui ExperimentList constructor.\n x, y, z: " + x + " " + y + " " + z);
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.screenID = 0;
        this.titleHeight = 20;
        this.guiSteps = null;
        
    }
   
    /**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiDevTool(EntityPlayer player) {
        this.player = player;
        this.screenID = 0;
        this.titleHeight = 20;
        this.guiSteps = null;
       	if(player.getHeldItem().getItem() instanceof ItemDevTool)
       		this.devTool = (ItemDevTool) player.getHeldItem().getItem();
    }
    
    /**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiDevTool(EntityPlayer player, ItemDevTool devTool) {
        this.player = player;
        this.screenID = 0;
        this.titleHeight = 20;
        this.guiSteps = new GuiDevToolStep(this, this.mc, devTool);
        this.devTool = devTool;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. This is run every time the screen 
     * needs to be rendered.
     */
    public void initGui()
    {
    	
    	guiSteps = new GuiDevToolStep(this, this.mc, devTool);

    	int x_pos = (this.width - 248) / 2 + X_PAD; //magic numbers from Minecraft. 
        int y_pos = (this.height - 198) / 2 + this.titleHeight; //magic numbers from minecraft
    	//Navigation buttons
        btnBack = new GuiButton(1, x_pos, y_pos + Y_HEIGHT + 2*Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "Close");
        btnNext = new GuiButton(2, x_pos + X_WIDTH/2 + X_PAD/2, y_pos + Y_HEIGHT + 2*Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "next");
        //add step buttons
        btnAddStepType = new GuiButton(10000+buttonCount++, x_pos+10, y_pos+4, (int) (X_WIDTH * .9), buttonheight, "Type: " + TutorialFeatureType.GENERIC.name());
        
        for (ItemDevTool.StateEnum option : ItemDevTool.StateEnum.values()) {
    		//Add config button:
    		GuiButton tempConfig = new GuiButton(10000+buttonCount++, x_pos+10, y_pos+4, (int) (X_WIDTH * .9), buttonheight, option.name());
        	//GuiButton temp = new GuiButton(buttonCount++, x_pos + 5*X_PAD / 4 + tempConfig.width, y_pos, (int) (X_WIDTH * .65), buttonheight, emd.expName);
        	y_pos+=(buttonheight + button_padding_y);
        	experimentsListButton.add(tempConfig);
        	//experimentsListButton.add(temp);
        }
        
        //start off in the right screen and run this before displaying
        screenSwitcher = this.screenChange(WhichScreen.DEV_MAIN);
        
   }
    
    /**
     * Handle Key Presses by the user
     * Superclass handles the escape key
     * the keys are ordered from top left to bottom on the keyboard (1 = esc, 2 = 1, 3=2, etc... backspace=14, tab=15, q = 16, etc...)
     * Capital keys (Shift/capslock) will change the char, but not the int
     * for now, if user presses r when inside that window, it will close.
     */
    @Override
    public void keyTyped(char c, int p) {
    	super.keyTyped(c, p);
    	if(screenSwitcher == WhichScreen.DEV_STEPS)
    			this.guiSteps.keyTyped(c, p);
    	else if(screenSwitcher == WhichScreen.DEV_ADD_STEP) {
    		for(GuiTextField textField: textFields) {
    			if(textField.isFocused())
    				textField.textboxKeyTyped(c, p);
    		}
    	}else if(screenSwitcher == WhichScreen.DEV_STEP_EDIT) {
    		for(GuiTextField textField: textFields) {
    			if(textField.isFocused())
    				textField.textboxKeyTyped(c, p);
    		}
    	}
    }
    
   
    
    @Override
	protected void exitGuiScreen() {
		// TODO Auto-generated method stub
    	this.mc.displayGuiScreen((GuiScreen)null);
        this.mc.setIngameFocus();
		
	}

	@Override
    protected void mouseClicked(int x, int y, int mouseEvent) {
    	
    	if(this.guiSteps != null) {
    		this.guiSteps.func_148179_a(x, y, mouseEvent);
    	}
    	for(GuiTextField textField: textFields) {
    		textField.mouseClicked(x, y, mouseEvent);
    	}
    	super.mouseClicked(x, y, mouseEvent);
    }
    
    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    @Override
    protected void mouseMovedOrUp(int x, int y, int mouseEvent)
    {
    	//super.mouseMovedOrUp(x, y, mouseEvent);
    	if(this.guiSteps != null) {
    		
    		if (mouseEvent != 0 || !this.guiSteps.func_148181_b(x, y, mouseEvent))
    		{
    			super.mouseMovedOrUp(x, y, mouseEvent);
    		}
		}else {
			super.mouseMovedOrUp(x, y, mouseEvent);
		}
            
    }
    

    /**
     * Override functionality on base class
     * This is what is triggered if any interaction happens on the Gui. 
     * This handles the various cases where a user wants to select an experiment and join it or withdraw
     * from that queue.
     * All user requests are currently relayed to the server. We can place a cooldown (in the future) that
     * prevents a single user from continually switching between experiments. Right now, button is disabled after a user requests to join an experiment.
     * we don't have enough users for this to be a problem
     * TODO: Make this more idiot proof and prevent click-spamming. 
     */
    protected void actionPerformed(GuiButton button) {
    	super.actionPerformed(button);
    	
    	if(Mouse.getEventNanoseconds()==lastEventNanoseconds) {
    		return;
    	}else {
    		lastEventNanoseconds = Mouse.getEventNanoseconds();
    	}
    	
    	//int x_pos = (this.width - 248) / 2 + 10;
    	//player.addChatMessage(new ChatComponentText("Selected Experiment: " + button.displayString));
    	//userFeedbackText = "You are in queue for: " + button.displayString;
    	int btnID = button.id;
    	
    	
    	switch(btnID) {
    	case 1:
    		//user pressed back button
    		switch(screenSwitcher) {
    		case DEV_MAIN:			//close the screen
    			this.exitGuiScreen();
    			break;
    		case DEV_STEPS:			//go back to main screen
    			screenSwitcher = screenChange(WhichScreen.DEV_MAIN);
    			break;
    		case DEV_ADD_STEP: 		//go back to steps screen
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		case DEV_STEP_EDIT:	//go back to steps screen
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		default:
    			break;
    		}
    		break;
    	case 2:
    		//user pressed next button
    		switch(screenSwitcher) {
    		case DEV_MAIN:			//go to steps screen
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		case DEV_STEPS:			//go to add step screen
    			screenSwitcher = screenChange(WhichScreen.DEV_ADD_STEP);
    			break;
    		case DEV_ADD_STEP: 		//save the new step and go back to steps screen
    			featureToAdd.updateValues();
    			devTool.addFeature(featureToAdd);
    			guiSteps.updateSteps();
				devTool.updateRenderBoxes();
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		case DEV_STEP_EDIT:	//save the step and go back to steps screen
    			featureToAdd.updateValues();
				devTool.updateRenderBoxes();
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		default:
    			break;
    		}
    		break;
    	default:
    		switch(screenSwitcher) {
    		case DEV_MAIN:			//close the screen
    			if(button.id > 10000) {
    	    		for(GuiButton gbtn : experimentsListButton) {
    	    			if(gbtn.id == btnID) {
    	    	    		String expID = gbtn.displayString;
    	    	    		for(ItemDevTool.StateEnum state: ItemDevTool.StateEnum.values()) {
    	    	    			if(expID.equals(state.name())) {
    	    	    				devTool.setState(expID);
    	    	    			}
    	    	    		}
    	    				return;
    	    			}
    	    		}
    	    	}
    			break;
    		case DEV_STEPS:			//Dev_steps buttons
    			//do nothing?
    			break;
    		case DEV_ADD_STEP: 		//go back to steps screen
    			if(button.id == btnAddStepType.id) {
    	    		featureToAddType = featureToAddType.next();
    	    		btnAddStepType.displayString = "Type: " + featureToAddType.name();
    	    		buildAddStepInputs(true);
    	    	}
    			if(button instanceof GuiPolyButtonCycle<?>) {
    				((GuiPolyButtonCycle<?>)button).nextOption();
    			}
    			break;
    		case DEV_STEP_EDIT:	//go back to steps screen
    			screenSwitcher = screenChange(WhichScreen.DEV_STEPS);
    			break;
    		default:
    			break;
    		}
			break;
    	}
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float otherValue)
    {
    	
    	// Get the position of the top left pixel.
		int x_start = (this.width - 248) / 2;
		int y_start = (this.height - 184) / 2;
    	
		
		// Operate the scroll bar.
		boolean flag = Mouse.isButtonDown(0);
		int i1 = x_start + 226; // x and y of scroll bar.
		int j1 = y_start + 8;
		int k1 = i1 + 14;
		int l1 = j1 + SCROLL_HEIGHT;
		
		
		// Start scrolling check.
		if (!this.wasClicking && flag && mouseX >= i1 && mouseY >= j1 && mouseX < k1 && mouseY < l1 + 17)
			this.scrolling = extraLines > 0;
		// Stop scrolling check.
		if (!flag)
			this.scrolling = false;
		// Set history of mouse clicking.
		this.wasClicking = flag;
		// Set scroll in progress.
		if (this.scrolling) {
			this.scroll = ((float) (mouseY - j1) - 7.5F) / ((float) (l1 - j1));
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}

    	
        this.drawDefaultBackground();
        switch(screenSwitcher) {
        	case DEV_MAIN:
        		drawDevMainScreen();
        		break;
        	case DEV_STEPS:
        		this.extraLines = this.guiSteps.getExtraScrollSpace();
        		this.guiSteps.drawScreenHandler(mouseX, mouseY, otherValue, this.scroll);
        		drawDevStepsScreen();
        		break;
        	case DEV_ADD_STEP:
        		drawAddStepScreen();
        		break;
        	case DEV_STEP_EDIT:
        		drawEditStepScreen();
        		break;
        	default:
        		//Do Nothing
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SCROLL_TAB);
		// The scroll bar sits at (226, 8) but the border is 1 wide so the scroll
		// indicator really starts at (227, 9).
		this.drawTexturedModalRect(x_start + 227, y_start + 9 + (int) (this.scroll * SCROLL_HEIGHT),
				232 + (extraLines > 0 ? 0 : 12), 0, 12, 15);
        
        super.drawScreen(mouseX, mouseY, otherValue);
    }
    
    public void drawDevMainScreen() {
		//the below "magic numbers" come from Minecraft's Demo files.
	    //I'm using them unless there's better ideas in the future
	    //TODO: understand why these are the correct magic numbers
	    int x_pos = (this.width - 248) / 2 + 10;
	    int y_pos = (this.height - 190) / 2 + 8;
	    this.fontRendererObj.drawString(I18n.format(TITLE, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
	    y_pos += 12;
	    this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.X_WIDTH, y_pos + this.Y_HEIGHT, 0x50A0A0A0);
	    this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
	    y_pos += buttonheight - button_padding_y - this.fontRendererObj.FONT_HEIGHT;
	    
	    for(GuiButton btn: (List<GuiButton>)buttonList) {
	    	if(btn.id > 10000) {
	    		if(btn.displayString.equalsIgnoreCase(devTool.getState().toString()))
	    			btn.enabled = false;
	    		else
	    			btn.enabled = true;
	    	}
	    }
	    
	}

	private void drawDevStepsScreen() {
    	//get top left of screen with padding.
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 192) / 2 + Y_PAD;
        this.drawRect(x_pos - 7, y_pos - 1, x_pos + this.X_WIDTH +7, y_pos + 20, 0xFFc5c5c5);
        this.fontRendererObj.drawString(I18n.format("Dev Steps", new Object[0]),
        		x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 20;
        this.drawRect(x_pos - 7, y_pos + this.Y_HEIGHT, x_pos + this.X_WIDTH +7, y_pos + this.Y_HEIGHT + 30, 0xFFc5c5c5);
        
	}

    public void drawAddStepScreen() {
    	//this.resetButtonList();
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("Add new Step", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //draw background rectangle
        int offset = 2;
        int offset2 = 1;
        this.drawRect(x_pos - 2, y_pos - offset +24, x_pos + this.X_WIDTH + 2, y_pos + this.Y_HEIGHT + 2, Format.getIntegerFromColor(new Color(128, 128, 128)));
        this.drawRect(x_pos - offset2, y_pos - offset2 +24, x_pos + this.X_WIDTH + offset2, y_pos + this.Y_HEIGHT + offset2, Format.getIntegerFromColor(new Color(200, 200, 200)));
        //IMPORTANT: user feedback text goes here
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
        
        for(GuiTextField textField: textFields) {
        	textField.drawTextBox();
        }
        for(GuiPolyLabel label: labels) {
        	label.drawLabel();
        }
        
    }
    
    public void drawEditStepScreen() {
    	//this.resetButtonList();
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("Edit Step", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //draw background rectangle
        int offset = 2;
        int offset2 = 1;
        this.drawRect(x_pos - 2, y_pos - offset +24, x_pos + this.X_WIDTH + 2, y_pos + this.Y_HEIGHT + 2, Format.getIntegerFromColor(new Color(128, 128, 128)));
        this.drawRect(x_pos - offset2, y_pos - offset2 +24, x_pos + this.X_WIDTH + offset2, y_pos + this.Y_HEIGHT + offset2, Format.getIntegerFromColor(new Color(200, 200, 200)));
        //IMPORTANT: user feedback text goes here
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
        
        for(GuiTextField textField: textFields) {
        	textField.drawTextBox();
        }
        for(GuiPolyLabel label: labels) {
        	label.drawLabel();
        }
        
    }
    
    
    private WhichScreen screenChange(WhichScreen newScreen) {
    	//On screen change, we need to update the button list and have it re-drawn.

		this.resetButtonList();
    	switch(newScreen) {
    		case DEV_MAIN:
    			btnBack.displayString = "Close";
    			btnNext.displayString = "Steps";
    			this.buttonList.addAll(this.experimentsListButton);
    			ylines = (Y_HEIGHT - titleHeight) / (this.buttonheight+5);
    			extraLines = this.experimentsListButton.size() - ylines;
    			break;
    		case DEV_STEPS:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Add Step";
    			ylines = (Y_HEIGHT - titleHeight) / (14);
    			extraLines = guiSteps.getSize() - ylines;
    			break;
    		case DEV_ADD_STEP:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Add Step";
    			featureToAddType = TutorialFeatureType.GENERIC;
    			buildAddStepInputs(true);
    			break;
    		case DEV_STEP_EDIT:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Save Step";
    			buildAddStepInputs(false);
    			break;
    		default:
    			break;
    	}
    	this.buttonList.add(btnBack);
        this.buttonList.add(btnNext);
    	this.scroll = 0F;
    	return newScreen;
    }
    
    public void editFeature(TutorialFeature feature) {
    	featureToAdd = feature;
    	featureToAddType = feature.getFeatureType();
    	screenSwitcher = screenChange(WhichScreen.DEV_STEP_EDIT);
    }
    
    
    /**
     * Build all buttons and textboxes for adding specific steps/features
     * */
    private void buildAddStepInputs(boolean addNew) {
    	boolean initRun = true;	//We only want to run some code once, because main buttons are already added when this runs the first time
    	if(screenSwitcher != WhichScreen.DEV_ADD_STEP) {
    		initRun = true;
    	}
		if(initRun)
    		this.resetButtonList();
		this.textFields.clear();
		this.labels.clear();
		GuiTextField textFieldtemp;
		GuiPolyNumField numFieldtemp;
		int x_pos = (this.width - 248) / 2 + X_PAD; //magic numbers from Minecraft. 
        int y_pos = (this.height - 198) / 2 + this.titleHeight; //magic numbers from minecraft
		switch(featureToAddType) {
		case GENERIC:
	        if(addNew)
	        	featureToAdd = new TutorialFeature("Feature " + devTool.getFeatures().size(), 
	        		Vec3.createVectorHelper(player.posX, player.posY, player.posZ),Color.GREEN);
	        featureToAdd.buildGuiParameters(this, x_pos, y_pos);
			break;
		case GUIDE:
			if(addNew)
	        	featureToAdd = new TutorialFeatureGuide("Feature " + devTool.getFeatures().size(), 
	        		Vec3.createVectorHelper(player.posX, player.posY, player.posZ),
	        		Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
	        featureToAdd.buildGuiParameters(this, x_pos, y_pos);
			break;
		case INSTRUCTION:
			textFieldtemp = new GuiTextField(this.fontRendererObj, x_pos + 5, y_pos + 30, (int) (X_WIDTH * .9), 14);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText("Name of Feature");
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(true);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Pos"));
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"X:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 40, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.posX));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Y:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 95, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.posY));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Z:"));
	        numFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 150, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        numFieldtemp.setMaxStringLength(32);
	        numFieldtemp.setText(Integer.toString((int)player.posZ));
	        numFieldtemp.setTextColor(16777215);
	        numFieldtemp.setVisible(true);
	        numFieldtemp.setCanLoseFocus(true);
	        numFieldtemp.setFocused(false);
	        textFields.add(numFieldtemp);
	        GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType> btnInstructionType = new GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType>(
	        		buttonCount + 1, x_pos + 10, y_pos + 65, (int) (X_WIDTH * .9), 20, 
	        		"Type",  TutorialFeatureInstruction.InstructionType.WASD);
	        buttonList.add(btnInstructionType);
			break;
		case START:
			textFieldtemp = new GuiTextField(this.fontRendererObj, x_pos + 5, y_pos + 30, (int) (X_WIDTH * .9), 14);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText("Name of Feature");
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(true);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Pos"));
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"X:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 40, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.posX));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Y:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 95, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.posY));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Z:"));
	        numFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 150, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        numFieldtemp.setMaxStringLength(32);
	        numFieldtemp.setText(Integer.toString((int)player.posZ));
	        numFieldtemp.setTextColor(16777215);
	        numFieldtemp.setVisible(true);
	        numFieldtemp.setCanLoseFocus(true);
	        numFieldtemp.setFocused(false);
	        textFields.add(numFieldtemp);
	        
	        y_pos += 15;
	        
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Pitch:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 40, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.rotationPitch % 360));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
	        labels.add(new GuiPolyLabel(this.fontRendererObj, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
	        		"Yaw:"));
	        textFieldtemp = new GuiPolyNumField(this.fontRendererObj, x_pos + 110, y_pos + 49, (int) (X_WIDTH * .2), 10);
	        textFieldtemp.setMaxStringLength(32);
	        textFieldtemp.setText(Integer.toString((int)player.rotationYaw % 360));
	        textFieldtemp.setTextColor(16777215);
	        textFieldtemp.setVisible(true);
	        textFieldtemp.setCanLoseFocus(true);
	        textFieldtemp.setFocused(false);
	        textFields.add(textFieldtemp);
			break;
		default:
			break;
			
		}
		
		if(initRun) {
			this.buttonList.add(btnBack);
	        this.buttonList.add(btnNext);
		}
		
		btnAddStepType.enabled=addNew;
				
        this.buttonList.add(btnAddStepType);
    	this.scroll = 0F;
    	
    }

    
    /**
	 * Handles mouse wheel scrolling.
	 */
	public void handleMouseInput() {
		
		int i = Mouse.getEventDWheel();
		if (i != 0 && extraLines > 0) {
			scroll -= Math.signum(i) / ((float) extraLines*8);
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}
		super.handleMouseInput();
	}

	/**
	 * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
	 */
	public void drawDefaultBackground()
	{
	    super.drawDefaultBackground();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    this.mc.getTextureManager().bindTexture(background_image);
	    //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
	    //System.out.println("Screen width & Height: " + this.width + " " + this.height);
	    int i = (this.width - 248) / 2;
	    int j = (this.height - 184) / 2; //old was 200
	   // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
	    this.drawTexturedModalRect(i, j, 0, 0, 248, 184);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
	    super.updateScreen();
	}
	
	/*
	 * Getter for fontRenderer
	 */
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	/**
     * Send a packet to the server requesting for a player to either Join or Withdraw from the queue for 
     * a particular experiment
     * @param experimentID The experiment in question
     * @param wantToJoin True if player wants to join, False if they want to withdraw
     */
    private void sendExperimentUpdateToServer(int experimentID, boolean wantToJoin) {
    	ExperimentManager.ExperimentParticipantMetaData part = ExperimentManager.INSTANCE.new ExperimentParticipantMetaData(player.getDisplayName(), experimentID, wantToJoin);
    	Gson gson = new Gson();
		Type gsonType = new TypeToken<ExperimentManager.ExperimentParticipantMetaData>(){}.getType();
		final String experimentUpdates = gson.toJson(part, gsonType);
		ClientEnforcer.INSTANCE.sendExperimentSelectionUpdate(experimentUpdates, ExperimentsPacketType.RequestJoinExperiment.ordinal());
    }
    
    private void sendExperimentUpdateToServer(int experimentID, ExperimentParameters params) {
    	ExperimentManager.ExperimentParticipantMetaData part = ExperimentManager.INSTANCE.new ExperimentParticipantMetaData(player.getDisplayName(), experimentID, params);
    	GsonBuilder gBuilder = new GsonBuilder();
    	gBuilder.setPrettyPrinting();
    	Gson gson = gBuilder.create();
    	Type gsonType = new TypeToken<ExperimentManager.ExperimentParticipantMetaData>() {}.getType();
    	final String experimentUpdates = gson.toJson(part, gsonType);
    	//System.out.println("Updates: \n" + experimentUpdates);
    	ClientEnforcer.INSTANCE.sendExperimentSelectionUpdate(experimentUpdates, ExperimentsPacketType.SendParameterUpdates.ordinal());
    	
    }
    
}
