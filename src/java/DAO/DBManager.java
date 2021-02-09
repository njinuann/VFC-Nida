package DAO;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author neptune
 */
public class DBManager implements Serializable
{

    static ArrayList<TDClient> dBUtilities = new ArrayList<>();
    static int connectinPoolSize = 10;

    public static void initialise()
    {
        for (int i = 0; i < connectinPoolSize; i++)
        {
            TDClient dBUtility = new TDClient();
            dBUtilities.add(dBUtility);
        }
    }

    public static synchronized TDClient fetchDBUtility()
    {
        TDClient DBUtility = null;
        if (dBUtilities.size() > 0)
        {
            DBUtility = dBUtilities.get(0);
            dBUtilities.remove(DBUtility);
        }
        else
        {
            DBUtility = new TDClient();
        }
        return DBUtility;
    }

    public static synchronized void releaseDBUtility(TDClient DBUtility)
    {
        if (dBUtilities.size() < connectinPoolSize)
        {
            dBUtilities.add(DBUtility);
        }
        else
        {
            DBUtility.dispose();
        }
    }

    public static synchronized void disposeProcessors()
    {
        for (TDClient dbUtility : dBUtilities)
        {
            try
            {
                dbUtility.dispose();
            }
            catch (Exception ex)
            {
            }
        }
    }

}
