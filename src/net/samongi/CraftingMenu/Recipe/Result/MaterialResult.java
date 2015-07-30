package net.samongi.CraftingMenu.Recipe.Result;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialResult implements Result
{
  private final MaterialData material;
  private final int max_amount;
  private final int min_amount;
  
  public MaterialResult(MaterialData mat, int amt)
  {
    this.material = mat;
    this.max_amount = amt;
    this.min_amount = amt;
  }
  public MaterialResult(MaterialData mat, int min_amt, int max_amt)
  {
  	this.material = mat;
  	this.min_amount = min_amt;
  	this.max_amount = max_amt;
  }
  
  @Override
  public void addResult(Player player)
  {
  	Random rand = new Random();
    int rem_amnt = this.min_amount + rand.nextInt(this.max_amount - this.min_amount + 1);
    while(rem_amnt > 0)
    {
      ItemStack item = new ItemStack(material.getItemType());
      int max_amnt = item.getMaxStackSize();
      if(max_amnt < rem_amnt) 
      {
        item.setAmount(max_amnt);
        rem_amnt -= max_amnt;
      }
      else
      {
        item.setAmount(rem_amnt);
        rem_amnt = 0;
      }
      // Giving the item to the player
      HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);
      if(ret != null) // if the item wasn't added or fully added, we'll drop it to the ground
      {
        for(int i : ret.keySet())
        {
          player.getWorld().dropItem(player.getLocation(), ret.get(i));
        }
      }
      // all items should be dropped now.
    }
    // the result should be added.
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getDisplay()
  {
    String raw_name = this.material.getItemType().toString();
    String raw_data = "" + this.material.getData();
    String raw_amnt = "" + this.min_amount + "-" + this.max_amount;
    return raw_name + ":" + raw_data + " x" + raw_amnt;
  }

  @Override
  public ItemStack[] getMenuItems()
  {
    int stacks = (int) Math.ceil((this.max_amount + this.min_amount) / 2 / 64.0);
    ItemStack[] items = new ItemStack[stacks];
    int remain = this.max_amount;
    for(int i = 0; i < stacks; i++)
    {
      ItemStack item = new ItemStack(this.material.getItemType());
      item.setData(this.material);
      if(remain >= 64) 
      {
        item.setAmount(64);
        remain -= 64;
      }
      else
      {
        item.setAmount(remain);
        remain = 0;
      }
      items[i] = item;
    }
    return items;
  }
}
