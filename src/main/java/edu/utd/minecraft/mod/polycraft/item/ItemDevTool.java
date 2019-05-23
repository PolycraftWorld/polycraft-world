package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameData;
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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyPortal;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.RenderBox;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialRender;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team.ColorEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemDevTool extends ItemCustom  {
	
	int[] lastBlock = new int[3]; //used to store last clicked block so we can schedule a block update if it breaks on the client side
	boolean updateLastBlock = false;
	ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	TutorialOptions tutOptions = new TutorialOptions();
	TutorialFeature selectedFeature;
	private long lastEventNanoseconds = 0;
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
		Load,
		GuiTest;
		
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
		//this.setTextureName(PolycraftMod.getAssetName("gripped_engineered_diamond_axe"));
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
	
	public StateEnum getState() {
		return currentState;
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
			
		if(player.isSneaking()) {
			//currentState = currentState.next();
			//player.addChatMessage(new ChatComponentText(currentState.toString() + "Mode"));
			if(world.isRemote)
				PolycraftMod.proxy.openDevToolGui(player);
		}else {
			switch(currentState) {
				case Save:
					if(world.isRemote)
						save();
					break;
				case Load:
			        load();
			        if(world.isRemote)
						updateRenderBoxes();
					break;
				default:
					break;
			}
		}
		return super.onItemRightClick(p_77659_1_, world, player);
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
							 float hitX, float hitY, float hitZ) {
		Vec3 blockPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		Vec3 hitPos = new Vec3(hitX, hitY, hitZ);
		if(!world.isRemote) {
			return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
		}
		if(Keyboard.isKeyDown(29)) {	//if holding ctrl select block in front of face clicked
			blockPos = getBlockAtFace(blockPos, hitPos);
		}
		if(Mouse.getEventNanoseconds()==lastEventNanoseconds) {
    		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
    	}else {
    		lastEventNanoseconds = Mouse.getEventNanoseconds();
    	}
		if(!player.isSneaking()) {		
			switch(currentState) {
				case AreaSelection:
					player.addChatMessage(new ChatComponentText("pos2 selected: " + pos.getX() + "::" + pos.getY() + "::" + pos.getZ()));
					tutOptions.size = pos;
					if(player.worldObj.isRemote)
						updateRenderBoxes();
					break;
				case FeatureTool:
					if(Keyboard.isKeyDown(56)) {	//if holding alt, remove feature at location
						for(int i =0;i<features.size();i++) {
							
							if(features.get(i).getPos().distanceSq(new Vec3i(blockPos.xCoord, blockPos.yCoord, blockPos.zCoord)) < 0.05) {
								features.remove(i);
								player.addChatMessage(new ChatComponentText("removed feature at: " + pos.getX() + "::" + pos.getY() + "::" + pos.getZ()));
								updateRenderBoxes();
							}
						}
					}else{
						features.add(new TutorialFeature("Feature " + features.size(), pos, Color.green));
						
						player.addChatMessage(new ChatComponentText("Added feature at: " + blockPos.xCoord + "::" + blockPos.yCoord + "::" + blockPos.zCoord));
						updateRenderBoxes();
					}
					break;
				case GuiTest:
					//TutorialRender(player);
				default:
					break;
			}
		}

		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos blockPos, EntityPlayer player) {
		
		if(!player.worldObj.isRemote) {
			//player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 20);
			return true;
		}
		
		switch(currentState) {
			case AreaSelection:
				player.addChatMessage(new ChatComponentText("pos1 selected: " + blockPos.getX() + "::" + blockPos.getY() + "::" + blockPos.getZ()));
				updateLastBlock = true;
				tutOptions.pos = blockPos;
				lastBlock[0] = blockPos.getX();
				lastBlock[1] = blockPos.getY();
				lastBlock[2] = blockPos.getZ();
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
	
	public void addFeature(TutorialFeature feature){
		features.add(feature);
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
				RenderBox box = new RenderBox(v.getPos().getX(), v.getPos().getZ(), v.getPos2().getX(), v.getPos2().getZ(),
						Math.min(v.getPos().getY(), v.getPos2().getY()), Math.max(Math.abs(v.getPos().getY()- v.getPos2().getY()), 1), 1, v.getName());
				box.setColor(v.getColor());
				renderboxes.add(box);
			}
		}
		if(tutOptions.pos.getY() != 0 && tutOptions.size.getY() != 0) {
			renderboxes.add(new RenderBox(new Vec3(tutOptions.pos), new Vec3(tutOptions.size), 1));
		}
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(updateLastBlock) {
				updateLastBlock = false;
				//player.worldObj.scheduleBlockUpdate(lastBlock[0], lastBlock[1], lastBlock[2], player.worldObj.getBlock(lastBlock[0], lastBlock[1], lastBlock[2]), 5);
				player.worldObj.markBlockRangeForRenderUpdate(lastBlock[0], lastBlock[1], lastBlock[2], lastBlock[0], lastBlock[1], lastBlock[2]);
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
	
	private Vec3 getBlockAtFace(Vec3 blockPos, Vec3 hitPos) {
		blockPos = new Vec3((int) (blockPos.xCoord + (hitPos.xCoord==0.0?-1:hitPos.xCoord==1.0?1:0))
		 				,(int) (blockPos.yCoord + (hitPos.yCoord==0.0?-1:hitPos.yCoord==1.0?1:0))
		 				,(int) (blockPos.zCoord + (hitPos.zCoord==0.0?-1:hitPos.zCoord==1.0?1:0)));
		
		return blockPos;
	}
	
	private void save() {
		NBTTagCompound nbtFeatures = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		if (tutOptions.pos == null)
			tutOptions.pos = new BlockPos(0, 0, 0);
		if (tutOptions.size == null)
			tutOptions.size = new BlockPos(0, 0, 0);
		for(int i =0;i<features.size();i++) {
//				NBTTagCompound nbt = new NBTTagCompound();
//				int pos[] = {(int)features.get(i).getPos().xCoord, (int)features.get(i).getPos().yCoord, (int)features.get(i).getPos().zCoord};
//				nbt.setIntArray("Pos",pos);
//				nbt.setString("name", features.get(i).getName());
			nbtList.appendTag(features.get(i).save());
		}
		nbtFeatures.setTag("features", nbtList);
		nbtFeatures.setTag("options", tutOptions.save());
		nbtFeatures.setTag("AreaData", saveArea());
		FileOutputStream fout = null;
		try {
			File file = new File(this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
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
	}
	
	private void load() {
		try {
        	features.clear();
        	
        	File file = new File(this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				features.add(test);
			}
			
			tutOptions.load(nbtFeats.getCompoundTag("options"));
            is.close();

        } catch (Exception e) {
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	
	private NBTTagCompound saveArea() {
		if(tutOptions.pos.getY() != 0 && tutOptions.size.getY() != 0)
		{
			int minX = Math.min(tutOptions.pos.getX(), tutOptions.size.getX());
			int maxX = Math.max(tutOptions.pos.getX(), tutOptions.size.getX());
			int minY = Math.min(tutOptions.pos.getY(), tutOptions.size.getY());
			int maxY = Math.max(tutOptions.pos.getY(), tutOptions.size.getY());
			int minZ = Math.min(tutOptions.pos.getZ(), tutOptions.size.getZ());
			int maxZ = Math.max(tutOptions.pos.getZ(), tutOptions.size.getZ());
			int[] intArray;
			short height;
			short length;
			short width;
			
			length=(short)(maxX-minX+1);
			height=(short)(maxY-minY+1);
			width=(short)(maxZ-minZ+1);
			int[] blocks = new int[length*height*width];
			byte[] data = new byte[length*height*width];
			int count=0;
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList tiles = new NBTTagList();
			
			TileEntity tile;
			for(int i=0;i<length;i++) {
				for(int j=0;j<height;j++) {
					for(int k=0;k<width;k++) {
						
						tile = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(minX+i, minY+j, minZ+k));
						if(tile!=null){
							NBTTagCompound tilenbt = new NBTTagCompound();
							tile.writeToNBT(tilenbt);
							tiles.appendTag(tilenbt);
							
						}
							
						Block blk = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(minX+i, minY+j, minZ+k)).getBlock();
						int id = blk.getIdFromBlock(blk);
						blocks[count]=id;
						data[count]=(byte) blk.getMetaFromState(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos((int)(minX+i), (int)(minY+j), (int)(minZ+k))));
						count++;
						
					}
				}
			}
			nbt.setTag("TileEntity", tiles);
			nbt.setIntArray("Blocks", blocks);
			nbt.setByteArray("Data", data);
			return nbt;
		}else {
			return null;
		}
	}
}
