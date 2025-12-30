package sokolov.spring.springmvc.controller;

import jakarta.validation.Valid;
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
            @RequestBody @Valid UserDto userDto
    ){

        UserDto createdUser = userService.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.update(id, userDto));

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
        return ResponseEntity.ok(userService.getById(id));
    }


}
