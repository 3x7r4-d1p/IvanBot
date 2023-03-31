package ivanbot;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandsList {
    public static List<CommandData> getBotCommands(){
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("ihelp","Справка по ИванБоту"));

        OptionData track = new OptionData(OptionType.STRING, "track", "Че слушать будем?", true);
        commandData.add(Commands.slash("iplay", "ыграть музыку пж").addOptions(track));
        commandData.add(Commands.slash("ip", "ыграть музыку пж").addOptions(track));
        commandData.add(Commands.slash("isearch", "поиск по ютубу").addOptions(track));

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

        commandData.add(Commands.slash("systemoutprintlnguildlist", "a debug command"));

        commandData.add(Commands.slash("testcommand", "test command"));
        OptionData hours = new OptionData(OptionType.INTEGER, "hours", "на сколько часов перематываем", true);
        OptionData minutes = new OptionData(OptionType.INTEGER, "minutes", "на сколько минут перематываем", true);
        OptionData seconds = new OptionData(OptionType.INTEGER, "seconds", "на сколько секунд перематываем", true);
        commandData.add(Commands.slash("iseek", "перемотка трека").addOptions(hours, minutes, seconds));

        OptionData role = new OptionData(OptionType.STRING, "role", "роль", true);
        OptionData secretPhrase = new OptionData(OptionType.STRING, "secretphrase", "secret phrase", true);
        commandData.add(Commands.slash("autorolesadd", "autorolesadd").addOptions(role, secretPhrase));
        return commandData;
    }

}
