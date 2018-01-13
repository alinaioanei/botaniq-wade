package info.uaic.wade.botaniq.Botaniq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {

    private long id;

    private User user;

    private String comment;

    private Entity entity;
}
