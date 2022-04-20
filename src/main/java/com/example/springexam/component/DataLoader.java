package com.example.springexam.component;

import com.example.springexam.entity.Role;
import com.example.springexam.entity.User;
import com.example.springexam.entity.enums.PermissionEnum;
import com.example.springexam.entity.enums.RoleEnum;
import com.example.springexam.repository.RoleRepository;
import com.example.springexam.repository.UsersRepository;
import com.example.springexam.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig securityConfig;

    @Value("${spring.sql.init.mode}")
    String mode;

    @Override
    public void run(String... args) throws Exception {
        if (mode.equals("always")) {
            PermissionEnum[] values = PermissionEnum.values();

            Role userRole = roleRepository.save(new Role(RoleEnum.ROLE_USER, new ArrayList<>(Arrays.asList(PermissionEnum.READ_ALL_BOOK, PermissionEnum.READ_ONE_BOOK))));
            Role adminUserRole = roleRepository.save(new Role(RoleEnum.ROLE_ADMINUSER, new ArrayList<>(Arrays.asList(PermissionEnum.READ_ALL_BOOK,PermissionEnum.READ_ONE_BOOK,PermissionEnum.CREATE_BOOK,PermissionEnum.UPDATE_BOOK,PermissionEnum.UPLOAD_BOOK))));
            Role superAdminRole = roleRepository.save(new Role(RoleEnum.ROLE_SUPERADMIN,Arrays.asList(values)));

            Set<Role> adminUser = new LinkedHashSet<>();
            adminUser.add(adminUserRole);

            Set<Role> user = new LinkedHashSet<>();
            user.add(userRole);

            Set<Role> superAdmin = new LinkedHashSet<>();
            superAdmin.add(superAdminRole);

            userRepository.save(new User("Admin", securityConfig.passwordEncoder().encode("123"),adminUser));
            userRepository.save(new User("User", securityConfig.passwordEncoder().encode("123"), user));
            userRepository.save(new User("SuperAdmin",securityConfig.passwordEncoder().encode("123"),superAdmin));
        }
    }
}
