package info.uaic.wade.botaniq.Botaniq.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */

@Data
public class Category extends Entity {

    private List<SubCategory> subCategories;


}
