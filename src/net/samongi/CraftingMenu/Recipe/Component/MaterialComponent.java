package net.samongi.CraftingMenu.Recipe.Component;

import java.util.HashMap;

import net.samongi.SamongiLib.Items.ItemUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialComponent implements Component
{
  private final MaterialData material;
  private final int amount;
  private final boolean check_data;
  
  public MaterialComponent(ItemStack item)
  {
    this.amount = item.getAmount();
    this.material = item.getData();
    this.check_data = false;
  }
  public MaterialComponent(ItemStack item, int amt)
  {
    this.material = item.getData();
    this.amount = amt;
    this.check_data = false;
  }
  public MaterialComponent(MaterialData mat, int amt)
  {
    this.material = mat;
    this.amount = amt;
    this.check_data = false;
  }
  public MaterialComponent(MaterialData mat, int amt, boolean check_data)
  {
    this.material = mat;
    this.amount = amt;
    this.check_data = check_data;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public boolean hasComponent(Player player)
  {
    Inventory inv = player.getInventory();
    ItemStack[] items = inv.getContents();
    int rem_amnt = this.amount;
    for(ItemStack i : items)
    {
      if(i == null) continue;
      Material mat = i.getData().getItemType();
      if(!mat.equals(material.getItemType())) continue;
      byte data = i.getData().getData();
      if(data != material.getData() && this.check_data) continue;
      int amnt = i.getAmount();
      if(rem_amnt >= amnt) rem_amnt -= amnt;
      else rem_amnt = 0;
    }
    return rem_amnt == 0;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void removeComponent(Player player)
  {
    Inventory inv = player.getInventory();
    HashMap<Integer, ? extends ItemStack> items = inv.all(material.getItemType());
    int rem_amnt = this.amount;
    for(int i : items.keySet())
    {
      ItemStack item = items.get(i);
      if(material.getData() != item.getData().getData() && this.check_data) continue;
      int amnt = item.getAmount();
      if(amnt > rem_amnt) // If we have more in the itemstack then we need.  The item will persist.
      {
        inv.getItem(i).setAmount(amnt - rem_amnt);
        rem_amnt = 0;
      }
      else // we have equal to or more needed than the itemstack requires.
      {
        rem_amnt -= amnt;
        inv.clear(i);
      }
      
      if(rem_amnt == 0) break;
    }
    player.updateInventory();
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getDisplay()
  {
    String raw_name = this.material.getItemType().toString();
    String raw_data = "" + this.material.getData();
    String raw_amnt = "" + this.amount;
    return raw_name + ":" + raw_data + " x" + raw_amnt;
  }
  @Override
  public ItemStack[] getMenuItems()
  {
    int stacks = (int) Math.ceil(amount / 64.0);
    ItemStack[] items = new ItemStack[stacks];
    int remain = this.amount;
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

  public static Component getComponent(String str)
  {
    ItemStack item = ItemUtil.getItemStack(str);
    if(item == null) return null;
    MaterialData data = item.getData();
    
    String[] split_str = str.split(":");
    int amnt = 1;
    if(split_str.length == 3) try{amnt = Integer.parseInt(split_str[2]);}catch(NumberFormatException e){amnt = 0;}
    if(amnt < 1) return null;
    
    return new MaterialComponent(data, amnt, true);
  }
}
