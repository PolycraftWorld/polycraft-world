package edu.utd.minecraft.mod.polycraft.client.gui.experiment;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentConfig;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.PolycraftGuiScreenBase;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentConfig.ConfigSlider;
import edu.utd.minecraft.mod.polycraft.client.gui.experiment.ExperimentDef.ExperimentType;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.ExperimentsPacketType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class GuiExperimentManager extends PolycraftGuiScreenBase {
	private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation background_image = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/consent_background.png"));
    private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
    
    private static final String __OBFID = "CL_00000691";
    private EntityPlayer player;
    private int x, y, z;
    private int screenID; //current screen
    private static final String TITLE = "Polycraft DEV TOOL";
    private final int screenContainerWidth = 230;
    private final int screenContainerHeight = 130;
    private boolean wasClicking; 
    public int buttonCount = 1;
    private int buttonheight = 20;
    private int button_padding_y = 4;
    private float scroll = 0.0F; // Amount of scroll, from 0.0 to 1.0 inclusive.
	private boolean scrolling; // True if the scroll bar is being dragged.
    ArrayList<String> lines = new ArrayList<String>();	//generic lines array for body text
	private int ylines; // The number of buttons/lines the text space can accommodate.
	private int extraLines; // How many buttons/lines are overflowing from the alloted text space.
	private long lastEventNanoseconds = 0;	//lastEventNanoseconds used to prevent the same mouse click causing multiple actions
	private ExperimentDef expToAdd;
	private ExperimentDef.ExperimentType expToAddType;
	public ExperimentParameters currentParameters;
	private GuiExperimentConfig guiConfig = null;
	
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
    GuiButton btnAddExpType;
    public ArrayList<GuiTextField> textFields = new ArrayList<GuiTextField>();
    public ArrayList<GuiPolyLabel> labels = new ArrayList<GuiPolyLabel>();
    public ArrayList<GuiPolyButtonCycle> options = new ArrayList<GuiPolyButtonCycle>();
	private GuiExperimentManagerList guiExperiments;
	
    
    private enum Screen {
    		MAIN,
    		EXP_LIST,
    		EXP_PARAMS,
    		EXP_EDIT,
    		EXP_ADD
    }
    
    private String userFeedbackText = "";
    private Screen screenSwitcher = Screen.MAIN;
    
    public GuiExperimentManager(EntityPlayer player, int x, int y, int z) {
        System.out.print("gui ExperimentList constructor.\n x, y, z: " + x + " " + y + " " + z);
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.screenID = 0;
        this.titleHeight = 20;
        this.guiExperiments = null;
        
    }
   
    /**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiExperimentManager(EntityPlayer player) {
        this.player = player;
        this.screenID = 0;
        this.titleHeight = 20;
        this.guiExperiments = null;
    }
    
    /**
     * Add button to button list
     */
    public void addBtn(GuiButton btn) {
    	this.buttonList.add(btn);
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. This is run every time the screen 
     * needs to be rendered.
     */
    public void initGui()
    {
    	
    	guiExperiments = new GuiExperimentManagerList(this, this.mc);

    	int x_pos = (this.width - 248) / 2 + X_PAD; //magic numbers from Minecraft. 
        int y_pos = (this.height - 198) / 2 + this.titleHeight; //magic numbers from minecraft
    	//Navigation buttons
        btnBack = new GuiButton(1, x_pos, y_pos + Y_HEIGHT + 2*Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "Close");
        btnNext = new GuiButton(2, x_pos + X_WIDTH/2 + X_PAD/2, y_pos + Y_HEIGHT + 2*Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "next");
        //add step buttons
        btnAddExpType = new GuiButton(10000+buttonCount++, x_pos+10, y_pos+4, (int) (X_WIDTH * .9), buttonheight, "Type: " + ExperimentType.CTB_FLAT.name());
        
        //start off in the right screen and run this before displaying
        screenSwitcher = this.screenChange(Screen.MAIN);
        
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
    	if(screenSwitcher == Screen.EXP_LIST)
    			this.guiExperiments.keyTyped(c, p);
    	else if(screenSwitcher == Screen.EXP_ADD) {
    		for(GuiTextField textField: textFields) {
    			if(textField.isFocused())
    				textField.textboxKeyTyped(c, p);
    		}
    	}else if(screenSwitcher == Screen.EXP_EDIT) {
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
    	
    	if(this.guiExperiments != null && screenSwitcher == Screen.EXP_LIST) {
    		this.guiExperiments.func_148179_a(x, y, mouseEvent);
    	}
    	//send mouse click to config class
    	if(this.guiConfig != null && screenSwitcher == Screen.EXP_PARAMS) {
    		this.guiConfig.func_148179_a(x, y, mouseEvent);
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
    	if(this.guiExperiments != null) {
    		
    		if (mouseEvent != 0 || !this.guiExperiments.func_148181_b(x, y, mouseEvent))
    		{
    			super.mouseMovedOrUp(x, y, mouseEvent);
    		}
		}else {
			super.mouseMovedOrUp(x, y, mouseEvent);
		}
    	
    	if(this.guiConfig != null) {
    		if (mouseEvent != 0 || !this.guiConfig.func_148181_b(x, y, mouseEvent)){
    			super.mouseMovedOrUp(x, y, mouseEvent);
    		}
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
    		case MAIN:			//close the screen
    			this.exitGuiScreen();
    			break;
    		case EXP_LIST:			//go back to main screen
    			screenSwitcher = screenChange(Screen.MAIN);
    			break;
    		case EXP_ADD: 		//go back to steps screen
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    			break;
    		case EXP_EDIT:	//go back to steps screen
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    			break;
    		case EXP_PARAMS:
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    		default:
    			break;
    		}
    		break;
    	case 2:
    		//user pressed next button
    		switch(screenSwitcher) {
    		case MAIN:			//go to steps screen
    			guiExperiments.updateExperiments();
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    			ExperimentManager.INSTANCE.requestExpDefs(player.getDisplayName());
    			break;
    		case EXP_LIST:			//go to add step screen
    			ExperimentManager.INSTANCE.requestExpDefs(player.getDisplayName());
    			screenSwitcher = screenChange(Screen.EXP_ADD);
    			break;
    		case EXP_ADD: 		//save the new step and go back to steps screen
    			expToAdd.updateValues();
    			ExperimentManager.INSTANCE.sendExpDefUpdate(-1, expToAdd, false);
    			guiExperiments.updateExperiments();
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    			break;
    		case EXP_EDIT:	//save the step and go back to steps screen
    			expToAdd.updateValues();
    			ExperimentManager.INSTANCE.sendExpDefUpdate(expToAdd.id, expToAdd, false);
    			screenSwitcher = screenChange(Screen.EXP_LIST);
    			break;
    		case EXP_PARAMS:
    			//user is sending ExperimentConfig updates
        		updateExpParams();
        		ExperimentManager.INSTANCE.sendExpDefUpdate(expToAdd.id, expToAdd, false);
        		this.screenSwitcher = this.screenChange(Screen.EXP_LIST);
    		default:
    			break;
    		}
    		break;
    	default:
    		switch(screenSwitcher) {
    		case MAIN:			//close the screen
    			//nothing
    			break;
    		case EXP_LIST:			//Dev_steps buttons
    			//do nothing
    			break;
    		case EXP_ADD: 		//go back to steps screen
    			if(button.id == btnAddExpType.id) {
    	    		expToAddType = expToAddType.next();
    	    		btnAddExpType.displayString = "Type: " + expToAddType.name();
    	    		buildExpInputs(true);
    	    	}
    			if(button instanceof GuiPolyButtonCycle<?>) {
    				((GuiPolyButtonCycle<?>)button).nextOption();
    			}
    			break;
    		case EXP_EDIT:	//go back to steps screen
    			screenSwitcher = screenChange(Screen.EXP_LIST);
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
        	case MAIN:
        		drawDevMainScreen();
        		break;
        	case EXP_LIST:
        		this.extraLines = this.guiExperiments.getExtraScrollSpace();
        		this.guiExperiments.drawScreenHandler(mouseX, mouseY, otherValue, this.scroll);
        		drawDevStepsScreen();
        		break;
        	case EXP_ADD:
        		drawAddStepScreen();
        		break;
        	case EXP_EDIT:
        		drawEditStepScreen();
        		break;
        	case EXP_PARAMS:
        		this.extraLines = this.guiConfig.getExtraScrollSpace();
        		this.guiConfig.drawScreenHandler(mouseX, mouseY, otherValue, this.scroll);
        		this.drawExperimentConfigScreen();
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
    
    /*
     * Main screen to describe the purpose of this GUI
     */
    public void drawDevMainScreen() {
		//the below "magic numbers" come from Minecraft's Demo files.
	    //I'm using them unless there's better ideas in the future
	    //TODO: understand why these are the correct magic numbers
	    int x_pos = (this.width - 248) / 2 + 10;
	    int y_pos = (this.height - 190) / 2 + 8;
	    this.fontRendererObj.drawString(I18n.format(TITLE, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
	    y_pos += 12;
	    this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.X_WIDTH, y_pos + this.Y_HEIGHT, 0x50A0A0A0);
	    // Set which line to start displaying text box to simulate scrolling.
 		int lineStart = Math.round(this.scroll * extraLines);
 		for (int i = 0; i < ylines; i++, y_pos += this.fontRendererObj.FONT_HEIGHT)
 			this.fontRendererObj.drawString(lines.get(lineStart + i), x_pos, y_pos, 0);
 		
	    y_pos += buttonheight - button_padding_y - this.fontRendererObj.FONT_HEIGHT;
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
    
    
    private void drawExperimentConfigScreen() {
    	//get top left of screen with padding.
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 192) / 2 + Y_PAD;
        this.fontRendererObj.drawString(I18n.format("Experiment Configuration: " + this.expToAdd.name, new Object[0]),
        		x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //this.guiConfig.drawScreen(p_148128_1_, p_148128_2_, p_148128_3_);
	}
    
    
    private Screen screenChange(Screen newScreen) {
    	//On screen change, we need to update the button list and have it re-drawn.

		this.resetButtonList();
    	switch(newScreen) {
    		case MAIN:
    			btnBack.displayString = "Close";
    			btnNext.displayString = "Steps";
    			this.buttonList.addAll(this.experimentsListButton);
    			lines.clear();
    		    lines.addAll(this.fontRendererObj.listFormattedStringToWidth(I18n.format("gui.expmanager.body"), X_WIDTH));
    			ylines = Math.min(lines.size(), (Y_HEIGHT - titleHeight) / this.fontRendererObj.FONT_HEIGHT);
    			extraLines = lines.size() - ylines;
    			break;
    		case EXP_LIST:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Add Step";
    			ylines = (Y_HEIGHT - titleHeight) / (14);
    			extraLines = guiExperiments.getSize() - ylines;
    			break;
    		case EXP_ADD:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Add Exp";
    			expToAddType = ExperimentType.CTB_FLAT;
    			buildExpInputs(true);
    			break;
    		case EXP_EDIT:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Save Step";
    			buildExpInputs(false);
    			break;
    		case EXP_PARAMS:
    			btnBack.displayString = "< Back";
    			btnNext.displayString = "Save Params";
    			this.currentParameters = expToAdd.params;
    			guiConfig = new GuiExperimentConfig(this, this.mc);
    		default:
    			break;
    	}
    	this.buttonList.add(btnBack);
        this.buttonList.add(btnNext);
    	this.scroll = 0F;
    	return newScreen;
    }
    
    public void editExp(ExperimentDef expDef) {
    	expToAdd = expDef;
    	expToAddType = expDef.getExpType();
    	screenSwitcher = screenChange(Screen.EXP_EDIT);
    }
  
    public void editExpParams(ExperimentDef expDef) {
    	expToAdd = expDef;
    	expToAddType = expDef.getExpType();
    	screenSwitcher = screenChange(Screen.EXP_PARAMS);
    }
    
    /**
     * Build all buttons and textboxes for adding specific steps/features
     * */
    private void buildExpInputs(boolean addNew) {
    	boolean initRun = true;	//We only want to run some code once, because main buttons are already added when this runs the first time
    	if(screenSwitcher != Screen.EXP_ADD) {
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
		expToAdd = new ExperimentDef("Experiment", 2, 2);
		expToAdd.expType = expToAddType;
		expToAdd.buildGuiParameters(this, x_pos, y_pos);
		if(initRun) {
			this.buttonList.add(btnBack);
	        this.buttonList.add(btnNext);
		}
		
		btnAddExpType.enabled=addNew;
				
        this.buttonList.add(btnAddExpType);
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
	
	@Override
	public void drawHoveringText(List p_146283_1_, int p_146283_2_, int p_146283_3_, FontRenderer font) {
		// TODO Auto-generated method stub
		super.drawHoveringText(p_146283_1_, p_146283_2_, p_146283_3_, font);
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
	
	public void forceUpdateExperiments() {
		this.guiExperiments.updateExperiments();
	}
	
	private void updateExpParams() {
		for(ConfigSlider slider : (ArrayList<ConfigSlider>) this.guiConfig.getChangedItems()) {
			if(this.currentParameters.timingParameters.containsKey(slider.getName())) {
				Integer[] timingVals = this.currentParameters.timingParameters.get(slider.getName());
				timingVals[0] = (int) Math.round(slider.getSelectedValue());
				this.currentParameters.timingParameters.put(slider.getName(), timingVals);
			}else if(this.currentParameters.scoringParameters.containsKey(slider.getName())) {
				Integer[] scoringVals = this.currentParameters.scoringParameters.get(slider.getName());
				scoringVals[0] = (int)Math.round(slider.getSelectedValue());
				this.currentParameters.scoringParameters.put(slider.getName(), scoringVals);
			}else if(this.currentParameters.extraParameters.containsKey(slider.getName())) {
				Integer[] scoringVals = (Integer[]) this.currentParameters.extraParameters.get(slider.getName());
				scoringVals[0] = (int)Math.round(slider.getSelectedValue());
				this.currentParameters.extraParameters.put(slider.getName(), scoringVals);
			}else {
				continue;
			}
		}
	}
    
	
	//might could use this for updating the params for server side
    private void sendExperimentUpdateToServer(int experimentID, ExperimentParameters params) {
    	ExperimentManager.ExperimentParticipantMetaData part = ExperimentManager.INSTANCE.new ExperimentParticipantMetaData(player.getDisplayName(), experimentID, params);
    	GsonBuilder gBuilder = new GsonBuilder();
    	gBuilder.setPrettyPrinting();
    	Gson gson = gBuilder.create();
    	Type gsonType = new TypeToken<ExperimentManager.ExperimentParticipantMetaData>() {}.getType();
    	final String experimentUpdates = gson.toJson(part, gsonType);
    	//System.out.println("Updates: \n" + experimentUpdates);
    	ClientEnforcer.INSTANCE.sendExperimentPacket(experimentUpdates, ExperimentsPacketType.SendParameterUpdates.ordinal());
    	
    }
    
}
