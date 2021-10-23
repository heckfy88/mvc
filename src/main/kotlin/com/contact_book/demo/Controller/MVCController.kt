package com.contact_book.demo.Controller

import com.contact_book.demo.Model.Contact
import com.contact_book.demo.Service.ContactBookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/app")
class MVCController @Autowired constructor(val contactBook: ContactBookService) {

    @GetMapping("/add")
    fun showAddContactForm(): String = "addContactForm"


    @PostMapping("/add")
    fun addContact(@ModelAttribute form: Contact, model: Model): String {
        contactBook.addContact(form)
        return "addContactForm"
    }


    @GetMapping("/list")
    fun showList(
        model: Model,
        @RequestParam(required = false) firstName: String?,
        @RequestParam(required = false) lastName: String?,
        @RequestParam(required = false) age: String?,
    ): String {
        model.addAttribute("users", contactBook.getContacts(firstName, lastName, age))
        return "showContactList"
    }

    @GetMapping("/{id}/view")
    fun getContact(
        @PathVariable("id") id: Int,
        model: Model,
    ): String {
        return if (contactBook.checkKey(id)) {
            model.addAttribute("id", id)
            model.addAttribute("user", contactBook.getContact(id))
            "viewContact"
        } else {
            "errorPage"
        }
    }

    @GetMapping("/{id}/edit")
    fun getUpdatePage(@PathVariable("id") id: Int, model: Model): String {
        return if (contactBook.checkKey(id)) {
            "editContactForm"
        } else {
            "errorPage"
        }
    }

    @PostMapping("/{id}/edit")
    fun updateContact(
        @PathVariable("id") id: Int,
        @ModelAttribute form: Contact
    ): String {
        return if (contactBook.checkKey(id)) {
            contactBook.putContact(id, Contact(form.firstName, form.lastName, form.age))
            "acceptChange"
        } else {
            "errorPage"
        }
    }

    @GetMapping("/{id}/delete")
    fun deleteContact(
        @PathVariable("id") id: Int,
        model: Model
    ): String {
        return if (contactBook.checkKey(id)) {
            contactBook.deleteContact(id)
            "deleteSuccess"
        } else {
            "errorPage"
        }
    }
}