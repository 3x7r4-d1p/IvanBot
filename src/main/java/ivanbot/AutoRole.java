package ivanbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class AutoRole {

    private static final Path FILE_PATH = Paths.get("/home/ivan/rolesConfig.txt");
    private static final Path HELP_FILE_PATH = Paths.get("/home/ivan/rolesConfigHelp.txt");
    public static void addAutoRole(Role role, Guild guild){
        try {
            FileWriter writer = new FileWriter(FILE_PATH.toFile(), true);
            String str = guild.getId() + "-" + role.getId() + "-" + guild.getName() + "\n";
            writer.append(str);
            writer.close();
        }
        catch (IOException e){
            System.out.println("IO Error");
        }
    }

    public static void autoRoleSet(Member member, Guild guild){
        try{
            File file = new File (FILE_PATH.toUri());
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String str = reader.nextLine();
                if (str.contains(guild.getId())){
                    String[] output = str.split("-");
                    Role role = guild.getRoleById(output[1]);
                    guild.addRoleToMember(member, role).queue();
                    reader.close();
                    return;
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    public static void autoRoleRemove(Guild guild, Role role){
        try{
            File file = new File (FILE_PATH.toUri());
            Scanner reader = new Scanner(file);
            try {
                FileWriter writer = new FileWriter(HELP_FILE_PATH.toFile(), true);

                while (reader.hasNextLine()){
                    String str = reader.nextLine() + "\n";
                    if (str.contains(guild.getId()) && str.contains(role.getId())){
                    }
                    else {
                        writer.append(str);
                    }
                }
                reader.close();
                file.delete();
                writer.close();

                    File newFile = new File (HELP_FILE_PATH.toUri());
                    Files.move(HELP_FILE_PATH, HELP_FILE_PATH.resolveSibling("rolesConfig.txt"));

            }
            catch (IOException e){
                System.out.println("IO Error");
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    public static void autoRoleDisplay(Guild guild, TextChannel channel){
        try{
            File file = new File (FILE_PATH.toUri());
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String str = reader.nextLine();
                channel.sendMessage(str).queue();
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }
}
