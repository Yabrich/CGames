package fr.yabrich.cgames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompleterRoulette implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		List<String> options = new ArrayList<>();
		List<String> tabcompleter = new ArrayList<>();
		
		if(args.length == 1) {
			options.add("start");
			options.add("mise");
			options.add("help");
			options.add("rules");
			
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
		return null;
	}

}
