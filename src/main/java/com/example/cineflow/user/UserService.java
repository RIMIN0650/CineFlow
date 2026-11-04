package com.example.cineflow.user;

import com.example.cineflow.user.model.User;
import com.example.cineflow.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserDto.SignupReq dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException(
                    "이미 사용중인 이메일입니다"
            );
        }

        User user = User.createUser(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name()
        );

        userRepository.save(user);
    }


    public void signupAdmin(UserDto.AdminSignupReq dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다");
        }

        User user = User.createAdmin(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name()
        );

        userRepository.save(user);

    }

}
