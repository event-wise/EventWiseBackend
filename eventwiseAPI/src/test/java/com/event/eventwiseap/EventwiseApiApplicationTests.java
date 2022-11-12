package com.event.eventwiseap;

import com.event.eventwiseap.model.*;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private EventService eventService;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void contextLoads() {
        boolean someTestBool = false;
        Long someTestLong = -1L;
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
                .acceptedEvents(new HashSet<>())
                .build();

        User user = User.builder()
                .username("ordinary")
                .email("ordinary@itu.edu.tr")
                .displayedName("ordinary")
                .location("ISTANBUL")
                .password(encoder.encode("ordinary"))
                .roles(user_roles)
                .groups(new HashSet<>())
                .acceptedEvents(new HashSet<>())
                .build();

        admin = userService.create(admin); // Create user
        user = userService.create(user); // Create user

        Long adminId = admin.getId();
        Long userId = user.getId();

        // 2) Create Group
        Group adminGroup = Group.builder()
                .groupName("Admin's group")
                .owner(userService.getById(adminId)) // Read user
                .groupMembers(new HashSet<>())
                .build();

        Group userGroup = Group.builder()
                .groupName("User's group")
                .owner(userService.getById(userId)) // Read user
                .groupMembers(new HashSet<>())
                .build();

        adminGroup = groupService.create(adminGroup); // Create Group
        userGroup = groupService.create(userGroup); // Create Group
        // 2.1) Admin group and members
        boolean added = adminGroup.addMember(admin);
        System.out.println(added);
        added = adminGroup.addMember(user);
        System.out.println(added);
        adminGroup = groupService.save(adminGroup);

        // 2.2) User group and members
        added = userGroup.addMember(user);
        System.out.println(added);
        added = userGroup.addMember(admin);
        System.out.println(added);
        userGroup = groupService.save(userGroup);

        System.out.println("SUCCESS");
//         2.3) Delete group (relation member-group)
//        Long deleted = groupService.delete(userGroup.getId());
//        System.out.println(deleted);
//        deleted = groupService.delete(adminGroup.getId());
//        System.out.println(deleted);

//        // 2.4) Delete members (relation member-group)
//        Long deleted = userService.delete(user.getId());
//        System.out.println(deleted);
//        deleted = userService.delete(admin.getId());
//        System.out.println(deleted);
        // 4) Create Event
        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        String formatDateTime = now.format(format);

        Event adminEvent = Event.builder()
                .dateTime(now)
                .name("Admin's event")
                .description("Admin is testing the relations")
                .location("ISTANBUL")
                .type("TEST")
                .organizer(admin)
                .group(adminGroup)
                .acceptedMembers(new HashSet<>())
                .build();

        adminEvent = eventService.save(adminEvent); // CRUCIAL:BEFORE ADDING ANY MEMBER, SAVE THE GROUP FOR PERSISTENCE!


        someTestBool = adminEvent.acceptedBy(admin);
        System.out.println(someTestBool);
        someTestBool = adminEvent.acceptedBy(user);
        System.out.println(someTestBool);
        adminEvent = eventService.save(adminEvent);
        System.out.println(adminEvent.getId());

//        eventService.delete(adminEvent.getId());

        // 5) Further deletion
        // 5.1) Delete event organizer
//        someTestLong = userService.delete(admin.getId());
//        System.out.println(someTestLong);
        // 5.2) Delete group
        groupService.delete(adminGroup.getId());

    }

}
