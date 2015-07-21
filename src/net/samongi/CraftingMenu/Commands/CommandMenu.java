package net.samongi.CraftingMenu.Commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.samongi.CraftingMenu.Menu.Menu;
import net.samongi.CraftingMenu.Menu.MenuManager;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandMenu extends BaseCommand
{
  
  public CommandMenu(String command_path)
  {
    super(command_path);
    
    
    this.permission = "craftmenu.commandopen";
    
    this.allowed_senders.add(SenderType.PLAYER);
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }

  private boolean hasPermission(Player player, String menu)
  {
    if(player.isOp()) return true;
    if(player.hasPermission("craftmenu.commandopen.*")) return true;
    else return player.hasPermission("craftmenu.commandopen." + menu);
  }
  
  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    MenuManager manager = MenuManager.getManager();
    if(args.length == 0)
    {
      sender.sendMessage(ChatColor.YELLOW + "Available Menus to Force Open: ");
      Set<String> menus = manager.getMenus();
      for(String s : menus)
      {
        if(this.hasPermission((Player) sender, s))
        {
          sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.AQUA + s);
        }
      }
      return true;  
    }
    if(args.length == 1)
    {
      String menu_name = args[0]; // Geting the "menu" the player asked for.
      if(!manager.containsMenu(menu_name))
      {
        sender.sendMessage(ChatColor.RED + "The Menu '"+ menu_name +"' does not exist.");
        return true;
      }
      if(!this.hasPermission((Player) sender, menu_name))
      {
        sender.sendMessage("You do not have permission to force open '" + menu_name + "'");
        return true;
      }
      Menu menu = manager.getMenu(menu_name);
      Player player = (Player) sender;
      menu.getInventoryMenu(player).openMenu();
    }
    
    return true;
  }

}
