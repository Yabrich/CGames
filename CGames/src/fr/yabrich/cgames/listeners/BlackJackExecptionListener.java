package fr.yabrich.cgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.commands.CommandBlackJack;
import fr.yabrich.cgames.commands.CommandRoulette;

public class BlackJackExecptionListener implements Listener {
	
	private final Main main;

	public BlackJackExecptionListener(Main main) {
		this.main = main;
	}
	
	//Empêche le drop d'item
		@EventHandler
		public void onItemDrop(PlayerDropItemEvent event) {
			Player player = event.getPlayer();
			
			if(CommandBlackJack.blackjackplaying.contains(player) || CommandRoulette.rouletteplaying.contains(player)) {
				player.sendMessage("§6[§4CGames§6] §cVous ne pouvez pas jeter d'item durant le jeu !");
				event.setCancelled(true);
				player.updateInventory();
			}
		}
		
		//Empêche de poser des blocks
		@EventHandler
		public void onBlockPlace(BlockPlaceEvent event) {
			Player player = event.getPlayer();
			
			if(CommandBlackJack.blackjackplaying.contains(player) || CommandRoulette.rouletteplaying.contains(player)) {
				player.sendMessage("§6[§4CGames§6] §cVous ne pouvez pas poser de block durant le jeu !");
				event.setCancelled(true);
				player.updateInventory();
			}
		}
		
		//Empêche de fermer le gui
		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			Player player = (Player)event.getPlayer();
			Inventory inv = event.getInventory();
			
			String inv_title = inv.getTitle();
			
			if(CommandRoulette.rouletteplaying.contains(player)) {
				if(RouletteListener.can_close.get(player)) {
					return;
				}
			}
			
			if((inv_title.equalsIgnoreCase("§4[§2BlackJack§4]") && CommandBlackJack.blackjackplaying.contains(player)) || 
					CommandRoulette.rouletteplaying.contains(player)) {
				Bukkit.getScheduler().runTaskLater(main, () -> {
					player.openInventory(inv);
					player.sendMessage("§6[§4CGames§6] §cVous ne pouvez pas fermer le menu durant le jeu !");
				}, 1L);
			}
		}
		
		//Empêche de bouger les items
		@EventHandler
		public void onItemClick(InventoryClickEvent event) {
			Player player = (Player)event.getWhoClicked();
			
			if(CommandBlackJack.blackjackplaying.contains(player) || CommandRoulette.rouletteplaying.contains(player)) {
				event.setCancelled(true);
			}
		}

}
