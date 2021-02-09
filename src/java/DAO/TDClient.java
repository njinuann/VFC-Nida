/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import CRB.CRBDataHolder;
import CRB.CustomerDataHolder;
import Controller.BRCrypt;
import Controller.BRLogger;
import Controller.CRBRController;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Neptune-Njinu
 */
public class TDClient implements Serializable
{

    private Connection dbConnection = null;
    private BRLogger logger = CRBRController.bRLogger;
    private PreparedStatement preparedStatement = null;
    private String errorMessage;
    private String utilityid;

    public TDClient()
    {
        CRBRController.initialize();
        setUtilityid(new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date()));
    }

    public void connectToDB()
    {
        try
        {
            System.err.println("connecting to db");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            dispose();
            setDbConnection(DriverManager.getConnection(CRBRController.CMSchemaJdbcUrl, CRBRController.CMSchemaName, CRBRController.CMSchemaPassword));
            System.err.println("connected to db");
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            CRBRController.bRLogger.logError("connectToDB()-ERROR", ex);
            setErrorMessage(ex.getMessage());
        }
    }

    public ResultSet executePrdStmtToResultSet(String query, Object... params)
    {
        return executePrdStmtToResultSet(query, true, params);
    }

    public ResultSet executePrdStmtToResultSet(String query, boolean retry, Object... params)
    {
        try
        {
            getLogger().logDebug(query + " params: [" + Arrays.toString(params) + "]");
            if (getDbConnection() != null ? getDbConnection().isClosed() : true)
            {
                connectToDB();
            }
            if (getDbConnection() != null)
            {

                initialisePreparedStatement(query);
                for (int i = 0; i < params.length; i++)
                {
                    getPreparedStatement().setObject((i + 1), params[i]);
                }
                return getPreparedStatement().executeQuery();
            }
        }
        catch (Exception ex)
        {

            if (String.valueOf(ex.getMessage()).contains("ORA-01000"))
            {
                dispose();
                if (retry)
                {
                    executePrdStmtToResultSet(query, false, params);
                }
            }
            else
            {
                getLogger().logEvent(ex);
            }
            /// getLogger().logEvent(ex);
        }
        return null;
    }

    public PreparedStatement initialisePreparedStatement(String query)
    {
        try
        {
            setPreparedStatement(getDbConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY));
        }
        catch (SQLException ex)
        {
            getLogger().logEvent(ex);
        }
        return null;
    }

    public boolean executePrpdStmtUpdate(String update, boolean retry, Object... params)
    {
        try
        {
            getLogger().logEvent(update + " params: [" + Arrays.toString(params) + "]");
            if (getDbConnection() != null ? getDbConnection().isClosed() : true)
            {
                connectToDB();
            }
            if (getDbConnection() != null)
            {
                initialisePreparedStatement(update);
                for (int i = 0; i < params.length; i++)
                {
                    getPreparedStatement().setObject((i + 1), params[i]);
                }

                getPreparedStatement().executeUpdate();
            }
            return true;
        }
        catch (Exception ex)
        {
            if (String.valueOf(ex.getMessage()).contains("ORA-01000"))
            {
                dispose();
                if (retry)
                {
                    executePrpdStmtUpdate(update, false, params);
                }
            }
            else
            {
                getLogger().logEvent("executePrpdStmtUpdate", ex);
            }

        }
        return false;
    }

    public boolean checkIfPSExists(ResultSet rs)
    {
        boolean exists = false;
        try
        {
            if (rs.next())
            {
                exists = true;
            }
        }
        catch (SQLException ex)
        {
            setErrorMessage(ex.getMessage());
            getLogger().logEvent(ex);
        }
        return exists;
    }

    private void closePreparedStatement(PreparedStatement preparedStatement)
    {
        try
        {
            if (preparedStatement != null)
            {
                preparedStatement.close();
                preparedStatement = null;
            }
        }
        catch (Exception ex)
        {
            preparedStatement = null;
        }
    }

    public void dispose()
    {
        try
        {
            closePreparedStatement(getPreparedStatement());
            if (getDbConnection() != null)
            {
                getDbConnection().close();
            }
        }
        catch (Exception ex)
        {
            ex = null;
        }
        setDbConnection(null);
    }

    public boolean loginAdminUser(String loginId, String password)
    {
        Boolean exists = false;

        try (ResultSet rs = executePrdStmtToResultSet("SELECT DISTINCT FIRST_NM || ' ' || LAST_NM AS NAME "
                + "FROM " + CRBRController.CoreSchemaName + ".V_USER_ROLE A, " + CRBRController.CoreSchemaName + ".SYSPWD_HIST B "
                + "WHERE A.LOGIN_ID= ? AND B.SYSUSER_ID=A.USER_ID AND A.BUSINESS_ROLE_ID IN (" + CRBRController.AllowedLoginRoles + ") AND A.REC_ST=B.REC_ST AND B.PASSWD = ? AND B.REC_ST='A'", loginId, BRCrypt.encrypt(password)))
        {
            exists = checkIfPSExists(rs);
            setErrorMessage(exists.equals(Boolean.FALSE)?"Invalid username or password. please retry":""); 
        }
        catch (Exception ex)
        {
            System.err.println(ex);
            setErrorMessage(ex.getMessage());
        }
         
        System.err.println("HEHEHEHEHE " + exists);
        return exists;
    }

    public boolean saveCRBRecord(CRBDataHolder cRBDataHolder, String userId)
    {
        CustomerDataHolder customerDataHolder = querycustomerById(cRBDataHolder.getIdentity_number().trim());
        return executePrpdStmtUpdate("INSERT INTO " + CRBRController.CMSchemaName + ".EI_CRB_STATUS(ID_NUMBER,ID_TYPE_CODE,ID_TYPE_DESC, DELINQUENCY_SUMMARY, DELINQUENCY_CODE_DESC, LOAN_AMOUNT, API_CODE, API_DESC, CUST_ID, CUST_NO, CUST_NAME, USER_ID) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)", true, cRBDataHolder.getIdentity_number(), cRBDataHolder.getIdentity_type(), cRBDataHolder.getId_type_desc(), cRBDataHolder.getDelinquency_summary(), cRBDataHolder.getDelinquency_code() + "[" + cRBDataHolder.getDelinquency_Desc() + "]", BigDecimal.valueOf(cRBDataHolder.getLoan_amount()),
                cRBDataHolder.getApi_code(), cRBDataHolder.getApi_code_description(), customerDataHolder.getCustId(), customerDataHolder.getCustNo(), customerDataHolder.getCustName(), userId);
    }

    public CustomerDataHolder querycustomerById(String idNumber)
    {
        CustomerDataHolder customerDataHolder = new CustomerDataHolder();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT B.CUST_NM,B.CUST_ID,B.CUST_NO FROM SHOPPERSLIVE.CUSTOMER_CONTACT_MODE A,SHOPPERSLIVE.CUSTOMER B WHERE A.CUST_ID = B.CUST_ID AND CONTACT  =? ", idNumber))
        {
            if (rs.next())
            {

                customerDataHolder.setCustName(rs.getString("CUST_NM"));
                customerDataHolder.setCustId(rs.getLong("CUST_ID"));
                customerDataHolder.setCustNo(rs.getString("CUST_NO"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return customerDataHolder;
    }

    public ResultSet getCRBData(String reportType)
    {
        String query = "";
        switch (reportType)
        {
            case "IC":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_INDIVIDUAL_CONSUMER";
                break;
            case "NI":
                query = "SELECT * FROM  " + CRBRController.CoreSchemaName + ".V_NON_INDIVIDUAL_CONSUMER";
                break;
            case "CI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_COLLATERAL_INFORMATION";
                break;
            case "CA":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_CREDIT_APP_INFORMATION";
                break;
            case "GI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_GUARANTOR_INFORMATION";
                break;
            case "SI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_STAKEHOLDER";
                break;
            default:
                break;
        }
        System.err.println("" + query);

        return executePrdStmtToResultSet(query);
    }

    public ArrayList getCRBData2(String reportType)
    {
        String query = "";
        // ArrayList crbData = new ArrayList<>();
        ArrayList<String> crbData = null;
        switch (reportType)
        {
            case "IC":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_INDIVIDUAL_CONSUMER";
                break;
            case "NI":
                query = "SELECT * FROM  " + CRBRController.CoreSchemaName + ".V_NON_INDIVIDUAL_CONSUMER";
                break;
            case "CI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_COLLATERAL_INFORMATION";
                break;
            case "CA":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_CREDIT_APP_INFORMATION";
                break;
            case "GI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_GUARANTOR_INFORMATION";
                break;
            case "SI":
                query = "SELECT * FROM " + CRBRController.CoreSchemaName + ".V_STAKEHOLDER";
                break;
            default:
                break;
        }
        System.err.println("" + query);

        try (ResultSet results = executePrdStmtToResultSet(query))
        {
            ResultSetMetaData rsmd = results.getMetaData();
            int columnCount = rsmd.getColumnCount();

            crbData = new ArrayList<>(columnCount);
            while (results.next())
            {
                int i = 1;
                while (i <= columnCount)
                {
                    crbData.add(results.getString(i++));
                }
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(TDClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return crbData;
    }

    /**
     * @return the dbConnection
     */
    public Connection getDbConnection()
    {
        return dbConnection;
    }

    /**
     * @param dbConnection the dbConnection to set
     */
    public void setDbConnection(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    /**
     * @return the logger
     */
    public BRLogger getLogger()
    {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(BRLogger logger)
    {
        this.logger = logger;
    }

    /**
     * @return the preparedStatement
     */
    public PreparedStatement getPreparedStatement()
    {
        return preparedStatement;
    }

    /**
     * @param preparedStatement the preparedStatement to set
     */
    public void setPreparedStatement(PreparedStatement preparedStatement)
    {
        this.preparedStatement = preparedStatement;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the utilityid
     */
    public String getUtilityid()
    {
        return utilityid;
    }

    /**
     * @param utilityid the utilityid to set
     */
    public void setUtilityid(String utilityid)
    {
        this.utilityid = utilityid;
    }

}
