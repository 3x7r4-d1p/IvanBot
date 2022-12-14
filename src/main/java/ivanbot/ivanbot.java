package ivanbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class ivanbot {

    private final Dotenv config;
    private final ShardManager shardManager;

    public ivanbot() throws LoginException
    {
        config = Dotenv.configure()
                .load();

        String TOKEN = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(TOKEN);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.enableCache(CacheFlag.VOICE_STATE);
        builder.setActivity(Activity.watching("/ihelp - справка"));
        shardManager = builder.build();

        shardManager.addEventListener(new CommandManager());
    }

    public Dotenv getConfig()
    {
        return config;
    }

    public ShardManager getShardManager()
    {
        return shardManager;
    }

    public static void main(String[] args)
    {
        try
        {
            ivanbot bot = new ivanbot();
        }
        catch (LoginException e)
        {
            System.out.println("Error: ti durachok, token ne rabotaet, zaloginitsa ne poluchilos");
        }
    }
}
