/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Holder.CRBDataHolder;
import Holder.CustomerDataHolder;
import Controller.BRCrypt;
import Controller.BRLogger;
import Controller.VNController;
import Helpers.VNValueKey;
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
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Neptune-Njinu
 */
public class TDClient implements Serializable
{

    private Connection dbConnection = null;
    private BRLogger logger = VNController.bRLogger;
    private PreparedStatement preparedStatement = null;
    private String errorMessage ="Error Occured! Please refer to Logs";
    private String utilityid;

    public TDClient()
    {
        VNController.initialize();
        setUtilityid(new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date()));
    }

    public void connectToDB()
    {
        try
        {
            System.err.println("connecting to db");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            dispose();
            setDbConnection(DriverManager.getConnection(VNController.CMSchemaJdbcUrl, VNController.CMSchemaName, VNController.CMSchemaPassword));
            System.err.println("connected to db");
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            VNController.bRLogger.logError("connectToDB()-ERROR", ex);
            setErrorMessage(ex.getMessage());
        }
    }

    public ResultSet executePrdStmtToResultSet(String query, Object... params)
    {
        return executePrdStmtToResultSet(query, true, params);
    }

    public ResultSet executePrdStmtToResultSet(String query, boolean retry, Object... params)
    {
        System.err.println(query + " params: [" + Arrays.toString(params) + "]");
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
                + "FROM " + VNController.CoreSchemaName + ".V_USER_ROLE A, " + VNController.CoreSchemaName + ".SYSPWD_HIST B "
                + "WHERE A.LOGIN_ID= ? AND B.SYSUSER_ID=A.USER_ID AND A.BUSINESS_ROLE_ID IN (" + VNController.AllowedLoginRoles + ") AND A.REC_ST=B.REC_ST AND B.PASSWD = ? AND B.REC_ST='A'", loginId, BRCrypt.encrypt(password)))
        {
            exists = checkIfPSExists(rs);
            setErrorMessage(exists.equals(Boolean.FALSE) ? "Invalid username or password. please retry" : "");
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
        return executePrpdStmtUpdate("INSERT INTO " + VNController.CMSchemaName + ".EI_CRB_STATUS(ID_NUMBER,ID_TYPE_CODE,ID_TYPE_DESC, DELINQUENCY_SUMMARY, DELINQUENCY_CODE_DESC, LOAN_AMOUNT, API_CODE, API_DESC, CUST_ID, CUST_NO, CUST_NAME, USER_ID) "
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
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_INDIVIDUAL_CONSUMER";
                break;
            case "NI":
                query = "SELECT * FROM  " + VNController.CoreSchemaName + ".V_NON_INDIVIDUAL_CONSUMER";
                break;
            case "CI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_COLLATERAL_INFORMATION";
                break;
            case "CA":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_CREDIT_APP_INFORMATION";
                break;
            case "GI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_GUARANTOR_INFORMATION";
                break;
            case "SI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_STAKEHOLDER";
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
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_INDIVIDUAL_CONSUMER";
                break;
            case "NI":
                query = "SELECT * FROM  " + VNController.CoreSchemaName + ".V_NON_INDIVIDUAL_CONSUMER";
                break;
            case "CI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_COLLATERAL_INFORMATION";
                break;
            case "CA":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_CREDIT_APP_INFORMATION";
                break;
            case "GI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_GUARANTOR_INFORMATION";
                break;
            case "SI":
                query = "SELECT * FROM " + VNController.CoreSchemaName + ".V_STAKEHOLDER";
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

    public TreeMap<String, String> queryCustomerType()
    {
        TreeMap<String, String> nValueKeys = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT CUST_TY_ID,CUST_CAT FROM " + VNController.CoreSchemaName + ".V_CUSTOMER_TYPE WHERE  REC_ST ='A' order by CUST_CAT ASC"))
        {
            while (rs.next())
            {
                nValueKeys.put(rs.getString("CUST_CAT"), rs.getString("CUST_TY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKeys;
    }

    public TreeMap<String, String> queryCustomerCategory()
    {
        TreeMap<String, String> nValueKeys = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".CUSTOMER_CAT_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKeys.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKeys;
    }

    public ArrayList<VNValueKey> queryTitle()
    {

        ArrayList<VNValueKey> nValueKeys = new ArrayList<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT TITLE_ID,TITLE_DESC FROM " + VNController.CoreSchemaName + ".TITLE_REF WHERE  REC_ST ='A' order by TITLE_DESC"))
        {
            while (rs.next())
            {
                VNValueKey nValueKey = new VNValueKey();
                nValueKey.setKey(rs.getString("TITLE_ID"));
                nValueKey.setValue(rs.getString("TITLE_DESC"));
                nValueKeys.add(nValueKey);
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKeys;
    }

    public TreeMap<String, String> queryTitles()
    {

        TreeMap<String, String> nValueKeys = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT TITLE_ID,TITLE_DESC FROM " + VNController.CoreSchemaName + ".TITLE_REF WHERE  REC_ST ='A' order by TITLE_DESC"))
        {
            while (rs.next())
            {
                nValueKeys.put(rs.getString("TITLE_DESC"), rs.getString("TITLE_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKeys;
    }

    public TreeMap<String, String> queryGender()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".GENDER_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));

            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryNationality()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT NATIONALITY_ID,NATIONALITY_DESC FROM " + VNController.CoreSchemaName + ".NATIONALITY_REF WHERE  REC_ST ='A' order by NATIONALITY_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("NATIONALITY_DESC"), rs.getString("NATIONALITY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryIndustry()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT INDUSTRY_ID,INDUSTRY_DESC FROM " + VNController.CoreSchemaName + ".INDUSTRY_REF WHERE  REC_ST ='A' order by INDUSTRY_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("INDUSTRY_DESC"), rs.getString("INDUSTRY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryStatus()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".ACCOUNT_STATUS_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryMaritalStatus()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".MARITAL_STATUS_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryLocale()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".LOCALE_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryCollaborationLevel()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT REF_KEY,REF_DESC FROM " + VNController.CoreSchemaName + ".COLLABORATION_LEVEL_REF order by REF_DESC"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("REF_DESC"), rs.getString("REF_KEY"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryCountry()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT CNTRY_CD,CNTRY_NM FROM " + VNController.CoreSchemaName + ".COUNTRY WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("CNTRY_NM"), rs.getString("CNTRY_CD"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryTaxGroup()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT TAX_GRP_ID,TAX_GRP_DESC FROM " + VNController.CoreSchemaName + ".CUSTOMER_TAX_GROUP_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("TAX_GRP_DESC"), rs.getString("TAX_GRP_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryReligion()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT RELIGION_ID,RELIGION_DESC FROM " + VNController.CoreSchemaName + ".RELIGION_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("RELIGION_DESC"), rs.getString("RELIGION_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryEducation()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT EDUCATION_ID,EDUCATION_DESC FROM " + VNController.CoreSchemaName + ".EDUCATION_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("EDUCATION_DESC"), rs.getString("EDUCATION_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryProfessionQualification()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT PROF_QUAL_ID,PROF_QUAL_DESC FROM " + VNController.CoreSchemaName + ".PROF_QUAL_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("PROF_QUAL_DESC"), rs.getString("PROF_QUAL_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryPropertyType()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT PROPERTY_TY_ID,PROPERTY_DESC FROM " + VNController.CoreSchemaName + ".PROPERTY_TYPE_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("PROPERTY_DESC"), rs.getString("PROPERTY_TY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryAddressType()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT ADDR_TY_ID,ADDR_DESC FROM " + VNController.CoreSchemaName + ".ADDRESS_TYPE_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("ADDR_DESC"), rs.getString("ADDR_TY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryCurrency()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT CRNCY_ID,CRNCY_CD_ISO FROM " + VNController.CoreSchemaName + ".CURRENCY WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("CRNCY_CD_ISO"), rs.getString("CRNCY_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryRisk()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT RISK_ID,RISK_DESC FROM " + VNController.CoreSchemaName + ".RISK_REF WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("RISK_DESC"), rs.getString("RISK_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryCreditAgency()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT CR_RATING_AGENCIES_ID,CR_RATING_AGENCIES_DESC FROM " + VNController.CoreSchemaName + ".CREDIT_RATING_AGENCIES WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("CR_RATING_AGENCIES_DESC"), rs.getString("CR_RATING_AGENCIES_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryBusinessUnit()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT BU_ID,BU_NM FROM " + VNController.CoreSchemaName + ".BUSINESS_UNIT WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("BU_NM"), rs.getString("BU_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryResponsibilityCenter()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT RESPONSIBLE_CENTRE_ID,CENTRE_DESC FROM " + VNController.CoreSchemaName + ".RESPONSIBILITY_CENTRE WHERE  REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("CENTRE_DESC"), rs.getString("RESPONSIBLE_CENTRE_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryUser(Long buId)
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT SYSUSER_ID,LOGIN_ID||'~'||LAST_NM||' '||FIRST_NM AS LOGIN_ID FROM " + VNController.CoreSchemaName + ".SYSUSER WHERE  REC_ST ='A' and MAIN_BRANCH_ID =" + buId + " order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("LOGIN_ID"), rs.getString("SYSUSER_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryOpeningReason()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT RSN_ID,RSN_DESC FROM " + VNController.CoreSchemaName + ".REASON_REF WHERE  RSN_CAT_CD = 'AC' and REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("RSN_DESC"), rs.getString("RSN_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> querysourceOfFund()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT SRC_OF_FUNDS_ID,SRC_OF_FUNDS_DESC FROM " + VNController.CoreSchemaName + ".SOURCE_OF_FUNDS_REF WHERE REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("SRC_OF_FUNDS_DESC"), rs.getString("SRC_OF_FUNDS_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
    }

    public TreeMap<String, String> queryMarketingCampaign()
    {
        TreeMap<String, String> nValueKey = new TreeMap<>();
        try (ResultSet rs = executePrdStmtToResultSet("SELECT CAMPAIGN_ID,CAMPAIGN_DESC FROM " + VNController.CoreSchemaName + ".MKT_CAMPAIGN_REF WHERE REC_ST ='A' order by 2"))
        {
            while (rs.next())
            {
                nValueKey.put(rs.getString("CAMPAIGN_DESC"), rs.getString("CAMPAIGN_ID"));
            }
        }
        catch (Exception ex)
        {
            getLogger().logEvent(ex);
        }
        return nValueKey;
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
