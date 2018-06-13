package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class CommandTP extends CommandBase{

	private static final String chatCommandTeleportArgUTD = "utd";
	private static final String chatCommandTeleportArgUser = "user";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private final List aliases;
  
	
	public CommandTP(){
		aliases = new ArrayList(); 
        aliases.add("teleport"); 
        aliases.add("goto"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "teleport";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/goto <utdpp>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) 
        { 
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			System.out.println("Processing on Server side"); 
			if (args.length > 0) {
				// teleport to UTD
				if (chatCommandTeleportArgUTD.equalsIgnoreCase(args[0])) {
					// only allow if the player is in a private property
					if (Enforcer.findPrivateProperty(player) != null) {
						player.setPositionAndUpdate(1 + .5,
								player.worldObj.getTopSolidOrLiquidBlock(1, 1) + 3,
								1 + .5);
					}
				}
				// only allow if the player is in chunk 0,0 (center of UTD), or if
				// they are in a private property already
				else if ((Math.abs(player.chunkCoordX) <= 5 && Math
						.abs(player.chunkCoordZ) <= 5)
						|| Enforcer.findPrivateProperty(player) != null) {
					boolean valid = false;
					int x = 0, z = 0;
					// teleport to a user
					if (chatCommandTeleportArgUser.equalsIgnoreCase(args[0])) {
						if (args.length > 1) {
							// teleport to a player
							final EntityPlayer targetPlayer = player.worldObj
									.getPlayerEntityByName(args[1]);
							if (targetPlayer != null && targetPlayer != player) {
								final PrivateProperty targetPrivateProperty = Enforcer.findPrivateProperty(targetPlayer);
								valid = targetPrivateProperty != null
										&& targetPrivateProperty.actionEnabled(
												player, Action.Enter);
								x = (int) targetPlayer.posX;
								z = (int) targetPlayer.posZ;
							}
						}
					}
					// teleport to a private property
					else if (chatCommandTeleportArgPrivateProperty.equalsIgnoreCase(args[0])) {
						if (args.length < 3) {
							final List<PrivateProperty> ownerPrivateProperties = Enforcer.getPrivatePropertiesByOwner()
									.get(player.getDisplayName().toLowerCase());
							if (ownerPrivateProperties != null) {
								int index = 0;
								try {
									index = Integer.parseInt(args[1]);
									if (index < 0
											|| index >= ownerPrivateProperties
													.size())
										return;
								} catch (final Exception e) {
								}

								final PrivateProperty targetPrivateProperty = ownerPrivateProperties
										.get(index);
								int minX = (targetPrivateProperty.boundTopLeft.x * 16) + 1;
								int minZ = (targetPrivateProperty.boundTopLeft.z * 16) + 1;
								int maxX = (targetPrivateProperty.boundBottomRight.x * 16) + 15;
								int maxZ = (targetPrivateProperty.boundBottomRight.z * 16) + 15;
								x = (minX + maxX) / 2;
								z = (minZ + maxZ) / 2;
								valid = true;
							}
						} else {
							try {
								x = Integer.parseInt(args[1]);
								z = Integer.parseInt(args[2]);
								final net.minecraft.world.chunk.Chunk chunk = player.worldObj
										.getChunkFromBlockCoords(x, z);
								final PrivateProperty targetPrivateProperty = Enforcer.findPrivateProperty(
										player, chunk.xPosition, chunk.zPosition);
								valid = targetPrivateProperty != null
										&& targetPrivateProperty.actionEnabled(
												player, Action.Enter);
							} catch (final Exception e) {
							}
						}
					}

					if (valid) {
						player.setPositionAndUpdate(x + .5,
								player.worldObj.getTopSolidOrLiquidBlock(x, z) + 3,
								z + .5);
					}
				}
			}
		}		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
