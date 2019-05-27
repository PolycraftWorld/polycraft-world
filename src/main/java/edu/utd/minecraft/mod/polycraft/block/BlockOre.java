package edu.utd.minecraft.mod.polycraft.block;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class BlockOre extends net.minecraft.block.BlockOre {

	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

	public final Ore ore;
	private final LabelTexture labelTexture;

	public BlockOre(final Ore ore) {
		this.ore = ore;
		if(ore.name == "OilField"){
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
		}
		if ((ore.source) instanceof Element)
		{
			final String texture = PolycraftMod.getFileSafeName(ore.name);
			this.labelTexture = new LabelTexture(texture, texture, texture + "_flipped");
		}
		else
		{
			final String texture = PolycraftMod.getFileSafeName(ore.name);
			this.labelTexture = new LabelTexture(texture, texture, texture + "_flipped");
		}

		this.setHardness(ore.hardness);
		this.setResistance(ore.resistance);
		this.setStepSound(Block.soundTypePiston);
		this.setUnlocalizedName(PolycraftMod.getFileSafeName(ore.name));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		if(ore.name == "OilField") {
			return getDefaultState().withProperty(LEVEL, meta);
		}else {
			return getDefaultState();
		}
	}

	protected BlockState createBlockState() {
		if(ore == null)
			return super.createBlockState();

		if(ore.name == "OilField") {
			return new BlockState(this, new IProperty[]{LEVEL});
		}else {
			return super.createBlockState();
		}
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		if(state.getPropertyNames().contains(LEVEL)) {
			return ((Integer)state.getValue(LEVEL)).intValue();
		}else {
			return 0;
		}
	}

	public static void checkJSONs(Ore ore, String path){
		String texture = PolycraftMod.getFileSafeName(ore.name);
		File json = new File(path + "blockstates\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//BlockState file
				String fileContent = String.format("{\n" +
						"  \"variants\": {\n" +
						"    \"normal\": { \"model\": \"polycraft:%s\" }\n" +
						"  }\n" +
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();

				//Model file
				fileContent = String.format("{\n" +
						"  \"parent\": \"block/cube_all\",\n" +
						"  \"textures\": {\n" +
						"    \"all\": \"polycraft:blocks/%s\"\n" +
						"  }\n" +
						"}", texture);

				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();

				//Item model file
				fileContent = String.format("{\n" +
						"    \"parent\": \"polycraft:%s\",\n" +
						"    \"display\": {\n" +
						"        \"thirdperson\": {\n" +
						"            \"rotation\": [ 10, -45, 170 ],\n" +
						"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
						"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
						"        }\n" +
						"    }\n" +
						"}", texture);

				writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	//	@Override
	//	public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_) {
	//		return MathHelper.getRandomIntegerInRange(rand, ore.dropExperienceMin, ore.dropExperienceMax);
	//	}

}