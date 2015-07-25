package net.samongi.CraftingMenu.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandUnlearn extends BaseCommand
{
  
  public CommandUnlearn(String command_path)
  {
    super(command_path);
    
    
    this.permission = "craftmenu.commandlearned";
    
    this.allowed_senders.add(SenderType.PLAYER);
    this.allowed_senders.add(SenderType.CONSOLE);
    
    ArgumentType[] types0 = {ArgumentType.STRING};
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING, ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }

  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    if(args.length == 1 && sender instanceof Player)
    {
      String recipe_str = args[0];
      if(recipe_str.equals("ALL")) // removing all the recipes
      {
        Player player = (Player) sender;
        PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
        profile.clearRecipes();
        
        player.sendMessage(ChatColor.YELLOW + "You have unlearned all the recipes you know.");
        return true;
      }
      Recipe recipe = RecipeManager.getManager().getRecipe(recipe_str);
      if(recipe == null)
      {
        sender.sendMessage(ChatColor.RED + "The player '" + ChatColor.AQUA + recipe_str + ChatColor.RED + "' does not exist.");
        return true;
      }
      Player player = (Player) sender;
      PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
      if(!profile.hasRecipe(recipe))
      {
        sender.sendMessage(ChatColor.RED + "The player '" + ChatColor.GREEN + player.getName() + ChatColor.RED + "' does not have the recipe '" + ChatColor.AQUA + recipe_str + ChatColor.RED + "'");
        return true;
      }
      profile.removeRecipe(recipe);
      sender.sendMessage(ChatColor.YELLOW + "You have unlearned the recipe '" + ChatColor.AQUA + recipe.getName() + ChatColor.YELLOW + "'");
    }
    else if(args.length == 2)
    {
      String player_name = args[0];
      Player player = Bukkit.getPlayer(player_name);
      if(player == null)
      {
        sender.sendMessage(ChatColor.RED + "The player '" + ChatColor.GREEN + player_name + ChatColor.RED + "' does not exist.");
        return true;
      }
      String recipe_str = args[1];
      if(recipe_str.equals("ALL")) // removing all the recipes
      {
        PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
        profile.clearRecipes();
        
        sender.sendMessage(ChatColor.YELLOW + "'" + ChatColor.GREEN + player.getName() + ChatColor.YELLOW + "' has unlearned all the recipes they know.");
        if(sender != player) player.sendMessage(ChatColor.YELLOW + "You have unlearned all the recipes you know.");
        return true;
      }
      Recipe recipe = RecipeManager.getManager().getRecipe(recipe_str);
      if(recipe == null)
      {
        sender.sendMessage(ChatColor.RED + "The Recipe '" + ChatColor.AQUA + recipe_str + ChatColor.RED + "' does not exist.");
        return true;
      }
      
      PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
      if(!profile.hasRecipe(recipe))
      {
        sender.sendMessage(ChatColor.RED + "The player '" + ChatColor.GREEN + player.getName() + ChatColor.RED + "' does not have the recipe '" + ChatColor.AQUA + recipe_str + ChatColor.RED + "'");
        return true;
      }
      profile.removeRecipe(recipe);
      
      sender.sendMessage(ChatColor.YELLOW + "'" + ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has unlearned the recipe '" + ChatColor.AQUA + recipe.getName() + ChatColor.YELLOW + "'");
      if(sender != player) player.sendMessage(ChatColor.YELLOW + "You have unlearned the recipe '" + ChatColor.AQUA + recipe.getName() + ChatColor.YELLOW + "'");
      return true;
    }
    return true;
  }
}
