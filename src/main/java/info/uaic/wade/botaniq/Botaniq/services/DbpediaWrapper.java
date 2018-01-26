package info.uaic.wade.botaniq.Botaniq.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Aioanei Alin Ionut on 26.01.2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DbpediaWrapper {

    private int id;
    private String info;
    private String classs;
    private String division;
    private String family;
    private String order;
    private String comment;
    private String link;
    private String image;

}
