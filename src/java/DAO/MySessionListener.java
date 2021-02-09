/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author neptune
 */
public class MySessionListener implements HttpSessionListener {

    public MySessionListener()
    {

    }

    @Override
    public void sessionCreated(HttpSessionEvent event)
    {

        System.out.println("Current Session created : "
                + event.getSession().getId() + " at " + new Date());

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {

// get the destroying session...
        HttpSession session = event.getSession();

        System.out.println("Current Session destroyed :"
                + session.getId() + " Logging out user...");

// Only if needed
        try {

            prepareLogoutInfoAndLogoutActiveUser(session);

        } catch (Exception e) {
            System.out.println("Error while logging out at session destroyed : "
                    + e.getMessage());

        }

    }

    /**
     * Clean your logout operations.
     */
    public void prepareLogoutInfoAndLogoutActiveUser(HttpSession httpSession)
    {
        // Only if needed  
        httpSession.invalidate();
    }
}
