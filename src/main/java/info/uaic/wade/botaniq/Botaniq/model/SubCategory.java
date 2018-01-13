package info.uaic.wade.botaniq.Botaniq.model;

import lombok.Data;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
public class SubCategory extends  Entity {

    private List<Plants> plants;
}

