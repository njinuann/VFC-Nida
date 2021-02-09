/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import DAO.SessionUtils;
import DAO.TDClient;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author neptune
 */
@ManagedBean(name = "LoginBean")
@SessionScoped

public class LoginBean implements Serializable
{

    private String username;
    private String password;
    private TDClient dClient = new TDClient();

    public LoginBean()
    {

    }

    public String login()
    {
        //  new ESProcessor().start();
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "";
        if (getUsername().equals(""))
        {
            msg = "Username is Empty";
        }
        else if (getPassword().equals(""))
        {
            msg = "Password is Empty";
        }
        else
        {
            if (getdClient().loginAdminUser(getUsername().toUpperCase(), getPassword()))
            {
                session.setAttribute("username", getUsername());
                session.setAttribute("userId", getUsername());
                session.setAttribute("auditLogId", getUsername());
                session.setAttribute("currentDate", new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
                title = "Welcome";
                msg = "Username: " + getUsername();
                nextpage = "crbReportingPortal?faces-redirect=true";

            }
            else
            {
                msg = "Access Restricted for Credentials \n\n" + getdClient().getErrorMessage().toUpperCase();
                //  nextpage = "login?faces-redirect=true";

            }
        }

        return nextpage;
    }

    public void showMessage(String title, String msg)
    {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, msg));
    }

    public String logout()
    {
        return "login?faces-redirect=true";
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the dClient
     */
    public TDClient getdClient()
    {
        return dClient;
    }

    /**
     * @param dClient the dClient to set
     */
    public void setdClient(TDClient dClient)
    {
        this.dClient = dClient;
    }

}
