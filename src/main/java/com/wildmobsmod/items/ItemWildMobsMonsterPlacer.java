package com.wildmobsmod.items;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.wildmobsmod.entity.passive.deer.EntityDeer;
import com.wildmobsmod.entity.passive.fox.EntityFox;
import com.wildmobsmod.entity.passive.jellyfish.EntityJellyfish;
import com.wildmobsmod.entity.passive.ocelot.EntityWMOcelot;
import com.wildmobsmod.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWildMobsMonsterPlacer extends ItemMonsterPlacer
{
    @SideOnly(Side.CLIENT)
    private IIcon theIcon;
    protected int colorBase = 0x000000;
    protected int colorSpots = 0xFFFFFF;
    protected int entityToSpawnVariant = 0;
    protected boolean booleanType = false;
    protected String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected String spawnEggName = "";
    protected EntityLiving entityToSpawn = null;

    public ItemWildMobsMonsterPlacer()
    {
        super();
    }
    
    public ItemWildMobsMonsterPlacer(String parEntityToSpawnName, int parPrimaryColor, int parSecondaryColor, int parEntityToSpawnVariant, boolean parBooleanType)
    {
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
        entityToSpawnVariant = parEntityToSpawnVariant;
        booleanType = parBooleanType;
        System.out.println("Spawn egg constructor for "+entityToSpawnName);
    }
    
    public ItemWildMobsMonsterPlacer(String parEntityToSpawnName, int parPrimaryColor, int parSecondaryColor, int parEntityToSpawnVariant, boolean parBooleanType, String parSpawnEggName)
    {
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
        entityToSpawnVariant = parEntityToSpawnVariant;
        booleanType = parBooleanType;
        spawnEggName = parSpawnEggName;
        System.out.println("Spawn egg constructor for "+entityToSpawnName);
    }
    
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            Block block = par3World.getBlock(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            double d0 = 0.0D;

            if (par7 == 1 && block.getRenderType() == 11)
            {
                d0 = 0.5D;
            }

            Entity entity = spawnEntity(par3World, par4 + 0.5D, par5 + d0, par6 + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
    
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par2World.isRemote)
        {
            return par1ItemStack;
        }
        else
        {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

            if (movingobjectposition == null)
            {
                return par1ItemStack;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!par2World.canMineBlock(par3EntityPlayer, i, j, k))
                    {
                        return par1ItemStack;
                    }

                    if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition
                          .sideHit, par1ItemStack))
                    {
                        return par1ItemStack;
                    }

                    if (par2World.getBlock(i, j, k) instanceof BlockLiquid)
                    {
                        Entity entity = spawnEntity(par2World, i, j, k);

                        if (entity != null)
                        {
                            if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                            {
                                ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());
                            }

                            if (!par3EntityPlayer.capabilities.isCreativeMode)
                            {
                                --par1ItemStack.stackSize;
                            }
                        }
                    }
                }

                return par1ItemStack;
            }
        }
    }
    
    public Entity spawnEntity(World parWorld, double parX, double parY, double parZ)
    {
       if (!parWorld.isRemote)
       {
            entityToSpawnNameFull = Strings.MODID+"."+entityToSpawnName;
            if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull))
            {
                entityToSpawn = (EntityLiving) EntityList.createEntityByName(entityToSpawnNameFull, parWorld);
                entityToSpawn.setLocationAndAngles(parX, parY, parZ, MathHelper.wrapAngleTo180_float(parWorld.rand.nextFloat() * 360.0F), 0.0F);
                parWorld.spawnEntityInWorld(entityToSpawn);
                entityToSpawn.onSpawnWithEgg((IEntityLivingData)null);
                if (entityToSpawn instanceof EntityDeer)
                {
                	((EntityDeer) entityToSpawn).setSkin(entityToSpawnVariant);
                }
                else if (entityToSpawn instanceof EntityFox)
                {
                	((EntityFox) entityToSpawn).setSkin(entityToSpawnVariant);
                }
                else if (entityToSpawn instanceof EntityJellyfish)
                {
                	((EntityJellyfish) entityToSpawn).setNether(booleanType);
                	if (booleanType == false)
                	{
                    	((EntityJellyfish) entityToSpawn).setSkin(parWorld.rand.nextInt(6));
                	}
                	else
                	{
                    	((EntityJellyfish) entityToSpawn).setSkin(entityToSpawnVariant);
                	}
                }
                else if (entityToSpawn instanceof EntityWMOcelot)
                {
                	((EntityWMOcelot) entityToSpawn).setTameSkin(entityToSpawnVariant);
                }
                entityToSpawn.playLivingSound();
            }
            else
            {
                //DEBUG
                System.out.println("Entity not found "+entityToSpawnName);
            }
        }
      
        return entityToSpawn;
    }


    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item parItem, CreativeTabs parTab, List parList)
    {
        parList.add(new ItemStack(parItem, 1, 0));     
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType)
    {
        return (parColorType == 0) ? colorBase : colorSpots;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        theIcon = par1IconRegister.registerIcon(getIconString() + "_overlay");
    }
    
    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int parDamageVal, int parRenderPass)
    {
        return parRenderPass > 0 ? theIcon : super.getIconFromDamageForRenderPass(parDamageVal, parRenderPass);
    }
    
    public void setColors(int parColorBase, int parColorSpots)
    {
     colorBase = parColorBase;
     colorSpots = parColorSpots;
    }
    
    public int getColorBase()
    {
     return colorBase;
    }
    
    public int getColorSpots()
    {
     return colorSpots;
    }
    
    public void setEntityToSpawnName(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
        entityToSpawnNameFull = Strings.MODID+"."+entityToSpawnName; 
    }
    
    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = Strings.MODID+"."+entityToSpawnName;
        String s2 = Strings.MODID+"."+spawnEggName;

        if (s1 != null)
        {
        	if (this.spawnEggName != "")
        	{
        		s = s + " " + StatCollector.translateToLocal("entity." + s2 + ".name");
        	}
        	else
        	{
        		s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        	}
        }

        return s;
    }
}