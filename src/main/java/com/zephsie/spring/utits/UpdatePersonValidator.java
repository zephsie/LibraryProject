package com.zephsie.spring.utits;

import com.zephsie.spring.dao.PersonDAO;
import com.zephsie.spring.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
public class UpdatePersonValidator implements Validator {
    PersonDAO personDAO;

    @Autowired
    public UpdatePersonValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PersonDAO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        Optional<Person> personFromDB = personDAO.get(person.getFullName());

        if (personFromDB.isPresent() && !Objects.equals(personFromDB.get().getId(), person.getId())) {
            errors.rejectValue("fullName", "person.fullName", "Person with this name already exists");
        }
    }
}
