package <%= config.packages.util %>;

import java.util.EnumMap;
import java.util.Map;

/**
 * Implements a counter C[a] for every constant "a" of enum type A.
 *
 * @param <A> Enum type
 * @author Gernot Salzer <salzer@logic.at>
 */
class EnumCounter<A extends Enum<A>> {

    private Map<A, Integer> counter;

    /**
     * Increment counter C[a]
     *
     * @param a enum constant
     */
    void inc(A a) {
        if (a != null) {
            if (counter == null) {
                counter = new EnumMap<>((Class<A>)a.getClass());
            }
            counter.putIfAbsent(a, 1);
            counter.computeIfPresent(a, (b, c) -> c + 1);
        }
    }

    /**
     * Get current value of counter C[a].
     *
     * @param a enum constant
     * @return current value of counter C[a].
     */
    public int get(A a) {
        return (counter == null || counter.containsKey(a)) ? 0 : counter.get(a);
    }

    /**
     * Convert counters to a string.
     *
     * @return string representation of current counter values.
     */
    @Override
    public String toString() {
        return counter == null ? null : counter.toString();
    }

}
