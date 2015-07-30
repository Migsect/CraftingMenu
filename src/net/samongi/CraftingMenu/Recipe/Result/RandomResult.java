package net.samongi.CraftingMenu.Recipe.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RandomResult implements Result
{
  private Map<Result, Integer> results = new HashMap<>();
  
  public RandomResult(){;} // Does nothing really.
  
  /**Adds the result to this random Material result
   * Each result has a weight as opposed to a chance as this
   * will allow more dynamic creation
   * 
   * @param result
   * @param weight
   */
  public void insertResult(Result result, int weight){results.put(result, weight);}
  
  private int getWeightSum()
  {
  	int sum = 0;
  	for(Integer i : this.results.values()) sum += i;
  	return sum;
  }
  private double getProbability(int weight){return weight / (double) this.getWeightSum();}
  
	@Override
	public void addResult(Player player)
	{
		// TODO
	}

	@Override
	public String getDisplay()
	{
		return "Check Results, Randomized Result";
		/*
		String ret_s = "";
		// This is going to result all the results.
		for(Result r : results.keySet())
		{
			int weight = results.get(r);
			// the probability of the item to get made.
			double probability = this.getProbability(weight);
			ret_s += r.getDisplay() + String.format("%.2f", probability * 100) + " % ";
		}
		return ret_s;
		*/
	}

	@Override
	public ItemStack[] getMenuItems()
	{
		List<ItemStack> ret_items = new ArrayList<>();
		// This is going to result all the results.
		for(Result r : results.keySet())
		{
			int weight = results.get(r);
			// the probability of the item to get made.
			double probability = this.getProbability(weight);
			ItemStack[] items = r.getMenuItems();
			for(ItemStack i : items)
			{
				ItemMeta im = i.getItemMeta();
				List<String> lore = im.getLore();
				if(lore == null) lore = new ArrayList<String>();
				lore.add("Probability: " + String.format("%.2f", probability * 100) + "%");
				im.setLore(lore);
				i.setItemMeta(im);
			}
			ret_items.addAll(Arrays.asList(items));
		}
		return (ItemStack[]) ret_items.toArray();
	}

}
