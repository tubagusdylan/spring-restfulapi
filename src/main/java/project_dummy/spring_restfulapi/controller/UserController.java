package project_dummy.spring_restfulapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.model.RegisterUserRequest;
import project_dummy.spring_restfulapi.model.UpdateUserRequest;
import project_dummy.spring_restfulapi.model.UserResponse;
import project_dummy.spring_restfulapi.model.WebResponse;
import project_dummy.spring_restfulapi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
        path = "/api/users", 
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    // ini pakai argument resolver
    // dimana setiap controller butuh data user, otomatis akan minta dari argumen resolver
    @GetMapping(
        path = "/api/users/current",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUser(User user){
        UserResponse userResponse = userService.getUser(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
        path = "/api/users/current",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> updateUser(User user, @RequestBody UpdateUserRequest request){
        UserResponse userResponse = userService.updateUser(user, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
