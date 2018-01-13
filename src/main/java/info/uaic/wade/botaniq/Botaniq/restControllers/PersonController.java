package info.uaic.wade.botaniq.Botaniq.restControllers;

import info.uaic.wade.botaniq.Botaniq.model.Person;
import io.swagger.annotations.*;
import org.springframework.hateoas.Link;
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
@Api(value = "personController", description = "CRUD operations over person entity from botaniq application")
public class PersonController {

    private static List<Person> persons;

    static {
        persons = new ArrayList<>();
        Person p1 = new Person();
        p1.setPersonId(1L);
        p1.setFirst_name("Alin");
        p1.setLast_name("Aioanei");
        p1.setGender("Male");

        Person p2 = new Person();
        p2.setPersonId(2L);
        p2.setFirst_name("Sandra");
        p2.setLast_name("Amarandei");
        p2.setGender("Female");

        Person p3 = new Person();
        p3.setPersonId(3L);
        p3.setFirst_name("Ramona");
        p3.setLast_name("Turcu");
        p3.setGender("Female");

        persons.add(p1);
        persons.add(p2);
        persons.add(p3);
    }

    @ApiOperation( value = "getAllPersons" , nickname = "getAllPersons")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "offset" , value = "Number of returned rows" , required = false , dataType = "Integer"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @GetMapping(value = "/persons")
    public List<Person> getAllPersons(@RequestParam (value = "offset", required = false) Integer offset) {
        if (offset != null) {
            return persons.subList(0, offset);
        }
        return persons;
    }

    @ApiOperation( value = "getSpecificPerson" , nickname = "getSpecificPerson", response = Person.class)
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @GetMapping(value = "/persons/{id}", produces = "application/json")
    public Person getPersonById(@PathVariable(value = "id") Long id) {
        Person person = persons.stream().filter(p -> p.getPersonId() == id).findFirst().get();
        Link link = new Link("http://localhost:8080/persons/" + id);
        if(!person.hasLinks()) {
            person.add(link);
        }
        return person;
    }

    @ApiOperation( value = "addPerson" , nickname = "addPerson")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "person" , value = "Person's payload" , required = true , dataType = "Person"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  201 , message = "Created"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @PostMapping(value = "/persons")
    public ResponseEntity<Void> addPerson(@RequestBody Person person, UriComponentsBuilder uriComponentsBuilder) {
        HttpHeaders headers = new HttpHeaders();
        persons.add(person);
        headers.setLocation(uriComponentsBuilder.path("/persons/{id}").buildAndExpand(person.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @ApiOperation( value = "deletePerson" , nickname = "deletePerson")
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @DeleteMapping(value = "/persons/delete/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable (value = "id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        persons.remove(persons.stream().filter(person -> person.getPersonId() == id).findFirst().get());
        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }

}
