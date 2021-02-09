/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRB;

import java.io.Serializable;

/**
 *
 * @author Neptune-Njinu
 */
public class CRBDataHolder implements Serializable
{

    private String api_code;
    private String api_code_description;
    private String delinquency_code;
    private String delinquency_summary;
    private boolean has_error = false;
    private String identity_number;
    private String identity_type;
    private Long loan_amount = 0L;
    private String trx_id;
    private String delinquency_Desc = "";
    private String is_listed = "";
    private String id_type_desc = "";

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
