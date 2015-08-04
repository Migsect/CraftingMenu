package net.samongi.CraftingMenu.Recipe.Result;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemResult implements Result
{
  private final ItemStack item;
  private final int amount;
  
  public ItemResult(ItemStack item)
  {
    this.item = item.clone();
    this.amount = item.getAmount();
    this.item.setAmount(1);
  }
  public ItemResult(ItemStack item, int amount)
  {
    this.item = item.clone();
    this.amount = amount;
    this.item.setAmount(1);
  }
  @Override
  public void addResult(Player player)
  {
    ItemStack item_clone = this.item.clone();
    int max_stack = item_clone.getMaxStackSize();
    int stacks = this.amount / max_stack;
    int remain = this.amount % max_stack;
    for(int i = 0; i < stacks ; i++) 
    {
      item_clone.setAmount(max_stack);
      HashMap<Integer, ItemStack> no_fit = player.getInventory().addItem(item_clone);
      for(ItemStack s : no_fit.values()) player.getWorld().dropItem(player.getLocation(), s); 
    }
    if(remain != 0)
    {
      item_clone.setAmount(remain);
      HashMap<Integer, ItemStack> no_fit = player.getInventory().addItem(item_clone);
      for(ItemStack s : no_fit.values()) player.getWorld().dropItem(player.getLocation(), s);
    }
  }

  @Override
  public String getDisplay()
  {
    // An Item Component will have its material name if it has no name.
    String mat_name = "";
    mat_name = this.item.getType().toString() + ":" + this.item.getDurability() + " x" + this.amount;
    String name = "";
    if(item.getItemMeta().hasDisplayName()) name = item.getItemMeta().getDisplayName() + " [" + mat_name + "]";
    else name = mat_name;
    
    return name;
  }

  @Override
  public ItemStack[] getMenuItems()
  {
    int stacks = (int) Math.ceil(amount / 64.0);
    ItemStack[] items = new ItemStack[stacks];
    int remain = this.amount;
    for(int i = 0; i < stacks; i++)
    {
      ItemStack item = this.item.clone();
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
