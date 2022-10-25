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
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Person> personRowMapper;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate, RowMapper<Person> personRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.personRowMapper = personRowMapper;
    }

    public Collection<Person> get() {
        return jdbcTemplate.query("SELECT id, full_name, year_of_birth FROM structure.person",
                personRowMapper);
    }

    public Optional<Person> get(Long id) {
        return jdbcTemplate.query("SELECT id, full_name, year_of_birth FROM structure.person WHERE id = ?",
                        personRowMapper,
                        id)
                .stream()
                .findAny();
    }

    public Optional<Person> get(String fullName) {
        return jdbcTemplate.query("SELECT id, full_name, year_of_birth FROM structure.person WHERE full_name = ?",
                        personRowMapper,
                        fullName)
                .stream()
                .findAny();
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO structure.person(full_name, year_of_birth) VALUES (?, ?)",
                person.getFullName(),
                person.getYearOfBirth());
    }

    public void update(Long id, Person person) {
        jdbcTemplate.update("UPDATE structure.person SET full_name = ?, year_of_birth = ? WHERE id = ?",
                person.getFullName(),
                person.getYearOfBirth(),
                id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM structure.person WHERE id = ?",
                id);
    }

    public Collection<Book> getBooks(Long id) {
        return jdbcTemplate.query("SELECT id, name, author, year FROM structure.book WHERE person_id = ?",
                new BeanPropertyRowMapper<>(Book.class),
                id);
    }
}