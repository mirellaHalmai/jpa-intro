package com.codecool.jpa.repository;

import com.codecool.jpa.entity.Address;
import com.codecool.jpa.entity.Location;
import com.codecool.jpa.entity.School;
import com.codecool.jpa.entity.Student;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.cert.LDAPCertStoreParameters;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class AllRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    public void saveOneSimple() {
        Student alpaca = Student.builder()
                .name("Alpaca")
                .email("alpaca@universe.com")
                .birthDate(LocalDate.of(1997, 7, 4))
                .build();
        studentRepository.save(alpaca);

        List<Student> studentList = studentRepository.findAll();

        assertThat(studentList).hasSize(1);
        assertThat(studentList).contains(alpaca);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveUniqueFieldTwice() {
        Student guanaco = Student.builder()
                .email("guanaco@universe.com")
                .name("Guanaco")
                .build();

        studentRepository.save(guanaco);

        Student llama = Student.builder()
                .email("guanaco@universe.com")
                .name("Llama")
                .build();

        studentRepository.saveAndFlush(llama);

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void emailShouldNotBeNull() {

        Student vicugna = Student.builder()
                .name("Vicugna")
                .build();

        studentRepository.save(vicugna);

    }

    @Test
    public void transientIsNotSaved() {

        Student alpaca = Student.builder()
                .name("Alpaca")
                .birthDate(LocalDate.of(1965, 8, 23))
                .email("alpaca@universe.com")
                .build();

        alpaca.calculateAge();
        assertThat(alpaca.getAge()).isGreaterThanOrEqualTo(53);

        studentRepository.save(alpaca);
        entityManager.clear();

        List<Student> studentList = studentRepository.findAll();

        assertThat(studentList).allMatch(student -> student.getAge() == 0L);

    }

    @Test
    public void addressIsPersistedWithStudent() {

        Address address = Address.builder()
                .country("Hungary")
                .city("Budapest")
                .address("Nagymezo utca 44")
                .zipCode(1065)
                .build();

        Student llama = Student.builder()
                .name("Llama")
                .email("llama@universe.com")
                .birthDate(LocalDate.of(1989, 4, 9))
                .address(address)
                .build();

        studentRepository.save(llama);

        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList)
                .hasSize(1)
                .allMatch(address1 -> address1.getId() > 0)
                .contains(address);

    }

    @Test
    public void studentsArePersistedAndDeletedWithNewSchool() {
        Set<Student> students = IntStream.range(1, 10)
                .boxed()
                .map(integer -> Student.builder().email("student" + integer + "@universe.com").build())
                .collect(Collectors.toSet());

        School school = School.builder()
                .location(Location.BUDAPEST)
                .students(students)
                .build();

        schoolRepository.save(school);

        assertThat(studentRepository.findAll())
                .hasSize(9)
                .anyMatch(student -> student.getEmail().equals("student9@universe.com"));

        schoolRepository.deleteAll();

        assertThat(studentRepository.findAll())
                .hasSize(0);

    }

    @Test
    public void findByNameStartingWithOrBirthdateBetween() {

        Student marcusAurelius = Student.builder()
                .name("Marcus Aurelius")
                .email("marcus.aurelius@universe.com")
                .birthDate(LocalDate.of(1987, 7, 7))
                .build();

        Student boadicea = Student.builder()
                .name("Boadicea")
                .email("boadicea@universe.com")
                .birthDate(LocalDate.of(1981, 7, 4))
                .build();

        Student atalanta = Student.builder()
                .name("Atalanta")
                .email("atalanta@universe.com")
                .birthDate(LocalDate.of(1993, 7, 19))
                .build();

        Student giordanoBruno = Student.builder()
                .name("Giordano Bruno")
                .email("giordano.bruno@universe.com")
                .birthDate(LocalDate.of(1987, 12, 31))
                .build();

        Student hermesTrismegistos = Student.builder()
                .name("Hermes Trismegistos")
                .email("hermes.trismegistos.com")
                .birthDate(LocalDate.of(1979, 4, 9))
                .build();

        studentRepository.saveAll(Lists.newArrayList(marcusAurelius, boadicea, atalanta, giordanoBruno, hermesTrismegistos));
        List<Student> filteredStudents = studentRepository.findByNameStartingWithOrBirthDateBetween(
                "H",
                LocalDate.of(1981, 1, 1),
                LocalDate.of(1987, 12, 31)
        );

        assertThat(filteredStudents).containsExactlyInAnyOrder(marcusAurelius, boadicea, giordanoBruno, hermesTrismegistos);

    }

    @Test
    public void findAllCountry() {

        Student first = Student.builder()
                .email("first@universe.com")
                .address(Address.builder().country("Hungary").build())
                .build();

        Student second = Student.builder()
                .email("second@universe.com")
                .address(Address.builder().country("Hungary").build())
                .build();

        Student third = Student.builder()
                .email("third@universe.com")
                .address(Address.builder().country("Japan").build())
                .build();

        Student fourth = Student.builder()
                .email("fourth@universe.com")
                .address(Address.builder().country("Norway").build())
                .build();

        Student fifth = Student.builder()
                .email("fifth@universe.com")
                .address(Address.builder().country("Scotland").build())
                .build();

        studentRepository.saveAll(Lists.newArrayList(first, second, third, fourth, fifth));

        List<String> countries = studentRepository.findAllCountry();

        assertThat(countries)
                .hasSize(4)
                .containsOnlyOnce("Hungary", "Japan", "Norway", "Scotland");

    }

    @Test
    public void updateAllToUKByStudentName() {

        Address address1 = Address.builder()
                .country("Hungary")
                .build();

        Address address2 = Address.builder()
                .country("Canada")
                .build();

        Address address3 = Address.builder()
                .country("Germany")
                .build();

        Student student = Student.builder()
                .address(address1)
                .email("satori@universe.org")
                .name("Avalokiteshvara")
                .build();

        studentRepository.save(student);
        addressRepository.saveAll(Lists.newArrayList(address2, address3));

        int updatedAddresses = addressRepository.updateAllToUKByStudentName("Avalokiteshvara");

        assertThat(updatedAddresses).isEqualTo(1);

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .anyMatch(address -> address.getCountry().equals("UK"));

    }

}