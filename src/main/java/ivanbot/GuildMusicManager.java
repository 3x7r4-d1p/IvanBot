package ivanbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class GuildMusicManager {

    private final AudioPlayer audioPlayer;

    private final trackOperator operator;

    private final audioPlayerSendHandler sendHandler;

    private AudioManager audioManager = null;


    private SearchList list;

    public GuildMusicManager(AudioPlayerManager manager, AudioManager audioManager){
        this.audioManager = audioManager;
        this.audioPlayer = manager.createPlayer();
        this.operator = new trackOperator(this.audioPlayer, audioManager);
        this.audioPlayer.addListener(this.operator);
        this.sendHandler = new audioPlayerSendHandler(this.audioPlayer);
        this.list = new SearchList();
    }
    public GuildMusicManager(AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.operator = new trackOperator(this.audioPlayer, this.audioManager);
        this.audioPlayer.addListener(this.operator);
        this.sendHandler = new audioPlayerSendHandler(this.audioPlayer);
        this.list = new SearchList();
    }

    public trackOperator getOperator(){
        return operator;
    }

    public void slistAdd(List<String> l){
        list.searchListAdd(l);
    }

    public String slistGetInfo(int num){
        return list.getInfo(num);
    }

    public audioPlayerSendHandler getSendHandler(){
        return this.sendHandler;
    }
}
