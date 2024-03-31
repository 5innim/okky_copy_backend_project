package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.error.exception.StatusCodeException;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.GenericFilterBean;

public class HandleStatusCodeExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (StatusCodeException ex) {
            ResponseUtil.setResponseToErrorResponse((HttpServletResponse) servletResponse, ex.getErrorCase());
        }
    }
}
