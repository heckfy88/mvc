package com.contact_book.demo

import com.contact_book.demo.Model.Contact
import com.contact_book.demo.Service.ContactBookService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.concurrent.ConcurrentHashMap


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTAppTests {

    private val headers: HttpHeaders = HttpHeaders()

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var contactBook: ContactBookService

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    private fun url(s: String): String {
        return "http://localhost:${port}/${s}"
    }


    @BeforeEach
    fun setCookieAndData() {

        val cookie = getCookieForUser("logged", "logged", "/LoginServlet")

        headers.add("Cookie", cookie)
    }

    private fun getCookieForUser(login: String, password: String, loginUrl: String): String? {

        val newMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        newMap.set("login", login)
        newMap.set("password", password)

        val loginResponse: ResponseEntity<String> =
            testRestTemplate.postForEntity(
                loginUrl,
                HttpEntity(
                    newMap, HttpHeaders()
                ),
                String::class.java
            )
        return loginResponse.headers["Set-Cookie"]!![0]
    }

    @Test
    fun `addContact returns status 201 and a right contact`() {

        val testContacts = Contact("Ivan", "Ivanoff", "32")

        val response = testRestTemplate.exchange(
            "/api/add",
            HttpMethod.POST,
            HttpEntity(testContacts, headers),
            Contact::class.java,
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(testContacts.firstName, response.body!!.firstName)
        assertEquals(testContacts.lastName, response.body!!.lastName)
        assertEquals(testContacts.age, response.body!!.age)
    }

    @Test
    fun `getList returns status 200 and all contacts`() {

        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))

        val response = testRestTemplate.exchange(
            "/api/list",
            HttpMethod.GET,
            HttpEntity(contact.values, headers),
            Collection::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

    }

    @Test
    fun `getList returns with search query returns status 200`() {


        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))

        val response = testRestTemplate.exchange(
            "/api/list?name=Ivan",
            HttpMethod.GET,
            HttpEntity(contact.values, headers),
            Collection::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

    }

    @Test
    fun `getContact returns status 400`() {

        val id = 0

        val response = testRestTemplate.exchange(
            "/api/$id/view",
            HttpMethod.GET,
            HttpEntity(null, headers),
            Collection::class.java
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `getContact returns status 200 and a contact`() {

        contactBook.addContact(Contact("Ivan", "Ivanoff", "32"))

        val id = 0

        val response = testRestTemplate.exchange(
            "/api/$id/view",
            HttpMethod.GET,
            HttpEntity(null, headers),
            Contact::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(contactBook.getContact(0)!!.firstName, response.body!!.firstName)
        assertEquals(contactBook.getContact(0)!!.lastName, response.body!!.lastName)
        assertEquals(contactBook.getContact(0)!!.age, response.body!!.age)
    }

    @Test
    fun `putContact returns status 200 and changes Contact`() {

        val contactOld = Contact("Ivan", "Ivanoff", "32")
        contactBook.addContact(contactOld)

        val id = 0

        val contactNew = Contact("Vasya", "Tinkoff", "320000000")

        val response = testRestTemplate.exchange(
            "/api/$id/edit",
            HttpMethod.PUT,
            HttpEntity(contactNew, headers),
            Contact::class.java
        )
        assertNotNull(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(contactNew.firstName, response.body!!.firstName)
        assertEquals(contactNew.lastName, response.body!!.lastName)
        assertEquals(contactNew.age, response.body!!.age)
    }

    @Test
    fun `deleteContact returns status 200 and deletes Contact`() {
        val contact = Contact("Ivan", "Ivanoff", "32")
        contactBook.addContact(contact)

        val id = 0

        val response = testRestTemplate.exchange(
            url("/api/$id/delete"),
            HttpMethod.DELETE,
            HttpEntity(null, headers),
            Contact::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(null, contactBook.getContact(0))
    }

}