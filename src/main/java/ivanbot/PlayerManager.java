package ivanbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.*;

import static ivanbot.TrackDuration.getTrackDuration;

public class PlayerManager {
    private final int MAX_SONG_AMOUNT = 500;

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;

    private final AudioPlayerManager audioPlayerManager;


    public PlayerManager()
    {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild, AudioManager audioManager){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID)->{
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, audioManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }
    public GuildMusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID)->{
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL, String username, AudioManager audioManager)
    {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild(), audioManager);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if (musicManager.getOperator().getQueue().size() >= MAX_SONG_AMOUNT)
                {
                    textChannel.sendMessage("Превышено максимальное количество треков - **" + MAX_SONG_AMOUNT + "**\nОперация не выполнена.").queue();
                    return;
                }
                musicManager.getOperator().queue(audioTrack);
                textChannel.sendMessage("**" + username + "** добавил трек **" + audioTrack.getInfo().title + " (" + getTrackDuration(audioTrack.getInfo().length) + ")**").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (trackURL.contains("audio")){
                    if (musicManager.getOperator().getQueue().size() >= MAX_SONG_AMOUNT){
                        textChannel.sendMessage("Превышено максимальное количество треков - **" + MAX_SONG_AMOUNT + "**\nОперация не выполнена.").queue();
                        return;
                    }

                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    musicManager.getOperator().queue(tracks.get(0));
                    textChannel.sendMessage("**" + username + "** добавил трек **" + tracks.get(0).getInfo().title + " ("+ getTrackDuration(tracks.get(0).getInfo().length) +")**" ).queue();
                }
                else {
                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    int spaceAvailable = MAX_SONG_AMOUNT - musicManager.getOperator().getQueue().size();
                    if (spaceAvailable > 0) {
                        if (!tracks.isEmpty()){
                            int i = 0;
                            for (; i < spaceAvailable && i < tracks.size(); i++) {
                                musicManager.getOperator().queue(tracks.get(i));
                            }
                            if (spaceAvailable < tracks.size())
                                textChannel.sendMessage("Превышено максимальное количество треков - **" + MAX_SONG_AMOUNT + "**.\nДобавлено **" + i + "** треков из **" + tracks.size() + "**. \nДа, я поставил лимит на количество треков, потому что могу.").queue();
                            else
                                textChannel.sendMessage( "**" +username + "** добавил плейлист из **" + i + "** треков.").queue();
                        }
                    }
                    else {
                        textChannel.sendMessage("Превышено максимальное количество треков - **" + MAX_SONG_AMOUNT + "**.\n Операция не выполнена. \nДа, я поставил лимит на количество треков, потому что могу.").queue();
                        return;
                    }
                }

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void search(TextChannel textChannel, String trackURL, AudioManager audioManager) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild(), audioManager);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                musicManager.slistClear();
                List<String> list = new ArrayList<>();
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                String output = "";

                for (int i = 0; i < 10 && i < tracks.size(); i++) {
                    AudioTrack track = tracks.get(i);
                    output += ("**[" + i + "]** " + track.getInfo().title) + " **(" + getTrackDuration(track.getInfo().length) + ")** \n";
                    list.add(track.getInfo().uri);
                }
                musicManager.slistAdd(list);
                textChannel.sendMessage(output + "Чтобы включить трек по номеру, использовать команду **/ips (номер)**").queue();
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void skip(TextChannel textChannel){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.getOperator().nextTrack();
    }

    public void printList(TextChannel textChannel){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.getOperator().printList(textChannel);
        textChannel.sendMessage("Сейчас играет:").queue();
        nowPlaying(textChannel);

    }

    public void nowPlaying(TextChannel textChannel)
    {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.getOperator().nowPlaying(textChannel);
    }

    public void skipMany(TextChannel textChannel, int amount){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.getOperator().skipMany(amount);
    }

    public boolean pause(TextChannel textChannel){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        return musicManager.getOperator().pause();
    }

    public void clearList(TextChannel textChannel){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.getOperator().drainList();
    }

    public String selectFromSearchList(TextChannel textChannel, int num){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        return musicManager.slistGetInfo(num);
    }



    public static PlayerManager GetINSTANCE(){
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
