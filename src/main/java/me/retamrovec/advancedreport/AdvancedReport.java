package me.retamrovec.advancedreport;

import me.retamrovec.advancedreport.commands.ReportCommand;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.discord.Bot;
import me.retamrovec.advancedreport.listeners.InventoryListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public final class AdvancedReport extends JavaPlugin {

    private Bot bot;
    private ConfigOptions configOptions;
    private HashMap<UUID, String> reportReasons;
    private HashMap<UUID, UUID> reporting;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.configOptions = new ConfigOptions(this);
        this.initialise();
        this.reportReasons = new HashMap<>();
        this.reporting = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);

        Bukkit.getPluginCommand("report").setExecutor(new ReportCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void initialise() {
        reloadConfig();
        if (!getConfig().getBoolean("discord-bot.enabled")) return;
        String token = getConfig().getString("discord-bot.token") == null ? "" : getConfig().getString("discord-bot.token");
        assert token != null;
        if (token.equals("")) return;
        JDABuilder bot = JDABuilder.createDefault(token);
        bot.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        Activity activity = Activity.of(Activity.ActivityType.valueOf(this.getConfigOptions().getString("discord-bot.activity")), this.getConfigOptions().getString("discord-bot.status"));
        bot.setActivity(activity);
        bot.setChunkingFilter(ChunkingFilter.NONE);
        bot.setLargeThreshold(50);
        bot.setStatus(OnlineStatus.ONLINE);
        JDA jda = bot.build();
        this.bot = new Bot(jda);

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Bot getBot() {
        return bot;
    }

    public ConfigOptions getConfigOptions() {
        return configOptions;
    }

    public HashMap<UUID, String> getReportReasons() {
        return reportReasons;
    }

    public HashMap<UUID, UUID> getReporting() {
        return reporting;
    }
}
