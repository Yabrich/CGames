package fr.yabrich.cgames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompleterBlackJack implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		List<String> options = new ArrayList<>();
		List<String> tabcompleter = new ArrayList<>();
		
		if(args.length == 1) {
			options.add("start");
			options.add("mise");
			options.add("help");
			options.add("rules");
			
			if(sender.hasPermission("cgames.blackjack.setmise")) {
				options.add("setmise");
			}
			
			for(String option : options) {
				if(option.startsWith(args[0])) {
					tabcompleter.add(option);
				}
			}
			
			if(tabcompleter.isEmpty()) {
				return options;
			}
			
			else {
				return tabcompleter;
			}
			
		}
		
		else if(args.length == 2 && args[0].equalsIgnoreCase("setmise") && sender.hasPermission("cgames.setmise")) {
			options.add("min");
			options.add("max");
			
			return options;
		}
		
		return null;
	}

}
