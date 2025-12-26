package sokolov.spring.springmvc.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sokolov.spring.springmvc.model.PetDto;

import java.lang.ref.SoftReference;
import java.util.*;

@Service
public class PetService {

    private final UserService userService;

    SoftReference<Map<Long, PetDto>> petMap;
    Long idCounter;

    public PetService(@Lazy UserService userService) {
        this.userService = userService;
        this.petMap = new SoftReference<>(new HashMap<>(), null);
        this.idCounter = 0L;
    }

    public PetDto save(PetDto petDto) {
        PetDto newPet = new PetDto();
        newPet.setId(++idCounter);
        newPet.setName(petDto.getName());
        newPet.setUserId(petDto.getUserId());

        userService.addPetToUser(newPet.getUserId(), newPet);

        Objects.requireNonNull(petMap.get()).put(idCounter, newPet);
        return newPet;
    }

    public PetDto saveByUserCreate(Long userId, PetDto petDto) {
        PetDto newPet = new PetDto();
        newPet.setId(++idCounter);
        newPet.setName(petDto.getName());
        newPet.setUserId(userId);

        Objects.requireNonNull(petMap.get()).put(idCounter, newPet);
        return newPet;
    }

    public PetDto update(Long id, PetDto petDto) {
        PetDto updatePet  = getById(id);

        Long oldUserId = updatePet.getUserId();

        updatePet.setName(petDto.getName());
        updatePet.setUserId(petDto.getUserId());

        if (!Objects.equals(oldUserId, updatePet.getUserId())){
            userService.removePetFromUser(oldUserId, updatePet.getId());
            userService.addPetToUser(petDto.getUserId(), petDto);
        }

        return updatePet;
    }

    public void delete(Long id) {
        PetDto petDto = getById(id);
        userService.removePetFromUser(petDto.getUserId(), id);
        Objects.requireNonNull(petMap.get()).remove(id);
    }

    public PetDto getById(Long id) {

        PetDto petDto = Objects.requireNonNull(petMap.get()).get(id);
        if (petDto == null){
            throw new NoSuchElementException("Not found Pet with id = "+ id);
        }
        return petDto;

    }

    public List<PetDto> getAll() {
        return Objects.requireNonNull(petMap.get()).values().stream().toList();

    }
}
