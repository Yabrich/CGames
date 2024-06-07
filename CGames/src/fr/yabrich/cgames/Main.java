package fr.yabrich.cgames;

import org.bukkit.plugin.java.JavaPlugin;

import fr.yabrich.cgames.commands.CommandBlackJack;
import fr.yabrich.cgames.commands.CommandForceMise;
import fr.yabrich.cgames.commands.CommandForcePlay;
import fr.yabrich.cgames.commands.CommandRoulette;
import fr.yabrich.cgames.listeners.BlackJackExecptionListener;
import fr.yabrich.cgames.listeners.BlackJackListener;
import fr.yabrich.cgames.listeners.RouletteListener;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		
		System.out.println("[CGames] Loading...");
		getCommand("blackjack").setExecutor(new CommandBlackJack(this));
		getCommand("blackjack").setTabCompleter(new TabCompleterBlackJack());;
		
		getCommand("forceplay").setExecutor(new CommandForcePlay(this));
		getCommand("forcemise").setExecutor(new CommandForceMise(this));
		
		getCommand("roulette").setExecutor(new CommandRoulette(this));
		getCommand("roulette").setTabCompleter(new TabCompleterRoulette());
		
		getServer().getPluginManager().registerEvents(new BlackJackListener(this), this);
		getServer().getPluginManager().registerEvents(new BlackJackExecptionListener(this), this);
		getServer().getPluginManager().registerEvents(new RouletteListener(this), this);
	}
	
	@Override
	public void onDisable() {
		System.out.println("[CGames] Stopped...");
	}
}
