package info.uaic.wade.botaniq.Botaniq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
public class User extends ResourceSupport{

    private long userId;

    private Person person;

    private List<Images> images;

    private List<Comments> comments;

    private List<Relations> relations;


}
