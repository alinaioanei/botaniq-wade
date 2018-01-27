package info.uaic.wade.botaniq.Botaniq.restControllers;

import info.uaic.wade.botaniq.Botaniq.model.Plants;
import info.uaic.wade.botaniq.Botaniq.model.User;
import info.uaic.wade.botaniq.Botaniq.services.DbpediaWrapper;
import info.uaic.wade.botaniq.Botaniq.services.SparqlUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aioanei Alin Ionut on 28.11.2017.
 */
@RestController
@Api(value = "plantsController", description = "CRUD operations over plants entity from botaniq application")
public class PlantsController {

    @Autowired
    private SparqlUtil sparqlUtil;

    private static List<Plants> plants;

    static {
        plants = new ArrayList<>();
        Plants p1 = new Plants();
        p1.setPlantId(1);
        p1.setName("Rosa bunda");
        p1.setDescription("Rosa bunda is rosa bunda end of story");

        plants.add(p1);
    }


    @GetMapping("/fetchDbpediaData")
    public List<DbpediaWrapper> fetchDbpediaData(){
        return sparqlUtil.fetchDbpediaData();
    }

    @GetMapping("/findOneFromDbpedia/{plant}")
    public DbpediaWrapper findOneFromDbpedia(@PathVariable("plant") String plant){
        return sparqlUtil.findOneFromDbpedia(plant);
    }

    @GetMapping("/sparql")
    public String sparqlGetEndpoint(@RequestParam("query") String query, @RequestParam("responseType") String responseType){
         return sparqlUtil.getRequestToStardog(query, responseType);
    }

    @PostMapping("/sparql")
    public String sparqlPostEndpoint(@RequestParam("query") String query, @RequestParam("responseType") String responseType){
        return sparqlUtil.postRequestToStardog(query, responseType);
    }

    @ApiOperation( value = "getPlantsList" , nickname = "getPlantsList")
    @RequestMapping( method = RequestMethod.GET , path = "/plants" , produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "offset" , value = "Number of returned rows" , required = false , dataType = "Plants"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes" ,response = User.class),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public List<Plants> getAllPlants () {
        return plants;
    }

    @ApiOperation( value = "getPlantsById" , nickname = "getPlantsById", response = Plants.class )
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes" ,response = Plants.class),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping ( method = RequestMethod.GET , path = "/plants/{id}" , produces = "application/json")
    public HttpEntity<Plants> getPlantById (@PathVariable( value = "id") Long id ) {
        Link link = new Link("http://localhost:8080/plants/" + id);
        Plants plant =  plants.stream().filter(p -> p.getPlantId() == id).findFirst().get();
        if(!plant.hasLinks()) {
            plant.add(link);
        }
        return new ResponseEntity<Plants>(plant, HttpStatus.OK);
    }

    @ApiOperation( value = "addPlant" , nickname = "addUser")
    @ApiImplicitParams({
            @ApiImplicitParam ( name = "plant" , value = "Plant's payload" , required = true , dataType = "Plants"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  201 , message = "Created"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping ( value = "/plants" , method =  RequestMethod.POST )
    public ResponseEntity<Void> addPlant (@RequestBody Plants plant , UriComponentsBuilder ucBuilder) {
        HttpHeaders headers = new HttpHeaders();
        plants.add(plant);
        headers.setLocation(ucBuilder.path("/plants/{id}").buildAndExpand(plant.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @ApiOperation( value = "deletePlant" , nickname = "deletePlant")
    @ApiImplicitParams({
            @ApiImplicitParam ( name = "id" , value = "Plant's id" , required = true , dataType = "long"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @DeleteMapping(value = "/plants/delete/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable (value = "id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        plants.remove(plants.stream().filter(plant -> plant.getPlantId() == id).findFirst().get());
        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }


}
