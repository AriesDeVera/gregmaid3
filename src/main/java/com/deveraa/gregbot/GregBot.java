package com.deveraa.gregbot;

import com.deveraa.gregbot.commands.CommandManager;
import com.deveraa.gregbot.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;


public class GregBot {
    
    private final Dotenv config;
    private final ShardManager shardManager;

    /**
     * Loads environment variables and builds the bot shard manager.
     * @throws LoginException occurs when bot token is invalid.
     */
    public GregBot() throws LoginException {
        String token = System.getenv("TOKEN");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("with your mum."));

        builder.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        shardManager = builder.build();

        // Register listeners
        shardManager.addEventListener(new EventListener(), new CommandManager());
    }

    /**
     * Retrieves config for gregBot
     * @return the Dotenv config instance for gregbot
     */
    public Dotenv getConfig()  {
        return config;
    }

    /**
     * Retrieves bot shard manager.
     * @return the ShardManager instance for gregBot.
     */
    public ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * Main method to start gregBot
     */
    public static void main(String[] args) {
        try {
            GregBot bot = new GregBot();
        } catch (LoginException e) {
            System.out.println("ERROR: Invalid bot token.");
        }
    }
}
