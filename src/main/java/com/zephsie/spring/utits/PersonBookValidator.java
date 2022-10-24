package com.zephsie.spring.utits;

import com.zephsie.spring.dao.PersonDAO;
import com.zephsie.spring.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonBookValidator implements Validator {
    private final PersonDAO personDAO;

    @Autowired
    public PersonBookValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Person.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if (personDAO.get(person.getId()).isEmpty()) {
            errors.rejectValue("id", "person.id", "Person with this id not found");
        }
    }
}
