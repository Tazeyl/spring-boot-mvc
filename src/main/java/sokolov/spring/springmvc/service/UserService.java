package sokolov.spring.springmvc.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sokolov.spring.springmvc.model.PetDto;
import sokolov.spring.springmvc.model.UserDto;

import java.lang.ref.SoftReference;
import java.util.*;

@Service
public class UserService {

    private final PetService petService;

    SoftReference<Map<Long, UserDto>> userMap;
    Long idCounter;

    public UserService(@Lazy PetService petService) {
        this.petService = petService;
        this.userMap = new SoftReference<>(new HashMap<>(), null);
        this.idCounter = 0L;
    }

    public UserDto save(UserDto userDto) {
        UserDto newUser = new UserDto();
        newUser.setId(++idCounter);
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setAge(userDto.getAge());

        userDto.getPets().forEach(petDto -> newUser.getPets().add(petService.saveByUserCreate(newUser.getId(), petDto)));

        Objects.requireNonNull(userMap.get()).put(idCounter, newUser);
        return newUser;
    }

    public UserDto update(Long id, UserDto userDto) {
        UserDto updateUser = getById(id);

        updateUser.setName(userDto.getName());
        updateUser.setEmail(userDto.getEmail());
        updateUser.setAge(userDto.getAge());

        List<PetDto> oldPets= updateUser.getPets();
        //Удаляем привязку старых
        updateUser.setPets(new ArrayList<>());

        userDto.getPets().forEach(petDto ->
        {
            if (petDto.getId() != null) {
                updateUser.getPets().add(petService.update(petDto.getId(), petDto));
            } else {
                updateUser.getPets().add(petService.saveByUserCreate(updateUser.getId(), petDto));
            }
        });

        List<Long> forRemove = oldPets.stream().map(PetDto::getId).filter(aLong -> userDto.getPets().stream().map(PetDto::getId).noneMatch(aLong1 -> Objects.equals(aLong,aLong1))).toList();
        forRemove.forEach(petService::delete);


        return updateUser;
    }

    public void delete(Long id) {
        getById(id);
        Objects.requireNonNull(userMap.get()).remove(id);
    }

    public UserDto getById(Long id) {

        UserDto userDto = Objects.requireNonNull(userMap.get()).get(id);
        if (userDto == null) {
            throw new NoSuchElementException("Not found User with id = " + id);
        }
        return userDto;

    }

    public void addPetToUser(Long id, PetDto petDto) {
        UserDto userDto = getById(id);
        userDto.getPets().add(petDto);
    }

    public void removePetFromUser(Long userId, Long petId) {
        UserDto userDto = getById(userId);
        List<PetDto> petDtoList = userDto.getPets();
        PetDto petDto = petDtoList.stream().filter(petDto1 -> Objects.equals(petDto1.getId(), petId)).findFirst().orElseThrow(() -> new NoSuchElementException("Not found Pet with id = "));
        petDtoList.remove(petDto);
    }

    public List<UserDto> getAll() {
        return new ArrayList<>(Objects.requireNonNull(userMap.get()).values());
    }
}
