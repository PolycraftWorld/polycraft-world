package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.util.Analytics;
import edu.utd.minecraft.mod.polycraft.util.PlayerKnockBackEvent;
import edu.utd.minecraft.mod.polycraft.util.PlayerKnockedBackEvent;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.world.World;

public class ItemKnockbackBomb  extends ItemCustom{

	private static final int Y_TOP = 255;
	private static final double KBB_RADIUS = 5;

	private int renderTicks = 0;
	private int maxRenderTicks = 60;
	protected Color color = Color.RED;
	private float lineWidth = 8;
	public static List list=null;

	public ItemKnockbackBomb(CustomObject config) {
		super(config);
		//this.setTextureName(PolycraftMod.getAssetName("knockback_bomb"));
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);

	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		// TODO Auto-generated method stub
		if(player.worldObj.isRemote) {
			player.addChatMessage(new ChatComponentText("test"));
		}
		super.onUsingTick(stack, player, count);

	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		// TODO Auto-generated method stub
		//		if(world.isRemote) {
		//			if(entity instanceof EntityPlayer) {
		//				if(((EntityPlayer)entity).getHeldItem().equals(itemStack)) {
		//					if(renderTicks < 0)
		//						renderTicks = maxRenderTicks;
		//					else
		//						renderTicks--;
		//					this.render(entity);
		//				}
		//			}
		//		}

		super.onUpdate(itemStack, world, entity, par4, par5);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		// TODO Auto-generated method stub

		knockback( world, player);
		itemstack.stackSize--;
		//boolean used=true;
		return super.onItemRightClick(itemstack, world, player);
	}

	protected List knockback(World world, EntityPlayer player) {
		if(!world.isRemote) {
			//System.out.println("This is player dimension:"+player.dimension);
			double x = -1*Math.sin(Math.toRadians(player.rotationYaw%360));
			double y = 1;
			double z = Math.cos(Math.toRadians(player.rotationYaw%360));
			double distance = 20 * Math.cos(Math.toRadians(player.rotationPitch%360) * 2);

			if(distance < KBB_RADIUS + 2 )
				distance = KBB_RADIUS + 2;
			else if(distance > 20)
				distance = 20;

			double posX = x*distance + player.posX;
			double posY = player.posY;
			double posZ = z*distance + player.posZ;
			while((player.worldObj.isAirBlock(new BlockPos((int) posX, (int)(posY -0.2), (int)posZ))
					|| player.worldObj.getBlockState( new BlockPos((int) posX, (int)(posY -0.2), (int)posZ)).getBlock().getMaterial() == Material.carpet)
					&& posY >= 1) {
				posY -= 0.25;
				posX += x/4;
				posZ += z/4;
				x -= x/32;
				z -= z/32;
			}
			//System.out.println("Rotation: " + player.rotationYaw +":: Rotation Mod: " + player.rotationYaw%360  );
			//player.setVelocity(x, y, z);
			EntityItem splosion = new EntityItem(world,posX, posY, posZ, new ItemStack(this,1,0));
			world.spawnEntityInWorld(splosion);
			world.removeEntity(splosion);
			float radius = 5F;
			float explosionSize = 5.0F;
			int i = MathHelper.floor_double(posX - (double)explosionSize - 1.0D);
			int j = MathHelper.floor_double(posX + (double)explosionSize + 1.0D);
			int k = MathHelper.floor_double(posY - (double)explosionSize - 1.0D);
			int i2 = MathHelper.floor_double(posY + (double)explosionSize + 1.0D);
			int l = MathHelper.floor_double(posZ - (double)explosionSize - 1.0D);
			int j2 = MathHelper.floor_double(posZ + (double)explosionSize + 1.0D);
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List list3 = new ArrayList();
			List list4 = new ArrayList();
			final String SEPARATOR = ",";
			List<Integer> running_experiments;

			List list = world.getEntitiesWithinAABBExcludingEntity(splosion, AxisAlignedBB.fromBounds((double)i, (double)k, (double)l, (double)j, (double)i2, (double)j2));
			for(Object entity : list) {
				//list.forEach(entity->{	//remove special lamda expression. Just causes too many issues
				if(entity instanceof EntityPlayer) {
					EntityPlayerMP entityPlayer = ((EntityPlayerMP)entity);
					list1.add(Enforcer.whitelist.get(entityPlayer.getDisplayNameString().toLowerCase()).toString());
					list4.add(entityPlayer.getDisplayName());

					//This commented if statement makes it so you can't knockback yourself on the corners of the bomb box
					//if(entityPlayer.getDistance(posX, posY, posZ)<radius) {

					double theta = 0 - Math.atan2(posX - entityPlayer.posX, posZ - entityPlayer.posZ);
					entityPlayer.playerNetServerHandler.sendPacket(
							new S27PacketExplosion(posX, posY, posZ, (float)explosionSize,
									new ArrayList(), 
									//Here's where direction of player knockback happens
									new Vec3(2*Math.sin(theta), 1, -2*Math.cos(theta))));

					//}else
					//	entityPlayer.playerNetServerHandler.sendPacket(
					//		new S27PacketExplosion(posX, posY, posZ, (float)explosionSize,
					//		new ArrayList(), 
					//		Vec3.createVectorHelper(0, 0, 0)));	

				}else {
					double theta = 0 - Math.atan2(posX - ((Entity)entity).posX, posZ - ((Entity)entity).posZ);
					//EntityPlayer entity1 = ((EntityPlayer)entity);
					list2.add(((Entity)entity).getClass().getSimpleName());

					//Here's where direction of animal knockback happens
					((Entity)entity).motionX = 2*Math.sin(theta);
					((Entity)entity).motionY = y;
					((Entity)entity).motionZ = -2*Math.cos(theta);
				}

			}

			list3.addAll(list1);	
			list3.addAll(list2);

			StringBuilder csvBuilder = new StringBuilder();

			for(Object entity1 : list3){
				csvBuilder.append(entity1.toString());
				csvBuilder.append(SEPARATOR);
			}

			String csv = csvBuilder.toString();

			//Remove last comma
			if(csv.length()>0)
				csv = csv.substring(0, csv.length() - SEPARATOR.length());

			/**
			 * Add 23-2 and 23-3 events on KBB and FKBB in experiment log files as well as in polycraft-analytics log file. 
			 */
			PlayerKnockBackEvent event = new PlayerKnockBackEvent(player,csv);
			Analytics.onKnockBackEvent(event);

			/**
			 * Add 23-2 and 23-3 events on KBB and FKBB in experiment log files as well as in polycraft-analytics log file. 
			 */
			for(Object entity1 : list4){
				PlayerKnockedBackEvent event1 = new PlayerKnockedBackEvent(entity1.toString(),player);
				Analytics.onKnockedBackEvent(event1);

			}
			return list;
		}
		return null;
	}

	public void render(Entity entity) {
		if(renderTicks < 0)
			renderTicks = maxRenderTicks;
		else
			renderTicks--;
		double distance = 20 * Math.cos(Math.toRadians(entity.rotationPitch%360) * 2);

		double x = -1*Math.sin(Math.toRadians(entity.rotationYaw%360));
		double y = 1;
		double z = Math.cos(Math.toRadians(entity.rotationYaw%360));
		if(distance < ((Math.abs(x)+Math.abs(z))*KBB_RADIUS))
			distance = (Math.abs(x)+Math.abs(z))*KBB_RADIUS;
		else if(distance > 20)
			distance = 20;
		double posX = x*distance + entity.posX;
		double posZ = z*distance + entity.posZ;
		double posY = entity.posY -1.5;

		while((entity.worldObj.isAirBlock(new BlockPos((int) posX, (int)(posY -0.2), (int)posZ))
				|| entity.worldObj.getBlockState(new BlockPos((int) posX, (int)(posY -0.2), (int)posZ)).getBlock().getMaterial() == Material.carpet)
				&& posY >= 1) {
			posY -= 0.25;
			posX += x/4;
			posZ += z/4;
			x -= x/32;
			z -= z/32;
		}

		double x1 = posX - 5;
		double z1 = posZ - 5;
		double x2 = posX + 5;
		double z2 = posZ + 5;

		double xRange = x2 - x1;
		double zRange = z2 - z1;
		double range = Math.max(xRange, zRange);
		double range2 = Math.pow(range, 2);
		double xRange2 = xRange / 2;
		double zRange2 = zRange / 2;




		double xComp = Math.pow(posX - x1 - xRange, 2);
		double yComp = Math.pow(posY, 2);
		double zComp = Math.pow(posZ - z1 - zRange, 2);
		double allDist = Math.sqrt(xComp + yComp + zComp) - range;
		double horizDist = Math.sqrt(xComp + (posY < y ? yComp : 0) + zComp) - range;
		double offset = renderTicks / (double) maxRenderTicks;
		xRange = xRange2 * offset;
		zRange = zRange2 * offset;
		xRange2 = xRange2 * (1 - offset);
		zRange2 = zRange2 * (1 - offset);
		double x1o1 = x1 + xRange;
		double z1o1 = z1 + zRange;
		double x2o1 = x2 - xRange;
		double z2o1 = z2 - zRange;
		double x1o2 = x1 + xRange2;
		double z1o2 = z1 + zRange2;
		double x2o2 = x2 - xRange2;
		double z2o2 = z2 - zRange2;
		double y1 = posY + 0.01;
		double y11 = posY + 0.02;
		double y2 = posY + entity.posY + Y_TOP;
		float horizWidth = lineWidth;
		float vertWidth = lineWidth;
		if (allDist > 0)
			horizWidth *= ((range2 - allDist) / range2);
		if (horizDist > 0)
			vertWidth *= ((range2 - horizDist) / range2);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.2F);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x2, y1, z2);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.2F);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glVertex3d(x1o1, y11, z1o1);
		GL11.glVertex3d(x2o1, y11, z1o1);
		GL11.glVertex3d(x1o1, y11, z2o1);
		GL11.glVertex3d(x2o1, y11, z2o1);

		GL11.glEnd();

		if (horizWidth > 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5F);
			GL11.glLineWidth(horizWidth);
			GL11.glBegin(GL11.GL_LINES);

			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y1, z1);

			GL11.glEnd();
		}

		//		if (vertWidth > 0 && (renderTicks > 20 || renderTicks % 2 == 1)) {
		//			GL11.glDisable(GL11.GL_TEXTURE_2D);
		//			GL11.glEnable(GL11.GL_BLEND);
		//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//			GL11.glDisable(GL11.GL_LIGHTING);
		//			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5F);
		//			GL11.glLineWidth(vertWidth);
		//			GL11.glBegin(GL11.GL_LINES);
		//
		//			GL11.glVertex3d(x1o2, y1, z1o2);
		//			GL11.glVertex3d(x1o2, y2, z1o2);
		//			GL11.glVertex3d(x2o2, y1, z1o2);
		//			GL11.glVertex3d(x2o2, y2, z1o2);
		//			GL11.glVertex3d(x2o2, y1, z2o2);
		//			GL11.glVertex3d(x2o2, y2, z2o2);
		//			GL11.glVertex3d(x1o2, y1, z2o2);
		//			GL11.glVertex3d(x1o2, y2, z2o2);
		//
		//			GL11.glEnd();
		//		}

		// GL11.glEnable(GL11.GL_CULL_FACE);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
}