package az.usta.usta.repositories;

import az.usta.usta.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person,Long> {
    Person findByEmail(String email);
    Person findByActivationCode(String activationCode);
    List<Person> findByCategory(String category);
}
