package ru.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.library.models.Book;
import ru.library.models.Person;

import java.util.List;
import java.util.Optional;

@Component
public class BookDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> index(){
        return jdbcTemplate.query("SELECT * FROM book", new BeanPropertyRowMapper<>(Book.class));
    }

    public Optional<Book> show(int id){
        return jdbcTemplate.query("SELECT * FROM book WHERE book_id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Book.class))
                .stream().findFirst();
    }

    public void save(Book book){
        jdbcTemplate.update("INSERT INTO book(title, year, author) VALUES (?, ?, ?)",
                book.getTitle(), book.getYear(), book.getAuthor());
    }

    public void update(int id, Book updateBook){
        jdbcTemplate.update("UPDATE book SET title=?, author=?, year=? WHERE book_id=?",
                updateBook.getTitle(), updateBook.getAuthor(), updateBook.getYear(), id);
    }


    public void delete(int id){
        jdbcTemplate.update("DELETE FROM book WHERE book_id=?", id);
    }

    /*назначение пользователя*/
    public void assign(int bookId, Person selectedPerson){
        jdbcTemplate.update("UPDATE book SET person_id=? WHERE book_id=?", selectedPerson.getPerson_id(),bookId);
    }

    /*освободить книгу*/
    public void unassign(int bookId){
        jdbcTemplate.update("UPDATE book SET person_id=NULL WHERE book_id=?", bookId);
    }

    /*получаем владельца книги*/
    public Optional<Person> getBookOwner(int bookId){
        return jdbcTemplate.query("SELECT person_lib.* FROM person_lib JOIN book ON person_lib.person_id = Book.person_id WHERE book_id=?",
                new Object[]{bookId}, new BeanPropertyRowMapper<>(Person.class)).stream().findFirst();
    }
}
