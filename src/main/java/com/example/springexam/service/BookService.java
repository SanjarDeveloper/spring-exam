package com.example.springexam.service;

import com.example.springexam.DTO.BookDTO;
import com.example.springexam.entity.ApiResponse;
import com.example.springexam.entity.Attachment;
import com.example.springexam.entity.AttachmentContent;
import com.example.springexam.entity.Book;
import com.example.springexam.entity.enums.BookClass;
import com.example.springexam.entity.enums.BookLanguage;
import com.example.springexam.repository.AttachmentContentRepository;
import com.example.springexam.repository.AttachmentRepository;
import com.example.springexam.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public ApiResponse getAllBook(String lang, String class_no) {
        int class_n = 0;
        for (BookClass value : BookClass.values()) {
            if (class_no.equals(value.name())) {
                class_n = value.ordinal();
            }
        }
        int lang_n = 0;
        for (BookLanguage value : BookLanguage.values()) {
            if (lang.equals(value.name())) {
                lang_n = value.ordinal();
            }
        }
        List<Book> all = bookRepository.findAllBooks(lang_n, class_n);
        if (all.size() > 0) {
            return new ApiResponse("Book List", true, all);
        } else return new ApiResponse("Empty!", true);
    }

    public ApiResponse getOneBook(String class_no, String lang, String book_name) {
        int class_n = 0;
        for (BookClass value : BookClass.values()) {
            if (class_no.equals(value.name())) {
                class_n = value.ordinal();
            }
        }
        int lang_n = 0;
        for (BookLanguage value : BookLanguage.values()) {
            if (lang.equals(value.name())) {
                lang_n = value.ordinal();
            }
        }

        Optional<Book> byClass_noAndBook_languageAndName = bookRepository.findOneBook(lang_n, class_n, book_name);
        if (!byClass_noAndBook_languageAndName.isPresent()) {
            return new ApiResponse("No such book", false);
        } else return new ApiResponse("Here", true, byClass_noAndBook_languageAndName.get());
    }

    public ApiResponse addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setName(bookDTO.getName());
        book.setClass_no(BookClass.valueOf(bookDTO.getClass_no()));
        book.setBook_language(BookLanguage.valueOf(bookDTO.getLanguage()));
        book.setDescription(bookDTO.getDescription());
        book.setAuthor(bookDTO.getAuthor());
        book.setActive(bookDTO.isActive());
        bookRepository.save(book);
        return new ApiResponse("Saved :)", true);
    }

    public ApiResponse editBook(String class_no, String lang, String book_name, BookDTO bookDTO) {
        int class_n = 0;
        for (BookClass value : BookClass.values()) {
            if (class_no.equals(value.name())) {
                class_n = value.ordinal();
            }
        }
        int lang_n = 0;
        for (BookLanguage value : BookLanguage.values()) {
            if (lang.equals(value.name())) {
                lang_n = value.ordinal();
            }
        }
        Optional<Book> book = bookRepository.findOneBook(lang_n, class_n, book_name);
        if (!book.isPresent()) {
            return new ApiResponse("No such book :(", false);
        } else {
            Book bookEdit = book.get();
            bookEdit.setName(bookDTO.getName());
            bookEdit.setClass_no(BookClass.valueOf(bookDTO.getClass_no()));
            bookEdit.setBook_language(BookLanguage.valueOf(bookDTO.getLanguage()));
            bookEdit.setDescription(bookDTO.getDescription());
            bookEdit.setAuthor(bookDTO.getAuthor());
            bookEdit.setActive(bookDTO.isActive());
            bookRepository.save(bookEdit);
            return new ApiResponse("Edited :)", true);
        }
    }

    public ApiResponse deleteBook(String class_no, String lang, String book_name) {
        int class_n = 0;
        for (BookClass value : BookClass.values()) {
            if (class_no.equals(value.name())) {
                class_n = value.ordinal();
            }
        }
        int lang_n = 0;
        for (BookLanguage value : BookLanguage.values()) {
            if (lang.equals(value.name())) {
                lang_n = value.ordinal();
            }
        }
        Optional<Book> book = bookRepository.findOneBook(lang_n, class_n, book_name);
        if (!book.isPresent()) {
            return new ApiResponse("No such book", false);
        } else {
            Book book1 = book.get();
            book1.setActive(false);
            bookRepository.save(book1);
            return new ApiResponse("Deleted :)", true);
        }
    }

    public ApiResponse upload(MultipartHttpServletRequest request, @PathVariable("class") String class_no, @PathVariable("lang") String lang, @PathVariable("book") String book_name) throws IOException {
        ApiResponse oneBook = this.getOneBook(class_no, lang, book_name);
        String bookName = book_name + "_" + class_no + "_" + lang;
        if (oneBook.isSuccess()) {
            Optional<Attachment> byBookName = attachmentRepository.findByBookName(bookName);
            if (byBookName.isPresent()){
                Attachment attachment = byBookName.get();
                Optional<AttachmentContent> byAttachmentId = attachmentContentRepository.findByAttachmentId(attachment.getId());
                if (byAttachmentId.isPresent()){
                    attachmentContentRepository.delete(byAttachmentId.get());
                    attachmentRepository.delete(byBookName.get());
                }
            }
            MultipartFile file = request.getFile("file");
            Attachment save = null;
            if (file != null || !file.isEmpty()) {
                Attachment attachment = new Attachment();
                attachment.setContentType(file.getContentType());
                attachment.setName(file.getOriginalFilename());
                attachment.setBookName(bookName);
                attachment.setSize(file.getSize());
                save = attachmentRepository.save(attachment);

                AttachmentContent attachmentContent = new AttachmentContent();
                attachmentContent.setBytes(file.getBytes());
                attachmentContent.setAttachment(save);

                attachmentContentRepository.save(attachmentContent);

                return new ApiResponse("Saved!", true);
            } else {
                return new ApiResponse("Not acceptable :(", false);
            }
        } else {
            return new ApiResponse("No such book! (Hint: create book first)", false);
        }
    }

    public ResponseEntity download(String class_no, String lang, String book_name) throws IOException {
        String bookName = book_name + "_" + class_no + "_" + lang;
        Optional<Attachment> byId = attachmentRepository.findByBookName(bookName);
        if (!byId.isPresent()) {
            return ResponseEntity.ok().body(new ApiResponse("No such file :(", false));
        } else {
            Attachment attachment = byId.get();

            Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepository.findByAttachmentId(attachment.getId());

            AttachmentContent attachmentContent = optionalAttachmentContent.get();
//        attachmentContent.getBytes(); //faylni baytlari

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "file")
                    .body(attachmentContent.getBytes());
        }
    }
}