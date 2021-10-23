package com.contact_book.demo


import com.contact_book.demo.Controller.MVCController
import com.contact_book.demo.Model.Contact
import com.contact_book.demo.Service.ContactBookService

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.hamcrest.core.StringContains.containsString


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest



import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders



import java.util.concurrent.ConcurrentHashMap


@SpringBootTest
@AutoConfigureMockMvc
class MVCMockTests {
    @RelaxedMockK
    private lateinit var service: ContactBookService

    @InjectMockKs
    private lateinit var controller: MVCController
    init {
        MockKAnnotations.init(this)
    }

    @BeforeEach
    fun before() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test showAddContactForm`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/add"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("addContactForm"))

    }

    @Test
    fun `test addContact`() {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/app/add")
                    .param("firstName", "Ivan")
                    .param("lastName", "Ivanov")
                    .param("age", "32")
            )
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(view().name("addContactForm"))


    }

    @Test
    fun `test showList`() {

        mockMvc.perform(MockMvcRequestBuilders.get("/app/list"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("showContactList"))
    }

    @Test
    fun `test showList with search query firstName = Ivan`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/list?firstName=\"Ivan\""))
        .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("showContactList"))

    }


    @Test
    fun `test getContact but with contact not in contactBook `() {

        val id = 100

        mockMvc.perform(MockMvcRequestBuilders.get("/app/$id/view"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(view().name("errorPage"))
    }

    @Test
    fun `test getContact with contact in contactBook`() {

        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))



        for (id in 0..2) {

            every { service.checkKey(id) } returns (id in contact.keys)
            every { service.getContact(id) } returns contact[id]

            mockMvc
                .perform(MockMvcRequestBuilders.get("/app/$id/view"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(view().name("viewContact"))
                .andExpect(model().attribute("id", id))
                .andExpect(model().attribute("user", service.getContact(id)))
        }
    }

    @Test
    fun `test getUpdatePage with contact in contactBook`() {

        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))

        for (id in 0..2) {

            every { service.checkKey(id) } returns (id in contact.keys)

            mockMvc.perform(MockMvcRequestBuilders.get("/app/$id/edit"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(view().name("editContactForm"))

        }
    }

    @Test
    fun `test updateContact but with contact not in contactBook`() {
        val id = 100

        mockMvc.perform(MockMvcRequestBuilders.get("/app/$id/edit"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(view().name("errorPage"))
    }

    @Test
    fun `test updateContact with contact in contactBook`() {

        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))

        for (id in 0..2) {

            every { service.checkKey(id) } returns (id in contact.keys)

            mockMvc.perform(MockMvcRequestBuilders.post("/app/$id/edit")
                .param("firstName", contact[id]!!.firstName)
                .param("lastName", contact[id]!!.lastName)
                .param("age", contact[id]!!.age)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(view().name("acceptChange"))
        }
    }

    @Test
    fun `test deleteContact with no contact in contactBook`() {
        val id = 100

        mockMvc.perform(MockMvcRequestBuilders.get("/app/$id/delete"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(view().name("errorPage"))
    }

    @Test
    fun `test deleteContact with contact in contactBook`() {

        val contact: ConcurrentHashMap<Int, Contact> = ConcurrentHashMap()

        contact.put(0, Contact("Ivan", "Ivanoff", "32"))
        contact.put(1, Contact("van", "off", "302"))
        contact.put(2, Contact("an", "Io", "3200"))

        for (id in 0..2) {

            every { service.checkKey(id) } returns (id in contact.keys)
            every { service.deleteContact(id) } returns contact.remove(id)

            mockMvc.perform(MockMvcRequestBuilders.get("/app/$id/delete"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(view().name("deleteSuccess"))
        }

    }

}