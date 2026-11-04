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
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "이미 사용중인 이메일입니다"
            );
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());


        User user = new User(
                dto.getEmail(),
                encodedPassword,
                dto.getName()
        );

        userRepository.save(user);
    }



}
