package fr.yabrich.cgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.yabrich.cgames.Main;

public class CommandForceMise implements CommandExecutor {
	
	private final Main main;
	

	public CommandForceMise(Main main) {
		this.main = main;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(args.length < 2) {
			sender.sendMessage("§cErreur : Argument(s) manquant(s)");
			sender.sendMessage("§cUsage : /forcemise [nom_jeu] [pseudo]");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("blackjack")) {
			Player targetPlayer = Bukkit.getPlayer(args[2]);
			
			if(targetPlayer == null) {
				sender.sendMessage("§cErreur : Joueur introuvable");
				return true;
			}
			
			if(args.length < 3) {
				sender.sendMessage("§cErreur : Argument(s) manquant(s)");
				sender.sendMessage("§cUsage : /forcemise blackjack [mise] [pseudo]");
				return true;
			}
			
			CommandBlackJack CommandBlackJackInstance = new CommandBlackJack(main);
			
			String mise = args[1];
			
			String[] newargs = {"mise",mise};
			String newmsg = "blackjack";
			Command newcmd = main.getCommand("blackjack");
			
			CommandBlackJackInstance.onCommand(targetPlayer, newcmd, newmsg, newargs);
			
			sender.sendMessage("§6[§eForceMise§6] §2Mise enregistrée à §a"+mise+" §2pour §a"+targetPlayer.getName());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("roulette")) {
			Player targetPlayer = Bukkit.getPlayer(args[1]);
			
			if(targetPlayer == null) {
				sender.sendMessage("§cErreur : Joueur introuvable");
				return true;
			}
			
			CommandRoulette CmdRouletteInstance = new CommandRoulette(main);
			
			String[] newargs = {"mise"};
			String newmsg = "roulette";
			Command newcmd = main.getCommand("roulette");
			
			CmdRouletteInstance.onCommand(targetPlayer, newcmd, newmsg, newargs);
			
			sender.sendMessage("§6[§eForceMise§6] §2Mise démarrée pour "+targetPlayer.getName());
			return true;
		}
		
		sender.sendMessage("§cErreur : Jeu introuvable");
		sender.sendMessage("§cUsage : /forcemise [nom_jeu] [pseudo]");
		return true;
	}

}
