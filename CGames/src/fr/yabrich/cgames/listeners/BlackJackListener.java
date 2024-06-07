package fr.yabrich.cgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.commands.CommandBlackJack;


public class BlackJackListener implements Listener {
	
	private final Main main;
	
	public BlackJackListener(Main main) {
		this.main = main;
	}
	
	private void GameLost(Player player, Inventory gui) {
		ItemStack vitre_perdu = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
		ItemMeta vitre_perdu_meta = vitre_perdu.getItemMeta();
		vitre_perdu_meta.setDisplayName("§4§lVous avez PERDU");
		
		vitre_perdu.setItemMeta(vitre_perdu_meta);
				
		for(int i=0;i<27;i++) {
			if(!(gui.getItem(i) == null)) {
				ItemStack item = gui.getItem(i);
				if(item.getType() == Material.STAINED_GLASS_PANE) {
					gui.setItem(i, vitre_perdu);
				}
			}
		}
		
		player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
		player.sendMessage("§4Vous avez PERDU !");
		player.sendMessage("§6--- ---------- ----");
		
		Bukkit.getScheduler().runTaskLater(main, () -> {
			CommandBlackJack.blackjackplaying.remove(player);
			CommandBlackJack.mises_list.remove(player);
			player.closeInventory();
		}, 60L);
	}
	
	public void GameWon(Player player, Inventory gui) {
		FileConfiguration config = main.getConfig();
		
		ItemStack vitre_gagne = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5);
		ItemMeta vitre_gagne_meta = vitre_gagne.getItemMeta();
		vitre_gagne_meta.setDisplayName("§a§lVous avez GAGNE");
		
		vitre_gagne.setItemMeta(vitre_gagne_meta);
		
		for(int i=0;i<27;i++) {
			if(!(gui.getItem(i) == null)) {
				ItemStack item = gui.getItem(i);
				if(item.getType() == Material.STAINED_GLASS_PANE) {
					gui.setItem(i, vitre_gagne);
				}
			}
		}
		
		player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
		player.sendMessage("§aVous avez GAGNE");
		player.sendMessage("§6--- ---------- ----");
		
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_give_money")+" "+player.getName()+" "+CommandBlackJack.mises_list.get(player)*2);
		
		Bukkit.getScheduler().runTaskLater(main, () -> {
			CommandBlackJack.blackjackplaying.remove(player);
			CommandBlackJack.mises_list.remove(player);
			player.closeInventory();
		}, 60L);
	}
	
	private void GameDraw(Player player, Inventory gui) {
		FileConfiguration config = main.getConfig();
		
		ItemStack vitre_draw = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15);
		ItemMeta vitre_draw_meta = vitre_draw.getItemMeta();
		vitre_draw_meta.setDisplayName("§7§lEgalité");
		
		vitre_draw.setItemMeta(vitre_draw_meta);
		
		for(int i=0;i<27;i++) {
			if(!(gui.getItem(i) == null)) {
				ItemStack item = gui.getItem(i);
				if(item.getType() == Material.STAINED_GLASS_PANE) {
					gui.setItem(i, vitre_draw);
				}
			}
		}
		
		player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
		player.sendMessage("§7Egalité");
		player.sendMessage("§6--- ---------- ----");
		
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_give_money")+" "+player.getName()+" "+CommandBlackJack.mises_list.get(player));
		
		Bukkit.getScheduler().runTaskLater(main, () -> {
			CommandBlackJack.blackjackplaying.remove(player);
			CommandBlackJack.mises_list.remove(player);
			player.closeInventory();
		}, 60L);
	}

	@EventHandler
	public void onItemClicked(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		ItemStack it = event.getCurrentItem();
		Inventory gui = event.getClickedInventory();
		
		if(!CommandBlackJack.blackjackplaying.contains(player) || it.getType() == Material.AIR || !gui.getTitle().equalsIgnoreCase("§4[§2BlackJack§4]")) {
			return;
		}
		
		String itemname = it.getItemMeta().getDisplayName();
		
		if(itemname.equalsIgnoreCase("§2§lTIRER")) {
			
			ItemStack player_card = CommandBlackJack.GiveCard();
			CommandBlackJack.points_joueur = CommandBlackJack.points_joueur + CommandBlackJack.getCardValue(player_card);
			
			int ptsjoueur_id = 0;
			
			for(int i=0;i<2;i++) {
				ItemStack vitre = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
				CommandBlackJack.renameItem(vitre, "§2§lPoints Joueur : " + CommandBlackJack.points_joueur);
				
				gui.setItem(ptsjoueur_id, vitre);
				ptsjoueur_id = ptsjoueur_id + 8;
			}
			
			gui.setItem(CommandBlackJack.putpcard, player_card);
			CommandBlackJack.putpcard++;
			
			player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
			player.sendMessage("§eVous avez tirer : "+player_card.getItemMeta().getDisplayName());
			player.sendMessage("§7Nombre de points total : "+CommandBlackJack.points_joueur);
			player.sendMessage("");
			
			if(CommandBlackJack.points_joueur > 21) {
				GameLost(player, gui);
				return;
			}
			
		}
		
		if(itemname.equalsIgnoreCase("§4§lRESTER")) {
			player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
			player.sendMessage("§eVous restez...");
			player.sendMessage("§7Nombre de points total : "+CommandBlackJack.points_joueur);
			player.sendMessage("");
			
			while(CommandBlackJack.points_croupier < 17) {
				player.sendMessage("§cLe croupier tire...");
				ItemStack croupier_card = CommandBlackJack.GiveCard();
				CommandBlackJack.points_croupier = CommandBlackJack.points_croupier + CommandBlackJack.getCardValue(croupier_card);
				
				int ptscroupier_id = 18;
				
				for(int i=0;i<2;i++) {
					ItemStack vitre = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
					CommandBlackJack.renameItem(vitre, "§4§lPoints Croupier : " + CommandBlackJack.points_croupier);
					
					gui.setItem(ptscroupier_id, vitre);
					ptscroupier_id = ptscroupier_id + 8;
				}
				
				gui.setItem(CommandBlackJack.putccard, croupier_card);
				CommandBlackJack.putccard++;
				
				player.sendMessage("§cLe croupier a tiré : "+croupier_card.getItemMeta().getDisplayName());
				player.sendMessage("§7Nombre de points total : "+CommandBlackJack.points_croupier);
			}
			
			player.sendMessage("§6--- ---------- ----");
			
			if(CommandBlackJack.points_croupier > 21 || CommandBlackJack.points_croupier < CommandBlackJack.points_joueur) {
				GameWon(player, gui);
				return;
			}
			
			if(CommandBlackJack.points_croupier == CommandBlackJack.points_joueur) {
				GameDraw(player, gui);
				return;
			}
			
			if(CommandBlackJack.points_croupier > CommandBlackJack.points_joueur) {
				GameLost(player, gui);
				return;
			}
		}
	}

}
