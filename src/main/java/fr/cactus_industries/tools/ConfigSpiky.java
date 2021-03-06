package fr.cactus_industries.tools;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Map;

public class ConfigSpiky {
    
    private static Map<String, Object> config = null;
    
    public static boolean init() {
        final File f = new File("./config.yml");
        if (!f.exists()) {
            try {
                final InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("config.yml");
                final OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                final byte[] buff = new byte[1024];
                int length;
                while ((length = in.read(buff)) > 0) {
                    out.write(buff, 0, length);
                }
                out.close();
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ConfigSpiky.config = new Yaml().load(new FileInputStream(f));
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
            System.out.println("Could not load config.");
            return false;
        }
        return true;
    }
    
    public static String getConfigString(final String path) {
        final String[] list = path.split("\\.");
        Object o = null;
        Map<String, Object> map = ConfigSpiky.config;
        for (final String s : list) {
            o = map.get(s);
            if (o == null) {
                return "";
            }
            if (o instanceof Map) {
                map = (Map<String, Object>)o;
            }
        }
        return (String)o;
    }
    
    public static <T> T getConfigObj(final String path, final Class<T> type) {
        final String[] list = path.split("\\.");
        Object o = null;
        Map<String, Object> map = ConfigSpiky.config;
        for (final String s : list) {
            o = map.get(s);
            if (o == null) {
                return null;
            }
            if (o instanceof Map) {
                map = (Map<String, Object>)o;
            }
        }
        return (T)o;
    }
    
    private static Map<String, Object> getConfig() {
        if (ConfigSpiky.config == null) {
            init();
        }
        return ConfigSpiky.config;
    }
}
