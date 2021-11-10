package com.contact_book.demo

import com.contact_book.demo.Model.Contact
import com.contact_book.demo.Service.ContactBookService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class SecurityMvcTests {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private val context: WebApplicationContext? = null
    @Autowired
    private lateinit var service: ContactBookService

    @BeforeAll
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context!!)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()

        service.addContact(Contact("Ivan", "Ivanov", "10"))
        service.addContact(Contact("Van", "Vanov", "20"))
        service.addContact(Contact("An", "Anov", "30"))
    }

    @WithAnonymousUser
    @Test
    @Throws(Exception::class)
    fun loginWithAnonymousUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    @Throws(Exception::class)
    fun loginWithUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    @Throws(Exception::class)
    fun loginWithAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun showAddContactFormWithUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/add"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("addContactForm"))

    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun addContactWithUser() {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/add")
                .param("firstName", "Ivan")
                .param("lastName", "Ivanov")
                .param("age", "32")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.view().name("addContactForm"))
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    fun addContactWithAdmin() {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/add")
                .param("firstName", "Ivan")
                .param("lastName", "Ivanov")
                .param("age", "32")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.view().name("addContactForm"))
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun viewContactListWithUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/list"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("showContactList"))
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    fun viewContactListWithAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/list"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("showContactList"))
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun viewContactWithUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/0/view"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("viewContact"))
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun viewContactWithUserWhenContactIsMissing() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/10/view"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("errorPage"))
    }


    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    fun viewContactWithAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/0/view"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.view().name("viewContact"))
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    fun viewContactWithAdminWhenContactIsMissing() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/10/view"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("errorPage"))
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    @Test
    fun deleteContactIsForbiddenForUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/1/delete"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    @Test
    fun deleteContactIsAllowedForAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get("/app/1/delete"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

}