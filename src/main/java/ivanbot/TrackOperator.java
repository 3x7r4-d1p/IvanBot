package ivanbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.*;

import static ivanbot.TrackDuration.getTrackDuration;
import static java.util.concurrent.TimeUnit.*;

public class TrackOperator extends AudioEventAdapter {
    private final AudioPlayer audioPlayer;
    private final AudioManager audioManager;
    private final BlockingQueue<AudioTrack> queue;

    private final int TIMEOUT_TIME = 5; // Time in minutes

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TrackOperator(AudioPlayer audioPlayer, AudioManager audioManager){
        this.audioPlayer = audioPlayer;
        this.audioManager = audioManager;
        this.queue = new LinkedBlockingQueue<>();
    }
    public void playingCheck() {
        final Runnable checker = new Runnable() {

            public void run() {
                AudioTrack track = audioPlayer.getPlayingTrack();
                if (track == null && audioManager.isConnected())
                {
                    audioManager.closeAudioConnection();
                }
            }
        };
        final ScheduledFuture<?> checkerHandle = scheduler.schedule(checker, TIMEOUT_TIME, MINUTES);
    }

    public void queue(AudioTrack track){
        if (!this.audioPlayer.startTrack(track, true)){
            this.queue.offer(track);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext)
            nextTrack();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {

    }

    public BlockingQueue<AudioTrack> getQueue(){
        return queue;
    }

    public boolean nextTrack(){
        this.audioPlayer.startTrack(this.queue.poll(), false);
        AudioTrack track = audioPlayer.getPlayingTrack();

        try {
            if (track == null && audioManager.isConnected()) {
                playingCheck();
                return false;
            }
        } catch (NullPointerException e){
            //its kinda empty
        }
        return true;
    }

    public boolean pause(){
        if (!audioPlayer.isPaused()){
            audioPlayer.setPaused(true);
            return true;
        }
        else {
            audioPlayer.setPaused(false);
            return false;
        }
    }

    public void skipMany(int amount){
        for (int i = 0; i < amount; i++)
        {
            if (!nextTrack())
            {
                break;
            }
        }
    }

    public void drainList(){
        this.queue.removeAll(queue);
    }

    public void printList(TextChannel textChannel){
        String output = "";
        int i = 1;
        for (AudioTrack track : queue)
        {
            output += ("**["+ i +"]** " + track.getInfo().title + " **(" + getTrackDuration(track.getInfo().length) + ")**" + "\n");
            i++;
            if (i >= 10) {
                break;
            }
        }
        if (i > 10)
        {
            output += "**и так далее...**\n";
        }
        output +=("Всего: **"+ queue.size() + "** треков.");

        textChannel.sendMessage(output).queue();
    }

    public void nowPlaying(TextChannel textChannel)
    {
        AudioTrack track = audioPlayer.getPlayingTrack();
        try
        {
            textChannel.sendMessage(track.getInfo().title).queue();
            TextArtPlayer textArtPlayer = new TextArtPlayer(track.getDuration(), track.getPosition());
            textChannel.sendMessage(textArtPlayer.buildAndPrintTextPlayer()).queue();
        }
        catch (NullPointerException e)
        {
            textChannel.sendMessage("Ниче не играет...").queue();
        }
    }

    public void seek(TextChannel textChannel, long position){
        AudioTrack track = audioPlayer.getPlayingTrack();

        track.setPosition(position);
        textChannel.sendMessage("Трек перемотан...").queue();
    }
}
