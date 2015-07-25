package net.samongi.CraftingMenu.Commands;

import org.bukkit.command.CommandSender;

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
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }

  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    // TODO Auto-generated method stub
    return true;
  }
}
