package ru.library.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.dao.BookDAO;
import ru.library.dao.PersonDAO;
import ru.library.models.Book;
import ru.library.models.Person;
import ru.library.services.BooksService;
import ru.library.services.PeopleService;

@Controller
@RequestMapping("/books")
public class BookController {

    private final PersonDAO personDAO;
    private final BookDAO bookDAO;
    private final PeopleService peopleService;
    private final BooksService booksService;

    @Autowired
    public BookController(BookDAO bookDAO, PersonDAO personDAO, PeopleService peopleService, BooksService booksService) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort", required = false) boolean sort){
        if(page == null || booksPerPage == null) {
            model.addAttribute("books", booksService.findAll(sort));
        } else {
            model.addAttribute("books", booksService.findWithPagination(page, booksPerPage, sort));
        }
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @ModelAttribute("person") Person person){
        Book book = booksService.findById(id);
        model.addAttribute("book", book);

        Person owner = book.getOwner();

        if(owner != null){
            model.addAttribute("owner", owner);
        } else {
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "books/new";
        }

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model){
        model.addAttribute("book", booksService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, @PathVariable("id") int id,
                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "books/edit";
        }

        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }

    /*удалить владельца*/
    @PatchMapping("/{id}/unassign")
    public String unassignOwner(@PathVariable("id") int id){
        booksService.unassign(id);
        return "redirect:/books/" + id;
    }

    /*назначаем владельца*/
    @PatchMapping("/{id}/assign")
    public String assignOwner(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson){
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query){
        model.addAttribute("books", booksService.searchByTitle(query));
        return "books/search";
    }
}
