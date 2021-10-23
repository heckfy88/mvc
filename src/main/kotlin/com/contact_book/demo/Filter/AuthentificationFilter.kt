package com.contact_book.demo.Filter

import java.util.*
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletContext
import javax.servlet.annotation.WebFilter
import javax.servlet.http.Cookie
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebFilter(
    urlPatterns = ["/api/*", "/app/*"]
)
class AuthentificationFilter : HttpFilter() {

    private lateinit var context: ServletContext

    override fun init(filterConfig: FilterConfig) {
        this.context = filterConfig.servletContext
        this.context.log("AuthentificationFilter")
    }

    override fun doFilter(req: HttpServletRequest?, resp: HttpServletResponse?, chain: FilterChain?) {


        val cookies = req!!.cookies

        if (cookies == null) {
            this.context.log("No cookies found")
            this.context.log("Unauthorized access request")
            resp!!.sendRedirect("login")
        } else {
            for (cookie: Cookie in cookies) {
                val currentTime = Calendar.getInstance().timeInMillis.toString()

                if (cookie.name != "auth") {
                    this.context.log("Wrong Cookie name:: " + cookie.name)

                    resp!!.sendRedirect("/login")
                } else if (cookie.value >= currentTime) {
                    this.context.log("Wrong Cookie value:: " + cookie.value + " is not less than " + currentTime)

                    resp!!.sendRedirect("/login")
                } else {
                    chain!!.doFilter(req, resp)

                }
            }
        }

    }

}