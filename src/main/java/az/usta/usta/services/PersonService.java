package az.usta.usta.services;

import az.usta.usta.entities.Person;
import az.usta.usta.entities.Role;
import az.usta.usta.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RegistrationMailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person person=personRepository.findByEmail(email);

        if(person==null || person.getIsActivated()!=1)
        {
            throw new UsernameNotFoundException(("User not found"));
        }

        return person;
    }

    public void saveUser(Person person)
    {
        personRepository.save(person);
    }


    public String registerUser(Person person) {

        if(!person.getPassword().equals(person.getPasswordConfirm()))
        {
            return "Passwords are not equal";
        }

        if(personRepository.findByEmail(person.getEmail())!=null)
        {
            return "Email is taken";
        }

        person.setIsActivated(0);
        person.setActivationCode(UUID.randomUUID().toString());
        person.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        personRepository.save(person);
        String message= String.format("Hello %s.\n\nWelcome to USTA.AZ. " +
                        "Go to this reference to activate your account https://usta.az/registration/activate/%s\n\nIf you didn't try to register, please just ignore this message",
                person.getUsername(),person.getActivationCode());
        mailSender.send(person.getEmail(),"Registration",message);

        return "OK";

    }


    public Person getUserByActivationCode(String activationCode) {
        return personRepository.findByActivationCode(activationCode);
    }

    public boolean sendMailAgain(String mail)
    {
        Person person=personRepository.findByEmail(mail);
        if(person.getIsActivated()==0)
        {
            person.setActivationCode(UUID.randomUUID().toString());
            this.saveUser(person);
            String message= String.format("Hello %s.\n\nWelcome to StudentZ. " +
                            "Go to this reference to activate your account https://usta.az/registration/activate/%s\n\nIf you didn't try to register, please just ignore this message",
                    person.getUsername(),person.getActivationCode());
            mailSender.send(person.getEmail(),"Registration",message);
            return true;
        }
        else
        {
            return false;
        }
    }

    public List<Person> getEmployeesByCategory(String category) {

        return personRepository.findByCategory(category);
    }
}
