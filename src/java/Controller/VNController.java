package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Neptune-Njinu
 */
public class VNController
{

    private static Properties settings;
    public static String EnableDebug = "Y";
    public static int YearsToKeepLogs;
    public static String bankName = "Vision Finance Bank";
    public static String CoreSchemaName, CoreWsdlURL, CMSchemaJdbcUrl, CMSchemaName, CMSchemaPassword;
    public static final String confDir = "nida/conf", logsDir = "nida/logs";
    public static int DisplayLines = 500;
    public static BRLogger bRLogger = new BRLogger("nida", "logs");
    public static String NidaPublicKey, NidaPrivateKey, NidaUrl;

    public static void initialize()
    {
        configure();

    }

    public static void configure()
    {
        FileInputStream in;
        settings = new Properties();

        try
        {
            new File(logsDir).mkdirs();

            new File(confDir).mkdirs();

            File propsFile = new File(confDir, "settings.prp");
            System.err.println(">>>>>>>>>>>>>>>>>>>> " + propsFile.getAbsolutePath());
            if (!propsFile.exists())
            {
                System.out.println("Missing bridge configuration file. Unable to load bridge settings...");
                bRLogger.logDebug("Missing bridge configuration file. Unable to load bridge settings...");
            }
            in = new FileInputStream(propsFile);
            settings.loadFromXML(in);

            CoreSchemaName = settings.getProperty("CoreSchemaName");
            CMSchemaPassword = BRCrypt.decrypt(settings.getProperty("CMSchemaPassword"));

            CMSchemaJdbcUrl = settings.getProperty("CMSchemaJdbcUrl");
          //  ReportPath = settings.getProperty("ReportPath");
            //CoreWsdlURL = settings.getProperty("CoreWsdlURL");

            EnableDebug = settings.getProperty("EnableDebug", "N");
            CMSchemaName = settings.getProperty("CMSchemaName");

            //JdbcDriverName = settings.getProperty("JdbcDriverName");
           // AllowedLoginRoles = settings.getProperty("AllowedLoginRoles");

           // BankCode = settings.getProperty("BankCode");
            NidaPublicKey = settings.getProperty("NidaPublicKey");
            
            NidaPrivateKey = settings.getProperty("NidaPrivateKey");
           NidaUrl = settings.getProperty("NidaUrl");
            try
            {
                DisplayLines = Integer.parseInt(settings.getProperty("DisplayLines"));
            }
            catch (Exception ex)
            {
                DisplayLines = 500;

                System.err.println("" + ex);

            }
            in.close();
        }
        catch (Exception ex)
        {
            bRLogger.logError("Exception", ex);
            System.err.println("" + ex);

        }

    }
}
