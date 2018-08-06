package <%= config.packages.serviceMapper %>;

import <%= config.packages.score %>.Score;
import <%= config.packages.restModel %>.ScoreModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for the entity Score and its Model called ScoreModel.
 */
@Mapper
public interface ScoreMapper {

	ScoreMapper INSTANCE = Mappers.getMapper(ScoreMapper.class);

	ScoreModel entityToModel(Score entity);

	List<ScoreModel> entityToModel(List<Score> entity);
}