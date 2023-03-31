package ivanbot;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventManager extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getGuild().getTextChannels().get(0).sendMessage("sup").queue();
        event.getGuild().updateCommands().addCommands(CommandsList.getBotCommands()).queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(CommandsList.getBotCommands()).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        System.out.println("Member has joined: " + event.getMember());
        AutoRole.autoRoleSet(event.getMember(), event.getGuild());
    }

}