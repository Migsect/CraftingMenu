package net.samongi.CraftingMenu.Recipe.Result;

import java.util.HashMap;

import net.samongi.SamongiLib.Items.ItemUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialResult implements Result
{
  private final MaterialData material;
  private final int amount;
  
  public MaterialResult(ItemStack item)
  {
    this.amount = item.getAmount();
    this.material = item.getData();
  }
  public MaterialResult(ItemStack item, int amt)
  {
    this.material = item.getData();
    this.amount = amt;
  }
  public MaterialResult(MaterialData mat, int amt)
  {
    this.material = mat;
    this.amount = amt;
  }
  
  @Override
  public void addResult(Player player)
  {
    int rem_amnt = amount;
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
  
  public static Result getResult(String str)
  {
    ItemStack item = ItemUtil.getItemStack(str);
    if(item == null) return null;
    MaterialData data = item.getData();
    
    String[] split_str = str.split(":");
    int amnt = 1;
    if(split_str.length == 3) try{amnt =Integer.parseInt(split_str[2]);}catch(NumberFormatException e){amnt = 0;}
    if(amnt < 1) return null;
    
    return new MaterialResult(data, amnt);
  }

}
