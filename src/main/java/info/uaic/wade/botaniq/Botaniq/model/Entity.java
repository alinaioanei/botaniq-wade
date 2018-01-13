package info.uaic.wade.botaniq.Botaniq.model;

import lombok.Data;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
public class Entity {

    private long id;

    private String name;

    private String description;

    private List<Comments> comments;
}
