package info.uaic.wade.botaniq.Botaniq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Garden {

    private long id;

    private String name;

    private String description;

    private List<Category> categories;


}
