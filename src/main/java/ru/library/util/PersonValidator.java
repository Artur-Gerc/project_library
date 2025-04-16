package ru.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.library.dao.PersonDAO;
import ru.library.models.Person;
import ru.library.services.PeopleService;

@Component
public class PersonValidator implements Validator {
    private PersonDAO personDAO;
    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PersonDAO personDAO, PeopleService peopleService) {
        this.personDAO = personDAO;
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if(peopleService.findByName(person.getName()).isPresent()) {
            errors.rejectValue("name", "duplicate", "Duplicate Person");
        }
    }
}
