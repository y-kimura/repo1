package viewer.util;


import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

public class PropertiesUtils {
    private PropertiesUtils(){}

//    private static final LogUtils LOG = new LogUtils(PropertiesUtils.class);

    public static Properties load(File file){
        Properties props = new Properties();
        if( file.exists() && file.isFile() ){
            FileInputStream fis = null;
            InputStreamReader isr = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis, "UTF-8");
                props.load(isr);
            }catch(IOException e){
 //               LOG.error("0001", e, file);
            }finally{
                IOUtils.close(isr);
                IOUtils.close(fis);
            }
        } else {
 //           LOG.info("0003", file);
        }
        return props;
    }
    public static void save(File file, Properties props){
        PrintWriter pw = null;
        if( !file.exists() ){
  //          LOG.info("0004", file);
        }
        if( !file.exists() || file.isFile() ){
            try {
                pw = new PrintWriter(file, "UTF-8");
                props.store(pw, "Java Graphic Browser 0.1");
            }catch(IOException e){
  //              LOG.error("0002", e, file);
            }finally{
                IOUtils.close(pw);
            }
        } else {
 //           LOG.error("0005", file);
        }
    }
    
    public static void set(Properties props, String key, int value){
        props.setProperty(key, Integer.toString(value));
    }
    public static int intValue(Properties props, String key, int def){
        try {
            return Integer.parseInt(props.getProperty(key, ""));
        }catch(NumberFormatException e){
            return def;
        }
    }
    
    public static void set(Properties props, String key, int[] values){
        props.setProperty(key, CollectionUtils.join(values, ","));
    }
    public static int[] intValues(Properties props, String key, int def){
        return CollectionUtils.split(props.getProperty(key, ""), ",", def);
    }

    public static void set(Properties props, String key, String value){
        props.setProperty(key, value);
    }
    public static String stringValue(Properties props, String key, String def){
        return props.getProperty(key, def);
    }

    public static void set(Properties props, String key, String[] values){
        props.setProperty(key, CollectionUtils.join(values, ","));
    }
    public static String[] stringValues(Properties props, String key, String[] def){
        if( !props.containsKey(key) ){
            return def;
        }
        return props.getProperty(key).split(",");
    }
    
    public static void set(Properties props, String key, boolean value){
        props.setProperty(key, Boolean.toString(value));
    }
    public static boolean booleanValue(Properties props, String key, boolean def){
        try {
            return Boolean.parseBoolean(props.getProperty(key));
        }catch(Exception e){
            return def;
        }
    }

    public static void set(Properties props, String key, Color color){
        props.setProperty(key, Integer.toHexString(color.getRGB()));
    }
    public static Color colorValue(Properties props, String key, Color color){
        try {
            return new Color(Integer.parseInt(props.getProperty(key, ""), 16));
        }catch(Exception e){
            return color;
        }
    }

    public static void set(Properties props, String key, long value){
        props.setProperty(key, Long.toString(value));
    }
    public static long longValue(Properties props, String key, long def){
        try {
            return Long.parseLong(props.getProperty(key));
        }catch(Exception e){
            return def;
        }
    }

    public static void set(Properties props, String key, File file){
        props.setProperty(key, file.getAbsolutePath());
    }
    public static File fileValue(Properties props, String key, File value){
        try {
            String val = props.getProperty(key);
            if( val == null ) return value;
            val = val.trim();
            if( val.isEmpty() ) return value;
            
            return new File(val);
        }catch(Exception e){
            return value;
        }
    }
}
