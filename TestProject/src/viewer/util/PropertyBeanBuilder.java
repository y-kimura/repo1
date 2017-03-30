package viewer.util;

public interface PropertyBeanBuilder<T> {
    public T build( PropertyReader in );
}
