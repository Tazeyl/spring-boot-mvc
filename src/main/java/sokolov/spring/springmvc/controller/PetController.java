package sokolov.spring.springmvc.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sokolov.spring.springmvc.model.PetDto;
import sokolov.spring.springmvc.service.PetService;

import java.util.List;


@RestController
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping(path = "/pets")
    public ResponseEntity<List<PetDto>> getAllPets(){
        return ResponseEntity.ok(petService.getAll());
    }

    @PostMapping (path = "/pets")
    public ResponseEntity<PetDto> createPet(
            @Valid @RequestBody PetDto petDto
    ){

        PetDto createdUser = petService.save(petDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(path = "/pets/{id}")
    public ResponseEntity<PetDto> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetDto petDto){
        PetDto updatedUser =  petService.update(id, petDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);

    }

    @DeleteMapping(path = "/pets/{id}")
    public ResponseEntity<Void> deletePet(
            @PathVariable Long id
    ){
        petService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path =  "/pets/{id}")
    public ResponseEntity<PetDto> getPetsById(@PathVariable Long id){
        PetDto petDto = petService.getById(id);
        return ResponseEntity.ok(petDto);
    }
}
