package viewer.util;

public interface PropertyWriter {
    public void set( String key, String value );
    public void set( String key, int value );
    public void set( String key, long value );
    public void set( String key, boolean value );
}
