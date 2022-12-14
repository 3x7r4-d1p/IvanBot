package ivanbot;

public class TrackDuration {
    public static String getTrackDuration(long ms){
        String output = "";
        ms /= 1000;
        Integer minutes = (int) ms / 60;
        Integer seconds = (int) ms - minutes * 60;
        Integer hours = 0;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }

        //I hope no one will see this:
        if (hours !=0) {
            if (hours < 10) {
                output += "0" + hours + ":";
            }
            else
            output += hours + ":";
        }

        if (minutes < 10) {
            output += "0" + minutes + ":";
        }
        else
            output += minutes + ":";

        if (seconds < 10) {
            output += "0" + seconds;
        }
        else
            output += seconds.toString();

        return output;
    }
}
