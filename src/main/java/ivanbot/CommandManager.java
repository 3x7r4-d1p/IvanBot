package ivanbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("ihelp","Справка по ИванБоту"));

        OptionData track = new OptionData(OptionType.STRING, "track", "Че слушать будем?", true);
        OptionData number = new OptionData(OptionType.INTEGER, "number", "номер трека по списку", true);
        commandData.add(Commands.slash("iplay", "ыграть музыку пж").addOptions(track));
        commandData.add(Commands.slash("ip", "ыграть музыку пж").addOptions(track));
        commandData.add(Commands.slash("isearch", "поиск по ютубу").addOptions(track));
        commandData.add(Commands.slash("ips", "включить трек по предварительно найденному номеру").addOptions(number));

        commandData.add(Commands.slash("iskip","пропустить трек."));
        commandData.add(Commands.slash("is", "пропустить трек."));

        commandData.add(Commands.slash("imychannel", "информация о голосовом канале."));

        commandData.add(Commands.slash("augh", "a secret command"));

        commandData.add(Commands.slash("iclear", "очистить очередь"));

        commandData.add(Commands.slash("ilist", "че там в очереди-то этой?"));
        commandData.add(Commands.slash("iqueue", "че там в очереди-то этой?"));

        commandData.add(Commands.slash("inow", "че за трек сейчас играет?"));

        commandData.add(Commands.slash("ileave", "покинуть канал"));

        OptionData amount = new OptionData(OptionType.INTEGER, "amount", "сколько пропускаем", true);
        commandData.add(Commands.slash("ijump", "пропустить несколько треков").addOptions(amount));

        commandData.add(Commands.slash("ipause", "поставить на паузу"));



        event.getGuild().updateCommands().addCommands(commandData).queue();
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

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
                event.reply("Обрабатываем ваш запрос, ожидайте...").queue();
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
                    event.reply("На канал сначала зайди...");
                    return;
                }
                OptionMapping am = event.getOption("amount");
                int amount = am.getAsInt();
                event.reply("пропускаем **" + amount + "** треков").queue();
                PlayerManager.GetINSTANCE().skipMany(event.getChannel().asTextChannel(), amount);
                break;

            case "ipause":
                if (!voiceChannelCheck(event)) {
                    event.reply("На канал сначала зайди...");
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
                    event.getChannel().asTextChannel().sendMessage("В канал сначала зайди...").queue();
                    return;
                }

                event.reply("Обрабатываем ваш запрос, ожидайте...").queue();
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

            case "ips":
                event.reply("Обрабатываем ваш запрос, ожидайте...").queue();
                OptionMapping option = event.getOption("number");
                int number = option.getAsInt();
                String l = PlayerManager.GetINSTANCE().selectFromSearchList(event.getChannel().asTextChannel(), number);
                loadTrack(event, l);
                break;
        }
    }

    public void loadTrack(SlashCommandInteractionEvent event, String l){

        if (!voiceChannelCheck(event)) {
            event.getChannel().asTextChannel().sendMessage("В канал сначала зайди...").queue();
            return;
        }

        Member member = event.getMember();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final User user = member.getUser();
        final String username = user.getName();

        String link = "";

        audioManager.openAudioConnection(memberChannel);

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
        VoiceChannel memberChannel = member.getVoiceState().getChannel().asVoiceChannel();

        if (memberChannel == null) {
            return false;
        }
        return true;
    }
}
