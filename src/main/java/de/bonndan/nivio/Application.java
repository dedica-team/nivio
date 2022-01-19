package de.bonndan.nivio;

import de.bonndan.nivio.appuser.AppUser;
import de.bonndan.nivio.appuser.AppUserRole;
import de.bonndan.nivio.appuser.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(AppUserRepository userRepository) {
//        return args -> {
//            AppUser mary = new AppUser("123","Mary", "Mary88", "mary@email.com", "avatarUrl", AppUserRole.USER);
//            userRepository.save(mary);
//        };
//    }
}
