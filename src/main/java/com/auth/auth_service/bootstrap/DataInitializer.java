package com.auth.auth_service.bootstrap;

import com.auth.auth_service.entity.AuthUser;
import com.auth.auth_service.enums.UserType;
import com.auth.auth_service.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ✅ Run ONLY if table is empty
        if (authUserRepository.count() > 0) {
            return;
        }

        // 🔐 Create Super Admin
        AuthUser superAdmin = new AuthUser();
        superAdmin.setUsername("superadmin");
        superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
        superAdmin.setUserType(UserType.SUPER_ADMIN);
        superAdmin.setIsActive(true);

        authUserRepository.save(superAdmin);

        System.out.println("✅ Initial users created (Super Admin & Admin)");
    }
}
