package info.uaic.wade.botaniq.Botaniq.model;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
public class Relations extends ResourceSupport{

      private Plants from;

      private Plants to;

      private List<Comments> comments;

      private long relationId;

      private String name;

      private String description;

      private User user;
}
