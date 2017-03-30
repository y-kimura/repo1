package viewer.util;

import java.util.Collection;
import java.util.List;

public class CollectionUtils {
    public static boolean startsWith(List<String> list, String str){
        for( String t: list ){
            if( str.startsWith(t) ) return true;
        }
        return false;
    }
    public static String toStrings(Collection e){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for( Object a: e ){
            sb.append(a.toString());
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    public static int[] toInts(String[] values, int def){
        int[] results = new int[values.length];
        for( int i=0; i<values.length; i++ ){
            try {
                results[i] = Integer.parseInt(values[i]);
            }catch(Exception e){
                results[i] = def;
            }
        }
        return results;
    }
    public static String[] toStrings(int[] values){
        String[] results = new String[values.length];
        for( int i=0; i<values.length; i++ ){
            results[i] = Integer.toString(values[i]);
        }
        return results;
    }
    public static int[] split(String value, String sep, int def){
        return toInts(split(value, sep), def);
    }
    public static String join(int[] values, String sep){
        return join(toStrings(values), sep);
    }
    public static String[] split(String value, String sep){
        return value.split(sep);
    }
    public static String join(String[] values, String sep){
        StringBuilder sb = new StringBuilder();
        if( values.length > 0 ){
            sb.append(values[0]);
            for( int i=1; i<values.length; i++ ){
                sb.append(sep);
                sb.append(values[i]);
            }
        }
        return sb.toString();
    }
}
