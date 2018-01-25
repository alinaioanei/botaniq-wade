package info.uaic.wade.botaniq.Botaniq.model;


import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */

@Data
public class Person extends ResourceSupport{

    private Long personId;

    private String first_name;

    private String last_name;

    private String gender;

    private User user;
}
