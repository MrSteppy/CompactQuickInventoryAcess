package steptech.compactquickinventoryaccess.api.functions;

public interface TriFunction<T, U, S, R> {
    R apply(T t, U u, S s);
}
