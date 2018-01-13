package info.uaic.wade.botaniq.Botaniq.restControllers;

import info.uaic.wade.botaniq.Botaniq.model.Person;
import info.uaic.wade.botaniq.Botaniq.model.Plants;
import info.uaic.wade.botaniq.Botaniq.model.Relations;
import info.uaic.wade.botaniq.Botaniq.model.User;
import io.swagger.annotations.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 29.11.2017.
 */
@RestController
@Api(value = "relationsController", description = "CRUD operations over relations entity from botaniq application")
public class RelationsController {

    private static List<Relations> relations;

    static {

        Plants p1 = new Plants();
        p1.setPlantId(1);
        p1.setName("Rosa bunda");
        p1.setDescription("Rosa bunda is rosa bunda end of story");

        Plants p2 = new Plants();
        p2.setPlantId(1);
        p2.setName("Lalea");
        p2.setDescription("Lalea is Lalea end of story");

        Person p = new Person();
        p.setPersonId(1L);
        p.setFirst_name("Alin");
        p.setLast_name("Aioanei");
        p.setGender("Male");

        User u1 = new User();
        u1.setUserId(1);
        u1.setPerson(p);

        Relations relations1 = new Relations();
        relations1.setFrom(p1);
        relations1.setTo(p2);
        relations1.setRelationId(1);
        relations1.setDescription("This is a relation between rosa bunda and lalea");
        relations1.setUser(u1);

        relations = new ArrayList<>();
        relations.add(relations1);
    }

    @ApiOperation( value = "getAllRelations" , nickname = "getAllRelations")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "offset" , value = "Number of returned rows" , required = false , dataType = "Long"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @GetMapping(value = "/relations")
    public List<Relations> getAllRelations() {
        return relations;
    }

    @ApiOperation( value = "getSpecificRelations" , nickname = "getSpecificRelations", response = Relations.class)
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @GetMapping(value = "/relations/{id}", produces = "application/json")
    public Relations getRelationById(@PathVariable(value = "id") Long id) {
        Relations rel = relations.stream().filter(p -> p.getRelationId() == id).findFirst().get();
        Link link = new Link("http://localhost:8080/persons/" + id);
        if(!rel.hasLinks()) {
            rel.add(link);
        }
        return rel;
    }

    @ApiOperation( value = "addRelation" , nickname = "addRelation")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "person" , value = "Relation's payload" , required = true , dataType = "Relations"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  201 , message = "Created"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @PostMapping(value = "/relations")
    public ResponseEntity<Void> addRelations(@RequestBody Relations relation, UriComponentsBuilder uriComponentsBuilder) {
        HttpHeaders headers = new HttpHeaders();
        relations.add(relation);
        headers.setLocation(uriComponentsBuilder.path("/relations/{id}").buildAndExpand(relation.getRelationId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @ApiOperation( value = "deleteRelation" , nickname = "deleteRelation")
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @DeleteMapping(value = "/relations/delete/{id}")
    public ResponseEntity<Void> deleteRelations(@PathVariable (value = "id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        relations.remove(relations.stream().filter(relation -> relation.getRelationId() == id).findFirst().get());
        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }

}
