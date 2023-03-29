package ivanbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class GuildMusicManager {

    private final AudioPlayer audioPlayer;

    private final TrackOperator operator;

    private final AudioPlayerSendHandler sendHandler;

    private AudioManager audioManager = null;


    public GuildMusicManager(AudioPlayerManager manager, AudioManager audioManager){
        this.audioManager = audioManager;
        this.audioPlayer = manager.createPlayer();
        this.operator = new TrackOperator(this.audioPlayer, audioManager);
        this.audioPlayer.addListener(this.operator);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }
    public GuildMusicManager(AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.operator = new TrackOperator(this.audioPlayer, this.audioManager);
        this.audioPlayer.addListener(this.operator);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public TrackOperator getOperator(){
        return operator;
    }

    public AudioPlayerSendHandler getSendHandler(){
        return this.sendHandler;
    }
}
