package com.zephsie.spring.dao;

import com.zephsie.spring.models.Book;
import com.zephsie.spring.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class BookDAO {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Book> bookRowMapper;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookRowMapper = new BeanPropertyRowMapper<>(Book.class);
    }

    public Collection<Book> get() {
        return jdbcTemplate.query("SELECT id, name, author, year FROM structure.book",
                bookRowMapper);
    }

    public Optional<Book> get(Long id) {
        return jdbcTemplate.query("SELECT id, name, author, year FROM structure.book WHERE id = ?",
                        bookRowMapper,
                        id)
                .stream()
                .findAny();
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO structure.book(name, author, year) VALUES (?, ?, ?)",
                book.getName(),
                book.getAuthor(),
                book.getYear());
    }

    public void update(Long id, Book book) {
        jdbcTemplate.update("UPDATE structure.book SET name = ?, author = ?, year = ? WHERE id = ?",
                book.getName(),
                book.getAuthor(),
                book.getYear(),
                id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM structure.book WHERE id = ?",
                id);
    }

    public Optional<Person> getOwner(Long id) {
        return jdbcTemplate.query("SELECT id, full_name, year_of_birth FROM structure.person WHERE id = (SELECT person_id FROM structure.book WHERE id = ?)",
                        new BeanPropertyRowMapper<>(Person.class),
                        id)
                .stream()
                .findAny();
    }

    public void setOwner(Long id, Person person) {
        jdbcTemplate.update("UPDATE structure.book SET person_id = ? WHERE id = ?",
                person.getId(),
                id);
    }

    public void removeOwner(Long id) {
        jdbcTemplate.update("UPDATE structure.book SET person_id = NULL WHERE id = ?",
                id);
    }
}