package <%= config.packages.serviceMapper %>;

import <%= config.packages.profile %>.PatientProfile;
import <%= config.packages.restModel %>.PatientModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for the entity PatientProfile and its Model called PatientModel.
 */
@Mapper
public interface PatientMapper {

	PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

	PatientProfile modelToEntity(PatientModel model);
}