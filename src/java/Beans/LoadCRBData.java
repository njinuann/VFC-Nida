/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import DAO.TDClient;
import Controller.VNController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

/**
 *
 * @author Neptune-Njinu
 */
@ManagedBean(name = "LoadCRBData")
@ViewScoped
public class LoadCRBData implements Serializable
{

    private TDClient tDClient = new TDClient();
    private static List<String> columns = new ArrayList<String>();
    private static ArrayList test = new ArrayList();
    private String reportType = "";
    private String reportpath = "";
    private ResultSet globalResultSet;

    public LoadCRBData()
    {
        //  dynamicColumns();
        setReportpath(VNController.ReportPath);
    }

    public String loadCrbReportData()
    {

        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        // System.err.println("<<<<<<<<<<save vendor record >>>>>>>>>>" + SVController.getWebServiceObjectString(vrecord));
        if (loadCrbData(getReportType()))
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

    public String extractCrbReportData()
    {

        String title = "Notfication", msg = "", nextpage = "";
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        // System.err.println("<<<<<<<<<<save vendor record >>>>>>>>>>" + SVController.getWebServiceObjectString(vrecord));
        if (extractData(getReportType()))
        {
            msg = "Extracted";

        }
        else
        {
            msg = "Error occured when Extracting Data";
            severity = FacesMessage.SEVERITY_FATAL;
        }
        // getData();
        return nextpage;
    }

    public boolean loadCrbData(String reportType)
    {
        boolean loaded = false;
        gettDClient().connectToDB();
        test = new ArrayList<>();
        columns = new ArrayList<>();

        try
        {
            try (ResultSet result = gettDClient().getCRBData(reportType))
            {
                System.err.println("Step 1 " + result);
                ResultSetMetaData md = result.getMetaData();
                int columnCount = md.getColumnCount();
                System.err.println("Step 2 " + columnCount);
                for (int i = 1; i <= columnCount; i++)
                {
                    columns.add(md.getColumnName(i));    //adding column name dynamicly
                    System.err.println("Step 3 " + md.getColumnName(i));
                }
                while (result.next())
                {
                    ArrayList child = new ArrayList();
                    for (int i = 1; i <= columnCount; i++)
                    {
                        child.add(result.getString(i)); //denpends on column add the data

                    }
                    test.add(child);
                }
            }
            loaded = true;
            gettDClient().dispose();
        }
        catch (SQLException ex)
        {
            System.out.println("" + ex);
        }
        return loaded;
    }

    public boolean extractData(String reportType)
    {
        boolean extracted = false;
        String extractionAction = reportType;
        switch (extractionAction)
        {
            case "IC":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\INDIVIDUAL_CONSUMER\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\INDIVIDUAL_CONSUMER");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCE" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        showMessage("Error", VNController.ReportPath + "\\INDIVIDUAL_CONSUMER" + " (file/folder not accessible)");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCE" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    //StringBuilder fileContent = new StringBuilder();
                    StringBuilder fileContent = new StringBuilder();

                    //TableModel tmodel = dataTable.getModel();
                    //  CSVUtils.writeLine(filewriter, test, '|', '\'');
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object SURNAME = rs.getString(1);
                            fileContent.append(SURNAME).append("|");
                            Object FORENAME_1 = rs.getString(2);
                            fileContent.append(FORENAME_1).append("|");
                            Object FORENAME_2 = rs.getString(3);
                            fileContent.append(FORENAME_2).append("|");
                            Object FORNAME_3 = rs.getString(4);
                            fileContent.append(FORNAME_3).append("|");
                            Object SALUTATION = formatString(rs.getString(5));
                            fileContent.append(SALUTATION).append("|");
                            Object DATE_OF_BIRTH = rs.getDate(6);
                            fileContent.append(formatter.format(DATE_OF_BIRTH)).append("|");
                            Object BRID = formatString(rs.getString(7));
                            fileContent.append(BRID).append("|");
                            Object ACCOUNT_NUMBER = formatString(rs.getString(8));
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object GENDER = formatString(rs.getString(9));
                            fileContent.append(GENDER).append("|");
                            Object NATIONALITY = formatString(rs.getString(10).replaceAll("KENYAN", "KE"));
                            fileContent.append(NATIONALITY).append("|");
                            Object MARITAL_STATUS = formatString(rs.getString(11));
                            fileContent.append(MARITAL_STATUS).append("|");
                            Object PRIMARY_IDENTIFICATION = formatString(rs.getString(12));
                            fileContent.append("00").append(PRIMARY_IDENTIFICATION).append("|");
                            Object SOCIAL_SECURITY_NO = formatString(rs.getString(13));
                            fileContent.append(SOCIAL_SECURITY_NO).append("|");
                            Object EMPTYDATA = formatString(rs.getString(14));
                            fileContent.append(EMPTYDATA).append("|");
                            Object EMPTYDATA1 = formatString(rs.getString(15));
                            fileContent.append(EMPTYDATA1).append("|");
                            Object EMPTYDATA2 = formatString(rs.getString(16));
                            fileContent.append(EMPTYDATA2).append("|");
                            Object EMPTYDATA3 = formatString(rs.getString(17));
                            fileContent.append(EMPTYDATA3).append("|");
                            Object MOBILE_NUMBER = rs.getString(18).replace("07", "2547").replace("+254", "254");
                            fileContent.append(MOBILE_NUMBER).append("|");
                            Object EMPTYDATA4 = formatString(rs.getString(19));
                            fileContent.append(EMPTYDATA4).append("|");
                            Object EMPTYDATA5 = formatString(rs.getString(20));
                            fileContent.append(EMPTYDATA5).append("|");
                            Object POSTAL_ADDRESS_1 = rs.getString(21);
                            fileContent.append(POSTAL_ADDRESS_1).append("|");
                            Object POSTAL_ADDRESS_2 = rs.getString(22);
                            fileContent.append(POSTAL_ADDRESS_2).append("|");
                            Object POSTAL_LOCATION_TOWN = rs.getString(23);
                            fileContent.append(POSTAL_LOCATION_TOWN).append("|");
                            Object POSTAL_LOCATION_COUNTRY = rs.getString(24);
                            fileContent.append(POSTAL_LOCATION_COUNTRY).append("|");
                            Object POSTAL_CODE = formatString(rs.getString(25));
                            fileContent.append(POSTAL_CODE).append("|");
                            Object PHYSICAL_ADDESS_1 = rs.getString(26);
                            fileContent.append(PHYSICAL_ADDESS_1).append("|");
                            Object EMPTYDATA6 = formatString(rs.getString(27));
                            fileContent.append(EMPTYDATA6).append("|");
                            Object EMPTYDATA7 = formatString(rs.getString(28));//tmodel.getValueAt(i, 28);
                            fileContent.append(EMPTYDATA7).append("|");
                            Object EMPTYDATA8 = formatString(rs.getString(29));//tmodel.getValueAt(i, 29);
                            fileContent.append(EMPTYDATA8).append("|");
                            Object LOCATION_COUNTRY = rs.getString(30);
                            fileContent.append(LOCATION_COUNTRY).append("|");
                            Object EMPTYDATA9 = formatString(rs.getString(31));
                            fileContent.append(EMPTYDATA9).append("|");
                            Object TAX_NO = formatString(rs.getString(32));
                            fileContent.append(TAX_NO).append("|");
                            Object EMAIL = formatString(rs.getString(33));
                            fileContent.append(EMAIL).append("|");
                            Object EMPTYDATA14 = formatString(rs.getString(34));
                            fileContent.append(EMPTYDATA14).append("|");
                            Object EMPLOYER_IND_TYPE = rs.getString(35);
                            fileContent.append(EMPLOYER_IND_TYPE).append("|");
                            Object EMPTYDATA10 = formatString(rs.getString(36));
                            fileContent.append(EMPTYDATA10).append("|");
                            Object EMPTYDATA11 = formatString(rs.getString(37));
                            fileContent.append(EMPTYDATA11).append("|");
                            Object EMPTYDATA12 = formatString(rs.getString(38));
                            fileContent.append(EMPTYDATA12).append("|");
                            Object LENDERS_REG_NAME = rs.getString(39);
                            fileContent.append(LENDERS_REG_NAME).append("|");
                            Object LENDERS_TRADING_NAME = rs.getString(40);
                            fileContent.append(LENDERS_TRADING_NAME).append("|");
                            Object LENDERS_BRANCH_NAME = rs.getString(41);
                            fileContent.append(LENDERS_BRANCH_NAME).append("|");
                            Object LENDERS_BRANCH_CODE = rs.getString(42);
                            fileContent.append(LENDERS_BRANCH_CODE).append("|");
                            Object ACCOUNT_INDICATOR = rs.getString(43);
                            fileContent.append(ACCOUNT_INDICATOR).append("|");
                            Object ACCOUNT_PRODUCT_TYPE = rs.getString(44);
                            fileContent.append(ACCOUNT_PRODUCT_TYPE).append("|");
                            Object ACCOUNT_OPENED = rs.getDate(45);
                            fileContent.append(formatter.format(ACCOUNT_OPENED)).append("|");
                            Object REPAYMENT_DUE_DT = rs.getDate(46);
                            fileContent.append(formatter.format(REPAYMENT_DUE_DT)).append("|");
                            Object DISBURSED_AMT = rs.getString(47).replace(".", "");
                            fileContent.append(DISBURSED_AMT).append("|");
                            Object CURRENCY_OF_FACILITY = rs.getString(48);
                            fileContent.append(CURRENCY_OF_FACILITY).append("|");
                            Object DISBURSED_AMT1 = rs.getString(49).replace(".", "");
                            fileContent.append(DISBURSED_AMT1).append("|");
                            Object CLEAR_BAL = rs.getString(50).replace(".", "");
                            fileContent.append(CLEAR_BAL).append("|");
                            Object OVERDUE_BALANCE = rs.getString(51).replace(".", "");
                            fileContent.append(OVERDUE_BALANCE).append("|");

                            Object OVERDUE_DATE = rs.getDate(52);
                            if (OVERDUE_DATE != null)
                            {
                                fileContent.append(formatter.format(OVERDUE_DATE)).append("|");
                            }
                            else
                            {
                                fileContent.append(" ").append("|");
                            }

                            Object DAYSINARREARS = rs.getString(53).trim();
                            fileContent.append(DAYSINARREARS).append("|");
                            Object NO_DAYS_IN_ARREARS = formatString(rs.getString(54));
                            fileContent.append(NO_DAYS_IN_ARREARS).append("|");
                            Object P_NPL_INDICATOR = rs.getString(55);
                            fileContent.append(P_NPL_INDICATOR).append("|");
                            Object ACCOUNT_STATUS = rs.getString(56);
                            fileContent.append(ACCOUNT_STATUS).append("|");
                            Object ACCOUNT_STATUS_DT = rs.getDate(57);
                            fileContent.append(formatter.format(ACCOUNT_STATUS_DT)).append("|");
                            Object EMPTYDATA13 = formatString(rs.getString(58));
                            fileContent.append(EMPTYDATA13).append("|");
                            Object TERMS = rs.getString(59);
                            fileContent.append(TERMS).append("|");
                            Object EMPTYDATA15 = formatString(rs.getString(60));
                            fileContent.append(EMPTYDATA15).append("|");
                            Object EMPTYDATA16 = formatString(rs.getString(61));
                            fileContent.append(EMPTYDATA16).append("|");
                            Object FREQUENCY = rs.getString(62);
                            fileContent.append(FREQUENCY).append("|");
                            Object INSTALLMENT_STRT_DT = rs.getDate(63);
                            fileContent.append(formatter.format(INSTALLMENT_STRT_DT)).append("|");
                            Object INSTALLMENT_AMT = rs.getString(64).replace(".", "");
                            fileContent.append(INSTALLMENT_AMT).append("|");
                            Object LAST_INSTALLMENT_DT = rs.getDate(65);
                            fileContent.append(formatter.format(LAST_INSTALLMENT_DT)).append("|");
                            Object LAST_PAYMENT_AMT = rs.getString(66).replace(".", "");
                            fileContent.append(LAST_PAYMENT_AMT).append("|");
                            Object TYPE_OF_SECURITY = rs.getString(67);
                            fileContent.append(TYPE_OF_SECURITY).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            case "NI":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\NON_INDIVIDUAL_CONSUMER\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\NON_INDIVIDUAL_CONSUMER");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCI" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCI" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    StringBuilder fileContent = new StringBuilder();
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object REGISTERD_NAME = rs.getString(1);
                            fileContent.append(REGISTERD_NAME).append("|");
                            Object TRADING_NAME = rs.getString(2);
                            fileContent.append(TRADING_NAME).append("|");
                            Object REGISTRATION_DATE = rs.getDate(3);
                            fileContent.append(formatter.format(REGISTRATION_DATE)).append("|");
                            Object REGISTRATION_NO = rs.getString(4);
                            fileContent.append(REGISTRATION_NO).append("|");
                            Object EMPTYDATA1 = formatString(rs.getString(5));
                            fileContent.append(EMPTYDATA1).append("|");
                            Object NATIONALITY = rs.getString(6);
                            fileContent.append(NATIONALITY).append("|");
                            Object BRID = rs.getString(7);
                            fileContent.append(BRID).append("|");
                            Object ACCOUNT_NUMBER = rs.getString(8);
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object COMPANY_TYPE = rs.getString(9);
                            fileContent.append(COMPANY_TYPE).append("|");
                            Object INDUSTRY_CODE = rs.getString(10);
                            fileContent.append(INDUSTRY_CODE).append("|");
                            Object EMPTYDATA2 = formatString(rs.getString(11));
                            fileContent.append(EMPTYDATA2).append("|");
                            Object PIN_NO = formatString(rs.getString(12));
                            fileContent.append(PIN_NO).append("|");
                            Object TAX_NO = formatString(rs.getString(13));
                            fileContent.append(TAX_NO).append("|");
                            Object NO_OF_SHAREHOLDERS = rs.getString(14);
                            fileContent.append(NO_OF_SHAREHOLDERS).append("|");
                            Object TRADING_STATUS = rs.getString(15);
                            fileContent.append(TRADING_STATUS).append("|");
                            Object EMPTYDATA3 = formatString(rs.getString(16));
                            fileContent.append(EMPTYDATA3).append("|");
                            Object MAIN_TELEPHONE_NUMBER = rs.getString(17);
                            fileContent.append(MAIN_TELEPHONE_NUMBER).append("|");
                            Object EMPTYDATA4 = formatString(rs.getString(18));
                            fileContent.append(EMPTYDATA4).append("|");
                            Object POSTAL_ADDRESS_1 = rs.getString(19);
                            fileContent.append(POSTAL_ADDRESS_1).append("|");
                            Object POSTAL_ADDRESS_2 = rs.getString(20);
                            fileContent.append(POSTAL_ADDRESS_2).append("|");
                            Object TOWN = rs.getString(21);
                            fileContent.append(TOWN).append("|");
                            Object COUNTRY = rs.getString(22);
                            fileContent.append(COUNTRY).append("|");
                            Object POSTAL_CODE = rs.getString(23);
                            fileContent.append(POSTAL_CODE).append("|");
                            Object PHYSICAL_ADDESS_1 = rs.getString(24);
                            fileContent.append(PHYSICAL_ADDESS_1).append("|");
                            Object PHYSICAL_ADDRESS_2 = formatString(rs.getString(25));
                            fileContent.append(PHYSICAL_ADDRESS_2).append("|");
                            Object EMPTYDATA5 = formatString(rs.getString(26));
                            fileContent.append(EMPTYDATA5).append("|");
                            Object PHYSICAL_LOCATION_TOWN = rs.getString(27);
                            fileContent.append(PHYSICAL_LOCATION_TOWN).append("|");
                            Object PHYSICAL_LOCATION_COUNTRY = rs.getString(28);
                            fileContent.append(PHYSICAL_LOCATION_COUNTRY).append("|");
                            Object LENDERS_REG_NAME = rs.getString(29);
                            fileContent.append(LENDERS_REG_NAME).append("|");
                            Object LENDERS_TRADING_NAME = rs.getString(30);
                            fileContent.append(LENDERS_TRADING_NAME).append("|");
                            Object LENDERS_BRANCH_NAME = rs.getString(31);
                            fileContent.append(LENDERS_BRANCH_NAME).append("|");
                            Object LENDERS_BRANCH_CODE = rs.getString(32);
                            fileContent.append(LENDERS_BRANCH_CODE).append("|");
                            Object ACCOUNT_INDICATOR = rs.getString(33);
                            fileContent.append(ACCOUNT_INDICATOR).append("|");
                            Object ACCOUNT_HOLDER_TYPE = rs.getString(34);
                            fileContent.append(ACCOUNT_HOLDER_TYPE).append("|");
                            Object ACCOUNT_PRODUCT_TYPE = rs.getString(35);
                            fileContent.append(ACCOUNT_PRODUCT_TYPE).append("|");
                            Object ACCOUNT_OPENED = rs.getDate(36);
                            fileContent.append(formatter.format(ACCOUNT_OPENED)).append("|");
                            Object REPAYMENT_DUE_DT = rs.getDate(37);
                            fileContent.append(formatter.format(REPAYMENT_DUE_DT)).append("|");
                            Object P_NPL_INDICATOR = rs.getString(38);
                            fileContent.append(P_NPL_INDICATOR).append("|");
                            Object ORIGINAL_AMOUNT = rs.getString(39).replace(".", "");
                            fileContent.append(ORIGINAL_AMOUNT).append("|");
                            Object CURRENCY_OF_FACILITY = rs.getString(40).replace("KES", "KE");
                            fileContent.append(CURRENCY_OF_FACILITY).append("|");
                            Object AMOUNT_IN_KES = rs.getString(41).replace(".", "");
                            fileContent.append(AMOUNT_IN_KES).append("|");
                            Object CURRENT_BALANCE = rs.getString(42).replace(".", "");
                            fileContent.append(CURRENT_BALANCE).append("|");
                            Object OVERDUE_BALANCE = rs.getString(43).replace(".", "");
                            fileContent.append(OVERDUE_BALANCE).append("|");
                            Object OVERDUE_DATE = rs.getDate(44);
                            fileContent.append(formatter.format(OVERDUE_DATE)).append("|");
                            Object DAYSINARREARS = rs.getString(45);
                            fileContent.append(DAYSINARREARS).append("|");
                            Object INSTALLMENT_IN_ARREARS = rs.getString(46);
                            fileContent.append(INSTALLMENT_IN_ARREARS).append("|");
                            Object ACCOUNT_STATUS = rs.getString(47);
                            fileContent.append(ACCOUNT_STATUS).append("|");
                            Object ACCOUNT_STATUS_DT = rs.getDate(48);
                            fileContent.append(formatter.format(ACCOUNT_STATUS_DT)).append("|");
                            Object EMPTYDATA6 = formatString(rs.getString(49));
                            fileContent.append(EMPTYDATA6).append("|");
                            Object REPAYMENT_PERIOD = rs.getString(50);
                            fileContent.append(REPAYMENT_PERIOD).append("|");
                            Object EMPTYDATA7 = formatString(rs.getString(51));
                            fileContent.append(EMPTYDATA7).append("|");
                            Object EMPTYDATA8 = formatString(rs.getString(52));
                            fileContent.append(EMPTYDATA8).append("|");
                            Object PAYMENT_FREQUENCY = rs.getString(53);
                            fileContent.append(PAYMENT_FREQUENCY).append("|");
                            Object FIRST_PMT_DATE = rs.getDate(54);
                            fileContent.append(formatter.format(FIRST_PMT_DATE)).append("|");
                            Object FIRST_PMT_AMOUNT = rs.getString(55).replace(".", "");
                            fileContent.append(FIRST_PMT_AMOUNT).append("|");

                            Object DATE_OF_LAST_PAYMENT = rs.getDate(56);
                            if (DATE_OF_LAST_PAYMENT != null)
                            {
                                fileContent.append(formatter.format(DATE_OF_LAST_PAYMENT)).append("|");
                            }
                            else
                            {
                                fileContent.append(" ").append("|");
                            }
                            Object LAST_PAYMENT_AMT = rs.getString(57).replace(".", "");
                            fileContent.append(LAST_PAYMENT_AMT).append("|");
                            Object TYPE_OF_SECURITY = rs.getString(58);
                            fileContent.append(TYPE_OF_SECURITY).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            case "CA":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\CREDIT_APP_INFO\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\CREDIT_APP_INFO");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCA" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCA" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    StringBuilder fileContent = new StringBuilder();
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object LENDERS_REG_NAME = rs.getString(1);
                            fileContent.append(LENDERS_REG_NAME).append("|");
                            Object LENDERS_TRADING_NAME = rs.getString(2);
                            fileContent.append(LENDERS_TRADING_NAME).append("|");
                            Object LENDERS_BRANCH_NAME = rs.getString(3);
                            fileContent.append(LENDERS_BRANCH_NAME).append("|");
                            Object LENDERS_BRANCH_CODE = rs.getString(4);
                            fileContent.append(LENDERS_BRANCH_CODE).append("|");
                            Object PRIMARY_IDENTIFICATION = rs.getString(5);
                            fileContent.append(PRIMARY_IDENTIFICATION).append("|");
                            Object PRIMARY_IDENTIFICATION_NUMBER = rs.getString(6);
                            fileContent.append(PRIMARY_IDENTIFICATION_NUMBER).append("|");
                            Object EMPTYDATA1 = formatString(rs.getString(7));
                            fileContent.append(EMPTYDATA1).append("|");
                            Object EMPTYDATA2 = formatString(rs.getString(8));
                            fileContent.append(EMPTYDATA2).append("|");
                            Object PRIMARY_IDENTIFICATION1 = rs.getString(9);
                            fileContent.append(PRIMARY_IDENTIFICATION1).append("|");
                            Object PRIMARY_IDENTIFICATION_NUMBER1 = rs.getString(10);
                            fileContent.append(PRIMARY_IDENTIFICATION_NUMBER1).append("|");
                            Object BRID = rs.getString(11);
                            fileContent.append(BRID).append("|");
                            Object PIN_NO = formatString(rs.getString(12));
                            fileContent.append(PIN_NO).append("|");
                            Object ACCOUNT_NUMBER = rs.getString(13);
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object APPL_DT = formatter.format(rs.getDate(14));
                            fileContent.append(APPL_DT).append("|");
                            Object APPL_TYPE = rs.getString(15);
                            fileContent.append(APPL_TYPE).append("|");
                            Object APPL_NUMBER = rs.getString(16);
                            fileContent.append(APPL_NUMBER).append("|");
                            Object FACILITY_APPL_TYPE = rs.getString(17);
                            fileContent.append(FACILITY_APPL_TYPE).append("|");
                            Object APPL_AMT = rs.getString(18).replace(".", "");
                            fileContent.append(APPL_AMT).append("|");
                            Object CRNRCY = rs.getString(19);
                            fileContent.append(CRNRCY).append("|");
                            Object PRODUCT_TYPE = rs.getString(20);
                            fileContent.append(PRODUCT_TYPE).append("|");
                            Object TERM_OF_LOAN = rs.getString(21);
                            fileContent.append(TERM_OF_LOAN).append("|");
                            Object APPL_STATUS = rs.getString(22);
                            fileContent.append(APPL_STATUS).append("|");
                            Object APPL_DECL_REASON_CODE = formatString(rs.getString(23));
                            fileContent.append(APPL_DECL_REASON_CODE).append("|");
                            Object APPL_STATUS_DT = formatter.format(rs.getDate(24));
                            fileContent.append(APPL_STATUS_DT).append("|");
                            Object EMPTYDATA3 = formatString(rs.getString(25));
                            fileContent.append(EMPTYDATA3).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            case "CI":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\COLLATERAL_INFORMATION\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\COLLATERAL_INFORMATION");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCR" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        filewriter = new FileWriter(outDir + "\\" + "CRBBCR" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    StringBuilder fileContent = new StringBuilder();
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object LENDERS_REG_NAME = rs.getString(1);
                            fileContent.append(LENDERS_REG_NAME).append("|");
                            Object LENDERS_TRADING_NAME = rs.getString(2);
                            fileContent.append(LENDERS_TRADING_NAME).append("|");
                            Object LENDERS_BRANCH_NAME = rs.getString(3);
                            fileContent.append(LENDERS_BRANCH_NAME).append("|");
                            Object LENDERS_BRANCH_CODE = rs.getString(4);
                            fileContent.append(LENDERS_BRANCH_CODE).append("|");
                            Object BRID = rs.getString(5);
                            fileContent.append(BRID).append("|");
                            Object ACCOUNT_NUMBER = rs.getString(6);
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object PRIMARY_IDENTIFICATION = formatString(rs.getString(7));
                            fileContent.append(PRIMARY_IDENTIFICATION).append("|");
                            Object PRIMARY_IDENTIFICATION_NUMBER = formatString(rs.getString(8));
                            fileContent.append(PRIMARY_IDENTIFICATION_NUMBER).append("|");
                            Object EMPTYDATA1 = formatString(rs.getString(9));
                            fileContent.append(EMPTYDATA1).append("|");
                            Object EMPTYDATA2 = formatString(rs.getString(10));
                            fileContent.append(EMPTYDATA2).append("|");
                            Object EMPTYDATA3 = formatString(rs.getString(11));
                            fileContent.append(EMPTYDATA3).append("|");
                            Object EMPTYDATA4 = formatString(rs.getString(12));
                            fileContent.append(EMPTYDATA4).append("|");
                            Object PIN_NO = formatString(rs.getString(13));
                            fileContent.append(PIN_NO).append("|");
                            Object EMPTYDATA5 = formatString(rs.getString(14));
                            fileContent.append(EMPTYDATA5).append("|");
                            Object COLLATERAL_TYPE = rs.getString(15);
                            fileContent.append(COLLATERAL_TYPE).append("|");
                            Object COLLATERAL_REF_NO = rs.getString(16);
                            fileContent.append(COLLATERAL_REF_NO).append("|");
                            Object COLLATERAL_LAST_VAL_AMT = rs.getString(17).replace(".", "");
                            fileContent.append(COLLATERAL_LAST_VAL_AMT).append("|");
                            Object COLLATERAL_CRNCY = rs.getString(18);
                            fileContent.append(COLLATERAL_CRNCY).append("|");
                            Object EMPTYDATA6 = formatString(rs.getString(19));
                            fileContent.append(EMPTYDATA6).append("|");
                            Object EMPTYDATA8 = formatString(rs.getString(20).replace(".", ""));
                            fileContent.append(EMPTYDATA8).append("|");
                            Object COLLATERAL_EXP_DT = Objects.equals(rs.getString(21), "N/A") ? "" : formatter.format(rs.getDate(21));
                            fileContent.append(COLLATERAL_EXP_DT).append("|");
                            Object EMPTYDATA7 = formatString(rs.getString(22));
                            fileContent.append(EMPTYDATA7).append("|");
                            Object LAST_VAL_DT = formatter.format(rs.getDate(23));
                            fileContent.append(LAST_VAL_DT).append("|");
                            Object SHARED_COLLATERAL = formatString(rs.getString(24));
                            fileContent.append(SHARED_COLLATERAL).append("|");
                            Object EMPTYDATA9 = formatString(rs.getString(25));
                            fileContent.append(EMPTYDATA9).append("|");
                            Object MULTIPLE_COLLATERAL = formatString(rs.getString(26));
                            fileContent.append(MULTIPLE_COLLATERAL).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            case "GI":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\GUARANTOR_INFORMATION\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\GUARANTOR_INFORMATION");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBGI" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        filewriter = new FileWriter(outDir + "\\" + "CRBBGI" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    StringBuilder fileContent = new StringBuilder();
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object LENDERS_REG_NAME = rs.getString(1);
                            fileContent.append(LENDERS_REG_NAME).append("|");
                            Object LENDERS_TRADING_NAME = rs.getString(2);
                            fileContent.append(LENDERS_TRADING_NAME).append("|");
                            Object LENDERS_BRANCH_NAME = rs.getString(3);
                            fileContent.append(LENDERS_BRANCH_NAME).append("|");
                            Object LENDERS_BRANCH_CODE = rs.getString(4);
                            fileContent.append(LENDERS_BRANCH_CODE).append("|");
                            Object BRID = rs.getString(5);
                            fileContent.append(BRID).append("|");
                            Object ACCOUNT_NUMBER = rs.getString(6);
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object SURNAME = rs.getString(7);
                            fileContent.append(SURNAME).append("|");
                            Object FORENAME_1 = rs.getString(8);
                            fileContent.append(FORENAME_1).append("|");
                            Object FORENAME_2 = rs.getString(9);
                            fileContent.append(FORENAME_2).append("|");
                            Object FORNAME_3 = formatString(rs.getString(10));
                            fileContent.append(FORNAME_3).append("|");
                            Object DATE_OF_BIRTH = formatter.format(rs.getDate(11));
                            fileContent.append(DATE_OF_BIRTH).append("|");
                            Object GENDER = rs.getString(12);
                            fileContent.append(GENDER).append("|");
                            Object NATIONALITY = rs.getString(13);
                            fileContent.append(NATIONALITY).append("|");
                            Object MARITAL_STATUS = rs.getString(14);
                            fileContent.append(MARITAL_STATUS).append("|");
                            Object GUARANTEE_TYPE = rs.getString(15);
                            fileContent.append(GUARANTEE_TYPE).append("|");
                            Object GROUP_NAME = formatString(rs.getString(16));
                            fileContent.append(GROUP_NAME).append("|");
                            Object GUARANTOR_RELATIONSHIP = rs.getString(17);
                            fileContent.append(GUARANTOR_RELATIONSHIP).append("|");
                            Object GUARANTEE_LIMIT = rs.getString(18).replace(".", "");
                            fileContent.append(GUARANTEE_LIMIT).append("|");
                            Object PRIMARY_IDENTIFICATION = rs.getString(19);
                            fileContent.append(PRIMARY_IDENTIFICATION).append("|");
                            Object PRIMARY_IDENTIFICATION_NUMBER = rs.getString(20);
                            fileContent.append(PRIMARY_IDENTIFICATION_NUMBER).append("|");
                            Object EMPTYDATA1 = formatString(rs.getString(21));
                            fileContent.append(EMPTYDATA1).append("|");
                            Object EMPTYDATA2 = formatString(rs.getString(22));
                            fileContent.append(EMPTYDATA2).append("|");
                            Object EMPTYDATA3 = formatString(rs.getString(23));
                            fileContent.append(EMPTYDATA3).append("|");
                            Object EMPTYDATA4 = formatString(rs.getString(24));
                            fileContent.append(EMPTYDATA4).append("|");
                            Object EMPTYDATA5 = formatString(rs.getString(25));
                            fileContent.append(EMPTYDATA5).append("|");
                            Object EMPTYDATA6 = formatString(rs.getString(26));
                            fileContent.append(EMPTYDATA6).append("|");
                            Object EMPTYDATA7 = formatString(rs.getString(27));
                            fileContent.append(EMPTYDATA7).append("|");
                            Object MOBILE_NUMBER = rs.getString(28).replace("07", "2547").replace("+254", "254");
                            fileContent.append(MOBILE_NUMBER).append("|");
                            Object EMPTYDATA8 = formatString(rs.getString(29));
                            fileContent.append(EMPTYDATA8).append("|");
                            Object EMPTYDATA9 = formatString(rs.getString(30));
                            fileContent.append(EMPTYDATA9).append("|");
                            Object POSTAL_ADDRESS_1 = rs.getString(31);
                            fileContent.append(POSTAL_ADDRESS_1).append("|");
                            Object POSTAL_ADDRESS_2 = formatString(rs.getString(32));
                            fileContent.append(POSTAL_ADDRESS_2).append("|");
                            Object POSTAL_LOCATION_TOWN = rs.getString(33);
                            fileContent.append(POSTAL_LOCATION_TOWN).append("|");
                            Object POSTAL_LOCATION_COUNTRY = rs.getString(34);
                            fileContent.append(POSTAL_LOCATION_COUNTRY).append("|");
                            Object POSTAL_CODE = rs.getString(35);
                            fileContent.append(POSTAL_CODE).append("|");
                            Object PHYSICAL_ADDESS_1 = rs.getString(36);
                            fileContent.append(PHYSICAL_ADDESS_1).append("|");
                            Object EMPTYDATA10 = formatString(rs.getString(37));
                            fileContent.append(EMPTYDATA10).append("|");
                            Object EMPTYDATA11 = formatString(rs.getString(38));
                            fileContent.append(EMPTYDATA11).append("|");
                            Object PHYSICAL_LOCATION_TOWN = rs.getString(39);
                            fileContent.append(PHYSICAL_LOCATION_TOWN).append("|");
                            Object PHYSICAL_LOCATION_COUNTRY = rs.getString(40);
                            fileContent.append(PHYSICAL_LOCATION_COUNTRY).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            case "SI":
            {
                try
                {
                    FileWriter filewriter;
                    java.util.Date timestamp = new java.util.Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    // String Ver = "001";
                    System.out.println("Program Start" + timestamp);

                    File outDir = new File(VNController.ReportPath + "\\STAKEHOLDER\\");
                    System.err.println(">>>>>>>>>>>> " + outDir);
                    if (!outDir.exists())
                    {
                        outDir.mkdir();
                    }
                    File[] all_files = outDir.listFiles();
                    if (all_files.length == 1)
                    {

                        String fname = all_files[0].getName();
                        System.out.println(fname);
                        String[] fname_parts = fname.split("D");
                        System.out.println(fname_parts[0].substring(14, 17));
                        int curr_version = Integer.parseInt(fname_parts[0].substring(14, 17));
                        curr_version++;

                        archivePrevFiles(VNController.ReportPath + "\\STAKEHOLDER");
                        filewriter = new FileWriter(outDir + "\\" + "CRBBSI" + formatter.format(timestamp) + format_version(curr_version) + "." + VNController.BankCode);
                    }
                    else
                    {
                        filewriter = new FileWriter(outDir + "\\" + "CRBBSI" + formatter.format(timestamp) + "001" + "." + VNController.BankCode);

                    }
                    StringBuilder fileContent = new StringBuilder();
                    try (ResultSet rs = gettDClient().getCRBData(getReportType()))
                    {

                        //=====================
                        System.err.println("Step 1 " + rs);
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        System.err.println("Step 2 " + columnCount);
                        for (int i = 1; i <= columnCount; i++)
                        {
                            columns.add(md.getColumnName(i));    //adding column name dynamicly
                            System.err.println("Step 3 " + md.getColumnName(i));
                        }
                        while (rs.next())
                        {
                            System.err.println("<< " + rs.getString(1));
                            Object SURNAME = rs.getString(1);
                            fileContent.append(SURNAME).append("|");
                            Object FORENAME1 = rs.getString(2);
                            fileContent.append(FORENAME1).append("|");
                            Object FORENAME2 = rs.getString(3);
                            fileContent.append(FORENAME2).append("|");
                            Object FORNAME3 = formatString(rs.getString(4));
                            fileContent.append(FORNAME3).append("|");
                            Object SALUTATION = rs.getString(5);
                            fileContent.append(SALUTATION).append("|");
                            Object DATE_OF_BIRTH = formatter.format(rs.getDate(6));
                            fileContent.append(DATE_OF_BIRTH).append("|");
                            Object GENDER = rs.getString(7);
                            fileContent.append(GENDER).append("|");
                            Object NATIONALITY = rs.getString(8);
                            fileContent.append(NATIONALITY).append("|");
                            Object BRID = formatString(rs.getString(9));
                            fileContent.append(BRID).append("|");
                            Object ACCOUNT_NUMBER = formatString(rs.getString(10));
                            fileContent.append(ACCOUNT_NUMBER).append("|");
                            Object PRIMARY_IDENTIFICATION = formatString(rs.getString(11));
                            fileContent.append(PRIMARY_IDENTIFICATION).append("|");
                            Object PRIMARY_ID_DOC_NUMBER = formatString(rs.getString(12));
                            fileContent.append(PRIMARY_ID_DOC_NUMBER).append("|");
                            Object SECONDARY_ID_DOC_TYPE = formatString(rs.getString(13));
                            fileContent.append(SECONDARY_ID_DOC_TYPE).append("|");
                            Object SECONDARY_ID_DOC_NUMBER = formatString(rs.getString(14));
                            fileContent.append(SECONDARY_ID_DOC_NUMBER).append("|");
                            Object OTHER_ID_DOC_TYPE = formatString(rs.getString(15));
                            fileContent.append(OTHER_ID_DOC_TYPE).append("|");
                            Object OTHER_ID_DOC_NUMBER = formatString(rs.getString(16));
                            fileContent.append(OTHER_ID_DOC_NUMBER).append("|");
                            Object EMAIL = formatString(rs.getString(17));
                            fileContent.append(EMAIL).append("|");
                            Object COMPANY_REGISTRATION_NUMBER = formatString(rs.getString(18));
                            fileContent.append(COMPANY_REGISTRATION_NUMBER).append("|");
                            Object PREVIOUS_REGISTRATION_NUMBER = formatString(rs.getString(19));
                            fileContent.append(PREVIOUS_REGISTRATION_NUMBER).append("|");
                            Object COMPANY_PIN_NUMBER = formatString(rs.getString(20));
                            fileContent.append(COMPANY_PIN_NUMBER).append("|");
                            Object COMPANY_VAT_NUMBER = formatString(rs.getString(21));
                            fileContent.append(COMPANY_VAT_NUMBER).append("|");
                            Object STAKEHOLDER_TYPE = formatString(rs.getString(22));
                            fileContent.append(STAKEHOLDER_TYPE).append("|");
                            Object PERCENTAGE_OF_SHARES = formatString(rs.getString(23));
                            fileContent.append(PERCENTAGE_OF_SHARES).append("|");
                            Object MOBILE_TEL_NUMBER = formatString(rs.getString(24)).toString().replace("07", "2547").replace("+254", "254");
                            fileContent.append(MOBILE_TEL_NUMBER).append("|");
                            Object HOME_TEL_NUMBER = formatString(rs.getString(25));
                            fileContent.append(HOME_TEL_NUMBER).append("|");
                            Object WORK_TEL_NUMBER = formatString(rs.getString(26));
                            fileContent.append(WORK_TEL_NUMBER).append("|");
                            Object POSTAL_ADDRESS_1 = formatString(rs.getString(27));
                            fileContent.append(POSTAL_ADDRESS_1).append("|");
                            Object POSTAL_ADDRESS_2 = formatString(rs.getString(28));
                            fileContent.append(POSTAL_ADDRESS_2).append("|");
                            Object TOWN = formatString(rs.getString(29));
                            fileContent.append(TOWN).append("|");
                            Object COUNTRY = formatString(rs.getString(30));
                            fileContent.append(COUNTRY).append("|");
                            Object POSTAL_CODE = formatString(rs.getString(31));
                            fileContent.append(POSTAL_CODE).append("|");
                            Object PHYSICAL_ADDESS_1 = formatString(rs.getString(32));
                            fileContent.append(PHYSICAL_ADDESS_1).append("|");
                            Object PHYSICAL_ADDESS_2 = formatString(rs.getString(33));
                            fileContent.append(PHYSICAL_ADDESS_2).append("|");
                            Object PHYSICAL_ADDESS_3 = formatString(rs.getString(34));
                            fileContent.append(PHYSICAL_ADDESS_3).append("|");
                            Object PLOT_NUMBER = formatString(rs.getString(35));
                            fileContent.append(PLOT_NUMBER).append("|");
                            Object POSTAL_LOCATION_TOWN = formatString(rs.getString(36));
                            fileContent.append(POSTAL_LOCATION_TOWN).append("|");
                            Object POSTAL_LOCATION_COUNTRY = formatString(rs.getString(37));
                            fileContent.append(POSTAL_LOCATION_COUNTRY).append("\n");
                        }

                    }
                    catch (SQLException ex)
                    {
                        System.out.println("" + ex);
                    }
                    filewriter.write(fileContent.toString());
                    filewriter.flush();
                    filewriter.close();
                    extracted = true;
                }
                catch (IOException | NumberFormatException ex)
                {
                    System.err.println("" + ex);
                }
                break;
            }
            default:
                break;
        }
        return extracted;
    }

    //export file to flatfile *** crb Kenya ***
    public void archivePrevFiles(String fromDir)
    {
        File outDir = new File(fromDir);
        System.out.println("archiving the file" + fromDir);
        File archDir = new File(fromDir + "-Archive");
        /*if (!outDir.exists() || !outDir.isDirectory()) {
         System.out.println("archiving the file....directiry found");
            //archivePrevFiles(VNController.ReportPath + "\\INDIVIDUAL_CONSUMER");
         return;
         }*/
        if (!archDir.exists())
        {
            archDir.mkdirs();
            System.out.println("archiving the file....directiry notfound");
        }

        File[] files = outDir.listFiles();
        for (File file : files)
        {
            if (!file.renameTo(new File(archDir, file.getName())))
            {
                System.out.println("archiving the file....delete files");
                file.delete();
            }
        }
    }

    public String format_version(int version)
    {
        String f_version = "";
        if (version < 10)
        {
            f_version = "00" + version;
        }
        else if (version >= 10)
        {
            f_version = "0" + version;
        }
        else if (version >= 100)
        {
            f_version = "" + version;
        }

        return f_version;
    }

    public Object formatString(Object text)
    {
        Object ftext = "";
        if (text == null || text.equals("NULL") || text.equals("null") || text.equals("n/a")
                || text.equals("N/A") || text.equals("") || text.toString().isEmpty() || text.toString().isEmpty())
        {
            ftext.equals("");
        }
        else
        {
            ftext = text;
        }
        return ftext;
    }

    public void showMessage(String title, String msg)
    {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, msg));
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
     * @return the reportType
     */
    public String getReportType()
    {
        return reportType;
    }

    /**
     * @param reportType the reportType to set
     */
    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

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
     * @return the reportpath
     */
    public String getReportpath()
    {
        return reportpath;
    }

    /**
     * @param reportpath the reportpath to set
     */
    public void setReportpath(String reportpath)
    {
        this.reportpath = reportpath;
    }

}
