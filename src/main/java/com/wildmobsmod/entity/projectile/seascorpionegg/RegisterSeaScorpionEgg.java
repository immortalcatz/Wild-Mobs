package com.wildmobsmod.entity.projectile.seascorpionegg;

import com.wildmobsmod.main.MainRegistry;

import cpw.mods.fml.common.registry.EntityRegistry;

public class RegisterSeaScorpionEgg {

	public static void mainRegistry(){
		registerModEntity();
	}

	public static void registerModEntity(){

		createEntity(EntitySeaScorpionEgg.class, "SeaScorpionEgg", 0xFFFFFF, 0xFFFFFF);

	}

	public static void createEntity(Class entityClass, String entityName, int solidColor, int spotColor)
	{
		boolean flag = MainRegistry.enableSeaScorpion;
		if (flag)
		{
			EntityRegistry.registerModEntity(entityClass, entityName, 27, MainRegistry.modInstance, 82, 1, true);
		}
	}
}