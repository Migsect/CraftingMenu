package net.samongi.CraftingMenu.Recipe.Result;

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
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getDisplay()
  {
    // TODO Auto-generated method stub
    return null;
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
