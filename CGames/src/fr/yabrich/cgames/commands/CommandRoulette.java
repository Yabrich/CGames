package fr.yabrich.cgames.commands;

import java.util.ArrayList;
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

import fr.yabrich.cgames.Main;
import fr.yabrich.cgames.listeners.RouletteListener;

public class CommandRoulette implements CommandExecutor {

	private Main main;
	
	public CommandRoulette(Main main) {
		this.main = main;
	}
	
	public static List<Player> rouletteplaying = new ArrayList<>();

	private ItemStack generateChiffrePair(int index, Map<Integer, Integer> nb_affiche) {
		ItemStack chiffrepair = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)15);
		
		int chiffre = -1;
		
		while(chiffre%2 != 0 || nb_affiche.containsKey(chiffre)) {
			Random random = new Random();
			chiffre = random.nextInt(35);
		}
		
		nb_affiche.put(chiffre, index);
		CommandBlackJack.renameItem(chiffrepair, "§8§l"+chiffre);
		
		return chiffrepair;
	}
	
	private ItemStack generateChffireImpair(int index, Map<Integer, Integer> nb_affiche) {
		ItemStack chiffreimpair = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14);
		
		int chiffre = 0;
		
		while(chiffre%2 == 0 || nb_affiche.containsKey(chiffre)) {
			Random random = new Random();
			chiffre = random.nextInt(36);
		}
		
		nb_affiche.put(chiffre, index);
		CommandBlackJack.renameItem(chiffreimpair, "§4§l"+chiffre);
		
		return chiffreimpair;
	}
	
	Map<Player, Integer> countp = new HashMap<>();
	Map<Player, Integer> taskIdp = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			FileConfiguration config = main.getConfig();
			
			if(args.length == 0) {
				player.sendMessage("§cErreur : Argument(s) manquant(s)");
				player.sendMessage("§cHelp : /roulette help");
				return true;
			}
			
			//Mise
			
			if(args[0].equalsIgnoreCase("mise")) {
				if(!player.hasPermission("cgames.roulette.mise")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				Inventory gui_mises = Bukkit.createInventory(null, 54, "§e[§6Table de mises§e]");
				rouletteplaying.add(player);
				RouletteListener.can_close.put(player, false);
				
				int num = 0;
				for(int j=0;j<54;j++) {
					if(j>8 && j<45) {
						ItemStack numero = new ItemStack(Material.PAPER);
						if(num%2 == 0) {
							CommandBlackJack.renameItem(numero, "§8§l"+num);
						}
						else {
							CommandBlackJack.renameItem(numero, "§4§l"+num);
						}
						
						gui_mises.setItem(j, numero);
						num++;
					}
					
					if(j==4) {
						ItemStack blackbanner = new ItemStack(Material.BANNER,1,(byte)0);
						CommandBlackJack.renameItem(blackbanner, "§8§lNOIR");
						
						gui_mises.setItem(j, blackbanner);
					}
					
					if(j==49) {
						ItemStack redbanner = new ItemStack(Material.BANNER,1,(byte)1);
						CommandBlackJack.renameItem(redbanner, "§4§lROUGE");
						
						gui_mises.setItem(j, redbanner);
					}
					
					if(j==53) {
						ItemStack vitreconfirm = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
						CommandBlackJack.renameItem(vitreconfirm, "§2§lConfirmer les mises");
						
						gui_mises.setItem(j, vitreconfirm);
					}
					
					if(j==45) {
						ItemStack vitreannul = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14);
						CommandBlackJack.renameItem(vitreannul, "§4§lAnnuler les mises");
						
						gui_mises.setItem(j, vitreannul);
					}
				}
				
				player.openInventory(gui_mises);
				
				
				
				return true;
			}
			
			//Jeu
			
			if(args[0].equalsIgnoreCase("start")) {
				if(!player.hasPermission("cgames.roulette.start")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				if(!RouletteListener.mises_joueurs.containsKey(player)) {
					player.sendMessage("§cErreur : Il est impossible de lancer la partie sans mise !");
					player.sendMessage("§cHelp : /roulette mise");
					return true;
				}
				
				rouletteplaying.add(player);
				RouletteListener.can_close.put(player, false);
				
				Inventory gui = Bukkit.createInventory(null, 27, "§e[§6Roulette§e]");
				Map<Integer, Integer> nb_affiche = new HashMap<>();
				
				countp.put(player, 0);
				Random random = new Random();
				int max_count = random.nextInt(15, 17);
				
				
				//SetUp GUI
				ItemStack vitre = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7);
				CommandBlackJack.renameItem(vitre, "§7Roulette");
				
				ItemStack vitreverte = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
				CommandBlackJack.renameItem(vitreverte, "§aRoulette");
				
				for(int i=0;i<27;i++) {
					if(i<9 || i>17) {
						gui.setItem(i, vitre);
					}
					
					if(i>=9 && i<=17) {
						if(i%2 == 0) {
							gui.setItem(i, generateChiffrePair(i,nb_affiche));
						}
						else {
							gui.setItem(i, generateChffireImpair(i,nb_affiche));
						}
					}
					
					if(i==4 || i==22) {
						gui.setItem(i, vitreverte);
					}
				}
				
				player.openInventory(gui);
				
				//JEU
				taskIdp.put(player,Bukkit.getScheduler().runTaskTimer(main, () -> {
					
					int count = countp.get(player);
					countp.replace(player, count+1);
					
					for(int i=17;i>8;i--) {
						if(i!=17) {
							int chiffrei = -1;
							for(int key : nb_affiche.keySet()) {
								if(nb_affiche.get(key) == i) {
									chiffrei = key;
								}
							}
								
							nb_affiche.replace(chiffrei, i+1);
								
							ItemStack chiffre = gui.getItem(i);
							gui.setItem(i+1, chiffre);
						}
						
						if(i==9){
							int chiffrei1 = -1;
							for(int key : nb_affiche.keySet()) {
								if(nb_affiche.get(key) == 10) {
									chiffrei1 = key;
								}
							}
								
							if(chiffrei1%2 == 0) {
								gui.setItem(i, generateChffireImpair(i, nb_affiche));
							}
							else {
								gui.setItem(i, generateChiffrePair(i, nb_affiche));
							}
						}
							
						if(i == 17) {
							int chiffre17 = -1;
							for(int key : nb_affiche.keySet()) {
								if(nb_affiche.get(key) == 17) {
									chiffre17 = key;
								}
							}
								
							nb_affiche.remove(chiffre17);
						}
						}
					
					if(countp.get(player) == max_count) {
						Bukkit.getScheduler().cancelTask(taskIdp.get(player));
						
						String num_gagnant = gui.getItem(13).getItemMeta().getDisplayName();
						int num_int = Integer.parseInt(num_gagnant.substring(4));
						
						player.sendMessage("§e[§6Roulette§e] §6Numéro gagnant : "+num_gagnant);
						
						String[] message = {"§6---- §e[§6Roulette§e] §6----",
								"§e§nRésumé des gains :",
								""};
						
						player.sendMessage(message);
						
						int gain_total = 0;
						
						Map<String, Integer> mises = RouletteListener.mises_joueurs.get(player);
						
						for(String mise : mises.keySet()) {
							if(mise.equalsIgnoreCase(num_gagnant)) {
								player.sendMessage("§eVous avez §agagné §evotre mise "+mise);
								gain_total = gain_total + mises.get(mise)*36;
							}
							else if((mise.equalsIgnoreCase("§4§lROUGE") && num_int%2 != 0) || 
									(mise.equalsIgnoreCase("§8§lNOIR") && num_int%2 == 0)) {
								player.sendMessage("§eVous avez §agagné §evotre mise "+mise);
								gain_total = gain_total + mises.get(mise)*2;
							}
							else {
								player.sendMessage("§eVous avez §4perdu §evotre mise "+mise);
							}
						}
						
						player.sendMessage("§6Total des gains : §3"+gain_total);
						player.sendMessage("§6---- [§4CGames§6] ----");
						Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), config.getString("cmd_give_money")+" "+player.getName()+" "+gain_total);
						
						Bukkit.getScheduler().runTaskLater(main, () -> {
							rouletteplaying.remove(player);
							RouletteListener.mises_joueurs.remove(player);
							player.closeInventory();
						}, 60L);	
					}
					
					}, 20L, 20L).getTaskId());
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help")) {
				if(!player.hasPermission("cgames.roulette.help")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				String[] message = {"§6---- §e[§6Roulette§e] §6----",
						"§e- /roulette start : §6Commence une partie",
						"§e- /roulette mise : §6Ouvre la table de mise pour la prochaine partie",
						"§e- /roulette rules : §6Affiche les règles du jeu",
						"§e- /roulette help : §6Affiche cette page d'aide",
						"§6---- [§4CGames§6] ----"};
				
				player.sendMessage(message);
				return true;
				}
			
			if(args[0].equalsIgnoreCase("rules")) {
				if(!player.hasPermission("cgames.roulette.rules")) {
					player.sendMessage("§4Vous n'avez pas la permission d'executer cette commande");
					return true;
				}
				
				String[] message = {"§6---- §e[§6Roulette§e] §6----",
						"§2§nRègles de la Roulette :",
						"\n§6Roue de la Roulette :§e La roulette comporte une roue avec des cases numérotées de 0 à 35, alternativement rouges et noires.",
						"§6Types de Paris :§e Les joueurs placent leurs paris sur une table pour parier. Les paris peuvent être \"internes\" (numéros spécifiques ou combinaisons de quelques numéros) ou \"externes\" (paris sur des catégories plus larges comme rouge/noir).",
						"§6Début du Jeu :§e La bille se lance dans la roulette, celle-ci est signifiée dans cette version par les cases vertes claires.",
						"§6Résultat :§e La bille finit par s'arrêter dans l'une des cases numérotées. Les gains sont distribués selon l'endroit où la bille s'arrête.",
						"§6Gains :§e Les gains varient en fonction du type de pari. Un pari interne paie 36 fois la mise jouée, tandis qu'un pari externe paie 2 fois la mise jouée.",
						"§6---- [§4CGames§6] ----"};
				
				player.sendMessage(message);
				return true;
			}
			
			
			player.sendMessage("§cErreur : Argument(s) incorrect(s)");
			player.sendMessage("§cHelp : /roulette help");
			
		}
		return true;
	}

}
