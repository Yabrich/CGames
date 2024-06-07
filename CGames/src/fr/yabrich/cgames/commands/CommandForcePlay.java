package fr.yabrich.cgames.commands;

import org.bukkit.Bukkit;

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.listeners.RouletteListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandForcePlay implements CommandExecutor {
	
	private final Main main;
	
	public CommandForcePlay(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(args.length < 2) {
			sender.sendMessage("§cErreur : Argument(s) manquant(s)");
			sender.sendMessage("§cUsage : /forceplay [nom_jeu] [pseudo]");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("blackjack")) {
			Player targetPlayer = Bukkit.getPlayer(args[1]);
			
			if(targetPlayer == null) {
				sender.sendMessage("§cErreur : Joueur introuvable");
				return true;
			}
			
			if(!CommandBlackJack.mises_list.containsKey(targetPlayer)) {
				sender.sendMessage("§cErreur : Le joueur n'a pas encore misé");
				sender.sendMessage("§cHelp : /forcemise blackjack [mise] [pseudo]");
				
				targetPlayer.sendMessage("§cErreur : Il est impossible de lancer la partie sans mise !");
				return true;
			}
			
			CommandBlackJack CommandBlackJackInstance = new CommandBlackJack(main);
			
			String[] newargs = {"start"};
			String newmsg = "blackjack";
			Command newcmd = main.getCommand("blackjack");
			
			CommandBlackJackInstance.onCommand(targetPlayer, newcmd, newmsg, newargs);
			
			sender.sendMessage("§6[§eForcePlay§6] §2BlackJack démarré pour §a"+targetPlayer.getName());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("roulette")) {
			Player targetPlayer = Bukkit.getPlayer(args[1]);
			
			if(targetPlayer == null) {
				sender.sendMessage("§cErreur : Joueur introuvable");
				return true;
			}
			
			if(!RouletteListener.mises_joueurs.containsKey(targetPlayer)) {
				sender.sendMessage("§cErreur : Le joueur n'a pas encore misé");
				sender.sendMessage("§cHelp : /forcemise roulette [pseudo]");
				
				targetPlayer.sendMessage("§cErreur : Il est impossible de lancer la partie sans mise !");
				return true;
			}
			
			CommandRoulette CmdRouletteInstance = new CommandRoulette(main);
			
			String[] newargs = {"start"};
			String newmsg = "roulette";
			Command newcmd = main.getCommand("roulette");
			
			CmdRouletteInstance.onCommand(targetPlayer, newcmd, newmsg, newargs);
			
			sender.sendMessage("§6[§eForcePlay§6] §2Roulette démarré pour §a"+targetPlayer.getName());
			return true;
		}
		
		sender.sendMessage("§cErreur : Jeu introuvable");
		sender.sendMessage("§cUsage : /forceplay [nom_jeu] [pseudo]");
		return true;
	}

}
