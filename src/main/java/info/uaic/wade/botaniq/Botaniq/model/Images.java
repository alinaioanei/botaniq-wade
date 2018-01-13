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
public class Images {

    private User user;

    private long id;

    private byte[] image;

    private String description;

    private String url;
}
