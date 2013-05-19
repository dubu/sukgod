package com.dubu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * User: kingkingdubu
 * Date: 13. 5. 19
 * Time: 오전 8:09
 */
public class ONetworkInfo {

    public static boolean IsWifiAvailable(Context context)
    {
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;

            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            bConnect = (info.isAvailable() && info.isConnected());

        }
        catch(Exception e)
        {
            return false;
        }

        return bConnect;
    }

    public static boolean Is3GAvailable(Context context)
    {
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;
            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            bConnect = (info.isAvailable() && info.isConnected());
        }
        catch(Exception e)
        {
            return false;
        }

        return bConnect;
    }
}
