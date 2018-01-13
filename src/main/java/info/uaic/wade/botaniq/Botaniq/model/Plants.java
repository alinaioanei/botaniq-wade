package info.uaic.wade.botaniq.Botaniq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@Data
public class Plants extends ResourceSupport{

     @JsonIgnore
     private List<Relations> relations;

     private SubCategory subCategory;

     private long plantId;

     private String name;

     private String description;

     private List<Comments> comments;

}
