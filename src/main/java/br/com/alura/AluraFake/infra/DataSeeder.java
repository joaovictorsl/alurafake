package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile)) return;

        if (userRepository.count() == 0) {
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
            User joao = new User("João", "joao@alura.com.br", Role.INSTRUCTOR, "password");
            userRepository.saveAll(Arrays.asList(caio, paulo, joao));
            courseRepository.save(new Course("Java", "Aprenda Java com Alura", paulo));
        }
    }
}