package net.samongi.CraftingMenu.Recipe.Component.Type;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.samongi.CraftingMenu.Recipe.Component.Component;
import net.samongi.CraftingMenu.Recipe.Component.ItemComponent;
import net.samongi.SamongiLib.Items.ItemUtil;

public class ItemComponentType implements ComponentType
{
  private final String name = "ITEM";

	@Override
	/**This will return the itemstack component based on the string.
	 * Items will make use of Universal ItemStack storage by the server.
	 * This functionality is provided by SamongiLib
	 */
	public Component getComponent(String str)
	{
		String[] split_str = str.split(" "); // For an item, the first element should be the path.
		String glob_path = split_str[0];
		String comp_flags = "";
		String amnt_str = "1"; 
    if(split_str.length > 1) amnt_str = split_str[1];
		int amnt = 1;
		try{amnt = Integer.parseInt(amnt_str);}catch(NumberFormatException e){amnt = 1;}
		if(split_str.length > 2) comp_flags =  split_str[2].toLowerCase();
		// Getting the itemstacks from the global itemstack folder.
		List<ItemStack> items = ItemUtil.getGlobalItems(glob_path);
		if(items == null) return null;
		if(items.isEmpty()) return null;
		ItemStack item = items.get(0); // Gets the first item.
		
		// Setting up all the compare amounts
		boolean comp_lore = false; 
		boolean comp_name = false;
		boolean comp_data = false;
		boolean comp_ench = false;
		if(comp_flags != null)
		{
			if(comp_flags.contains("l")) comp_lore = true;
			if(comp_flags.contains("n")) comp_name = true;
			if(comp_flags.contains("d")) comp_data = true;
			if(comp_flags.contains("e")) comp_ench = true;
		}
		
		ItemComponent comp = new ItemComponent(item, amnt);
		comp.setCompareData(comp_data);
		comp.setCompareEnch(comp_ench);
		comp.setCompareLore(comp_lore);
		comp.setCompareName(comp_name);
		return comp;
	}

	@Override
	public String getName(){return this.name;}
}
