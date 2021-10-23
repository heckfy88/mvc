package com.contact_book.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@ServletComponentScan
@SpringBootApplication
class ContactBookApplication

fun main(args: Array<String>) {
    runApplication<ContactBookApplication>(*args)
}
