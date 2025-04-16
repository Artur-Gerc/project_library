package ru.library.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.models.Book;
import ru.library.models.Person;
import ru.library.repositories.BooksRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BooksService {

    private final BooksRepository booksRepository;

    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findWithPagination(int page, int booksPerPage, boolean sortByYear) {
        if(sortByYear){
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        } else {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        }
    }

    public List<Book> findAll(boolean sortByYear) {
        if(sortByYear){
            return booksRepository.findAll(Sort.by("year"));
        } else {
            return booksRepository.findAll();
        }
    }

    public Book findById(int id) {
        return booksRepository.findById(id).get();
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updateBook) {

        /*добавляем по сути новую книгу (которая не находится в Persistence context), поэтому нужен save()*/
        updateBook.setId(id);
        /*чтобы не терялась связь при обновлении. Так как та сущность, которая пришла с формы (updateBook), имеет
        * в поле owner null*/
        updateBook.setOwner(booksRepository.findById(id).get().getOwner());
        booksRepository.save(updateBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    /*назначение пользователя*/
    @Transactional
    public void assign(int bookId, Person selectedPerson) {
        booksRepository.findById(bookId).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setTakeAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void unassign(int bookId) {
        booksRepository.findById(bookId).ifPresent(book -> {
            book.setOwner(null);
            book.setTakeAt(null);
        });
    }

    public List<Book> searchByTitle(String query) {
        return booksRepository.findByTitleStartingWith(query);
    }
}
