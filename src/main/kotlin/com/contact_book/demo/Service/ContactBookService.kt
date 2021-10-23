package com.contact_book.demo.Service

import com.contact_book.demo.Model.Contact
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ContactBookService {

    val contactBookRepo: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()
    var counter = 0

    fun addContact(contact: Contact) {
        contactBookRepo[counter++] = contact
    }

    fun getContacts(
        firstName: String?,
        lastName: String?,
        age: String?
    ): Collection<Contact> {
        if (firstName != null && lastName != null && age != null) {
            return contactBookRepo.values.filter { firstName == it.firstName && lastName == it.lastName && age == it.lastName }
        } else if (firstName == null && lastName != null && age != null) {
            return contactBookRepo.values.filter { lastName == it.lastName && age == it.age }
        } else if (firstName != null && lastName == null && age != null) {
            return contactBookRepo.values.filter { firstName == it.firstName && age == it.age }
        } else if (firstName != null && lastName != null && age == null) {
            return contactBookRepo.values.filter { firstName == it.firstName && lastName == it.lastName }
        } else if (firstName != null && lastName == null && age == null) {
            return contactBookRepo.values.filter { firstName == it.firstName }
        } else if (firstName == null && lastName != null && age == null) {
            return contactBookRepo.values.filter { lastName == it.lastName }
        } else if (firstName == null && lastName == null && age != null) {
            return contactBookRepo.values.filter { age == it.age }
        } else {
            return contactBookRepo.values
        }
    }

    fun checkKey(id: Int): Boolean = id in contactBookRepo.keys

    fun getContact(id: Int): Contact? = contactBookRepo[id]

    fun putContact(id: Int, contact: Contact): Contact? {
        if (contact.firstName.isNotEmpty())
            contactBookRepo[id]!!.firstName = contact.firstName
        if (contact.lastName.isNotEmpty())
            contactBookRepo[id]!!.lastName = contact.lastName
        if (contact.age.isNotEmpty())
            contactBookRepo[id]!!.age = contact.age
        return contactBookRepo[id]
    }

    fun deleteContact(id: Int) = contactBookRepo.remove(id)

}
