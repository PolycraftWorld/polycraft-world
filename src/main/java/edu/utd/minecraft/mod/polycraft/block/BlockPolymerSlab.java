package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.util.DummyBlockProperty;

public class BlockPolymerSlab extends BlockSlab implements BlockBouncy {

	public final PolymerSlab polymerSlab;
	private final BlockPolymerHelper helper;
	
	public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
	/**
	 * Used purely so that the slab places correctly
	 */
	public static final PropertyEnum<BlockPolymerSlab.EnumType> VARIANT = PropertyEnum.<BlockPolymerSlab.EnumType>create("variant", BlockPolymerSlab.EnumType.class);

	public BlockPolymerSlab(final PolymerSlab polymerSlab) {
		super(Material.cloth);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerSlab = polymerSlab;
		this.helper = new BlockPolymerHelper(polymerSlab.source.source.source, 15);
		IBlockState iblockstate = this.blockState.getBaseState();
		if (this.isDouble())
        {
            iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf(false));
        }
        else
        {
            iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(SEAMLESS, Boolean.valueOf(false));
        }

        this.setDefaultState(iblockstate.withProperty(VARIANT, BlockPolymerSlab.EnumType.POLYMER));
		this.useNeighborBrightness = true; // Makes it so that you don't get dark patches on the block
	}
	
	/**
	 * Makes it so that your block doesn't accept meta data
	 */
	@Override
	public String getUnlocalizedName(int meta) {
		return this.getUnlocalizedName();
	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		return helper.getIcon(side, colorIndex);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		helper.registerBlockIcons(p_149651_1_);
//	}

	@Override
	public int getActiveBounceHeight() {
		return polymerSlab.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return helper.getMomentumReturnedOnPassiveFall();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_) {
		return PolycraftRegistry.getItem(polymerSlab.itemSlabName);
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(PolycraftRegistry.getItem(polymerSlab.name), 2, this.getMetaFromState(state) & 7);
	}
	
	/**
	 * Only use if your block has multiple types, i.e {@link ChipTypes}. If
	 * yours does not then just use what I put
	 */
	@Override
	public IProperty getVariantProperty() {
		return VARIANT;
	}

	/**
	 * Only use if your block has multiple types, i.e {@link ChipTypes}. If yours does not then just use what I put
	 */
//	@Override
//	public Comparable<?> getTypeForItem(ItemStack stack) {
//		return EnumBlockHalf.BOTTOM;
//	}

	/**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPolymerSlab.EnumType.byMetadata(meta & 7));

        if (this.isDouble())
        {
            iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf((meta & 8) != 0));
        }
        else
        {
            iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((BlockPolymerSlab.EnumType)state.getValue(VARIANT)).getMetadata();

        if (this.isDouble())
        {
            if (((Boolean)state.getValue(SEAMLESS)).booleanValue())
            {
                i |= 8;
            }
        }
        else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

	/**
	 * Register it so that your block has its own half. MUST DO!!
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] {SEAMLESS, VARIANT, HALF});
	}

	@Override
	public boolean isDouble() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getVariant(ItemStack stack) {
		return BlockPolymerSlab.EnumType.byMetadata(stack.getMetadata() & 7);
	}
	
	public static enum EnumType implements IStringSerializable
    {
        POLYMER(0, MapColor.stoneColor, "polymer");

        private static final BlockPolymerSlab.EnumType[] META_LOOKUP = new BlockPolymerSlab.EnumType[values().length];
        private final int meta;
        private final MapColor field_181075_k;
        private final String name;
        private final String unlocalizedName;

        private EnumType(int p_i46381_3_, MapColor p_i46381_4_, String p_i46381_5_)
        {
            this(p_i46381_3_, p_i46381_4_, p_i46381_5_, p_i46381_5_);
        }

        private EnumType(int p_i46382_3_, MapColor p_i46382_4_, String p_i46382_5_, String p_i46382_6_)
        {
            this.meta = p_i46382_3_;
            this.field_181075_k = p_i46382_4_;
            this.name = p_i46382_5_;
            this.unlocalizedName = p_i46382_6_;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public MapColor func_181074_c()
        {
            return this.field_181075_k;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockPolymerSlab.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (BlockPolymerSlab.EnumType blockstoneslab$enumtype : values())
            {
                META_LOOKUP[blockstoneslab$enumtype.getMetadata()] = blockstoneslab$enumtype;
            }
        }
    }
}