/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import CRB.CRBDataHolder;
import CRB.CRBProcessor;
import DAO.TDClient;
import Controller.CRBRController;
import DAO.SessionUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.sql.ResultSet;
import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Neptune-Njinu
 */
@ManagedBean(name = "CRBDelinqSearch")
@ViewScoped
public class CRBDelinqSearch implements Serializable
{

    private TDClient tDClient = new TDClient();
    private static List<String> columns = new ArrayList<String>();
    private static ArrayList test = new ArrayList();
    private String idNumber = "";
    private String amount = "";
    private ResultSet globalResultSet;
    private String api_code = "";
    private String api_code_description = "";
    private String delinquency_code = "";
    private String delinquency_Desc = "";
    private String is_listed = "";
    private String id_type_desc = "";
    private String delinquency_summary = "";
    private boolean has_error = false;
    private String identity_number = "";
    private String identity_type = "";
    private Long loan_amount = 0L;
    private String trx_id = "";

    public CRBDelinqSearch()
    {

    }

    public String loadCrbDelinquencyData()
    {

        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        // System.err.println("<<<<<<<<<<save vendor record >>>>>>>>>>" + SVController.getWebServiceObjectString(vrecord));
        if (loadCrbDelinqData())
        {
            msg = "Loaded";

        }
        else
        {
            msg = "Error occured when Saving the record";
            severity = FacesMessage.SEVERITY_FATAL;
        }
        // getData();
        return nextpage;
    }

    public boolean loadCrbDelinqData()
    {
        HttpSession session = SessionUtils.getSession();
        CRBDataHolder cRBDataHolder = new CRBDataHolder();
        boolean loaded = false;

        try
        {

            Object obj = JSONValue.parse(new CRBProcessor().CRBQueryResponse(getIdNumber(), Integer.parseInt(getAmount())));
            JSONObject jsonObject = (JSONObject) obj;
            cRBDataHolder.setApi_code((String) jsonObject.get("api_code"));
            cRBDataHolder.setApi_code_description((String) jsonObject.get("api_code_description"));
            cRBDataHolder.setDelinquency_code((String) jsonObject.get("delinquency_code"));
            cRBDataHolder.setDelinquency_summary((String) jsonObject.get("delinquency_summary"));
            cRBDataHolder.setHas_error((boolean) jsonObject.get("has_error"));
            cRBDataHolder.setIdentity_number((String) jsonObject.get("identity_number"));
            cRBDataHolder.setIdentity_type((String) jsonObject.get("identity_type"));
            cRBDataHolder.setLoan_amount((Long) jsonObject.get("loan_amount"));
            cRBDataHolder.setTrx_id((String) jsonObject.get("trx_id"));
            cRBDataHolder.setDelinquency_Desc(getdelinquencyStatusDesc(cRBDataHolder.getDelinquency_code()));
            cRBDataHolder.setId_type_desc(getIdTypeDec(cRBDataHolder.getIdentity_type()));
            cRBDataHolder.setIs_listed("004".equals(cRBDataHolder.getDelinquency_code()) ? "Listed" : "Not Listed");

            setApi_code(cRBDataHolder.getApi_code());
            setApi_code_description(cRBDataHolder.getApi_code_description());
            setDelinquency_code(cRBDataHolder.getDelinquency_code());
            setDelinquency_summary(cRBDataHolder.getDelinquency_summary());
            setHas_error(cRBDataHolder.isHas_error());
            setIdentity_number(cRBDataHolder.getIdentity_number());
            setIdentity_type(cRBDataHolder.getIdentity_type());
            setLoan_amount(cRBDataHolder.getLoan_amount());
            setTrx_id(cRBDataHolder.getTrx_id());
            setDelinquency_Desc(cRBDataHolder.getDelinquency_Desc());
            setId_type_desc(cRBDataHolder.getId_type_desc());
            setIs_listed(cRBDataHolder.getIs_listed());

            if (!cRBDataHolder.getIdentity_number().isEmpty())
            {
                gettDClient().saveCRBRecord(cRBDataHolder, session.getAttribute("userId").toString());
            }

        }
        catch (Exception ex)
        {
            CRBRController.bRLogger.logError("setVal", ex);
        }
        return loaded;
    }

    private String getdelinquencyStatusDesc(String delinqCode)
    {
        String statusDesc = "";
        switch (delinqCode)
        {
            case "001":
                statusDesc = "ID number has not been found in the database";
                break;
            case "002":
                statusDesc = "ID number has been found but it has no credit information";
                break;
            case "003":
                statusDesc = "ID Number has credit info with No NPA";
                break;
            case "004":
                statusDesc = "Currently delinquent True";
                break;
            case "005":
                statusDesc = "ID Number has credit information with at least one Historical NPA";
                break;
            default:
                break;
        }
        return statusDesc;
    }

    private String getIdTypeDec(String delinqCode)
    {
        String statusDesc = "";
        switch (delinqCode)
        {
            case "001":
                statusDesc = "National ID";
                break;
            case "002":
                statusDesc = "Passport";
                break;
            case "003":
                statusDesc = "Service ID";
                break;
            case "004":
                statusDesc = "Alien Registration";
                break;
            case "005":
                statusDesc = "Company/Business Registration";
                break;
            default:
                break;
        }
        return statusDesc;
    }

    /**
     * @return the tDClient
     */
    public TDClient gettDClient()
    {
        return tDClient;
    }

    /**
     * @param tDClient the tDClient to set
     */
    public void settDClient(TDClient tDClient)
    {
        this.tDClient = tDClient;
    }

    /*getter and setter for above variables*/
    public List<String> getColumns()
    {
        return columns;
    }

    public void setColumns(List<String> columns)
    {
        this.columns = columns;
    }

    public ArrayList getTest()
    {
        return test;
    }

    public void setTest(ArrayList test)
    {
        this.test = test;
    }

    /*end of getter and setter*/
    /**
     * @return the globalResultSet
     */
    public ResultSet getGlobalResultSet()
    {
        return globalResultSet;
    }

    /**
     * @param globalResultSet the globalResultSet to set
     */
    public void setGlobalResultSet(ResultSet globalResultSet)
    {
        this.globalResultSet = globalResultSet;
    }

    /**
     * @return the api_code
     */
    public String getApi_code()
    {
        return api_code;
    }

    /**
     * @param api_code the api_code to set
     */
    public void setApi_code(String api_code)
    {
        this.api_code = api_code;
    }

    /**
     * @return the api_code_description
     */
    public String getApi_code_description()
    {
        return api_code_description;
    }

    /**
     * @param api_code_description the api_code_description to set
     */
    public void setApi_code_description(String api_code_description)
    {
        this.api_code_description = api_code_description;
    }

    /**
     * @return the delinquency_code
     */
    public String getDelinquency_code()
    {
        return delinquency_code;
    }

    /**
     * @param delinquency_code the delinquency_code to set
     */
    public void setDelinquency_code(String delinquency_code)
    {
        this.delinquency_code = delinquency_code;
    }

    /**
     * @return the delinquency_summary
     */
    public String getDelinquency_summary()
    {
        return delinquency_summary;
    }

    /**
     * @param delinquency_summary the delinquency_summary to set
     */
    public void setDelinquency_summary(String delinquency_summary)
    {
        this.delinquency_summary = delinquency_summary;
    }

    /**
     * @return the identity_number
     */
    public String getIdentity_number()
    {
        return identity_number;
    }

    /**
     * @param identity_number the identity_number to set
     */
    public void setIdentity_number(String identity_number)
    {
        this.identity_number = identity_number;
    }

    /**
     * @return the identity_type
     */
    public String getIdentity_type()
    {
        return identity_type;
    }

    /**
     * @param identity_type the identity_type to set
     */
    public void setIdentity_type(String identity_type)
    {
        this.identity_type = identity_type;
    }

    /**
     * @return the trx_id
     */
    public String getTrx_id()
    {
        return trx_id;
    }

    /**
     * @param trx_id the trx_id to set
     */
    public void setTrx_id(String trx_id)
    {
        this.trx_id = trx_id;
    }

    /**
     * @return the idNumber
     */
    public String getIdNumber()
    {
        return idNumber;
    }

    /**
     * @param idNumber the idNumber to set
     */
    public void setIdNumber(String idNumber)
    {
        this.idNumber = idNumber;
    }

    /**
     * @return the amount
     */
    public String getAmount()
    {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    /**
     * @return the has_error
     */
    public boolean isHas_error()
    {
        return has_error;
    }

    /**
     * @param has_error the has_error to set
     */
    public void setHas_error(boolean has_error)
    {
        this.has_error = has_error;
    }

    /**
     * @return the loan_amount
     */
    public Long getLoan_amount()
    {
        return loan_amount;
    }

    /**
     * @param loan_amount the loan_amount to set
     */
    public void setLoan_amount(Long loan_amount)
    {
        this.loan_amount = loan_amount;
    }

    /**
     * @return the delinquency_Desc
     */
    public String getDelinquency_Desc()
    {
        return delinquency_Desc;
    }

    /**
     * @param delinquency_Desc the delinquency_Desc to set
     */
    public void setDelinquency_Desc(String delinquency_Desc)
    {
        this.delinquency_Desc = delinquency_Desc;
    }

    /**
     * @return the is_listed
     */
    public String getIs_listed()
    {
        return is_listed;
    }

    /**
     * @param is_listed the is_listed to set
     */
    public void setIs_listed(String is_listed)
    {
        this.is_listed = is_listed;
    }

    /**
     * @return the id_type_desc
     */
    public String getId_type_desc()
    {
        return id_type_desc;
    }

    /**
     * @param id_type_desc the id_type_desc to set
     */
    public void setId_type_desc(String id_type_desc)
    {
        this.id_type_desc = id_type_desc;
    }

}
