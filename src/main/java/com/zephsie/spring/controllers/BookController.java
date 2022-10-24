package com.zephsie.spring.controllers;

import com.zephsie.spring.dao.BookDAO;
import com.zephsie.spring.dao.PersonDAO;
import com.zephsie.spring.models.Book;
import com.zephsie.spring.models.Person;
import com.zephsie.spring.utits.PersonBookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookDAO bookDAO;

    private final PersonDAO personDAO;

    private final PersonBookValidator personBookValidator;

    @Autowired
    public BookController(BookDAO bookDAO, PersonDAO personDAO, PersonBookValidator personBookValidator) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
        this.personBookValidator = personBookValidator;
    }

    @GetMapping
    public String show(Model model) {
        try {
            model.addAttribute("books", bookDAO.get());
            return "books/index";
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") long id,
                       Model model,
                       @ModelAttribute("person") Person person) {

        Optional<Book> book;
        Optional<Person> owner;

        try {
            book = bookDAO.get(id);
            owner = bookDAO.getOwner(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        if (book.isEmpty()) {
            model.addAttribute("message", "Book with id " + id + " not found");
            return "error_page";
        }

        model.addAttribute("book", book.get());

        if (owner.isPresent()) {
            model.addAttribute("owner", owner.get());
        } else {
            try {
                model.addAttribute("people", personDAO.get());
            } catch (Exception e) {
                model.addAttribute("message", "Internal server error");
                return "error_page";
            }
        }

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "/books/new";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        try {
            bookDAO.save(book);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        Optional<Book> book;

        try {
            book = bookDAO.get(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        if (book.isEmpty()) {
            model.addAttribute("message", "Book with id " + id + " not found");
            return "error_page";
        }

        model.addAttribute("book", book.get());

        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") long id,
                         @ModelAttribute("book") Book book,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        try {
            bookDAO.update(id, book);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id,
                         Model model) {

        try {
            bookDAO.delete(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/books";
    }

    @PatchMapping("/{id}/set_owner")
    public String setOwner(@PathVariable("id") long id,
                           @ModelAttribute("person") Person person,
                           BindingResult bindingResult,
                           Model model) {

        personBookValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/show";
        }

        try {
            bookDAO.setOwner(id, person);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/remove_owner")
    public String removeOwner(@PathVariable("id") long id,
                              Model model) {

        try {
            bookDAO.removeOwner(id);
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            return "error_page";
        }

        return "redirect:/books/" + id;
    }
}