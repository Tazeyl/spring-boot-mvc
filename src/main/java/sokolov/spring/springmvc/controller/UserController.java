package sokolov.spring.springmvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sokolov.spring.springmvc.model.UserDto;
import sokolov.spring.springmvc.service.UserService;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<UserDto>> getAlLUsers(){
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto
    ){

        UserDto createdUser = userService.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto){
        UserDto updatedUser =  userService.update(id, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);

    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ){
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path =  "/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        UserDto userDto = userService.getById(id);
        return ResponseEntity.ok(userDto);
    }


}
