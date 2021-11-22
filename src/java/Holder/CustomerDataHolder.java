/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Holder;

import java.io.Serializable;

/**
 *
 * @author Neptune-Njinu
 */
public class CustomerDataHolder implements Serializable
{

    private String custName;
    private String custNo;
    private Long custId;

    

    /**
     * @return the custNo
     */
    public String getCustNo()
    {
        return custNo;
    }

    /**
     * @param custNo the custNo to set
     */
    public void setCustNo(String custNo)
    {
        this.custNo = custNo;
    }

    /**
     * @return the custId
     */
    public Long getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(Long custId)
    {
        this.custId = custId;
    }

    /**
     * @return the custName
     */
    public String getCustName()
    {
        return custName;
    }

    /**
     * @param custName the custName to set
     */
    public void setCustName(String custName)
    {
        this.custName = custName;
    }

}
