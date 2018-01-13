package info.uaic.wade.botaniq.Botaniq.restControllers;

import info.uaic.wade.botaniq.Botaniq.model.Person;
import info.uaic.wade.botaniq.Botaniq.model.User;
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
@Api(value = "userController", description = "CRUD operations over users entity from botaniq application")
public class UserController {

    private static List<User> users;

    static {
        users = new ArrayList<>();

        Person p1 = new Person();
        p1.setPersonId(1L);
        p1.setFirst_name("Alin");
        p1.setLast_name("Aioanei");
        p1.setGender("Male");

        User u1 = new User();
        u1.setUserId(1);
        u1.setPerson(p1);

        users.add(u1);
    }

    @ApiOperation( value = "getUsersList" , nickname = "getUsersList")
    @RequestMapping( method = RequestMethod.GET , path = "/users" , produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "offset" , value = "Number of returned rows" , required = false , dataType = "Person"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes" ,response = User.class),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public List<User> getAllUsers () {
        return users;
    }

    @ApiOperation( value = "getUserById" , nickname = "getUserById" , response = User.class)
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes" ,response = User.class),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping ( method = RequestMethod.GET , path = "/users/{id}" , produces = "application/json")
    public User getSpecificUser (@PathVariable( value = "id") Long id ) {
        Link link = new Link("http://localhost:8080/users/" + id);
        User user = users.stream().filter(u -> u.getUserId() == id).findFirst().get();
        if(!user.hasLinks()) {
            user.add(link);
        }
        return  user;
    }

    @ApiOperation( value = "addUser" , nickname = "addUser")
    @ApiImplicitParams({
            @ApiImplicitParam ( name = "user" , value = "User's payload" , required = true , dataType = "User"),
    })
    @ApiResponses( value = {
            @ApiResponse(code =  201 , message = "Created"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping ( value = "/users" , method =  RequestMethod.POST )
    public ResponseEntity<Void> addUser (@RequestBody User user , UriComponentsBuilder ucBuilder) {
        HttpHeaders headers = new HttpHeaders();
        users.add(user);
        headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @ApiOperation( value = "deleteUser" , nickname = "deleteUser")
    @ApiResponses( value = {
            @ApiResponse(code =  200 , message = "Succes"),
            @ApiResponse(code = 401, message =  "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @DeleteMapping(value = "/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable (value = "id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        users.remove(users.stream().filter(user -> user.getUserId() == id).findFirst().get());
        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }



}
