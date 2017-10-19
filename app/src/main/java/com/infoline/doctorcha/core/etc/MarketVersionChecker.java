package com.infoline.doctorcha.core.etc;

import android.provider.DocumentsContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017-07-06.
 */

public class MarketVersionChecker {


    public static String getMarketVersion(final String packageName) {
        try {
            final Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
            final Elements Version = doc.select(".content");

            for (Element mElement : Version) {
                if (mElement.attr("itemprop").equals("softwareVersion")) {
                    return mElement.text().trim();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getMarketVersionFast(final String packageName) {
        String mData = "";
        String mVer = null;

        try {
            final URL mUrl = new URL("https://play.google.com/store/apps/details?id=" + packageName);
            final HttpURLConnection mConnection = (HttpURLConnection) mUrl.openConnection();

            if (mConnection == null) return null;

            mConnection.setConnectTimeout(5000);
            mConnection.setUseCaches(false);
            mConnection.setDoOutput(true);

            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final BufferedReader mReader = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));

                while (true) {
                    String line = mReader.readLine();
                    if (line == null)
                        break;
                    mData += line;
                }

                mReader.close();
            }

            mConnection.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        final String startToken = "softwareVersion\">";
        final String endToken = "<";
        final int index = mData.indexOf(startToken);

        if (index == -1) {
            mVer = null;

        } else {
            mVer = mData.substring(index + startToken.length(), index + startToken.length() + 100);
            mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
        }

        return mVer;
    }
}