package <%= config.packages.score %>;

import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.category %>.HasParts;
import <%= config.packages.enum %>.Category;
import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.profile %>.PatientProfile;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Util class for computing conflicts
 */
public final class ConflictUtil {

    /**
     * Determines whether there is a conflict between the patientProfile and the diseaseProfile
     * @param diseaseProfile - the disease
     * @param patientProfile - the patient
     * @return true if there is a conflict; false otherwise
     */
    public static boolean isConflict(DiseaseProfile diseaseProfile, PatientProfile patientProfile) {
        return         
        <%_ var first = true _%>
        <%_ for (var i = 0; i < categories.length; i++) {_%>
            <%_ if(!(categories[i].type === 'ratio') && !categories[i].noHardConflict) {_%>
                <%_ if(categories[i].exclusive) {_%>
                <%= first ? '' : '||' %> conflict<%-categories[i].hasParts ? 'PartOf' : ''%>(diseaseProfile.get<%= categories[i].name %>(), setOf(patientProfile.get<%= categories[i].name %>()), diseaseProfile.getSoftConflictCategories().contains(Category.<%- categories[i].enumName %>))
                <%_} else {_%>
                <%= first ? '' : '||' %> conflict<%-categories[i].hasParts ? 'PartOf' : ''%>(diseaseProfile.get<%= categories[i].name %>(), patientProfile.get<%= categories[i].name %>(), diseaseProfile.getSoftConflictCategories().contains(Category.<%- categories[i].enumName %>))
                <%_} _%>
                <%_ var first = false _%>
        <%_}}_%>;

    }

    private static <T extends Enum<T>> boolean conflict(Map<T, Likelihood> disease, Set<T> patient, boolean excludeConflict) {
        if (!excludeConflict && disease != null && patient != null && !patient.isEmpty()) {
            for (T p : patient) {
                if (disease.get(p) != null && disease.get(p).equals(Likelihood.NO)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T extends Enum<T>> boolean conflict(EnumSet<T> disease, EnumSet<T> patient, boolean excludeConflict) {
        if (!excludeConflict && disease != null && !disease.isEmpty()
                && patient != null && !patient.isEmpty()) {
            for (T p : patient) {
                // todo partof?
                if (!disease.contains(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T extends Enum<T> & HasParts<T>> boolean conflictPartOf(EnumSet<T> disease, EnumSet<T> patient, boolean excludeConflict) {
        if (!excludeConflict && disease != null && !disease.isEmpty()
                && patient != null && !patient.isEmpty()) {
            for (T p : patient) {
                // todo partof?
                boolean match = false;
                for (T d : disease) {
                    match |= p.isPartOf(d) || d.isPartOf(p);
                }
                if (!match) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <E extends Enum<E>> Set<E> setOf(E e) {
        return e == null ? null
                : EnumSet.of(e);
    }

}