package viewer.util;

public interface PropertyBuilder<T> {
    public void build( T bean, PropertyWriter out );
}
