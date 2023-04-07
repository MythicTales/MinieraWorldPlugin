package it.mythictales.mondoeventi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

public final class MondoEventi extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[MondoEventi] Plugin Abilitato");
        getServer().getPluginManager().registerEvents(this,this);
        createConfig();
    }

    private void createConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "Creazione del file config.yml...");
            saveDefaultConfig();
        }
    }

    public int convertMinutesToTicks(int minutes) {
        int ticksPerSecond = 20;
        int secondsPerMinute = 60;
        int ticksPerMinute = ticksPerSecond * secondsPerMinute;
        return minutes * ticksPerMinute;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Bukkit.getLogger().info("Controllo se il player entra nel mondo miniera ed in caso lo caccio");

        Player player = event.getPlayer();

        String worldLogin = player.getWorld().getName();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        if (worldLogin.contains(config.getString("mondoEvento"))){
            String warpName = config.getString("warpSeLoggaInMiniera");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"warp "+ warpName +" "+ player.getName());
            player.sendMessage(ChatColor.DARK_RED + "Hai Sloggato nel mondo miniera, sei stato portato allo spawn");
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
        Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[MondoEventi] Rilevato cambio di mondo per l'utente " + event.getPlayer());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        Player player = event.getPlayer();
        String playerWorldEnteredName = player.getWorld().getName();
        String targetWorldName = getConfig().getString("mondoEvento");

        int tempo = getConfig().getInt("DurataEvento");
        int tempoVip = getConfig().getInt(("DurataEventoVIP"));
        int minuteInTick = convertMinutesToTicks(tempo);
        int minuteinTickVIP = convertMinutesToTicks(tempoVip);

        int timeAccounce30 = tempo - 30; // Tempo rimanente prima dell'annuncio a 30 minuti
        int timeAccounce30VIP = tempo - 45; // Tempo rimanente prima dell'annuncio a 30 minuti

        int timeAccounce15 = tempo - 15; // Tempo rimanente prima dell'annuncio a 15 minuti
        int timeAccounce15VIP = tempo - 30; // Tempo rimanente prima dell'annuncio a 15 minuti

        int timeAccounce5 = tempo - 5; // Tempo rimanente prima dell'annuncio a 15 minuti
        int timeAccounce5VIP = tempo - 10; // Tempo rimanente prima dell'annuncio a 15 minuti

        if (playerWorldEnteredName.equals(targetWorldName)){
            Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[MondoEventi] Rilevato che il player " + player.getName() + "E' entrato nel mondo miniera");
            if (player.hasPermission("mondoeventi.timer.bypass")){
                player.sendMessage("Sei staffer, time bypassato.");
            }
            else if (player.hasPermission("mondoeventi.timer.vip")) {
                player.sendMessage(ChatColor.GREEN + "Hai "+ tempo +" Minuti a disposizione dopodichè verrai riportato allo Spawn.");

                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 30 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce30VIP));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 15 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce15VIP));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 5 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce5VIP));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"warp spawn "+ player.getName());
                    }
                }, tempoVip);
            }
            else {
                player.sendMessage(ChatColor.GREEN + "Hai "+ tempo +" Minuti a disposizione dopodichè verrai riportato allo Spawn.");

                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 30 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce30));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 15 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce15));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.DARK_RED + "[EVENTO] 5 minuti rimanenti");
                    }
                }, convertMinutesToTicks(timeAccounce5));

                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"warp spawn "+ player.getName());
                    }
                }, minuteInTick);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[MondoEventi] Plugin Disabilitato");
    }
}
