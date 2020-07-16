package az.usta.usta.controllers;

import az.usta.usta.entities.Person;
import az.usta.usta.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public String getPage()
    {
        return "registration";
    }

    @PostMapping
    public String registrationOfTheUser(@ModelAttribute Person person, MultipartFile avatarFile, Model model) throws IOException {
        person.setAvatar(Base64.getEncoder().encodeToString(avatarFile.getBytes()));
        String registrationStatus=personService.registerUser(person);

        if(registrationStatus.equals("OK"))
        {
            model.addAttribute("email",person.getEmail());
            return "mail_was_sent";
        }

        model.addAttribute("error",registrationStatus);
        return "registration";
    }

    @GetMapping("/activate/{activationCode}")
    public String activateUser(@PathVariable("activationCode") String activationCode,Model model)
    {
        Person person=personService.getUserByActivationCode(activationCode);
        if(person!=null)
        {
            person.setIsActivated(1);
            person.setActivationCode("");
            personService.saveUser(person);
            model.addAttribute("username",person.getUsername());
            return "user_was_activated";
        }

        return "denied_page";
    }

    @GetMapping("/sendMailAgain")
    public String doSendMailAgain()
    {
        return "send_mail_again";
    }

    @PostMapping("/sendMailAgain")
    public String doSendMailAgainPost(@RequestParam String email)
    {
        if(personService.sendMailAgain(email))
        {
            return "mail_was_sent";
        }
        else
        {
            return "denied_page";
        }

    }

}