package Controller;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
 public class BRLogger implements  Serializable
{
    private String indent = "    ";
    private final Long maxSize = 10485760L;
    private final AXFile brFile = new AXFile();
    public String realm = "brx", logsPath = "brx" + File.separator + "logs";
    
    public BRLogger(String realm, String logsPath)
    {
        this.realm = realm;
        this.logsPath = logsPath;
    }

    public void logDebug(Object event)
    {
        if ("Y".equalsIgnoreCase(CRBRController.EnableDebug))
        {
            logEvent(null, null, event);
        }
    }

    public void logDebug(String eventKey, Object event)
    {
        if ("Y".equalsIgnoreCase(CRBRController.EnableDebug))
        {
            logEvent(eventKey, null, event);
        }
    }

    public void logEvent(Object event)
    {
        logEvent(null, null, event);
    }

    public void logEvent(String eventKey, Object event)
    {
        logEvent(eventKey, null, event);
    }

    public void logError(String message, Throwable ex)
    {
        logEvent(null, message, ex);
    }

    public void logError(String eventKey, String message, Throwable ex)
    {
        logEvent(eventKey, message, ex);
    }

    public void logEvent(String eventKey, String message, Object event)
    {
        try
        {
            StringBuilder logEvent = new StringBuilder("<event realm=\"" + (eventKey != null ? eventKey.toLowerCase() : realm) + "\" datetime=\"" + new Date() + "\">");
            if (event instanceof Throwable)
            {
                logEvent.append("\r\n").append(getIndent()).append("<error>");
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<class>").append(((Throwable) event).getClass().getSimpleName()).append("</class>");

                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<message>").append(message == null ? "" : message).append("[ ").append(cleanText(((Throwable) event).getMessage())).append(" ]").append("</message>");
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<stacktrace>");
                for (StackTraceElement s : ((Throwable) event).getStackTrace())
                {
                    logEvent.append("\r\n").append(getIndent()).append(getIndent()).append(getIndent()).append("at ").append(s.toString());
                }
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("</stacktrace>");
                logEvent.append("\r\n").append(getIndent()).append("</error>");
                logEvent.append("\r\n").append("</event>\r\n");
            }
            else if (String.valueOf(event).trim().startsWith("<") && String.valueOf(event).trim().endsWith(">"))
            {
                logEvent.append("\r\n").append(indentAllLines(String.valueOf(event))).append("\r\n");
                logEvent.append("</event>\r\n");
            }
            else
            {
                logEvent.append("\r\n").append(getIndent()).append("<info>").append(String.valueOf(event)).append("</info>");
                logEvent.append("\r\n").append("</event>\r\n");
            }
            new Thread(()
                -> 
                {
                    writeToLog(logEvent.toString(), event instanceof Throwable);
            }).start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String indentAllLines(String text)
    {
        String line = "", buffer = "";
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
        {
            while (line != null)
            {
                buffer += getIndent() + line + "\r\n";
                line = bis.readLine();
            }
        }
        catch (IOException ex)
        {
            return buffer;
        }

        return getIndent() + buffer.trim();
    }

    private String cleanText(String text)
    {
        String line, buffer = "";
        InputStream is = new ByteArrayInputStream(String.valueOf(text).getBytes());
        try
        {
            BufferedReader bis = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = bis.readLine()) != null)
            {
                buffer += line;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return buffer;
    }

    private void archiveOldLog(String lastDate, File logs, File logFile)
    {
        rotateExistingLogs(lastDate, logs);
        try
        {
            brFile.appendToFile(logFile, "</logger>");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        File archLog = new File(logs, "events-" + lastDate + "-0.log");
        logFile.renameTo(archLog);
        try
        {
            brFile.compressFileToGzip(archLog);
            brFile.deleteFile(archLog);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        purgeOldLogs();
    }

    private void rotateExistingLogs(String lastDate, File logs)
    {
        int count = 99;
        while (count >= 0)
        {
            try
            {
                File prev = new File(logs, "events-" + lastDate + "-" + count + ".log.gz");
                if (prev.exists())
                {
                    if (count >= 99)
                    {
                        brFile.deleteFile(prev);
                    }
                    else
                    {
                        prev.renameTo(new File(logs, "events-" + lastDate + "-" + (count + 1) + ".log.gz"));
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            count--;
        }
    }

    private File getNewLog(File logsDir, File logFile)
    {
        logFile = new File(logsDir, "events.log");
        try
        {
            logFile.createNewFile();
            brFile.appendToFile(logFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            brFile.appendToFile(logFile, "<logger class=\"" + BRLogger.class.getName() + "\" datetime=\"" + new Date() + "\">");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return logFile;
    }

    private synchronized void writeToLog(String logEvent, boolean error)
    {
        try
        {
            (error ? System.err : System.out).println(logEvent);
            brFile.appendToFile(getLog(), logEvent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private File getLog()
    {
        File logs = new File(logsPath);
        if (!logs.exists())
        {
            logs.mkdirs();
        }
        File logFile = new File(logsPath, "events.log");
        if (logFile.exists())
        {
            String lastDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(logFile.lastModified()));
            if (!lastDate.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) || logFile.length() >= maxSize)
            {
                archiveOldLog(lastDate, logs, logFile);
                return getNewLog(logs, logFile);
            }
            return logFile;
        }
        return getNewLog(logs, logFile);
    }

    private void purgeOldLogs()
    {

        File logs = new File(logsPath);
        for (File log : logs.listFiles())
        {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date(log.lastModified()));
            if (Calendar.getInstance().get(Calendar.MONTH) - c1.get(Calendar.MONTH) >= 0 && Calendar.getInstance().get(Calendar.YEAR) - c1.get(Calendar.YEAR) >= CRBRController.YearsToKeepLogs)
            {
                try
                {
                    brFile.deleteFile(log);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @return the indent
     */
    public String getIndent()
    {
        return indent;
    }

    /**
     * @param indent the indent to set
     */
    public void setIndent(String indent)
    {
        this.indent = indent;
    }
}
