package info.uaic.wade.botaniq.Botaniq.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 26.01.2018.
 */
@Data
@NoArgsConstructor
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
    private String name;
    private String plant;
    private List<String> userComments;
    private List<String> userImages;
    private List<String> userRelation;

    public DbpediaWrapper(int id, String info, String classs, String division, String family, String order, String comment, String link, String image, String name, String plant) {
        this.id = id;
        this.info = info;
        this.classs = classs;
        this.division = division;
        this.family = family;
        this.order = order;
        this.comment = comment;
        this.link = link;
        this.image = image;
        this.name = name;
        this.plant = plant;
    }
}
