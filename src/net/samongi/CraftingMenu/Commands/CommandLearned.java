package net.samongi.CraftingMenu.Commands;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandLearned extends BaseCommand
{
  
  public CommandLearned(String command_path)
  {
    super(command_path);
    
    
    this.permission = "craftmenu.commandlearned";
    
    this.allowed_senders.add(SenderType.PLAYER);
    this.allowed_senders.add(SenderType.CONSOLE);
    
    this.usage.add("Will display the learned recipes for yourself or for a named player.");
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }

  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    // If the player sending the command is a player and wants to know thier own
    if(sender instanceof Player && args.length == 0)
    {
      this.displayLearned(sender, (Player) sender);
      return true;
    }
    else // we are going to get the player name.
    {
      Player player = Bukkit.getPlayer(args[0]);
      if(player == null)
      {
        sender.sendMessage(ChatColor.RED + "The player name you have entered does not exist.");
        return true;
      }
      this.displayLearned(sender, player);
    }
    return true;
  }
  
  private void displayLearned(CommandSender sender, Player player)
  {
    PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
    Set<String> recipes = profile.getRecipes();
    sender.sendMessage(ChatColor.YELLOW + "Recipes learned by '" + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'");
    for(String s : recipes)
    {
      sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.AQUA + s);
    }
  }
}
