package ivanbot;

import static ivanbot.TrackDuration.getTrackDuration;

public class TextArtPlayer {
    private long duration;
    private long position;

    public TextArtPlayer (long duration, long position){
        this.duration = duration;
        this.position = position;
    }

    public String buildAndPrintTextPlayer(){
        long timelinePart = duration / 12;
        long timelinePartsCompleted = position / timelinePart;
        long timelinePartsRemaining = 12 - timelinePartsCompleted - 1;

        return getTrackDuration(position) + "─".repeat((int)timelinePartsCompleted) + "⬤" + "─".repeat((int)timelinePartsRemaining) + getTrackDuration(duration);
    }
}
