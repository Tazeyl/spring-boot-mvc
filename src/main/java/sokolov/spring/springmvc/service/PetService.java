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
        this.idCounter = 0L;
    }

    private Map<Long, PetDto> getPetMap(){
        if (petMap.get() == null){
            petMap = new SoftReference<>(new HashMap<>(), null);
        }
        return petMap.get();
    }

    public PetDto save(PetDto petDto) {
        PetDto newPet = new PetDto();
        newPet.setId(++idCounter);
        newPet.setName(petDto.getName());
        newPet.setUserId(petDto.getUserId());

        userService.addPetToUser(newPet.getUserId(), newPet);

        getPetMap().put(idCounter, newPet);
        return newPet;
    }

    public PetDto saveByUserCreate(Long userId, PetDto petDto) {
        PetDto newPet = new PetDto();
        newPet.setId(++idCounter);
        newPet.setName(petDto.getName());
        newPet.setUserId(userId);

        getPetMap().put(idCounter, newPet);
        return newPet;
    }

    public PetDto update(Long id, PetDto petDto) {
        PetDto updatePet  = getById(id);

        updatePet.setName(petDto.getName());
        if (!Objects.equals(updatePet.getUserId(), petDto.getUserId())){
            Long oldUserId = updatePet.getUserId();
            updatePet.setUserId(petDto.getUserId());
            userService.removePetFromUser(oldUserId, updatePet.getId());
            userService.addPetToUser(petDto.getUserId(), petDto);
        }

        return updatePet;
    }

    public void delete(Long id) {
        PetDto petDto = getById(id);
        userService.removePetFromUser(petDto.getUserId(), id);
        getPetMap().remove(id);
    }

    public PetDto getById(Long id) {

        PetDto petDto = getPetMap().get(id);
        if (petDto == null){
            throw new NoSuchElementException("Not found Pet with id = "+ id);
        }
        return petDto;

    }

    public List<PetDto> getAll() {
        return getPetMap().values().stream().toList();

    }
}
