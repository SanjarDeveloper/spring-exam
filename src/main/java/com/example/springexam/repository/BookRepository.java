package com.example.springexam.repository;

import com.example.springexam.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query(value = "select * from books b where b.book_language = ?1 and b.class_no = ?2 and b.active = true", nativeQuery = true)
    List<Book> findAllBooks(Integer book_language, Integer class_no);

    @Query(value = "select * from books b where b.book_language = ?1 and b.class_no = ?2 and b.name = ?3 and b.active = true", nativeQuery = true)
    Optional<Book> findOneBook(Integer book_language, Integer class_no, String book_name);

    List<Book> findByName(String name);
}
