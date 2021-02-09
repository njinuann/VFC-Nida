/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author NJINU
 */
@ManagedBean(name = "CrbReportType")
@ViewScoped
public class CrbReportType implements  Serializable
{

    private Map<String, Map<String, String>> data = new HashMap<>();
    private String statusId;
    private String statusDesc;
    private Map<String, String> statIds;
    private Map<String, String> statDesc;

    @PostConstruct
    public void init()
    {
        statIds = new HashMap<>();
        statIds.put("Individual Consumer Report", "IC");
        statIds.put("Non Individual Consumer", "NI");
    }

    public void onStatusIdChange()
    {
        if (getStatusId() != null && !statusId.equals(""))
        {
            setStatDesc(getData().get(getStatusDesc()));
        }
        else
        {
            setStatDesc(new HashMap<String, String>());
        }
    }

    public void displayLocation()
    {
        FacesMessage msg;
        if (getStatusDesc() != null && getStatusId() != null)
        {
            msg = new FacesMessage("Selected", getStatusDesc() + " of " + getStatusId());
        }
        else
        {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", " not selected.");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the data
     */
    public Map<String, Map<String, String>> getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<String, Map<String, String>> data)
    {
        this.data = data;
    }

    /**
     * @return the statusId
     */
    public String getStatusId()
    {
        return statusId;
    }

    /**
     * @param statusId the statusId to set
     */
    public void setStatusId(String statusId)
    {
        this.statusId = statusId;
    }

    /**
     * @return the statusDesc
     */
    public String getStatusDesc()
    {
        return statusDesc;
    }

    /**
     * @param statusDesc the statusDesc to set
     */
    public void setStatusDesc(String statusDesc)
    {
        this.statusDesc = statusDesc;
    }

    /**
     * @return the statIds
     */
    public Map<String, String> getStatIds()
    {
        return statIds;
    }

    /**
     * @param statIds the statIds to set
     */
    public void setStatIds(Map<String, String> statIds)
    {
        this.statIds = statIds;
    }

    /**
     * @return the statDesc
     */
    public Map<String, String> getStatDesc()
    {
        return statDesc;
    }

    /**
     * @param statDesc the statDesc to set
     */
    public void setStatDesc(Map<String, String> statDesc)
    {
        this.statDesc = statDesc;
    }
}
