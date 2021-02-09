/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRB;

/**
 *
 * @author skamau
 */
import Controller.CRBRController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONValue;

/**
 *
 * @author skamau
 */
public class CRBProcessor
{

    private static final String API_CONTENT_TYPE = "Content-Type";
    private static final String API_KEY = "X-METROPOL-REST-API-KEY";
    private static final String API_HASH = "X-METROPOL-REST-API-HASH";
    private static final String API_TIMESTAMP = "X-METROPOL-REST-API-TIMESTAMP";
    private static final String CONTENT_TYPE = "application/json";
    private static final String REPORT_TYPE = "report_type";
    private static final String IDENTITY_NUMBER = "identity_number";
    private static final String IDENTITY_TYPE = "identity_type";
    private static final String LOAN_AMT = "loan_amount";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmsss");

    public String CRBQueryResponse(String IdNumber, int loanAmount)
    {
        String output;
        CRBProcessor req = new CRBProcessor();
        output = req.connectToService(IdNumber, loanAmount);
        CRBRController.bRLogger.logEvent(output);
        return output;
    }

    public String connectToService(String IdNumber, int loanAmount)
    {

        String response = null;
        String restUrl = CRBRController.CRBUrl;
        String publickey = CRBRController.CRBPublicKey;
        String privatekey = CRBRController.CRBPrivateKey;
        try
        {
            CRBRController.bRLogger.logEvent("restUrl", restUrl);
            CRBRController.bRLogger.logEvent("publickey", publickey);
            CRBRController.bRLogger.logEvent("privatekey", privatekey);
            String time = getUTCdatetimeAsString();
            Map<String, String> headers = new HashMap();
            StringWriter out = new StringWriter();
            JSONValue.writeJSONString(mapCrbParameters(IdNumber, loanAmount), out);
            String jsonData = out.toString();

            CRBRController.bRLogger.logEvent("TIME LENGTH", "length - " + time.length());
            String hash256 = privatekey + jsonData + publickey + time;
            String hashString = getSHA256Hash(hash256).toLowerCase();
            CRBRController.bRLogger.logEvent("hash256", hash256);

            headers.put(API_CONTENT_TYPE, CONTENT_TYPE);
            headers.put(API_KEY, publickey);
            headers.put(API_HASH, hashString);
            headers.put(API_TIMESTAMP, time);

            CRBRController.bRLogger.logEvent("HEADER", "Headers: " + headers.toString());
            response = httpsPOSTRequest(restUrl, jsonData, headers);
            CRBRController.bRLogger.logEvent("RESPONSE", response);

        }
        catch (Exception ex)
        {
            CRBRController.bRLogger.logError("connectToService", ex);
        }
        return response;
    }

    public String httpsPOSTRequest(String httpsurl, String message, Map<String, String> headers)
    {
        String response = "";
        String res;
        try
        {
            SSLContext ssl_ctx = SSLContext.getInstance("TLS");
            TrustManager[] trust_mgr = getTrustManager();

            ssl_ctx.init(null, trust_mgr, new SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

            java.net.URL url = new java.net.URL(httpsurl);
            javax.net.ssl.HttpsURLConnection conn = url.openConnection() instanceof javax.net.ssl.HttpsURLConnection ? (javax.net.ssl.HttpsURLConnection) url.openConnection() : null;

            if (headers != null && !headers.isEmpty())
            {
                for (Map.Entry<String, String> entry : headers.entrySet())
                {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(message);
            osw.flush();

            try (InputStreamReader in = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
            {
                try (BufferedReader reader = new BufferedReader(in))
                {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    boolean cont = true;
                    while (cont)
                    {
                        line = reader.readLine();
                        if (line != null)
                        {
                            sb.append(line);
                        }
                        else
                        {
                            cont = false;
                        }
                    }
                    res = sb.toString();
                }
            }
            response = res;
        }
        catch (Exception ex)
        {
            Logger.getLogger(CRBProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public Map mapCrbParameters(String IdNumber, int loanAmount)
    {
        Map jsonLoad = new LinkedHashMap();
        jsonLoad.put(REPORT_TYPE, 2);
        jsonLoad.put(IDENTITY_NUMBER, IdNumber);
        jsonLoad.put(IDENTITY_TYPE, "001");
        jsonLoad.put(LOAN_AMT, loanAmount);
        return jsonLoad;
    }

    private String getSHA256Hash(String data)
    {
        String result = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        }
        catch (NoSuchAlgorithmException ex)
        {
            CRBRController.bRLogger.logError("Hashing ", ex);
        }
        return result;
    }

    public String getUTCdatetimeAsString()
    {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return utcTime;
    }

    private String bytesToHex(byte[] hash)
    {
        return DatatypeConverter.printHexBinary(hash);
    }

    private static TrustManager[] getTrustManager()
    {
        TrustManager[] certs = new TrustManager[]
        {
            new X509TrustManager()
            {
                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String t)
                {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String t)
                {
                }

            }
        };
        return certs;
    }
}
