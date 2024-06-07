package fr.yabrich.cgames.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.commands.CommandBlackJack;
import fr.yabrich.cgames.commands.CommandRoulette;

public class RouletteListener implements Listener {
	
	public static Map<Player, Map<String,Integer>> mises_joueurs = new HashMap<>();
	private Map<Player, Inventory> tables_joueurs = new HashMap<>();
	private Map<Player, Integer> mise_temp = new HashMap<>();
	
	public static Map<Player, Boolean> can_close = new HashMap<>();
	
	private Main main;

	public RouletteListener(Main main) {
		this.main = main;
	}
	
	//GUI MISE1
	@EventHandler
	public void onItemClickedTable(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		Inventory gui = event.getInventory();
		ItemStack it = event.getCurrentItem();
		FileConfiguration config = main.getConfig();
		
		if(it.getType() == Material.AIR || !gui.getTitle().equalsIgnoreCase("§e[§6Table de mises§e]")) {
			return;
		}
		
		String nickname = it.getItemMeta().getDisplayName();
		
		if(nickname.equalsIgnoreCase("§4§lAnnuler les mises")) {
			player.sendMessage("§e[§6Roulette§e] §6Vous avez §cannulé §6vos mises");
			mises_joueurs.remove(player);
			CommandRoulette.rouletteplaying.remove(player);
			player.closeInventory();
		}
		
		if((it.getType() == Material.PAPER || it.getType() == Material.BANNER)) {
			Inventory gestionmise = Bukkit.createInventory(null, 27, "§e[§6Mise sur "+nickname+"§e]");
			ItemStack vitrefill = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
			CommandBlackJack.renameItem(vitrefill, "§8Mise");
			
			for(int i=0;i<27;i++) {
				if(i<9 || i>17) {
					gestionmise.setItem(i, vitrefill);
				}
				if(i==13) {
					ItemStack itmise = it.clone();
					ItemMeta itmisemeta = itmise.getItemMeta();
					itmisemeta.setLore(Arrays.asList("0"));
					itmise.setItemMeta(itmisemeta);
					gestionmise.setItem(i, itmise);
				}
				if(i==10) {
					ItemStack vitrered = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14);
					CommandBlackJack.renameItem(vitrered, "§4Diminuer la mise");
					gestionmise.setItem(i, vitrered);
				}
				if(i==16) {
					ItemStack vitregreen = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
					CommandBlackJack.renameItem(vitregreen, "§2Augmenter la mise");
					gestionmise.setItem(i, vitregreen);
				}
				if(i==20) {
					ItemStack redbanner = new ItemStack(Material.BANNER,1,(byte)1);
					CommandBlackJack.renameItem(redbanner, "§4Annuler la mise");
					gestionmise.setItem(i, redbanner);
				}
				if(i==24) {
					ItemStack greenbanner = new ItemStack(Material.BANNER,1,(byte)10);
					CommandBlackJack.renameItem(greenbanner, "§2Confirmer la mise");
					gestionmise.setItem(i, greenbanner);
				}
			}
			
			tables_joueurs.put(player, gui);
			mise_temp.put(player, 0);
			can_close.put(player, true);
			player.openInventory(gestionmise);
			can_close.replace(player, false);
		}
		
		if(nickname.equalsIgnoreCase("§2§lConfirmer les mises")) {
			Map<String,Integer> mises = new HashMap<>();
			for(ItemStack item : gui.getContents()) {
				if(item != null) {
					if(item.getItemMeta().hasLore()) {
						List<String> lore = item.getItemMeta().getLore();
						int mise = Integer.parseInt(lore.get(0));
						String numero_mise = item.getItemMeta().getDisplayName();
						
						mises.put(numero_mise, mise);
						mises_joueurs.put(player, mises);
						
						CommandRoulette.rouletteplaying.remove(player);
					}
				}
			}
			
			player.sendMessage("§e[§6Roulette§e] §6Vos mises ont été confirmées");
			
			//Eco Take
			
			for(String mise : mises.keySet()) {
				int bet = mises.get(mise);
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_take_money")+" "+player.getName()+" "+bet);
			}
			
			player.closeInventory();
		}
	}
	
	//GUI MISE 2
	@EventHandler
	public void onItemClickedMise(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		Inventory gui = event.getInventory();
		ItemStack it = event.getCurrentItem();
		
		if(it.getType() == Material.AIR || !gui.getTitle().startsWith("§e[§6Mise sur ")) {
			return;
		}
		
		String nickname = it.getItemMeta().getDisplayName();
		ItemStack itmise = gui.getItem(13);
		ItemMeta itmisemeta = itmise.getItemMeta();
		Inventory table = tables_joueurs.get(player);
		int mise = mise_temp.get(player);
		
		if(nickname.equalsIgnoreCase("§4Diminuer la mise")) {
			if(mise == 0) {
				player.sendMessage("§cImpossible, la mise est déjà nulle");
				itmisemeta.removeEnchant(Enchantment.LUCK);
				event.setCancelled(true);
				return;
			}
			
			mise = mise - 100;
		}
		
		if(nickname.equalsIgnoreCase("§2Augmenter la mise")) {
			mise = mise + 100;
			itmisemeta.addEnchant(Enchantment.LUCK, 1, false);
			itmisemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		mise_temp.replace(player, mise);
		itmisemeta.setLore(Arrays.asList(""+mise));
		itmise.setItemMeta(itmisemeta);
		
		if(nickname.equalsIgnoreCase("§4Annuler la mise")) {
			can_close.put(player, true);
			player.openInventory(table);
			can_close.put(player, false);
		}
		
		if(nickname.equalsIgnoreCase("§2Confirmer la mise")) {
			if(mise == 0) {
				player.sendMessage("§cImpossible, la mise est nulle");
				event.setCancelled(true);
				return;
			}
			
			for(int k=0;k<50;k++) {
				ItemStack item = table.getItem(k);
				if(item != null) {
					if(!(item.getType() == Material.AIR)) {
						if(item.getItemMeta().getDisplayName().equalsIgnoreCase(itmise.getItemMeta().getDisplayName())) {
							table.setItem(k, itmise);
							player.sendMessage("§e[§6Roulette§e] §6Mise enregistrée !");
						}
					}
				}
			}
			can_close.put(player, true);
			player.openInventory(table);
			can_close.put(player, false);
		}
	}

}
