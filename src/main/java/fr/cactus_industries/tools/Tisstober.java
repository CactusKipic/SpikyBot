package fr.cactus_industries.tools;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tisstober {
    
    private static DiscordApi api;
    private static Map<String, Object> config = null;
    private static Timer timer = new Timer();
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    
    public static void Initiate(DiscordApi discordapi){
        File f = new File("./tisstober.yml");
        if(!f.exists()){
            System.out.println("Config file for tisstober not found !");
            return;
        }
        try {
            config = new Yaml().load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not load tisstober config.");
            return;
        }
        config.keySet().stream().forEach(s -> System.out.println("Row: "+s));
        api = discordapi;
        
        ScheduleNext();
        
        /*try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse("23:48 29/09/2020"));
            
            System.out.println("Date: "+sdf.format(new Date()));
            
            timer.schedule(new TissSchedule(api, api.getChannelById((long) config.get("chanID")).get().asTextChannel().get(), true), cal.getTime());
            
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        
    }
    
    public static Calendar makeDate(){
        Calendar cal = Calendar.getInstance();
        String[] str = getConfigString("hour").split(":");
        
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),Integer.parseInt(str[0]), Integer.parseInt(str[1]));
        
        return cal;
    }
    
    public static void ScheduleNext() {
        timer.cancel();
        timer = new Timer();
        Calendar cal = makeDate();
        if(!cal.getTime().after(new Date())){
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Calendar endDate = Calendar.getInstance();
        try {
            endDate.setTime(sdf.parse(getConfigString("hour")+" "+getConfigString("enddate")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(cal.compareTo(endDate)<=0)
            timer.schedule(new TissSchedule(api, api.getChannelById((long) config.get("chanID")).get().asTextChannel().get(), true), cal.getTime());
        else
            System.out.println("Fin du Tisstober");
    }
    
    public static String getConfigString(String path){
        String[] list = path.split("\\.");
        Object o = null;
        Map<String, Object> map = config;
        for (String  s:list){
            o = map.get(s);
            if(o == null)
                return "";
            else
                if(o instanceof Map)
                    map = (Map<String, Object>) o;
        }
        
        return (String) o;
    }
    
    public static void FakeTask(TextChannel tchan, boolean b){
        TissSchedule t = new TissSchedule(api, tchan,b);
        t.run();
    }
    
    public static void FireTask(){
        TissSchedule t = new TissSchedule(api, api.getChannelById((long) config.get("chanID")).get().asTextChannel().get(), true);
        t.run();
    }
    
    public static boolean ReloadYml(){
        File f = new File("./tisstober.yml");
        if(!f.exists()){
            System.out.println("Config file for tisstober not found !");
            return false;
        }
        try {
            config = new Yaml().load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not load tisstober config.");
            return false;
        }
        return true;
    }
    
    
    
    
}
