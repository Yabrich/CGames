package fr.yabrich.cgames.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.listeners.BlackJackListener;

public class CommandBlackJack implements CommandExecutor {
	
	public static int points_joueur;
	public static int points_croupier;
	
	public static int putpcard;
	public static int putccard;
	
	public static ArrayList<Player> blackjackplaying = new ArrayList<Player>();
	
	private final Main main;
	
	public CommandBlackJack(Main main) {
		this.main = main;
	}
	
 	public static ItemStack GiveCard() {
		ItemStack card = new ItemStack(Material.PAPER);
		ItemMeta card_meta = card.getItemMeta();
		
		String[] enseignes = {"Coeur","Carreau","Trèfle","Pique"};
		String[] valeurs = {"As","2","3","4","5","6","7","8","9","10","Valet","Dame","Roi"};
		
		Random random = new Random();
		
		int enseigne = random.nextInt(enseignes.length);
		int valeur = random.nextInt(valeurs.length);
		
		
		String card_name;
		
		if(enseigne < 2) {
			card_name = "§4§l";
		}
		else {
			card_name = "§8§l";
		}
		
		card_name = card_name + valeurs[valeur] + " de " + enseignes[enseigne];
		
		card_meta.setDisplayName(card_name);
		
		String card_value;
		
		if(valeur == 0) {
			card_value = "11";
		}
		
		else if(valeur > 8) {
			card_value = "10";
		}
		
		else {
			card_value = ""+(valeur+1);
		}
		
		card_meta.setLore(Arrays.asList(card_value));
		
		card.setItemMeta(card_meta);
		
		return card;
	}
	
	public static int getCardValue(ItemStack card) {
		List<String> card_lore = card.getItemMeta().getLore();
		int value = Integer.parseInt(card_lore.get(0));
		
		return value;
	}
	
	public static void renameItem(ItemStack it, String name) {
		ItemMeta it_meta = it.getItemMeta();
		it_meta.setDisplayName(name);
		it.setItemMeta(it_meta);
	}
	
	public static Map<Player, Integer> mises_list = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			FileConfiguration config = main.getConfig();
			
			if(args.length == 0) {
				player.sendMessage("§cErreur : Argument(s) manquant(s)");
				player.sendMessage("§cHelp : /blackjack help");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("start")) {
				if(!player.hasPermission("cgames.blackjack.start")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
					
					if(!mises_list.containsKey(player)) {
						player.sendMessage("§cErreur : Il est impossible de lancer la partie sans mise !");
						player.sendMessage("§cHelp : /blackjack mise [mise]");
						return true;
					}
				
					blackjackplaying.add(player);
					
					ItemStack tirer = new ItemStack(Material.LEVER);
					ItemMeta tirer_meta = tirer.getItemMeta();
					tirer_meta.setDisplayName("§2§lTIRER");
					
					tirer.setItemMeta(tirer_meta);
					
					ItemStack rester = new ItemStack(Material.COAL);
					ItemMeta rester_meta = rester.getItemMeta();
					rester_meta.setDisplayName("§4§lRESTER");
					
					rester.setItemMeta(rester_meta);
					
					ItemStack croupier_card1 = GiveCard();
					
					points_croupier = getCardValue(croupier_card1);
					
					ItemStack player_card1 = GiveCard();
					ItemStack player_card2 = GiveCard();
					
					points_joueur = getCardValue(player_card1)+getCardValue(player_card2);
					
					//GUI
					Inventory bj_gui = Bukkit.createInventory(null, 27, "§4[§2BlackJack§4]");
					
					putpcard = 3;
					putccard = 20;
					
					//SETUP VITRE
					for(int i=0;i<27;i++) {
						ItemStack vitre = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
						
						if(i == 0 || i == 8) {
							renameItem(vitre, "§2§lPoints Joueur : "+points_joueur);
							bj_gui.setItem(i, vitre);
						}
						
						if(i == 18 || i == 26) {
							renameItem(vitre, "§4§lPoints Croupier : "+points_croupier);
							bj_gui.setItem(i, vitre);
						}
						
						if(i >= 10 && i <= 16) {
							renameItem(vitre, "§8BLACKJACK");
							bj_gui.setItem(i, vitre);
						}
					}
					
					//SETUP BOUTON
					
					bj_gui.setItem(9, tirer);
					bj_gui.setItem(17, rester);
					
					//SETUP CARTES
					
					bj_gui.setItem(1, player_card1);
					bj_gui.setItem(2, player_card2);
					
					bj_gui.setItem(19, croupier_card1);
					
					player.openInventory(bj_gui);
					
					player.sendMessage("§6---- §4[§2BlackJack§4] §6----");
					player.sendMessage("§eDébut de la partie :");
					player.sendMessage("");
					player.sendMessage("§cMain du Croupier : "+croupier_card1.getItemMeta().getDisplayName());
					player.sendMessage("§7Nombre de points : "+points_croupier);
					player.sendMessage("");
					player.sendMessage("§aVotre Main : "+player_card1.getItemMeta().getDisplayName() + " §8/ "+player_card2.getItemMeta().getDisplayName());
					player.sendMessage("§7Nombre de points : "+points_joueur);
					
					if(points_joueur == 21) {
						
						BlackJackListener BlackJackListenerInstance = new BlackJackListener(main);
						
						BlackJackListenerInstance.GameWon(player, bj_gui);
						
						return true;
					}
					
					player.sendMessage("§6--- ---------- ----");
					return true;
			}
				
			if(args[0].equalsIgnoreCase("mise")) {
				if(!player.hasPermission("cgames.blackjack.mise")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				if(args.length < 2) {
					player.sendMessage("§cErreur : Mise requise");
					player.sendMessage("§cUsage : /blackjack mise [mise]");
					return true;
				}
					
				int mise_joueur = 0;
					
				try {
					mise_joueur = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.sendMessage("§cErreur : Mise incorrecte");
					return true;
				}
				
				int mise_min = config.getInt("blackjack.mise_min");
				int mise_max = config.getInt("blackjack.mise_max");
				
				boolean mise_max_on = true;
				
				if(mise_max == 0) {
					mise_max_on = false;
				}
				
				if(mise_joueur < mise_min || (mise_joueur > mise_max && mise_max_on == true)) {
					player.sendMessage("§cErreur : Mise impossible");
					player.sendMessage("§6Mise minimale : §e"+mise_min);
					if(mise_max_on) {
						player.sendMessage("§6Mise maximale : §e"+mise_max);
					}
					
					return true;
				}
					
				if(mises_list.containsKey(player)) {
					
					if(mises_list.get(player) == mise_joueur) {
						player.sendMessage("§4[§2BlackJack§4] §cVotre mise est déjà enregistré à §e"+mise_joueur+"$");
						return true;
					}

					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_give_money")+" "+player.getName()+" "+mises_list.get(player));
				}
					
				mises_list.put(player, mise_joueur);
				player.sendMessage("§4[§2BlackJack§4] §6Votre mise a bien été enregistré à §e"+mise_joueur+"$");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_take_money")+" "+player.getName()+" "+mise_joueur);
					
				return true;
			}
			
			if(args[0].equalsIgnoreCase("setmise")) {
				if(!player.hasPermission("cgames.blackjack.setmise")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				if(args.length < 3) {
					player.sendMessage("§cErreur : Argument(s) manquant(s)");
					player.sendMessage("§cUsage : /blackjack setmise [min/max] (mise)");
					return true;
				}
				
				String path;
				if(args[1].equalsIgnoreCase("min")) {
					path = "blackjack.mise_min";
				}
				
				else if(args[1].equalsIgnoreCase("max")) {
					path = "blackjack.mise_max";
				}
				
				else{
					player.sendMessage("§cErreur : Argument(s) incorrect(s)");
					player.sendMessage("§cUsage : /blackjack setmise [min/max] (mise)");
					return true;
				}
				
				int mise = 0;
				
				try {
					mise = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					player.sendMessage("§cErreur : Mise incorrecte");
					return true;
				}
				
				config.set(path, mise);
				
				if(mise == 0) {
					player.sendMessage("§4[§2BlackJack§4] §6La mise §e"+args[1]+" §6a bien été §4désactivée");
				}
				else {
					player.sendMessage("§4[§2BlackJack§4] §6La mise §e"+args[1]+" §6a bien été enregistré à §e"+mise+"$");
				}
				
				main.saveConfig();
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help")){
				if(!player.hasPermission("cgames.blackjack.help")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				String[] message = {"§6---- §4[§2BlackJack§4]§6 ----",
						"§e- /blackjack start : §6Commence une partie",
						"§e- /blackjack mise (nombre) : §6Mise pour la prochaine partie",
						"§e- /blackjack setmise [min/max] (nombre) : §6Définir les mises minimales et maximales",
						"§e- /blackjack rules : §6Affiche les règles du jeu",
						"§e- /blackjack help : §6Affiche cette page d'aide",
						"§6---- [§4CGames§6] ----"};
				player.sendMessage(message);
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("rules")) {
				if(!player.hasPermission("cgames.blackjack.rules")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				String[] message = {"§6---- §4[§2BlackJack§4]§6 ----",
						"§2§nRègles du BlackJack :",
						"\n§6Objectif du Jeu :§e Atteindre une valeur totale de cartes plus élevée que celle du croupier, sans dépasser 21.",
						"§6Valeur des Cartes :§e Les cartes numérotées valent leur nombre, les figures (Roi, Dame, Valet) valent 10. Dans cette version, l'As vaut 11.",
						"§6Début du Jeu :§e Chaque joueur reçoit deux cartes. tandis que le croupier en reçoit une seule.",
						"§6Tour des Joueurs :§e Chaque joueur décide de tirer (prendre une autre carte) ou de rester (garder sa main actuelle). Les joueurs peuvent continuer à tirer autant de cartes qu'ils le souhaitent, mais s'ils dépassent 21, ils 'bust' et perdent immédiatement.",
						"§6Tour du Croupier :§e Après que tous les joueurs ont terminé, le croupier tire a son tour. Les règles du croupier sont fixes : tirer jusqu'à atteindre un total de 17 ou plus, puis s'arrêter.",
						"§6Fin du Jeu :§e Si le croupier dépasse 21, tous les joueurs restants gagnent. Sinon, les joueurs avec un total plus élevé que celui du croupier gagnent, tandis que ceux avec un total inférieur perdent. En cas d'égalité, le joueur récupère sa mise.",
						"§6---- [§4CGames§6] ----"};
				player.sendMessage(message);
				
				return true;
			}
			
			player.sendMessage("§cErreur : Argument(s) incorrect(s)");
			player.sendMessage("§cHelp : /blackjack help");
		}
		return true;
	}

}
