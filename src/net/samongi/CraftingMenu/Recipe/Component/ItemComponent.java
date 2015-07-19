package net.samongi.CraftingMenu.Recipe.Component;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemComponent implements Component
{
  private final ItemStack item;
  private final int amount;
  
  private boolean compare_lore = true;
  private boolean compare_name = true;
  private boolean compare_ench = true;
  private boolean compare_data = true;
  
  public ItemComponent(ItemStack item)
  {
    this.item = item.clone();
    this.amount = item.getAmount();
    this.item.setAmount(1);
  }
  public ItemComponent(ItemStack item, int amount)
  {
    this.item = item.clone();
    this.amount = amount;
    this.item.setAmount(1);
  }
  
  public void setCompareLore(boolean compare_lore){this.compare_lore = compare_lore;}
  public void setCompareName(boolean compare_name){this.compare_name = compare_name;}
  public void setCompareEnch(boolean compare_ench){this.compare_ench = compare_ench;}
  public void setCompareData(boolean compare_data){this.compare_data = compare_data;}

  @Override
  public boolean hasComponent(Player player)
  {
    Inventory inv = player.getInventory();
    ItemStack[] items = inv.getContents();
    int rem_amnt = this.amount;
    for(ItemStack i : items)
    {
      // Checking all the conditions.
      if(!i.getType().equals(item.getType())) continue;
      if(compare_data && !i.getData().equals(item.getData())) continue;
      if(compare_name && 
          i.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName() && 
          !i.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) continue;
      if(compare_ench && !i.getEnchantments().equals(item.getEnchantments())) continue;
      if(compare_lore && !i.getItemMeta().getLore().equals(item.getItemMeta().getLore())) continue;
      if(i.getAmount() >= rem_amnt) return true;
      else rem_amnt -= i.getAmount();
    }
    if(rem_amnt > 0) return false;
    return true;
  }

  @Override
  public void removeComponent(Player player)
  {
    Inventory inv = player.getInventory();
    HashMap<Integer, ? extends ItemStack> items = inv.all(this.item.getType());
    int rem_amnt = this.amount;
    for(int index : items.keySet())
    {
      ItemStack i = items.get(index);
      // Checking all the conditions.
      if(!i.getType().equals(item.getType())) continue;
      if(compare_data && !i.getData().equals(item.getData())) continue;
      if(compare_name && 
          i.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName() && 
          !i.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) continue;
      if(compare_ench && !i.getEnchantments().equals(item.getEnchantments())) continue;
      if(compare_lore && !i.getItemMeta().getLore().equals(item.getItemMeta().getLore())) continue;
      if(i.getAmount() > rem_amnt)
      {
        i.setAmount(i.getAmount() - rem_amnt); // removing the number needed.
        rem_amnt = 0;
        return;
      }
      else
      {
        rem_amnt -= i.getAmount();
        inv.clear(index);
      }
    }
  }

  @Override
  public String getDisplay()
  {
    // An Item Component will have its material name if it has no name.
    String mat_name = "";
    if(this.compare_data) mat_name = this.item.getType().toString() + ":" + this.item.getDurability() + " x" + this.amount;
    else mat_name = this.item.getType().toString() + " x" + this.amount;
    String name = "";
    if(this.compare_name && item.getItemMeta().hasDisplayName()) name = item.getItemMeta().getDisplayName() + " [" + mat_name + "]";
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
