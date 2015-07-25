package net.samongi.CraftingMenu.Commands;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandRecipes extends BaseCommand
{
  
  public CommandRecipes(String command_path)
  {
    super(command_path);
    
    
    this.permission = "craftmenu.recipes";
    
    this.allowed_senders.add(SenderType.PLAYER);
    this.allowed_senders.add(SenderType.CONSOLE);
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }

  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    if(args.length == 0)
    {
      Set<Recipe> recipes = RecipeManager.getManager().getRecipes();
      Set<String> recipe_names = new TreeSet<>();
      for(Recipe r : recipes) recipe_names.add(r.getName().toLowerCase().replace(" ", "_"));
      sender.sendMessage(ChatColor.YELLOW + "All Current Recipes:");
      for(String n : recipe_names)
      {
        sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.AQUA + n);
      }
      return true;
    }
    else
    {
      String r = args[0];
      Recipe recipe = RecipeManager.getManager().getRecipe(r);
      if(recipe == null)
      {
        sender.sendMessage(ChatColor.RED + "The recipe '" + ChatColor.AQUA + r + ChatColor.RED + "' you are looking for does not exist.");
        return true;
      }
      // Displaying the recipe's name
      sender.sendMessage(ChatColor.YELLOW + "Listing Recipe  '" + ChatColor.AQUA + recipe.getName() + ChatColor.YELLOW + "':");
      sender.sendMessage(ChatColor.GRAY + "Sorting Tags:");
      for(String s : recipe.getSortingTags()) sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s);
      sender.sendMessage(ChatColor.GRAY + "Conflict Pool:");
      for(String s : recipe.getConflictPool()) sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s);
      sender.sendMessage(ChatColor.GRAY + "Requirement Pool:");
      for(String s : recipe.getPrerequisites()) sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s);
      sender.sendMessage(ChatColor.GRAY + "Learn Pool:");
      for(String s : recipe.getLearnPool()) sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s);
      sender.sendMessage(ChatColor.GRAY + "True Learn Pool:");
      for(Recipe s : recipe.getTrueLearnPool()) sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s.getName());
      return true;
    }
  }
}
