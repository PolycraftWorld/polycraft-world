
package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class PolycraftTeleporter extends Teleporter {

	public PolycraftTeleporter(WorldServer server) {
		super(server);
	}
	
	@Override
	public void placeInPortal(Entity entity, double var1, double var2, double var3, float var4) {
		 entity.setLocationAndAngles(2, 90, 2, entity.rotationYaw, 0.0F);
         entity.motionX = entity.motionY = entity.motionZ = 0.0D;
	}


}