package com.contact_book.demo.Servlet

import java.util.*
import javax.servlet.RequestDispatcher
import javax.servlet.annotation.WebServlet
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet("/LoginServlet")
class LoginServlet : HttpServlet() {
    private val login: String = "logged"
    private val password: String = "logged"

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val loginPost = req!!.getParameter("login")
        val passwordPost = req.getParameter("password")

        if (login.equals(loginPost) && password.equals(passwordPost)) {
            val cookie = Cookie("auth", Calendar.getInstance().timeInMillis.toString())
            resp!!.addCookie(cookie)
            resp.sendRedirect("/app/add")
        } else {
            val requestDispatcher: RequestDispatcher = servletContext.getRequestDispatcher("/index.html")
            val out = resp!!.writer
            out.println("<font color=red>Either user name or password is wrong.</font>")
            requestDispatcher.include(req, resp)
        }

    }

}