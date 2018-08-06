package <%= config.packages.util %>;

import java.util.EnumMap;
import java.util.Map;

/**
 * Implements a counter C[a,b] for every constant "a" of enum type A and every
 * constant "b" of enum type B.
 *
 * @author Gernot Salzer <salzer@logic.at>
 * @param <A> enum type
 * @param <B> enum type
 */
public class EnumMapCounter<A extends Enum<A>, B extends Enum<B>> {

    private Map<A, EnumCounter<B>> counter;

    /**
     * Increment counter C[a,b]
     *
     * @param a enum constant
     * @param b enum constant
     */
    public void inc(A a, B b) {
        if (a != null) {
            if (counter == null) {
                counter = new EnumMap<>((Class<A>) a.getClass());
            }
            counter.computeIfAbsent(a, k -> new EnumCounter<>());
            counter.get(a).inc(b);
        }
    }

    /**
     * Increment counter C[a,b] for every mapping a->b in the map
     *
     * @param map collection of a->b mappings
     */
    public void add(Map<A, B> map) {
        if (map != null) {
            for (A a : map.keySet()) {
                inc(a, map.get(a));
            }
        }
    }

    /**
     * Get current value of counter C[a,b].
     *
     * @param a enum constant
     * @param b enum constant
     * @return current value of counter C[a,b].
     */
    public int get(A a, B b) {
        return (counter == null || counter.containsKey(a)) ? 0 : counter.get(a).get(b);
    }

    /**
     * Convert counters to a string.
     *
     * @return string representation of current counter values.
     */
    @Override
    public String toString() {
        if (counter == null) {
            return null;
        } else {
            StringBuilder string = new StringBuilder();
            for (A a : counter.keySet()) {
                string.append(a).append(": ").append(counter.get(a)).append("\n");
            }
            return string.toString();
        }
    }

}
