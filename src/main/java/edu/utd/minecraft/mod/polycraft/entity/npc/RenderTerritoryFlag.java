package edu.utd.minecraft.mod.polycraft.entity.npc;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.block.Block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.IItemRenderer;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class RenderTerritoryFlag extends RenderLiving
{
    private static final ResourceLocation snowManTextures = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/territory_flag.png");
;
    /** A reference to the Snowman model in RenderSnowMan. */
    private ModelTerritoryFlag TerritoryFlagModel;
    private static final String __OBFID = "CL_00001025";

    public RenderTerritoryFlag()
    {
        super(new ModelTerritoryFlag(), 0.5F);
        this.TerritoryFlagModel = (ModelTerritoryFlag)super.mainModel;
        this.setRenderPassModel(this.TerritoryFlagModel);
    }

    protected void renderEquippedItems(EntityTerritoryFlag p_77029_1_, float p_77029_2_)
    {

        
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTerritoryFlag p_110775_1_)
    {
        return snowManTextures;
    }

    protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_)
    {
        
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((EntityTerritoryFlag)p_110775_1_);
    }
}