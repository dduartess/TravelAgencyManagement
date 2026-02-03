package _dduartess.travelaencymanagement.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import _dduartess.travelaencymanagement.entities.user.User;
import _dduartess.travelaencymanagement.entities.user.UserRole;
import _dduartess.travelaencymanagement.repositories.UserRepository;

@Configuration
public class AdminSeeder {

    @Value("${app.admin.login:admin}")
    private String adminLogin;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            var existing = userRepository.findByLogin(adminLogin);
            if (existing == null) {
                var encrypted = encoder.encode(adminPassword);
                var admin = new User(adminLogin, encrypted, UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("✅ Admin criado: " + adminLogin);
            }
        };
    }
}