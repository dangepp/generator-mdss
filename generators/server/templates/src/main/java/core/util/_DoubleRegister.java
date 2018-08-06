package <%= config.packages.util %>;

import java.util.EnumMap;
import java.util.Map;

/**
 * Implements a double register R[a] for every constant "a" of enum type A.
 *
 * @author Gernot Salzer <salzer@logic.at>
 * @param <A> enum type
 */
public class DoubleRegister<A extends Enum<A>> {

    private Map<A, Double> register;

    /**
     * Add value of f to register R[a]
     *
     * @param a enum constant identifying the register
     * @param f number to add
     */
    public void add(A a, Double f) {
        if (a != null) {
            if (register == null) {
                register = new EnumMap<>(a.getDeclaringClass());
            }

            register.putIfAbsent(a, f);
            register.computeIfPresent(a, (b, c) -> c + f);
        }
    }

    /**
     * For every mapping a->f in the map, add value of f to register R[a]
     *
     * @param map collection of a->f mappings
     */
    public void add(Map<A, Double> map) {
        if (map != null) {
            for (A a : map.keySet()) {
                add(a, map.get(a));
            }
        }
    }

    /**
     * Get value of register R[a]
     *
     * @param a enum constant identifying the register
     * @return value of register R[a]
     */
    public Double get(A a) {
        return (register == null || register.get(a) == null) ? 0.0
                : register.get(a);
    }

    /**
     * Convert registers to a string.
     *
     * @return string representation of current register values.
     */
    @Override
    public String toString() {
        return register == null ? null : register.toString();
    }

}