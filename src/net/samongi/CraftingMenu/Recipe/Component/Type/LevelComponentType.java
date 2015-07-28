package net.samongi.CraftingMenu.Recipe.Component.Type;

import net.samongi.CraftingMenu.Recipe.Component.Component;
import net.samongi.CraftingMenu.Recipe.Component.LevelComponent;

public class LevelComponentType implements ComponentType
{
  private final String name = "LEVEL";
	
	@Override
	public Component getComponent(String str)
	{
	  /* This will grab the level from a string.  The string generally has the form of:
	   *   X lv
	   *   X lvs
	   *   X level
	   *   X levels
	   * The way we parse the component is by splitting and reading the second item to see if it is a level indicator
	   */
		String[] split_str = str.split(" ");
		if(split_str.length != 2) return null;
		String ind_str = split_str[1].toLowerCase();
		String amn_str = split_str[0];
		if(!ind_str.equals("lv") && 
				!ind_str.equals("lvs") &&
				!ind_str.equals("level") &&
				!ind_str.equals("levels")) return null;
		int amn_num = 1;
		try{amn_num = Integer.parseInt(amn_str);}catch(NumberFormatException e){amn_num = -1;}
		if(amn_num <= 0) return null;
		return new LevelComponent(amn_num);
		
	}

	@Override
	public String getName(){return this.name;}

}
