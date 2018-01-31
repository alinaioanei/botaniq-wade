package info.uaic.wade.botaniq.Botaniq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 31.01.2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private List<CommentForm> commentForms;
    private List<String> userComments;
    private List<String> userImages;
    private List<String> userRelation;
}
