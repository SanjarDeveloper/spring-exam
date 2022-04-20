package com.example.springexam.entity;

import com.example.springexam.entity.enums.BookClass;
import com.example.springexam.entity.enums.BookLanguage;
import lombok.*;

import javax.persistence.*;

@Entity(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Enumerated
    private BookClass class_no;
    @Enumerated
    private BookLanguage book_language;
    private String description;
    private String Author;
    private boolean active;
}
