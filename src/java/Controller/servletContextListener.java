/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

/**
 *
 * @author neptune
 */
import DAO.DBManager;
import DAO.TDClient;
import java.sql.ResultSet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class servletContextListener implements ServletContextListener
{

    @Override
    //run this when tomcat is being stopped
    public void contextDestroyed(ServletContextEvent sce)
    {
        sce.getServletContext().removeAttribute("bankName");
        DBManager.disposeProcessors();
        System.out.println("Tomcat Stopped");
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        //  String getBankName = CRBRController.bankName;
        //    sce.getServletContext().setAttribute("bankName", getBankName);
        System.out.println("Tomcat Started");       
        CRBRController.initialize();
        DBManager.initialise();
        TDClient dbUtility = DBManager.fetchDBUtility();
        String getBankName = "SELECT DISPLAY_VALUE FROM " + CRBRController.CoreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD = ? ";
        try
        {
            try (ResultSet rs = dbUtility.executePrdStmtToResultSet(getBankName, "S04"))
            {
                if (rs.next())
                {
                    sce.getServletContext().setAttribute("bankName", rs.getString("DISPLAY_VALUE"));
                }
            }
        }
        catch (Exception m)
        {
            CRBRController.bRLogger.logEvent("[contextInitialized]", m);
        }
        DBManager.releaseDBUtility(dbUtility);
    }
}
