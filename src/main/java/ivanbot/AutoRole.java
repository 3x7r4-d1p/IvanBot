package ivanbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.*;
import java.util.Scanner;


public class AutoRole {

    private static final String FILE_PATH = "/home/ivan/rolesConfig.txt";
    public static void addAutoRole(Role role, Guild guild){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            String str = guild.getId() + "-" + role.getId() + "-" + guild.getName() + "\n";
            writer.write(str);
            writer.close();
        }
        catch (IOException e){
            System.out.println("File not found");
        }
    }

    public static void autoRoleSet(Member member, Guild guild){
        try{
            File file = new File (FILE_PATH);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String str = reader.nextLine();
                if (str.contains(guild.getId())){
                    String[] output = str.split("-");
                    Role role = guild.getRoleById(output[1]);
                    guild.addRoleToMember(member, role).queue();
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");

        }
    }
}
