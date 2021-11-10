package com.contact_book.demo.Controller

import com.contact_book.demo.Model.Contact
import com.contact_book.demo.Service.ContactBookService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap


@RestController
@RequestMapping("/api")
class RESTController @Autowired constructor(val contactBook: ContactBookService) {

    @PostMapping("/add")
    fun addContact(@RequestBody contact: Contact): ResponseEntity<Contact> {
        contactBook.addContact(contact)
        return ResponseEntity(contact, HttpStatus.CREATED)
    }

    @GetMapping("/list")
    fun getList(@RequestParam(required = false) firstName: String?,
                @RequestParam(required = false) lastName: String?,
                @RequestParam(required = false) age: String?): ResponseEntity<Collection<Contact>> {
        return ResponseEntity(contactBook.getContacts(firstName, lastName, age), HttpStatus.OK)
    }

    @GetMapping("/{id}/view")
    fun getContact(@PathVariable("id") id: Int): ResponseEntity<Contact> {
        return if (contactBook.checkKey(id)) {
            val contactViewed = contactBook.getContact(id)
            ResponseEntity(contactViewed, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{id}/edit")
    fun putContact(@PathVariable id: Int,
                   @RequestBody contact: Contact): ResponseEntity<Contact> {
            val editedContact = contactBook.putContact(id, contact)
            return ResponseEntity(editedContact, HttpStatus.OK)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/delete")
    fun deleteContact(@PathVariable("id") id: Int) {
        if (contactBook.checkKey(id)) {
            ResponseEntity(contactBook.deleteContact(id), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}