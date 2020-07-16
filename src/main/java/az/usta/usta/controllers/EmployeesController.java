package az.usta.usta.controllers;


import az.usta.usta.entities.Person;
import az.usta.usta.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeesController {
    @Autowired
    private PersonService personService;


    @GetMapping("/{category}")
    private List<Person> doGetEmployees(@PathVariable String category)
    {
        return personService.getEmployeesByCategory(category);
    }
}
