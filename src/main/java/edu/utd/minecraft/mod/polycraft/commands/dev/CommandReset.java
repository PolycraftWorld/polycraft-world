package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.util.BotAPI;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CommandReset extends CommandBase{

	private final List aliases;
	private static final String chatCommandCollect = "collect";
	private static final String chatCommandSetup = "setup";
	private static final String chatCommandTrees = "trees";
	private static final String chatCommandHills = "hills";
	
  
	
	public CommandReset(){
		aliases = new ArrayList(); 
        aliases.add("reset"); 
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "reset";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/reset";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException {
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) 
        { 
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			int x = BotAPI.pos.get(0), y = BotAPI.pos.get(1), z = BotAPI.pos.get(2);
			int xMax = BotAPI.pos.get(3), yMax = BotAPI.pos.get(4), zMax = BotAPI.pos.get(5);
			BlockPos pos = new BlockPos(x, y, z);
			String response;
			System.out.println("Processing on Server side"); 
			if(args.length > 0)
				switch(args[0].toLowerCase()) {
				case chatCommandCollect:
					Gson gson = new Gson();
					JsonObject jobject = new JsonObject();
					
					ArrayList<Integer> map = new ArrayList<Integer>();
					for(int i = 0; i < 16; i++) {
						for(int k = 0; k < 16; k++) {
							map.add(Block.getIdFromBlock(player.worldObj.getBlockState(pos.add(i, 0, k)).getBlock()));
						}
					}
					JsonElement result = gson.toJsonTree(map);
					jobject.add("map",result);
					jobject.addProperty("playerX", (int)player.posX - x);
					jobject.addProperty("playerZ", (int)player.posZ - z);
					player.addChatComponentMessage(new ChatComponentText(jobject.toString()));
					break;
				case chatCommandSetup:	//setup using player position
					BotAPI.pos.set(0, ((int)1 >> 4) << 4);
					BotAPI.pos.set(1, (int)player.posY);
					BotAPI.pos.set(2, ((int)1 >> 4) << 4);
					pos = new BlockPos(BotAPI.pos.get(0), y = BotAPI.pos.get(1), z = BotAPI.pos.get(2));
					x = BotAPI.pos.get(0);
					y = BotAPI.pos.get(1);
					z = BotAPI.pos.get(2);
					
					if(args.length > 3) {
						BotAPI.pos.set(3, Integer.parseInt(args[1]));
						BotAPI.pos.set(4, Integer.parseInt(args[2]));
						BotAPI.pos.set(5, Integer.parseInt(args[3]));
						xMax = BotAPI.pos.get(3);
						yMax = BotAPI.pos.get(4);
						zMax = BotAPI.pos.get(5);
					}
					
					for(int i = 0; i <= xMax; i++) {
						for(int k = 0; k <= zMax; k++) {
							if(i == 0) {
								player.worldObj.setBlockState(pos.add(i - 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
								player.worldObj.setBlockState(pos.add(i - 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
								if(k==0) {
									player.worldObj.setBlockState(pos.add(i - 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
									player.worldObj.setBlockState(pos.add(i - 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
									player.worldObj.setBlockState(pos.add(i, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
									player.worldObj.setBlockState(pos.add(i, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
								}
							}
							if(i == xMax) {
								player.worldObj.setBlockState(pos.add(i + 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
								player.worldObj.setBlockState(pos.add(i + 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
							}
							if(k == 0) {
								player.worldObj.setBlockState(pos.add(i + 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
								player.worldObj.setBlockState(pos.add(i + 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
							}
							if(k == zMax) {
								player.worldObj.setBlockState(pos.add(i, 0, k + 1), Blocks.wool.getStateFromMeta(15), 2);
								player.worldObj.setBlockState(pos.add(i, 1, k + 1), Blocks.wool.getStateFromMeta(15), 2);
							}
							
							for(int j = 0; j < 20; j++) {
								player.worldObj.setBlockToAir(pos.add(i, j, k));
							}
						}
					}
					player.setPositionAndUpdate(x + Math.random() * xMax, y, z + Math.random() * zMax);
					player.worldObj.setBlockState(pos.add(Math.random() * xMax, 0, Math.random() * zMax), Blocks.log.getDefaultState(), 2);
					
					break;
				case chatCommandTrees:
					if(args.length > 1) {
						int count = Integer.parseInt(args[1]);
						Random rand = new Random();
						while(count-- > 0) {
							IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
			                IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
			                WorldGenerator worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
			                worldgenerator.generate(player.worldObj, rand, pos.add(Math.random() * xMax, 0, Math.random() * xMax));
						}
					}
				default:
					player.setPosition(x + Math.random() * 15, y, z + Math.random() * 15);
					break;
				}
			else {
				List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(pos, pos.add(xMax, yMax, zMax)));
				for(Entity entity: entities) {
					entity.setDead();
				}
				for(int i = 0; i < 16; i++) {
					for(int k = 0; k < 16; k++) {
						if(i == 0) {
							player.worldObj.setBlockState(pos.add(i - 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i - 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
							if(k==0) {
								player.worldObj.setBlockState(pos.add(i - 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
								player.worldObj.setBlockState(pos.add(i - 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
							}
						}
						if(i == 15) {
							player.worldObj.setBlockState(pos.add(i + 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i + 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						if(k == 0) {
							player.worldObj.setBlockState(pos.add(i + 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i + 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						if(k == 15) {
							player.worldObj.setBlockState(pos.add(i, 0, k + 1), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i, 1, k + 1), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						
						player.worldObj.setBlockToAir(pos.add(i, 0, k));
					}
				}
				player.setPositionAndUpdate(x + Math.random() * 15, y, z + Math.random() * 15);
				player.worldObj.setBlockState(pos.add(Math.random() * 15, 0, Math.random() * 15), Blocks.log.getDefaultState(), 2);
			}
			
		}		
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_, BlockPos blockPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
