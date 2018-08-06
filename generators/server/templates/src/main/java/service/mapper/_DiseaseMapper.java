package <%= config.packages.serviceMapper %>;

import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.restModel %>.DiseaseModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for the entity DiseaseProfile and its Model called DiseaseModel.
 */
@Mapper
public interface DiseaseMapper {

	DiseaseMapper INSTANCE = Mappers.getMapper(DiseaseMapper.class);

	DiseaseProfile modelToEntity(DiseaseModel model);

	List<DiseaseProfile> modelToEntity(List<DiseaseModel> model);

	DiseaseModel entityToModel(DiseaseProfile model);

	List<DiseaseModel> entityToModel(List<DiseaseProfile> model);

}