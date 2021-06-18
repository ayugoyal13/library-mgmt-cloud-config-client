package com.epam.libraryconfigclient.client.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.epam.libraryconfigclient.model.Book;

@RestController
@RequestMapping("/book-service")
@RefreshScope
public class BookServiceConfigClient {

	@Autowired
	@Lazy
	private RestTemplate restTemplate;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Value("${book.service.welcome}")
	private String welcomeUrl;
	
	@Value("${book.service.findAllBooks}")
	private String findAllBooksUrl;
	
	@Value("${book.service.findBookById}")
	private String findBookByIdUrl;
	
	@Value("${book.service.addNewBook}")
	private String addNewBookUrl;
	
	@Value("${book.service.updateExistingBook}")
	private String updateExistingBookUrl;
	
	@Value("${book.service.deleteBookById}")
	private String deleteBookByIdUrl;
	
	@GetMapping("/")
	public String welcomeBookService() {
		return restTemplate.getForObject(welcomeUrl, String.class);
	}
	
	@GetMapping("/books")
	public List<Book> findAllBooks() {
		List<Book> books = new ArrayList<Book>();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(mediaTypes);
		HttpEntity<Book> httpEntity = new HttpEntity<Book>(null, headers);
		try {
			ResponseEntity<Book[]> responseEntity = restTemplate.exchange(findAllBooksUrl, HttpMethod.GET, httpEntity, Book[].class);
			Book[] result = responseEntity.getBody();
			for(Book book : result) {
				books.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return books;
	}
	
	@GetMapping("/books/{bookId}")
	public Book findBookById(@PathVariable long bookId) {
		Book book = null;
		try {
			book = restTemplate.getForObject(findBookByIdUrl, Book.class, bookId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return book;
	}
	
	@PostMapping("/books")
	public Book addNewBook(@RequestBody Book book) {
		Book result = null;
		try {
			Book newBook = new Book(0, book.getBookName(), book.getBookAuthor(),
					book.getBookCategory(), book.getBookDescription(), book.getBookIssuedDate());
			result = restTemplate.postForObject(addNewBookUrl, newBook, Book.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@PutMapping("/books")
	public Book updateExistingBook(@RequestBody Book book) {
		Book updatedBook = null;
		try {
			long bookId = book.getBookId();
			restTemplate.put(updateExistingBookUrl, book);
			updatedBook = findBookById(bookId);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return updatedBook;
	}
	
	@DeleteMapping("/books/{bookId}")
	public void deleteBookById(@PathVariable long bookId) {
		try {
			restTemplate.delete(deleteBookByIdUrl, bookId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
