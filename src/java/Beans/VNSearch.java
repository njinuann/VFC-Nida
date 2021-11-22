/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import Controller.BRLogger;
import DAO.TDClient;
import Controller.VNController;
import DAO.SessionUtils;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.shaded.json.JSONException;

/**
 *
 * @author Neptune-Njinu
 */
@ManagedBean(name = "VNSearch")
@SessionScoped
public class VNSearch implements Serializable
{

    HttpSession session = SessionUtils.getSession();
    private TDClient tDClient = new TDClient();
    private String nid;
    private String custImage;
    private String custSignature;
    private String console;
    private String age;
    private String employedRadio = "";
    private String residentRadio = "";
    private String idNumber = "";
    private String title = "";
    private String custType = "";
    private String custCategory = "";
    private String gender = "";
    private String addressCity = "";
    private Long addressCountryId;
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String addressLine3 = "";
    private String addressLine4 = "";
    private Long propertyTypeId;
    private String addressState = "";
    private Long addressTypeId;
    private Long businessUnitCodeId;
    private String contactModeDesc = "";
    private Long contactModeId;
    private Long countryOfBirthId;
    private Long countryOfResidenceId;
    private String countryOfResidence;
    private Long countryOfRiskId;
    private Long creditRatingAgencyId;
    private Long custAddressId;
    private Long custContactModeId;
    private Long custIdentificationId;
    private String custIdentificationNumber = "";
    private Long custIdentificationRefCode;
    private String customerCategory = "";
    private Long customerSegmentId;
    private Long customerType;
    private String custShortName = "";
    private Long educationalQualificationId;
    private Boolean employmentFlag;
    private Long ethnicGroupId;
    private String fathersName = "";
    private String firstName = "";
    private String grandfathersName = "";
    private String lastName = "";
    private String locale = "";
    private String maidenName = "";
    private Long mainBusinessUnitId;
    private String mainBusinessUnit = "";
    private String maritalStatus = "";
    private Long marketingCampaignId;
    private String middleName = "";
    private String mothersMaidenName = "";
    private Long nationalityId;
    private Long noOfDependents;
    private Long openingReasonId;
    private Long operationCurrencyId;
    private String organisationName = "";
    private String postalCode = "";
    private String preferredName = "";
    private Boolean primaryAddress;
    private Long primaryRelationshipOfficerId;
    private Long privacyLevelId;
    private Long professionalQualificationId;
    private Long professionId;
    private String registrationNumber = "";
    private Long religionId;
    private Boolean residentFlag;
    private String riskCode = "";
    private Long serviceLevelId;
    private String spouseName = "";
    private String status = "";
    private String strDate = "";
    private String strDateOfBirth = "";
    private String strFromDate = "";
    private String strIssueDate = "";
    private String strRegistrationDate = "";
    private String strToDate = "";
    private Long supplementaryRelationshipOfficerId;
    private String swiftAddress = "";
    private Long taxGroupId;
    private Long titleId;
    private Boolean verifiedFlag;
    private String xapiServiceCode = "";
    private Long zipCode;
    private String accountNumber = "";
    private String customerNumber = "";
    private String customerName = "";
    private Long customerId;
    private Long industryId;
    private String industry;
    private Long taxStatusId;
    private String nationalIdNumber = "";
    private boolean wildCardFlag;
    private String collateralRef = "";
    private String collateralStatus = "";
    private String collateralType = "";
    private String contactModeCategory = "";
    private String custRelshipType = "";
    private String mainBUNumber = "";
    private String referredBy = "";
    private String taxIdentificationNo = "";
    private String currencyCode = "";
    private Long sourceOfFunds;
    private String district = "";
    private String state = "";
    private String village = "";
    private static BRLogger bRLogger = new BRLogger("vnsearch", "logs");
    private VNResolver vNResolver = new VNResolver();
    private VNCustomer vNCustomer = new VNCustomer();
    private List<VNCustomer> customers;
    private int custId;
    private String custNo;
    private String custName;
    

    @PostConstruct
    public void init()
    {
        setEmployedRadio("No");
        setResidentRadio("Yes");
        setStatus("A");
    }
//    public VNSearch()
//    {
//        setEmployedRadio("No");
//        setResidentRadio("Yes");
//    }

    public String loadCrbDelinquencyData()
    {
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        // System.err.println("<<<<<<<<<<save vendor record >>>>>>>>>>" + SVController.getWebServiceObjectString(vrecord));
        if ("Y".equals("N"))
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

    public static boolean isBlank(Object object)
    {
        return object == null || "".equals(String.valueOf(object).trim()) || "null".equals(String.valueOf(object).trim()) || String.valueOf(object).trim().toLowerCase().contains("---select");
    }

    public String searchNid()
    {
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        // System.err.println("<<<<<<<<<<save vendor record >>>>>>>>>>" + SVController.getWebServiceObjectString(vrecord));
        if ("Y".equals("N"))
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

    public String queryNida()
    {
        int age = 0;
        String nidaURL = "https://197.243.51.186:7005/NIDA/getcitizenPS";
        String number = "1192770003989043";
        String keyphrase = "9ANMY!Jm7XK2x3Zyz@bPHRYM!40CipqD29X8$6PxMK1FP_mp@$cYt3jtTaR!jfcTGfaEf6BJxUd_5s$@SMHnnHk_0Dq$pUoGGh-K_YjeLKBQ0kDAdWsOO6G_@bUSQFtT";
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        StringBuilder posterString = new StringBuilder();
        try
        {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            Unirest.setHttpClient(httpclient);
            System.err.println("Started query for  " + getIdNumber());

            //token  JSONObject response, token = Unirest.get(nidaURL).queryString("grant_type", "client_credentials").header("Content-Type", "application/json").header("Cache-Control", "no-cache").header("authorization", "Basic " + DatatypeConverter.printBase64Binary((VXController.mpesaConsumerKey + ":" + VXController.mpesaConsumerSecret).getBytes(StandardCharsets.ISO_8859_1))).asJson().getBody().getObject();
            //  JSONObject response =   Unirest.get(nidaURL).header("Content-Type", "application/json").asJson().getBody().getObject();
            JSONObject request = new JSONObject("{\"documentnumber\": \"" + number + "\" ,\"keyPhrase\": \"" + keyphrase + "\"}");
            JSONObject response = Unirest.post(nidaURL).header("Content-Type", "application/json").header("Cache-Control", "no-cache").body(request.toString()).asJson().getBody().getObject();
            // bRLogger.logEvent(token.toString() + " = " + request.toString() + " = " + (response = Unirest.post(VXController.mpesaRegisterUrl).header("Content-Type", "application/json").header("Cache-Control", "no-cache").header("authorization", "Bearer " + token.getString("access_token")).body(request.toString()).asJson().getBody().getObject()).toString());
            System.err.println("response " + response);

            VNResponse readResponse = readResponse(response.toString());
            System.err.println("fname " + readResponse.getForeName());
            System.err.println("getSurnames " + readResponse.getSurnames());
            System.err.println("getDocumentNumber " + readResponse.getDocumentNumber());
            System.err.println(getvNResolver().resolveGender(readResponse.getSex()) + "sex " + readResponse.getSex());
            int year = new Date().getYear();
            int year2 = new Date(readResponse.getDateOfBirth()).getYear();
            age = year - year2;
            setFirstName(readResponse.getForeName());
            setLastName(readResponse.getSurnames());
            setPreferredName(readResponse.getForeName());
            setIdNumber(readResponse.getDocumentNumber().replaceAll("\\s+", ""));
            setNid(readResponse.getDocumentNumber());
            setCustImage(readResponse.getPhoto());
            setCustSignature(readResponse.getSignature());
            setStrDateOfBirth(readResponse.getDateOfBirth());
            setGender(readResponse.getSex());
            setNationalityId(getvNResolver().resolveNationality(readResponse.getNationality()));
            setCountryOfResidence(readResponse.getCountryOfDomicile());
            setSpouseName(readResponse.getSpouse());
            setMothersMaidenName(readResponse.getMotherNames());
            setFathersName(readResponse.getFatherNames());
            setMaritalStatus(getvNResolver().resolveMaritalStatus(readResponse.getMaritalStatus()));
            setCountryOfBirthId(getvNResolver().resolveNationality(readResponse.getNationality()));
            setTitleId(getvNResolver().resolveTitle(readResponse.getSex(), readResponse.getMaritalStatus()));
            setAge(String.valueOf(age));
            // setStrFromDate(readResponse.getDateOfIssue());

        }
        catch (UnirestException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | org.json.JSONException ex)
        {
            Logger.getLogger(VNSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nextpage;
    }

    public VNResponse readResponse(String respMessage)
    {
        VNResponse nResponse = new VNResponse();
        JSONObject responseObject = new JSONObject(respMessage);
        nResponse.setDocumentNumber(responseObject.getString("documentNumber"));
        nResponse.setDocumentType(responseObject.getString("documentType"));
        nResponse.setIssueNumber(responseObject.getString("issueNumber"));
        nResponse.setDateOfIssue(responseObject.getString("dateOfIssue"));
        nResponse.setDateOfExpiry(responseObject.getString("dateOfExpiry"));
        nResponse.setPlaceOfIssue(responseObject.getString("placeOfIssue"));
        nResponse.setApplicationNumber(responseObject.getString("applicationNumber"));
        nResponse.setForeName(responseObject.getString("foreName"));
        nResponse.setSurnames(responseObject.getString("surnames"));
        nResponse.setFatherNames(responseObject.getString("fatherNames"));
        nResponse.setMotherNames(responseObject.getString("motherNames"));
        nResponse.setSex(responseObject.getString("sex"));
        nResponse.setDateOfBirth(responseObject.getString("dateOfBirth"));
        nResponse.setPlaceOfBirth(responseObject.getString("placeOfBirth"));
        nResponse.setVillageId(responseObject.getString("villageId"));
        nResponse.setVillage(responseObject.getString("village"));
        nResponse.setCell(responseObject.getString("cell"));
        nResponse.setSector(responseObject.getString("sector"));
        nResponse.setDistrict(responseObject.getString("district"));
        nResponse.setProvince(responseObject.getString("province"));
        nResponse.setCivilStatus(responseObject.getString("civilStatus"));
        nResponse.setSpouse(responseObject.getString("spouse"));
        nResponse.setStatus(responseObject.getString("status"));
        nResponse.setPhoto(responseObject.getString("photo"));
        nResponse.setSignature(responseObject.getString("signature"));
        nResponse.setNationality(responseObject.getString("nationality"));
        nResponse.setApplicantType(responseObject.getString("applicantType"));
        nResponse.setMaritalStatus(responseObject.getString("maritalStatus"));
        nResponse.setCountryOfDomicile(responseObject.getString("countryOfDomicile"));
        return nResponse;
    }

    public String queryCustomer()
    {
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "Template?faces-redirect=true";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        System.err.println("Here to redirect ");
        return "/Template?faces-redirect=true";
    }

    public String redirectToMain()
    {
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "Template?faces-redirect=true";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;

        return nextpage;
    }

    public String redirectToNew()
    {
        HttpSession session = SessionUtils.getSession();
        String title = "Notfication", msg = "", nextpage = "Template?faces-redirect=true";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;

        return nextpage;
    }

    public void handleToggle(ToggleEvent event)
    {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Toggled", "Visibility:" + event.getVisibility());
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the console
     */
    public String getConsole()
    {
        return console;
    }

    /**
     * @param console the console to set
     */
    public void setConsole(String console)
    {
        this.console = console;
    }

    /**
     * @return the employedRadio
     */
    public String getEmployedRadio()
    {
        return employedRadio;
    }

    /**
     * @param employedRadio the employedRadio to set
     */
    public void setEmployedRadio(String employedRadio)
    {
        this.employedRadio = employedRadio;
    }

    /**
     * @return the residentRadio
     */
    public String getResidentRadio()
    {
        return residentRadio;
    }

    /**
     * @param residentRadio the residentRadio to set
     */
    public void setResidentRadio(String residentRadio)
    {
        this.residentRadio = residentRadio;
    }

    /**
     * @return the custType
     */
    public String getCustType()
    {
        return custType;
    }

    /**
     * @param custType the custType to set
     */
    public void setCustType(String custType)
    {
        this.custType = custType;
    }

    /**
     * @return the custCategory
     */
    public String getCustCategory()
    {
        return custCategory;
    }

    /**
     * @param custCategory the custCategory to set
     */
    public void setCustCategory(String custCategory)
    {
        this.custCategory = custCategory;
    }

    /**
     * @return the gender
     */
    public String getGender()
    {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    /**
     * @return the addressCity
     */
    public String getAddressCity()
    {
        return addressCity;
    }

    /**
     * @param addressCity the addressCity to set
     */
    public void setAddressCity(String addressCity)
    {
        this.addressCity = addressCity;
    }

    /**
     * @return the addressCountryId
     */
    public Long getAddressCountryId()
    {
        return addressCountryId;
    }

    /**
     * @param addressCountryId the addressCountryId to set
     */
    public void setAddressCountryId(Long addressCountryId)
    {
        this.addressCountryId = addressCountryId;
    }

    /**
     * @return the addressLine1
     */
    public String getAddressLine1()
    {
        return addressLine1;
    }

    /**
     * @param addressLine1 the addressLine1 to set
     */
    public void setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    /**
     * @return the addressLine2
     */
    public String getAddressLine2()
    {
        return addressLine2;
    }

    /**
     * @param addressLine2 the addressLine2 to set
     */
    public void setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    /**
     * @return the addressLine3
     */
    public String getAddressLine3()
    {
        return addressLine3;
    }

    /**
     * @param addressLine3 the addressLine3 to set
     */
    public void setAddressLine3(String addressLine3)
    {
        this.addressLine3 = addressLine3;
    }

    /**
     * @return the addressLine4
     */
    public String getAddressLine4()
    {
        return addressLine4;
    }

    /**
     * @param addressLine4 the addressLine4 to set
     */
    public void setAddressLine4(String addressLine4)
    {
        this.addressLine4 = addressLine4;
    }

    /**
     * @return the addressPropertyTypeId
     */
    public Long getPropertyTypeId()
    {
        return propertyTypeId;
    }

    /**
     * @param addressPropertyTypeId the addressPropertyTypeId to set
     */
    public void setPropertyTypeId(Long propertyTypeId)
    {
        this.propertyTypeId = propertyTypeId;
    }

    /**
     * @return the addressState
     */
    public String getAddressState()
    {
        return addressState;
    }

    /**
     * @param addressState the addressState to set
     */
    public void setAddressState(String addressState)
    {
        this.addressState = addressState;
    }

    /**
     * @return the addressTypeId
     */
    public Long getAddressTypeId()
    {
        return addressTypeId;
    }

    /**
     * @param addressTypeId the addressTypeId to set
     */
    public void setAddressTypeId(Long addressTypeId)
    {
        this.addressTypeId = addressTypeId;
    }

    /**
     * @return the businessUnitCodeId
     */
    public Long getBusinessUnitCodeId()
    {
        return businessUnitCodeId;
    }

    /**
     * @param businessUnitCodeId the businessUnitCodeId to set
     */
    public void setBusinessUnitCodeId(Long businessUnitCodeId)
    {
        this.businessUnitCodeId = businessUnitCodeId;
    }

    /**
     * @return the contactModeDesc
     */
    public String getContactModeDesc()
    {
        return contactModeDesc;
    }

    /**
     * @param contactModeDesc the contactModeDesc to set
     */
    public void setContactModeDesc(String contactModeDesc)
    {
        this.contactModeDesc = contactModeDesc;
    }

    /**
     * @return the contactModeId
     */
    public Long getContactModeId()
    {
        return contactModeId;
    }

    /**
     * @param contactModeId the contactModeId to set
     */
    public void setContactModeId(Long contactModeId)
    {
        this.contactModeId = contactModeId;
    }

    /**
     * @return the countryOfBirthId
     */
    public Long getCountryOfBirthId()
    {
        return countryOfBirthId;
    }

    /**
     * @param countryOfBirthId the countryOfBirthId to set
     */
    public void setCountryOfBirthId(Long countryOfBirthId)
    {
        this.countryOfBirthId = countryOfBirthId;
    }

    /**
     * @return the countryOfResidenceId
     */
    public Long getCountryOfResidenceId()
    {
        return countryOfResidenceId;
    }

    /**
     * @param countryOfResidenceId the countryOfResidenceId to set
     */
    public void setCountryOfResidenceId(Long countryOfResidenceId)
    {
        this.countryOfResidenceId = countryOfResidenceId;
    }

    /**
     * @return the countryOfRiskId
     */
    public Long getCountryOfRiskId()
    {
        return countryOfRiskId;
    }

    /**
     * @param countryOfRiskId the countryOfRiskId to set
     */
    public void setCountryOfRiskId(Long countryOfRiskId)
    {
        this.countryOfRiskId = countryOfRiskId;
    }

    /**
     * @return the creditRatingAgencyId
     */
    public Long getCreditRatingAgencyId()
    {
        return creditRatingAgencyId;
    }

    /**
     * @param creditRatingAgencyId the creditRatingAgencyId to set
     */
    public void setCreditRatingAgencyId(Long creditRatingAgencyId)
    {
        this.creditRatingAgencyId = creditRatingAgencyId;
    }

    /**
     * @return the custAddressId
     */
    public Long getCustAddressId()
    {
        return custAddressId;
    }

    /**
     * @param custAddressId the custAddressId to set
     */
    public void setCustAddressId(Long custAddressId)
    {
        this.custAddressId = custAddressId;
    }

    /**
     * @return the custContactModeId
     */
    public Long getCustContactModeId()
    {
        return custContactModeId;
    }

    /**
     * @param custContactModeId the custContactModeId to set
     */
    public void setCustContactModeId(Long custContactModeId)
    {
        this.custContactModeId = custContactModeId;
    }

    /**
     * @return the custIdentificationId
     */
    public Long getCustIdentificationId()
    {
        return custIdentificationId;
    }

    /**
     * @param custIdentificationId the custIdentificationId to set
     */
    public void setCustIdentificationId(Long custIdentificationId)
    {
        this.custIdentificationId = custIdentificationId;
    }

    /**
     * @return the custIdentificationNumber
     */
    public String getCustIdentificationNumber()
    {
        return custIdentificationNumber;
    }

    /**
     * @param custIdentificationNumber the custIdentificationNumber to set
     */
    public void setCustIdentificationNumber(String custIdentificationNumber)
    {
        this.custIdentificationNumber = custIdentificationNumber;
    }

    /**
     * @return the custIdentificationRefCode
     */
    public Long getCustIdentificationRefCode()
    {
        return custIdentificationRefCode;
    }

    /**
     * @param custIdentificationRefCode the custIdentificationRefCode to set
     */
    public void setCustIdentificationRefCode(Long custIdentificationRefCode)
    {
        this.custIdentificationRefCode = custIdentificationRefCode;
    }

    /**
     * @return the customerCategory
     */
    public String getCustomerCategory()
    {
        return customerCategory;
    }

    /**
     * @param customerCategory the customerCategory to set
     */
    public void setCustomerCategory(String customerCategory)
    {
        this.customerCategory = customerCategory;
    }

    /**
     * @return the customerSegmentId
     */
    public Long getCustomerSegmentId()
    {
        return customerSegmentId;
    }

    /**
     * @param customerSegmentId the customerSegmentId to set
     */
    public void setCustomerSegmentId(Long customerSegmentId)
    {
        this.customerSegmentId = customerSegmentId;
    }

    /**
     * @return the customerType
     */
    public Long getCustomerType()
    {
        return customerType;
    }

    /**
     * @param customerType the customerType to set
     */
    public void setCustomerType(Long customerType)
    {
        this.customerType = customerType;
    }

    /**
     * @return the custShortName
     */
    public String getCustShortName()
    {
        return custShortName;
    }

    /**
     * @param custShortName the custShortName to set
     */
    public void setCustShortName(String custShortName)
    {
        this.custShortName = custShortName;
    }

    /**
     * @return the educationalQualificationId
     */
    public Long getEducationalQualificationId()
    {
        return educationalQualificationId;
    }

    /**
     * @param educationalQualificationId the educationalQualificationId to set
     */
    public void setEducationalQualificationId(Long educationalQualificationId)
    {
        this.educationalQualificationId = educationalQualificationId;
    }

    /**
     * @return the employmentFlag
     */
    public Boolean getEmploymentFlag()
    {
        return employmentFlag;
    }

    /**
     * @param employmentFlag the employmentFlag to set
     */
    public void setEmploymentFlag(Boolean employmentFlag)
    {
        this.employmentFlag = employmentFlag;
    }

    /**
     * @return the ethnicGroupId
     */
    public Long getEthnicGroupId()
    {
        return ethnicGroupId;
    }

    /**
     * @param ethnicGroupId the ethnicGroupId to set
     */
    public void setEthnicGroupId(Long ethnicGroupId)
    {
        this.ethnicGroupId = ethnicGroupId;
    }

    /**
     * @return the fathersName
     */
    public String getFathersName()
    {
        return fathersName;
    }

    /**
     * @param fathersName the fathersName to set
     */
    public void setFathersName(String fathersName)
    {
        this.fathersName = fathersName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the grandfathersName
     */
    public String getGrandfathersName()
    {
        return grandfathersName;
    }

    /**
     * @param grandfathersName the grandfathersName to set
     */
    public void setGrandfathersName(String grandfathersName)
    {
        this.grandfathersName = grandfathersName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the locale
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    /**
     * @return the maidenName
     */
    public String getMaidenName()
    {
        return maidenName;
    }

    /**
     * @param maidenName the maidenName to set
     */
    public void setMaidenName(String maidenName)
    {
        this.maidenName = maidenName;
    }

    /**
     * @return the mainBusinessUnitId
     */
    public Long getMainBusinessUnitId()
    {
        return mainBusinessUnitId;
    }

    /**
     * @param mainBusinessUnitId the mainBusinessUnitId to set
     */
    public void setMainBusinessUnitId(Long mainBusinessUnitId)
    {
        this.mainBusinessUnitId = mainBusinessUnitId;
    }

    /**
     * @return the maritalStatus
     */
    public String getMaritalStatus()
    {
        return maritalStatus;
    }

    /**
     * @param maritalStatus the maritalStatus to set
     */
    public void setMaritalStatus(String maritalStatus)
    {
        this.maritalStatus = maritalStatus;
    }

    /**
     * @return the marketingCampaignId
     */
    public Long getMarketingCampaignId()
    {
        return marketingCampaignId;
    }

    /**
     * @param marketingCampaignId the marketingCampaignId to set
     */
    public void setMarketingCampaignId(Long marketingCampaignId)
    {
        this.marketingCampaignId = marketingCampaignId;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    /**
     * @return the mothersMaidenName
     */
    public String getMothersMaidenName()
    {
        return mothersMaidenName;
    }

    /**
     * @param mothersMaidenName the mothersMaidenName to set
     */
    public void setMothersMaidenName(String mothersMaidenName)
    {
        this.mothersMaidenName = mothersMaidenName;
    }

    /**
     * @return the nationalityId
     */
    public Long getNationalityId()
    {
        return nationalityId;
    }

    /**
     * @param nationalityId the nationalityId to set
     */
    public void setNationalityId(Long nationalityId)
    {
        this.nationalityId = nationalityId;
    }

    /**
     * @return the noOfDependents
     */
    public Long getNoOfDependents()
    {
        return noOfDependents;
    }

    /**
     * @param noOfDependents the noOfDependents to set
     */
    public void setNoOfDependents(Long noOfDependents)
    {
        this.noOfDependents = noOfDependents;
    }

    /**
     * @return the openingReasonId
     */
    public Long getOpeningReasonId()
    {
        return openingReasonId;
    }

    /**
     * @param openingReasonId the openingReasonId to set
     */
    public void setOpeningReasonId(Long openingReasonId)
    {
        this.openingReasonId = openingReasonId;
    }

    /**
     * @return the operationCurrencyId
     */
    public Long getOperationCurrencyId()
    {
        return operationCurrencyId;
    }

    /**
     * @param operationCurrencyId the operationCurrencyId to set
     */
    public void setOperationCurrencyId(Long operationCurrencyId)
    {
        this.operationCurrencyId = operationCurrencyId;
    }

    /**
     * @return the organisationName
     */
    public String getOrganisationName()
    {
        return organisationName;
    }

    /**
     * @param organisationName the organisationName to set
     */
    public void setOrganisationName(String organisationName)
    {
        this.organisationName = organisationName;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * @return the preferredName
     */
    public String getPreferredName()
    {
        return preferredName;
    }

    /**
     * @param preferredName the preferredName to set
     */
    public void setPreferredName(String preferredName)
    {
        this.preferredName = preferredName;
    }

    /**
     * @return the primaryAddress
     */
    public Boolean getPrimaryAddress()
    {
        return primaryAddress;
    }

    /**
     * @param primaryAddress the primaryAddress to set
     */
    public void setPrimaryAddress(Boolean primaryAddress)
    {
        this.primaryAddress = primaryAddress;
    }

    /**
     * @return the primaryRelationshipOfficerId
     */
    public Long getPrimaryRelationshipOfficerId()
    {
        return primaryRelationshipOfficerId;
    }

    /**
     * @param primaryRelationshipOfficerId the primaryRelationshipOfficerId to
     * set
     */
    public void setPrimaryRelationshipOfficerId(Long primaryRelationshipOfficerId)
    {
        this.primaryRelationshipOfficerId = primaryRelationshipOfficerId;
    }

    /**
     * @return the privacyLevelId
     */
    public Long getPrivacyLevelId()
    {
        return privacyLevelId;
    }

    /**
     * @param privacyLevelId the privacyLevelId to set
     */
    public void setPrivacyLevelId(Long privacyLevelId)
    {
        this.privacyLevelId = privacyLevelId;
    }

    /**
     * @return the professionalQualificationId
     */
    public Long getProfessionalQualificationId()
    {
        return professionalQualificationId;
    }

    /**
     * @param professionalQualificationId the professionalQualificationId to set
     */
    public void setProfessionalQualificationId(Long professionalQualificationId)
    {
        this.professionalQualificationId = professionalQualificationId;
    }

    /**
     * @return the professionId
     */
    public Long getProfessionId()
    {
        return professionId;
    }

    /**
     * @param professionId the professionId to set
     */
    public void setProfessionId(Long professionId)
    {
        this.professionId = professionId;
    }

    /**
     * @return the registrationNumber
     */
    public String getRegistrationNumber()
    {
        return registrationNumber;
    }

    /**
     * @param registrationNumber the registrationNumber to set
     */
    public void setRegistrationNumber(String registrationNumber)
    {
        this.registrationNumber = registrationNumber;
    }

    /**
     * @return the religionId
     */
    public Long getReligionId()
    {
        return religionId;
    }

    /**
     * @param religionId the religionId to set
     */
    public void setReligionId(Long religionId)
    {
        this.religionId = religionId;
    }

    /**
     * @return the residentFlag
     */
    public Boolean getResidentFlag()
    {
        return residentFlag;
    }

    /**
     * @param residentFlag the residentFlag to set
     */
    public void setResidentFlag(Boolean residentFlag)
    {
        this.residentFlag = residentFlag;
    }

    /**
     * @return the riskCode
     */
    public String getRiskCode()
    {
        return riskCode;
    }

    /**
     * @param riskCode the riskCode to set
     */
    public void setRiskCode(String riskCode)
    {
        this.riskCode = riskCode;
    }

    /**
     * @return the serviceLevelId
     */
    public Long getServiceLevelId()
    {
        return serviceLevelId;
    }

    /**
     * @param serviceLevelId the serviceLevelId to set
     */
    public void setServiceLevelId(Long serviceLevelId)
    {
        this.serviceLevelId = serviceLevelId;
    }

    /**
     * @return the spouseName
     */
    public String getSpouseName()
    {
        return spouseName;
    }

    /**
     * @param spouseName the spouseName to set
     */
    public void setSpouseName(String spouseName)
    {
        this.spouseName = spouseName;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @return the strDate
     */
    public String getStrDate()
    {
        return strDate;
    }

    /**
     * @param strDate the strDate to set
     */
    public void setStrDate(String strDate)
    {
        this.strDate = strDate;
    }

    /**
     * @return the strDateOfBirth
     */
    public String getStrDateOfBirth()
    {
        return strDateOfBirth;
    }

    /**
     * @param strDateOfBirth the strDateOfBirth to set
     */
    public void setStrDateOfBirth(String strDateOfBirth)
    {
        this.strDateOfBirth = strDateOfBirth;
    }

    /**
     * @return the strFromDate
     */
    public String getStrFromDate()
    {
        return strFromDate;
    }

    /**
     * @param strFromDate the strFromDate to set
     */
    public void setStrFromDate(String strFromDate)
    {
        this.strFromDate = strFromDate;
    }

    /**
     * @return the strIssueDate
     */
    public String getStrIssueDate()
    {
        return strIssueDate;
    }

    /**
     * @param strIssueDate the strIssueDate to set
     */
    public void setStrIssueDate(String strIssueDate)
    {
        this.strIssueDate = strIssueDate;
    }

    /**
     * @return the strRegistrationDate
     */
    public String getStrRegistrationDate()
    {
        return strRegistrationDate;
    }

    /**
     * @param strRegistrationDate the strRegistrationDate to set
     */
    public void setStrRegistrationDate(String strRegistrationDate)
    {
        this.strRegistrationDate = strRegistrationDate;
    }

    /**
     * @return the strToDate
     */
    public String getStrToDate()
    {
        return strToDate;
    }

    /**
     * @param strToDate the strToDate to set
     */
    public void setStrToDate(String strToDate)
    {
        this.strToDate = strToDate;
    }

    /**
     * @return the supplementaryRelationshipOfficerId
     */
    public Long getSupplementaryRelationshipOfficerId()
    {
        return supplementaryRelationshipOfficerId;
    }

    /**
     * @param supplementaryRelationshipOfficerId the
     * supplementaryRelationshipOfficerId to set
     */
    public void setSupplementaryRelationshipOfficerId(Long supplementaryRelationshipOfficerId)
    {
        this.supplementaryRelationshipOfficerId = supplementaryRelationshipOfficerId;
    }

    /**
     * @return the swiftAddress
     */
    public String getSwiftAddress()
    {
        return swiftAddress;
    }

    /**
     * @param swiftAddress the swiftAddress to set
     */
    public void setSwiftAddress(String swiftAddress)
    {
        this.swiftAddress = swiftAddress;
    }

    /**
     * @return the taxGroupId
     */
    public Long getTaxGroupId()
    {
        return taxGroupId;
    }

    /**
     * @param taxGroupId the taxGroupId to set
     */
    public void setTaxGroupId(Long taxGroupId)
    {
        this.taxGroupId = taxGroupId;
    }

    /**
     * @return the titleId
     */
    public Long getTitleId()
    {
        return titleId;
    }

    /**
     * @param titleId the titleId to set
     */
    public void setTitleId(Long titleId)
    {
        this.titleId = titleId;
    }

    /**
     * @return the verifiedFlag
     */
    public Boolean getVerifiedFlag()
    {
        return verifiedFlag;
    }

    /**
     * @param verifiedFlag the verifiedFlag to set
     */
    public void setVerifiedFlag(Boolean verifiedFlag)
    {
        this.verifiedFlag = verifiedFlag;
    }

    /**
     * @return the xapiServiceCode
     */
    public String getXapiServiceCode()
    {
        return xapiServiceCode;
    }

    /**
     * @param xapiServiceCode the xapiServiceCode to set
     */
    public void setXapiServiceCode(String xapiServiceCode)
    {
        this.xapiServiceCode = xapiServiceCode;
    }

    /**
     * @return the zipCode
     */
    public Long getZipCode()
    {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(Long zipCode)
    {
        this.zipCode = zipCode;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber()
    {
        return customerNumber;
    }

    /**
     * @param customerNumber the customerNumber to set
     */
    public void setCustomerNumber(String customerNumber)
    {
        this.customerNumber = customerNumber;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName()
    {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    /**
     * @return the customerId
     */
    public Long getCustomerId()
    {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    /**
     * @return the industryId
     */
    public Long getIndustryId()
    {
        return industryId;
    }

    /**
     * @param industryId the industryId to set
     */
    public void setIndustryId(Long industryId)
    {
        this.industryId = industryId;
    }

    /**
     * @return the taxStatusId
     */
    public Long getTaxStatusId()
    {
        return taxStatusId;
    }

    /**
     * @param taxStatusId the taxStatusId to set
     */
    public void setTaxStatusId(Long taxStatusId)
    {
        this.taxStatusId = taxStatusId;
    }

    /**
     * @return the nationalIdNumber
     */
    public String getNationalIdNumber()
    {
        return nationalIdNumber;
    }

    /**
     * @param nationalIdNumber the nationalIdNumber to set
     */
    public void setNationalIdNumber(String nationalIdNumber)
    {
        this.nationalIdNumber = nationalIdNumber;
    }

    /**
     * @return the wildCardFlag
     */
    public boolean isWildCardFlag()
    {
        return wildCardFlag;
    }

    /**
     * @param wildCardFlag the wildCardFlag to set
     */
    public void setWildCardFlag(boolean wildCardFlag)
    {
        this.wildCardFlag = wildCardFlag;
    }

    /**
     * @return the collateralRef
     */
    public String getCollateralRef()
    {
        return collateralRef;
    }

    /**
     * @param collateralRef the collateralRef to set
     */
    public void setCollateralRef(String collateralRef)
    {
        this.collateralRef = collateralRef;
    }

    /**
     * @return the collateralStatus
     */
    public String getCollateralStatus()
    {
        return collateralStatus;
    }

    /**
     * @param collateralStatus the collateralStatus to set
     */
    public void setCollateralStatus(String collateralStatus)
    {
        this.collateralStatus = collateralStatus;
    }

    /**
     * @return the collateralType
     */
    public String getCollateralType()
    {
        return collateralType;
    }

    /**
     * @param collateralType the collateralType to set
     */
    public void setCollateralType(String collateralType)
    {
        this.collateralType = collateralType;
    }

    /**
     * @return the contactModeCategory
     */
    public String getContactModeCategory()
    {
        return contactModeCategory;
    }

    /**
     * @param contactModeCategory the contactModeCategory to set
     */
    public void setContactModeCategory(String contactModeCategory)
    {
        this.contactModeCategory = contactModeCategory;
    }

    /**
     * @return the custRelshipType
     */
    public String getCustRelshipType()
    {
        return custRelshipType;
    }

    /**
     * @param custRelshipType the custRelshipType to set
     */
    public void setCustRelshipType(String custRelshipType)
    {
        this.custRelshipType = custRelshipType;
    }

    /**
     * @return the mainBUNumber
     */
    public String getMainBUNumber()
    {
        return mainBUNumber;
    }

    /**
     * @param mainBUNumber the mainBUNumber to set
     */
    public void setMainBUNumber(String mainBUNumber)
    {
        this.mainBUNumber = mainBUNumber;
    }

    /**
     * @return the age
     */
    public String getAge()
    {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(String age)
    {
        this.age = age;
    }

    /**
     * @return the industry
     */
    public String getIndustry()
    {
        return industry;
    }

    /**
     * @param industry the industry to set
     */
    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    /**
     * @return the custImage
     */
    public String getCustImage()
    {
        return custImage;
    }

    /**
     * @param custImage the custImage to set
     */
    public void setCustImage(String custImage)
    {
        this.custImage = custImage;
    }

    /**
     * @return the custSignature
     */
    public String getCustSignature()
    {
        return custSignature;
    }

    /**
     * @param custSignature the custSignature to set
     */
    public void setCustSignature(String custSignature)
    {
        this.custSignature = custSignature;
    }

    /**
     * @return the nid
     */
    public String getNid()
    {
        return nid;
    }

    /**
     * @param nid the nid to set
     */
    public void setNid(String nid)
    {
        this.nid = nid;
    }

    /**
     * @return the countryOfResidence
     */
    public String getCountryOfResidence()
    {
        return countryOfResidence;
    }

    /**
     * @param countryOfResidence the countryOfResidence to set
     */
    public void setCountryOfResidence(String countryOfResidence)
    {
        this.countryOfResidence = countryOfResidence;
    }

    /**
     * @return the vNResolver
     */
    public VNResolver getvNResolver()
    {
        return vNResolver;
    }

    /**
     * @param vNResolver the vNResolver to set
     */
    public void setvNResolver(VNResolver vNResolver)
    {
        this.vNResolver = vNResolver;
    }

    /**
     * @return the referredBy
     */
    public String getReferredBy()
    {
        return referredBy;
    }

    /**
     * @param referredBy the referredBy to set
     */
    public void setReferredBy(String referredBy)
    {
        this.referredBy = referredBy;
    }

    /**
     * @return the taxIdentificationNo
     */
    public String getTaxIdentificationNo()
    {
        return taxIdentificationNo;
    }

    /**
     * @param taxIdentificationNo the taxIdentificationNo to set
     */
    public void setTaxIdentificationNo(String taxIdentificationNo)
    {
        this.taxIdentificationNo = taxIdentificationNo;
    }

    /**
     * @return the currencyCode
     */
    public String getCurrencyCode()
    {
        return currencyCode;
    }

    /**
     * @param currencyCode the currencyCode to set
     */
    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    /**
     * @return the sourceOfFunds
     */
    public Long getSourceOfFunds()
    {
        return sourceOfFunds;
    }

    /**
     * @param sourceOfFunds the sourceOfFunds to set
     */
    public void setSourceOfFunds(Long sourceOfFunds)
    {
        this.sourceOfFunds = sourceOfFunds;
    }

    /**
     * @return the district
     */
    public String getDistrict()
    {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district)
    {
        this.district = district;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the village
     */
    public String getVillage()
    {
        return village;
    }

    /**
     * @param village the village to set
     */
    public void setVillage(String village)
    {
        this.village = village;
    }

    /**
     * @return the bRLogger
     */
    public static BRLogger getbRLogger()
    {
        return bRLogger;
    }

    /**
     * @param abRLogger the bRLogger to set
     */
    public static void setbRLogger(BRLogger abRLogger)
    {
        bRLogger = abRLogger;
    }

    /**
     * @return the vNCustomer
     */
    public VNCustomer getvNCustomer()
    {
        return vNCustomer;
    }

    /**
     * @param vNCustomer the vNCustomer to set
     */
    public void setvNCustomer(VNCustomer vNCustomer)
    {
        this.vNCustomer = vNCustomer;
    }

    /**
     * @return the custId
     */
    public int getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(int custId)
    {
        this.custId = custId;
    }

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

    /**
     * @return the customers
     */
    public List<VNCustomer> getCustomers()
    {
        return customers;
    }

    /**
     * @param customers the customers to set
     */
    public void setCustomers(List<VNCustomer> customers)
    {
        this.customers = customers;
    }

   
}
