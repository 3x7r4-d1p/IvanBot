package ivanbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        Dotenv file = Dotenv.configure().load();
        String PHRASE = file.get("PHRASE");

        switch(command)
        {
            case "ihelp":
                String output = "";
                try {
                    File helpFile = new File("/home/ivan/help.txt");
                    Scanner helpReader = new Scanner(helpFile);
                    while (helpReader.hasNextLine()) {
                        output += helpReader.nextLine().toString() + "\n";
                    }
                    event.reply(output).queue();
                    helpReader.close();
                } catch (FileNotFoundException e) {
                    event.reply("failed to open help.txt").queue();
                }
                break;

            case "iplay":

            case "ip":
                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                loadTrack(event, null);
                break;

            case "iskip":

            case "is":
                event.reply("Следующий трек...").queue();
                PlayerManager.GetINSTANCE().skip(event.getChannel().asTextChannel());
                break;

            case "iclear":
                event.reply("Очищаем очередь...").queue();
                PlayerManager.GetINSTANCE().clearList(event.getChannel().asTextChannel());
                break;

            case "ilist":

            case "iqueue":
                event.reply("Содержимое очереди __(первые 10 треков)__:").queue();
                PlayerManager.GetINSTANCE().printList(event.getChannel().asTextChannel());
                break;

            case "inow":
                event.reply("Сейчас играет трек:").queue();
                PlayerManager.GetINSTANCE().nowPlaying(event.getChannel().asTextChannel());
                break;

            case "ileave":
                final AudioManager audioManager = event.getGuild().getAudioManager();
                PlayerManager.GetINSTANCE().clearList(event.getChannel().asTextChannel());
                PlayerManager.GetINSTANCE().skip(event.getChannel().asTextChannel());
                event.reply("Покидаем канал.").queue();
                audioManager.closeAudioConnection();
                break;

            case "ijump":
                if (!voiceChannelCheck(event)) {
                    event.reply("На канал сначала зайди...").queue();
                    return;
                }
                OptionMapping am = event.getOption("amount");
                int amount = am.getAsInt();
                event.reply("пропускаем **" + amount + "** треков").queue();
                PlayerManager.GetINSTANCE().skipMany(event.getChannel().asTextChannel(), amount);
                break;

            case "ipause":
                if (!voiceChannelCheck(event)) {
                    event.reply("На канал сначала зайди...").queue();
                    return;
                }
                if (PlayerManager.GetINSTANCE().pause(event.getChannel().asTextChannel()))
                {
                    event.reply("Ставим на паузу...").queue();
                }
                else event.reply("Возобновляем трек...").queue();
                break;

            case "isearch":
                if (!voiceChannelCheck(event)) {
                    event.getChannel().asTextChannel().sendMessage("На канал сначала зайди...").queue();
                    return;
                }

                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                Member member = event.getMember();
                final VoiceChannel memberChannel = (VoiceChannel) member.getVoiceState().getChannel();
                final User user = member.getUser();
                final String username = user.getName();

                String link;

                OptionMapping messageOption = event.getOption("track");
                link = messageOption.getAsString();


                link = "ytsearch:" + link;

                PlayerManager.GetINSTANCE().search(event.getChannel().asTextChannel(), link, event.getGuild().getAudioManager());
                break;

            case "systemoutprintlnguildlist":
                event.reply("debug info has been sent to the console").queue();
                for (Guild g : event.getJDA().getGuilds()){
                    System.out.println(g.toString());
                    for (Member m: g.getMembers()){
                        System.out.println(m.toString());
                    }
                    System.out.println();
                }
                break;

            case "testcommand":
                break;

            case "iseek":
                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                OptionMapping hours = event.getOption("hours");
                OptionMapping minutes = event.getOption("minutes");
                OptionMapping seconds = event.getOption("seconds");
                long position = seconds.getAsInt() * 1000;
                position += minutes.getAsInt() * 1000 * 60;
                position += hours.getAsInt() * 1000 * 3600;

                PlayerManager.GetINSTANCE().seek(event.getChannel().asTextChannel(), position);
                break;

            case "autorolesadd":
                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                OptionMapping roleName = event.getOption("role");
                OptionMapping secretPhrase = event.getOption("secretphrase");
                String r = roleName.getAsString();
                Role role = event.getGuild().getRolesByName(r, true).get(0);

                if (!secretPhrase.getAsString().equals(PHRASE)){
                    event.getChannel().asTextChannel().sendMessage("SECRET PHRASE CHECK FAILED").queue();
                }
                else{
                    if (!role.equals(null)) {
                        event.getChannel().asTextChannel().sendMessage("AUTO ROLE ADDED").queue();
                        AutoRole.addAutoRole(role, event.getGuild());
                    }
                }
                break;

            case "autoroleremove":
                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                Role role_ = event.getGuild().getRolesByName(event.getOption("role").getAsString(), true).get(0);
                if (!event.getOption("secretphrase").getAsString().equals(PHRASE)){
                    event.getChannel().asTextChannel().sendMessage("SECRET PHRASE CHECK FAILED").queue();
                }
                else if (!role_.equals(null)){
                    AutoRole.autoRoleRemove(event.getGuild(), role_);
                }
                break;

            case "autorolesdisplay":
                event.reply("Обрабатываем ваш запрос, ожидайте...").setEphemeral(true).queue();
                if (!event.getOption("secretphrase").getAsString().equals(PHRASE)){
                    event.getChannel().asTextChannel().sendMessage("SECRET PHRASE CHECK FAILED").queue();
                }
                else AutoRole.autoRoleDisplay(event.getChannel().asTextChannel());
                break;
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        event.getMessage().delete().queue();
        event.reply("Трек выбран.").setEphemeral(true).queue();
        String l = event.getValues().get(0);
        if (l != null)
            loadTrack(event, l);
    }

    public void loadTrack(SlashCommandInteractionEvent event, String l){

        if (!voiceChannelCheck(event)) {
            event.getChannel().asTextChannel().sendMessage("На канал сначала зайди...").queue();
            return;
        }

        Member member = event.getMember();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final User user = member.getUser();
        final String username = user.getName();

        String link = "";
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(memberChannel);
        }


        if (l == null)
        {
            OptionMapping messageOption = event.getOption("track");
            link = messageOption.getAsString();

            if (!isUrl(link)){
                link = "ytsearch:" + link + " audio";
            }
        }
        else {
            link = l;
        }
        PlayerManager.GetINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), link, username, audioManager);

    }

    public void loadTrack(StringSelectInteractionEvent event, String l){

        if (!voiceChannelCheck(event)) {
            event.getChannel().asTextChannel().sendMessage("На канал сначала зайди...").queue();
            return;
        }

        Member member = event.getMember();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final User user = member.getUser();
        final String username = user.getName();

        audioManager.openAudioConnection(memberChannel);
        PlayerManager.GetINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), l, username, audioManager);

    }
    public boolean isUrl(String url) {
        try{
            URL u = new URL(url);
            try {
                u.toURI();
                return true;
            }
            catch (URISyntaxException p)
            {
                return false;
            }
        } catch (MalformedURLException e){
            return false;
        }
    }

    public boolean voiceChannelCheck(SlashCommandInteractionEvent event){
        Member member = event.getMember();
        try {
            VoiceChannel memberChannel = member.getVoiceState().getChannel().asVoiceChannel();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
    public boolean voiceChannelCheck(StringSelectInteractionEvent event){
        Member member = event.getMember();
        try {
            VoiceChannel memberChannel = member.getVoiceState().getChannel().asVoiceChannel();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}