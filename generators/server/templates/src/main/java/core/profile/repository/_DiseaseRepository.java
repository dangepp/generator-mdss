package <%= config.packages.profileRepo %>;

import <%= config.packages.profile %>.DiseaseProfile;

import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for disease profiles.
 */
@Repository
public interface DiseaseRepository extends MongoRepository<DiseaseProfile, String> {
}