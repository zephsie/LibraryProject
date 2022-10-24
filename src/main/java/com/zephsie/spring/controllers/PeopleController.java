package com.zephsie.spring.controllers;

import com.zephsie.spring.dao.PersonDAO;
import com.zephsie.spring.models.Book;
import com.zephsie.spring.models.Person;
import com.zephsie.spring.utits.PersonValidator;
import com.zephsie.spring.utits.UpdatePersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PersonDAO personDAO;

    private final PersonValidator personValidator;

    private final UpdatePersonValidator updatePersonValidator;

    @Autowired
    public PeopleController(PersonDAO personDAO, PersonValidator personValidator, UpdatePersonValidator updatePersonValidator) {
        this.personDAO = personDAO;
        this.personValidator = personValidator;
        this.updatePersonValidator = updatePersonValidator;
    }

    @GetMapping
    public String show(Model model) {
        try {
            model.addAttribute("people", personDAO.get());
            return "people/index";
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") long id,
                       Model model) {

        Optional<Person> person;
        Collection<Book> books;

        try {
            person = personDAO.get(id);
            books = personDAO.getBooks(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        if (person.isEmpty()) {
            model.addAttribute("message", "Person with id " + id + " not found");
            return "error_page";
        }

        model.addAttribute("person", person.get());
        model.addAttribute("books", books);

        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "/people/new";
    }

    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         Model model) {

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        try {
            personDAO.save(person);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        Optional<Person> person;

        try {
            person = personDAO.get(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        if (person.isEmpty()) {
            model.addAttribute("message", "Person with id " + id + " not found");
            return "error_page";
        }

        model.addAttribute("person", person.get());
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         Model model) {

        updatePersonValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/people/edit";
        }

        try {
            personDAO.update(id, person);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id,
                         Model model) {

        try {
            personDAO.delete(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/people";
    }
}
