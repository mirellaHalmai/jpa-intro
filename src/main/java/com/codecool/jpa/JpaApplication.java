package com.codecool.jpa;

import com.codecool.jpa.entity.Address;
import com.codecool.jpa.entity.Location;
import com.codecool.jpa.entity.School;
import com.codecool.jpa.entity.Student;
import com.codecool.jpa.repository.AddressRepository;
import com.codecool.jpa.repository.SchoolRepository;
import com.codecool.jpa.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class JpaApplication {

/*    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;*/

    @Autowired
    private SchoolRepository schoolRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }

    @Bean
    @Profile("production")
    public CommandLineRunner init() {
        return args -> {

            Address address = Address.builder()
                    .country("Hungary")
                    .city("Budapest")
                    .address("Nagymezo utca 44.")
                    .zipCode(1065)
                    .build();

            Address address2 = Address.builder()
                    .country("Hungary")
                    .city("Budapest")
                    .address("Alkotmany utca 20.")
                    .build();

            Student mirai = Student.builder()
                    .name("Mirai")
                    .birthDate(LocalDate.of(1981, 9, 21))
                    .email("mirai@universe.com")
                    .address(address)
                    .phoneNumber("555-6666")
                    .phoneNumber("555-7777")
                    .phoneNumber("555-8888")
                    .build();

            Student touma = Student.builder()
                    .name("Touma")
                    .birthDate(LocalDate.of(1984, 7, 19))
                    .email("touma@universe.com")
                    .address(address2)
                    .phoneNumbers(Arrays.asList("333-7777", "777-3333"))
                    .build();

            School school = School.builder()
                    .location(Location.BUDAPEST)
                    .name("Codecool Budapest")
                    .student(mirai)
                    .student(touma)
                    .build();

            mirai.setSchool(school);
            touma.setSchool(school);

            schoolRepository.save(school);



//            mirai.calculateAge();

//            studentRepository.save(mirai);
        };
    }

}
