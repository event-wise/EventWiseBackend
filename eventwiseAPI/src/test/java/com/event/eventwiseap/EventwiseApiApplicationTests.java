package com.event.eventwiseap;

import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class EventwiseApiApplicationTests {
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void contextLoads() {
        // 0) Get roles
        Role admin_role = roleService.findByName(RoleType.ROLE_ADMIN);
        Role user_role = roleService.findByName(RoleType.ROLE_USER);

        Set<Role> admin_roles = new HashSet<>();
        admin_roles.add(admin_role);
        admin_roles.add((user_role));

        Set<Role> user_roles = new HashSet<>();
        user_roles.add(user_role);

        // 1) Create user/admin
        User admin = User.builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .password(encoder.encode("bal_admin"))
                .roles(admin_roles)
                .groups(new HashSet<>())
                .ownedGroups(new HashSet<>())
                .build();

        User user = User.builder()
                .username("ordinary")
                .email("ordinary@itu.edu.tr")
                .displayedName("ordinary")
                .location("ISTANBUL")
                .password(encoder.encode("ordinary"))
                .roles(user_roles)
                .groups(new HashSet<>())
                .ownedGroups(new HashSet<>())
                .build();

        admin = userService.create(admin); // Create user
        user = userService.create(user); // Create user

        Long adminId = admin.getId();
        Long userId = user.getId();

        // 2) Create Group
        Group adminGroup = Group.builder()
                .groupName("Admin's group")
                .owner(userService.getById(adminId)) // Read user
                .events(new HashSet<>())
                .build();

        Group userGroup = Group.builder()
                .groupName("User's group")
                .owner(userService.getById(userId)) // Read user
                .events(new HashSet<>())
                .build();

        adminGroup = groupService.create(adminGroup); // Create Group
        userGroup = groupService.create(userGroup); // Create Group
        // 2.1) Groups and creators
        admin.addGroup(adminGroup);
        user.addGroup(userGroup);

        // 2.2) Groups and members
        admin.addGroup(userGroup);
        user.addGroup(adminGroup);

        admin = userService.update(admin);
        user = userService.update(user);

        Long adminGroupId = adminGroup.getId();
        Long userGroupId = userGroup.getId();

        // 2.3) Delete members
        Long deleted = userService.delete(user.getId());
        deleted = userService.delete(admin.getId());
        System.out.println(deleted);
        // 4) Create Event


    }

}
