package viewer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import viewer.Main;

public class IOUtils {
    private IOUtils(){}

//    private static final logUtils log = new logUtils(IOUtils.class);
    
    private static class PropertiesPropertyReaderWriter implements  PropertyReader, PropertyWriter {
        private Properties properties;
        private String header;
        private PropertiesPropertyReaderWriter(Properties props, String header){
            this.properties = props;
            this.header = header+".";
        }
        public void set( String key, String value ){
            PropertiesUtils.set(properties, header+key, value);
        }
        public String stringValue( String key, String def ){
            return PropertiesUtils.stringValue(properties, header+key, def);
        }
        public void set( String key, int value ){
            PropertiesUtils.set(properties, header+key, value);
        }
        public int intValue( String key, int def ){
            return PropertiesUtils.intValue(properties, header+key, def);
        }
        public void set( String key, boolean value ){
            PropertiesUtils.set(properties, header+key, value);
        }
        public boolean booleanValue( String key, boolean def ){
            return PropertiesUtils.booleanValue(properties, header+key, def);
        }
        public void set( String key, long value ){
            PropertiesUtils.set(properties, header+key, value);
        }
        public long longValue( String key, long def ){
            return PropertiesUtils.longValue(properties, header+key, def);
        }
    }
//    
    private static class PropertiesPropertyReaderIterable implements Iterable<PropertyReader> {
        private Properties properties;
        private String header;
        private int size;
        public PropertiesPropertyReaderIterable( Properties properties, String header, int size ){
            this.properties = properties;
            this.header = header;
            this.size = size;
        }
        public Iterator<PropertyReader> iterator(){
            return new Iterator<PropertyReader>() {
                int count = 0;
                public boolean hasNext(){
                    return count < size;
                }
                public PropertyReader next(){
                    return new PropertiesPropertyReaderWriter(properties, header+"."+(count++));
                }
                public void remove(){
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }
    }
    
    public static void close(Closeable c){
        if( c != null ){
            try{ c.close(); }catch(Exception e){}
        }
    }
//    public static Map<String,List<String>> readListMapFromCSVFile(File file){
//        Map<String,List<String>> result = new HashMap<String, List<String>>();
//        if( file.exists() && file.isFile() && file.canRead() ){
//            FileInputStream fis = null;
//            InputStreamReader isr = null;
//            BufferedReader br = null;
//            try {
//                fis = new FileInputStream(file);
//                isr = new InputStreamReader(fis, "UTF-8");
//                br = new BufferedReader(isr);
//                String line = null;
//                while((line=br.readLine()) != null){
//                    String[] items = line.split(",");
//                    List<String> tmp = new ArrayList<String>();
//                    for( int i=1; i<items.length; i++ ){
//                        tmp.add(items[i]);
//                    }
//                    result.put(items[0], tmp);
//                }
//            }catch(IOException e){
////                log.error("0001");
//            }finally{
//                close(br);
//                close(isr);
//                close(fis);
//            }
//        }
//        return result;
//    }
//    public static Map<String,String> readMapFromCSVFile(File file){
//        Map<String,String> result = new HashMap<String, String>();
//        if( file.exists() && file.isFile() && file.canRead() ){
//            FileInputStream fis = null;
//            InputStreamReader isr = null;
//            BufferedReader br = null;
//            try {
//                fis = new FileInputStream(file);
//                isr = new InputStreamReader(fis, "UTF-8");
//                br = new BufferedReader(isr);
//                String line = null;
//                while((line=br.readLine()) != null){
//                    String[] items = line.split(",");
//                    result.put(items[0], items[1]);
//                }
//            }catch(IOException e){
// //               log.error("0002", e, file);
//            }finally{
//                close(br);
//                close(isr);
//                close(fis);
//            }
//        }
//        return result;
//    }
//    public static<T> List<T> readListFromCSVFile(File file, Class<T> klass, StringsBeanBuilder<T> builder){
//        List<T> list = new ArrayList<T>();
//        if( file.exists() && file.isFile() && file.canRead() ){
//            FileInputStream fis = null;
//            InputStreamReader isr = null;
//            BufferedReader br = null;
//            try {
//                fis = new FileInputStream(file);
//                isr = new InputStreamReader(fis, "UTF-8");
//                br = new BufferedReader(isr);
//                String line;
//                while((line=br.readLine()) != null){
//                    String[] items = line.split(",");
//                    list.add(builder.build(items));
//                }
//            }catch(IOException e){
// //               log.error("0003", e, file, klass);
//            }finally{
//                close(br);
//                close(isr);
//                close(fis);
//            }
//        }
//        return list;
//    }
//    public static<T> void writeListToCSVFile(List<T> beans, File file, StringsBuilder<T> builder){
//        if( !file.exists() || (!file.isDirectory() && file.canWrite()) ){
//            PrintWriter pw = null;
//            try {
//                pw = new PrintWriter(file, "UTF-8");
//                for( T bean: beans ){
//                    String[] items = builder.build(bean);
//                    if( items.length > 0 ){
//                        pw.print(items[0]);
//                        for( int i=1; i<items.length; i++ ){
//                            pw.print(",");
//                            pw.print(items[i]);
//                        }
//                        pw.println();
//                    }
//                }
//            }catch(IOException e){
//  //              log.error("0004", e, file);
//            }finally{
//                close(pw);
//            }
//        }
//    }
    public static<T> List<T> readListFromPropertiesFile(File file, Class<T> klass, PropertyBeanBuilder<T> builder){
        return readListFromProperties(PropertiesUtils.load(file), klass, builder);
    }
    public static<T> List<T> readListFromProperties(Properties props, Class<T> klass, PropertyBeanBuilder<T> builder){
        List<T> result = new ArrayList<T>();
        int size = PropertiesUtils.intValue(props, "SIZE", 0);
        for( int i=0; i<size; i++ ){
            PropertyReader in = new PropertiesPropertyReaderWriter(props, "ITEM."+i);
            result.add(builder.build(in));
        }
        return result;
    }
    public static<T> void writeListToPropertiesFile(List<T> beans, File file, PropertyBuilder<T> builder){
        Properties props = new Properties();
        writeListToProperties(beans, props, builder);
        PropertiesUtils.save(file, props);
    }
    public static<T> void writeListToProperties(List<T> beans, Properties props, PropertyBuilder<T> builder){
        PropertiesUtils.set(props, "SIZE", beans.size());
        for( int i=0; i<beans.size(); i++ ){
            PropertyWriter out = new PropertiesPropertyReaderWriter(props, "ITEM."+i);
            builder.build(beans.get(i), out);
        }
    }
    public static<T> void writeIterableToPropertiesFile(Iterable<T> beans, File file, PropertyBuilder<T> builder){
        Properties props = new Properties();
        writeIterableToProperties(beans, props, builder);
        PropertiesUtils.save(file, props);
    }
    public static<T> void writeIterableToProperties(Iterable<T> beans, Properties props, PropertyBuilder<T> builder){
        int count = 0;
        for( T bean: beans ){
            PropertyWriter out = new PropertiesPropertyReaderWriter(props, "ITEM."+count);
            builder.build(bean, out);
            count++;
        }
        PropertiesUtils.set(props, "SIZE", count);
    }
    public static Iterable<PropertyReader> createPropertyReaderIterable(File file){
        return createPropertyReaderIterable(PropertiesUtils.load(file));
    }
    public static Iterable<PropertyReader> createPropertyReaderIterable(Properties props){
        int size = PropertiesUtils.intValue(props, "SIZE", 0);
        return new PropertiesPropertyReaderIterable(props, "ITEM", size);
    }
//    public static boolean copy(File src, File dst){
//        byte[] bytes = new byte[1024];
//        int len = 0;
//        FileInputStream fis = null;
//        BufferedInputStream bis = null;
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        try {
//            fis = new FileInputStream(src);
//            bis = new BufferedInputStream(fis);
//            fos = new FileOutputStream(dst);
//            bos = new BufferedOutputStream(fos);
//
//            while((len = bis.read(bytes)) >= 0){
//                bos.write(bytes, 0, len);
//            }
//            
//        }catch(IOException e){
// //           log.error("0005", src, dst, e);
//            return false;
//        }finally{
//            close(bos);
//            close(fos);
//            close(bis);
//            close(fis);
//        }
//        return true;
//    }
//    public static boolean rotate(File file, int max, boolean backup){
//        File next = new File(file.getPath()+"."+max);
//        if( next.exists() ){
//            boolean suc = next.delete();
//            if( !suc ) return false;
//        }
//        for( int i=max; i>1; i-- ){
//            next = new File(file.getPath()+"."+i);
//            File cur = new File(file.getPath()+"."+(i-1));
//            if( cur.exists() ){
//                boolean suc = cur.renameTo(next);
//                if( !suc ) return false;
//            }
//        }
//        next = new File(file.getPath()+".1");
//        if( backup ){
//            return copy(file, next);
//        } else {
//            return file.renameTo(next);
//        }
//    }
}
