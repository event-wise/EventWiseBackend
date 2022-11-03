package com.event.eventwiseap;

import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class EventwiseApiApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Set<Integer> roles = new HashSet<>();
        roles.add(1);
        roles.add(2);
//        User user = User.builder()
//                        .username('balik')
//                        .email('balik18@itu.edu.tr')
//                        .displayedName('balon')
//                        .password(encoder.encode(('bal_admin')))
//                        .location('Istanbul')
//                        .roles(roles)
//                        .build();

    }

}
