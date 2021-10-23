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

@WebFilter("/*")
class RequestLoggingFilter : HttpFilter() {

    private lateinit var context: ServletContext

    override fun init(filterConfig: FilterConfig) {
        this.context = filterConfig.servletContext
        this.context.log("RequestLoggingFilterInitialized")
    }


    override fun doFilter(req: HttpServletRequest?, resp: HttpServletResponse?, chain: FilterChain?) {
        val method: String = req!!.method
        val uri: String = req.requestURI
        this.context.log("Requested Resource:: $method $uri")
        val params: Enumeration<String> = req.parameterNames

        while (params.hasMoreElements()) {
            val name = params.nextElement()
            val value = req.getParameter(name)
            this.context.log(req.remoteAddr + "::Request Params::{" + name + "=" + value + "}")
        }
        val cookies = req.cookies
        if (cookies != null) {
            for (cookie: Cookie in cookies) {
                this.context.log(req.remoteAddr + "::Cookie:: {" + cookie.name + " , " + cookie.value + "}")
            }
        }
        chain!!.doFilter(req, resp)

    }

}