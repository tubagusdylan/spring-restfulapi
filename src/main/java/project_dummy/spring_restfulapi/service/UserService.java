package project_dummy.spring_restfulapi.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.model.RegisterUserRequest;
import project_dummy.spring_restfulapi.model.UpdateUserRequest;
import project_dummy.spring_restfulapi.model.UserResponse;
import project_dummy.spring_restfulapi.repository.UserRepository;
import project_dummy.spring_restfulapi.security.BCrypt;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);

    }

    public UserResponse getUser(User user){
        return UserResponse.builder().username(user.getUsername()).name(user.getName()).build();
    }

    @Transactional
    public UserResponse updateUser(User user, UpdateUserRequest request){
        validationService.validate(request);
        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.builder().name(user.getName()).username(user.getUsername()).build();
    }

}
