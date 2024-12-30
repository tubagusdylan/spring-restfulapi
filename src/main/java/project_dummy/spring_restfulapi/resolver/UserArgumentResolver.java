package project_dummy.spring_restfulapi.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.repository.UserRepository;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public boolean supportsParameter(MethodParameter parameter){
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvContinaer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){       
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        // cek di header ada tokennya atau tidak
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // cek di database, user sudah login atau belum, dengan melihat keberadaan token
        User user = userRepository.findFirstByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (user.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Expired");
        }

        return user;
    }
}
