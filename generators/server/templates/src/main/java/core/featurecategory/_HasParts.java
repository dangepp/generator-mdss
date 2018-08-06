package <%= config.packages.category %>;

/**
 * Interface for describing transitive relationships between features of categories.
 */
public interface HasParts<E extends Enum<E>> {

  /**
   * Checks whether the present value is a part of 
   * the given value.
   * @param whole - the part to be checked
   * @return - true if the present part is part of whole; false otherwise
   */
  boolean isPartOf(E whole);

}