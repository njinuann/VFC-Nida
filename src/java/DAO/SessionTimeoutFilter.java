/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author neptune
 */
public class SessionTimeoutFilter implements Filter
{

//    private String timeoutPage = "faces/login.xhtml";
    private String timeoutPage = "login.xhtml";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        //We will not process anything in init method so we can omit this part too.
    }

    //Triggers for every faces-servlet request
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        // System.err.println("Passing in filter");
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))
        {
            //System.err.println("Passing in filter request response");
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // is session expire control required for this request?
            if (isSessionControlRequiredForThisResource(httpServletRequest))
            {
                // is session invalid?
                if (isSessionInvalid(httpServletRequest))
                {
                    String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage();
                    //System.out.println("Session is invalid! redirecting to timeoutpage : " + timeoutUrl);
                    httpServletResponse.sendRedirect(timeoutUrl);
                    return;
                }
            }

            //System.err.println("request.getRequestURI() -> " + ((HttpServletRequest) request).getRequestURI());
            //System.err.println("request.getContextPath() -> " + ((HttpServletRequest) request).getContextPath());
            if (((HttpServletRequest) request).getRequestURI().contains("crbReportingPortal.xhtml"))
            {
                //System.err.println("username in session " + httpServletRequest.getSession().getAttribute("username"));
                if (httpServletRequest.getSession().getAttribute("username") == null)
                {
                    String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage();
                    //System.out.println("Session is invalid! redirecting to timeoutpage : " + timeoutUrl);
                    httpServletResponse.sendRedirect(timeoutUrl);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest)
    {

        String requestPath = httpServletRequest.getRequestURI();

        boolean controlRequired = !StringUtils.contains(requestPath, getTimeoutPage());

        return controlRequired;

    }

    //Check whether the session is  valid
    private boolean isSessionInvalid(HttpServletRequest httpServletRequest)
    {

        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();

        return sessionInValid;

    }

    @Override
    public void destroy()
    {

    }

    public String getTimeoutPage()
    {
        //Return timeout page to which we mentioned ablove   
        return timeoutPage;
    }

    public void setTimeoutPage(String timeoutPage)

    {
        //Set timeout page to which we mentioned ablove    
        this.timeoutPage = timeoutPage;
    }
}
