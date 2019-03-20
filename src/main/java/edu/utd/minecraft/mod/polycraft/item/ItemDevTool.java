package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyPortal;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.RenderBox;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team.ColorEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemDevTool extends ItemCustom  {
	
	int[] pos1= new int[3];
	int[] pos2 = new int[3];
	int[] lastBlock = new int[3]; //used to store last clicked block so we can schedule a block update if it breaks on the client side
	boolean updateLastBlock = false;
	ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	boolean pos1set = false;
	boolean pos2set = false;
	String tool;
	boolean setting;
	String outputFileName = "output";
	String outputFileExt = ".psm";
	
	private StateEnum currentState;
	public static enum StateEnum {
		AreaSelection,
		FeatureTool,
		GuideTool,
		Save,
		Load;
		
		public StateEnum next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public StateEnum previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	public static ArrayList renderboxes= new ArrayList();
	
	
	public ItemDevTool(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("gripped_engineered_diamond_axe"));
		//this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.setting =false;
		currentState = StateEnum.AreaSelection;
	}
	
	public void setState(String state, EntityPlayer player) {
		currentState = StateEnum.valueOf(state);
		player.addChatMessage(new ChatComponentText("State: " + state));
	}
	
	public void setState(String state) {
		currentState = StateEnum.valueOf(state);
	}
		
	@Override
	// Doing this override means that there is no localization for language
	// unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return "Dev Tool";
	}  
	@Override
	public boolean  hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_)
	{
		p_77644_2_.setHealth(0);
		return true;
	}

	
	@Override
	public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
		if(!world.isRemote)
			return super.onItemRightClick(p_77659_1_, world, player);		
		if(player.isSneaking()) {
			//currentState = currentState.next();
			//player.addChatMessage(new ChatComponentText(currentState.toString() + "Mode"));
			PolycraftMod.proxy.openDevToolGui(player);
		}
		return super.onItemRightClick(p_77659_1_, world, player);
	}
	
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}
		if(!player.isSneaking()) {		
			switch(currentState) {
				case AreaSelection:
					player.addChatMessage(new ChatComponentText("pos2 selected: " + x + "::" + y + "::" + z));
					pos2[0] = x;
					pos2[1] = y;
					pos2[2] = z;
					pos2set = true;
					if(player.worldObj.isRemote)
						updateRenderBoxes();
					break;
				case FeatureTool:
					boolean removed = false;
					for(int i =0;i<features.size();i++) {
						if(features.get(i).getPos().xCoord == x && features.get(i).getPos().yCoord == y && features.get(i).getPos().zCoord == z) {
							features.remove(i);
							player.addChatMessage(new ChatComponentText("removed feature at: " + x + "::" + y + "::" + z));
							updateRenderBoxes();
							removed = true;
						}
					}
					if(!removed) {
						features.add(new TutorialFeature("POI" + features.size(), Vec3.createVectorHelper(x, y, z), Color.green));
						
						player.addChatMessage(new ChatComponentText("Added feature at: " + x + "::" + y + "::" + z));
						updateRenderBoxes();
					}
					break;
				case Save:
					player.addChatMessage(new ChatComponentText("Attempting to save Features " ));
					
					NBTTagCompound nbtFeatures = new NBTTagCompound();
					NBTTagList nbtList = new NBTTagList();
					for(int i =0;i<features.size();i++) {
						NBTTagCompound nbt = new NBTTagCompound();
						int pos[] = {(int)features.get(i).getPos().xCoord, (int)features.get(i).getPos().yCoord, (int)features.get(i).getPos().zCoord};
						nbt.setIntArray("Pos",pos);
						nbt.setString("name", features.get(i).getName());
						nbtList.appendTag(nbt);
					}
					nbtFeatures.setTag("features", nbtList);
					FileOutputStream fout = null;
					try {
						File file = new File("C:\\Users\\mjg150230\\Desktop\\"+this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
						fout = new FileOutputStream(file);
						
						if (!file.exists()) {
							file.createNewFile();
						}
						CompressedStreamTools.writeCompressed(nbtFeatures, fout);
						fout.flush();
						fout.close();
						
					}catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					
					}catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
					
					break;
				case Load:
			        try {
			        	player.addChatMessage(new ChatComponentText("Attempting to load Features " ));
					
			        	File file = new File("C:\\Users\\mjg150230\\Desktop\\"+this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
			        	InputStream is = new FileInputStream(file);

			            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
			            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
						for(int i =0;i<nbtFeatList.tagCount();i++) {
							NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
							int featPos[]=nbtFeat.getIntArray("Pos");
							String featName = nbtFeat.getString("name");
							features.add(new TutorialFeature(featName, Vec3.createVectorHelper(featPos[0], featPos[1], featPos[2]), Color.green));
						}

			            is.close();

			        } catch (Exception e) {
			            System.out.println("I can't load schematic, because " + e.toString());
			        }
					break;
				default:
					break;
			}
		}

		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
		
		if(!player.worldObj.isRemote) {
			return true;
		}
		
		switch(currentState) {
			case AreaSelection:
				player.addChatMessage(new ChatComponentText("pos1 selected: " + X + "::" + Y + "::" + Z));
				player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X+1, Y, Z), 20);
				updateLastBlock = true;
				pos1[0] = X;
				pos1[1] = Y;
				pos1[2] = Z;
				lastBlock[0] = X;
				lastBlock[1] = Y;
				lastBlock[2] = Z;
				pos1set = true;
				if(player.worldObj.isRemote)
					updateRenderBoxes();
				break;
			case FeatureTool:
				break;
			default:
				break;
		}
		
		return true;
	}
	
	public ArrayList<TutorialFeature> getFeatures(){
		return features;
	}
	
	public void swapFeatures(int i, int j){
		Collections.swap(features, i, j);
	}
	
	public void removeFeatures(int i){
		features.remove(i);
		updateRenderBoxes();
	}
	
	public void updateRenderBoxes() {
		renderboxes.clear();
		
		if(!features.isEmpty()) {
			int counter = 0;
			for(TutorialFeature v: features) {
				counter++;
				RenderBox box = new RenderBox(v.getPos().xCoord, v.getPos().zCoord, v.getPos().xCoord, v.getPos().zCoord, v.getPos().yCoord, 1, 1, v.getName());
				if(v.getName().equalsIgnoreCase("Start"))
					box.setColor(Color.CYAN);
				else
					box.setColor(Color.GREEN);
				renderboxes.add(box);
			}
		}
		if(pos1[0] != 0 || pos1[1] != 0 ||pos1[2] != 0 || pos2[0] != 0 || pos2[1] != 0 ||pos2[2] != 0) {
			renderboxes.add(new RenderBox(pos1[0], pos1[2], pos2[0], pos2[2], Math.min(pos1[1], pos2[1]), Math.abs(pos1[1]-pos2[1]) + 1, 1));
		}
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(updateLastBlock) {
				updateLastBlock = false;
				player.worldObj.scheduleBlockUpdate(lastBlock[0], lastBlock[1], lastBlock[2], player.worldObj.getBlock(lastBlock[0], lastBlock[1], lastBlock[2]), 5);
				player.addChatMessage(new ChatComponentText("Update block"));
			}
				
			
			
		}
		super.onUpdate(itemstack, world, entity, par4, par5);
	}
	
	public static void render(Entity entity) {
		synchronized (renderboxes) {
			Iterator<RenderBox> boxes = renderboxes.iterator();
			while (boxes.hasNext()) {
				RenderBox box = boxes.next();
				box.render(entity);
			}
		}
	}
	
	
}
