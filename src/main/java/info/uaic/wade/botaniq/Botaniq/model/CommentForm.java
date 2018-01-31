package info.uaic.wade.botaniq.Botaniq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Aioanei Alin Ionut on 27.01.2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentForm {

    private String comment;
    private String plant;
}
