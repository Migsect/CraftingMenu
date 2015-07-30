package net.samongi.CraftingMenu.Recipe.Component;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelComponent implements Component
{
  private final int level;
  public LevelComponent(int level)
  {
    this.level = level;
  }
  
  @Override
  public boolean hasComponent(Player player)
  {
    return level <= player.getLevel();
  }

  @Override
  public void removeComponent(Player player)
  {
    player.setLevel(player.getLevel() - level); 
  }

  @Override
  public String getDisplay()
  {
    if(level > 1) return level + " Levels";
    else return level + " Level";
  }

  @Override
  public ItemStack[] getMenuItems()
  {
    ItemStack item = new ItemStack(Material.BOOK);
    
    ItemMeta im = item.getItemMeta();
    if(level > 1) im.setDisplayName(ChatColor.GREEN + "" + level + " Levels");
    else im.setDisplayName(ChatColor.GREEN + "" + level + " Level");
    item.setItemMeta(im);
    
    return new ItemStack[]{item};
  }
}
