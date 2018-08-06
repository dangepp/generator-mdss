package <%= config.packages.util %>;

import java.util.Set;

/**
 * Implements two counters Seen[a] and Unseen[a] for every constant "a" of enum
 * type A in order to count how often "a" was seen/not seen in the sets added to
 * the counter.
 *
 * @author Gernot Salzer <salzer@logic.at>
 * @param <A> enum type
 */
public class EnumSetCounter<A extends Enum<A>> {

    private enum Seen {

        no, yes
    }
    private final EnumMapCounter<A, Seen> counter = new EnumMapCounter<>();

    /**
     * Count the elements in the set as seen and the rest as unseen. Nothing is
     * done if the set is null or empty.
     *
     * @param set set of enum constants
     */
    public void add(Set<A> set) {
        if (set != null && !set.isEmpty()) {
            Class<A> classA = set.iterator().next().getDeclaringClass();
            for (A a : classA.getEnumConstants()) {
                if (set.contains(a)) {
                    counter.inc(a, Seen.yes);
                } else {
                    counter.inc(a, Seen.no);
                }
            }
        }
    }

    /**
     * Returns the number how often "a" has been seen in the added sets
     *
     * @param a enum constant
     * @return number of added sets containing "a"
     */
    public int seen(A a) {
        return counter.get(a, Seen.yes);
    }

    /**
     * Returns the number how often "a" has not been seen in the added sets
     *
     * @param a enum constant
     * @return number of added sets not containing "a"
     */
    public int unseen(A a) {
        return counter.get(a, Seen.no);
    }

    @Override
    public String toString() {
        return counter.toString();
    }

}
