package net.samongi.CraftingMenu.Recipe.Result.Type;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import net.samongi.CraftingMenu.Recipe.Result.MaterialResult;
import net.samongi.CraftingMenu.Recipe.Result.Result;

public class MaterialResultType implements ResultType
{
  private final String name = "MATERIAL";

	@SuppressWarnings("deprecation")
	@Override
	public Result getResult(String str)
	{
		// Splitting the string and assigning the splits to be better understood.
		String[] split_str = str.split(":"); // Splits the string by the colons.
		String mat_str = null;
		String dat_str = null;
		String amn_str = null;
		if(split_str.length > 0) mat_str = split_str[0];
		if(split_str.length > 1) dat_str = split_str[1];
		if(split_str.length > 2) amn_str = split_str[2];
		
		// Getting all information from the type.
		int type_id = 0;
		try{type_id = Integer.parseInt(mat_str);}catch(NumberFormatException e){type_id = -1;}
		Material mat = null;
		if(type_id >= 0) mat = Material.getMaterial(type_id);
		else mat = Material.getMaterial(mat_str);
		if(mat == null) return null; // Error occured, returning null because nothing was found.
		
		// Getting all information from the data
		int dat_num = 0;
		if(dat_str != null) try{dat_num = Integer.parseInt(dat_str);}catch(NumberFormatException e){dat_num = 0;}
		
		// Getting the amount
		// Amounts have a range as opposed to a singular amount.
		int max_amn = 1;
		int min_amn = 1;
		if(amn_str != null)
		{
  		String[] split_amn_str = amn_str.split("-");
  		String min_amn_str = null;
  		String max_amn_str = null;
  		if(split_amn_str.length > 0) min_amn_str = split_amn_str[0];
  		if(split_amn_str.length > 1) max_amn_str = split_amn_str[1];
  		if(min_amn_str != null) try{min_amn = Integer.parseInt(min_amn_str);}catch(NumberFormatException e){min_amn = 1;}
  		if(max_amn_str != null) try{max_amn = Integer.parseInt(max_amn_str);}catch(NumberFormatException e){max_amn = -1;}
  		if(max_amn < min_amn) max_amn = min_amn;
		}
		
		// Compiling the component
		MaterialData mat_data = new MaterialData(mat, (byte) dat_num);
		return new MaterialResult(mat_data, min_amn, max_amn);
	}
	

	@Override
	public String getName(){return this.name;}
  
}
