package viewer.util;

public interface PropertyReader {
    public String stringValue( String key, String def );
    public int intValue( String key, int def );
    public long longValue( String key, long def );
    public boolean booleanValue( String key, boolean def );
}
