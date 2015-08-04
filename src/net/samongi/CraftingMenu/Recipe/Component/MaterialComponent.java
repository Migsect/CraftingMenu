package net.samongi.CraftingMenu.Recipe.Component;

import java.util.HashMap;
import java.util.Random;

import net.samongi.SamongiLib.Items.ItemUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialComponent implements Component
{
  private final MaterialData material;
  private final int max_amnt;
  private final int min_amnt;
  private final boolean check_data;
  
  public MaterialComponent(MaterialData mat, int amt)
  {
    this.material = mat;
    this.min_amnt = amt;
    this.max_amnt = amt;
    this.check_data = false;
  }
  public MaterialComponent(MaterialData mat, int amt, boolean check_data)
  {
    this.material = mat;
    this.min_amnt = amt;
    this.max_amnt = amt;
    this.check_data = check_data;
  }
  public MaterialComponent(MaterialData mat, int min_amt, int max_amt, boolean check_data)
  {
  	this.material = mat;
    this.min_amnt = min_amt;
    this.max_amnt = max_amt;
    this.check_data = check_data;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public boolean hasComponent(Player player)
  {
    Inventory inv = player.getInventory();
    ItemStack[] items = inv.getContents();
    int rem_amnt = this.max_amnt;
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
    // Getting the amount to remove, which will be between the min and max range.
    Random rand = new Random();
    int rem_amnt = this.min_amnt + rand.nextInt(this.max_amnt - this.min_amnt + 1);
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
      // When we reach 0, we will break because we do not need to search any more items.
      if(rem_amnt == 0) break;
    }
    player.updateInventory();
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getDisplay()
  {
  	// Getting the material name, this should be replaced with a more friendly means of doing so.
    String raw_name = this.material.getItemType().toString();
    
    // getting the data
    String raw_data = "";
    if(this.check_data) raw_data = "" + this.material.getData();
    else raw_data = "?";
    
    // Getting the raw_amount
    String raw_amnt = "" + this.min_amnt + "-" + this.max_amnt;
    if(this.min_amnt == this.max_amnt) raw_amnt = "" + this.max_amnt;
    
    // Compiling all the above variables.
    return raw_name + ":" + raw_data + " x" + raw_amnt;
  }
  @Override
  public ItemStack[] getMenuItems()
  {
    int stacks = (int) Math.ceil(this.max_amnt / 64.0);
    ItemStack[] items = new ItemStack[stacks];
    int remain = this.max_amnt;
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
