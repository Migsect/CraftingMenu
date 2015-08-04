package net.samongi.CraftingMenu.Recipe.Result.Type;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.samongi.CraftingMenu.Recipe.Result.ItemResult;
import net.samongi.CraftingMenu.Recipe.Result.Result;
import net.samongi.SamongiLib.Items.ItemUtil;

public class ItemResultType implements ResultType
{
	private final String name = "ITEM";
	@Override
	public Result getResult(String str)
	{
	  String[] split_str = str.split(" "); // For an item, the first element should be the path.
    String glob_path = split_str[0];
    String amnt_str = "1"; 
    if(split_str.length > 1) amnt_str = split_str[1];
    int amnt = 1;
    try{amnt = Integer.parseInt(amnt_str);}catch(NumberFormatException e){amnt = 1;}
    // Getting the itemstacks from the global itemstack folder.
    List<ItemStack> items = ItemUtil.getGlobalItems(glob_path);
    if(items == null) return null;
    if(items.isEmpty()) return null;
    ItemStack item = items.get(0); // Gets the first item.
    
		return new ItemResult(item, amnt);
	}

	@Override
	public String getName(){return this.name;}
  
}
