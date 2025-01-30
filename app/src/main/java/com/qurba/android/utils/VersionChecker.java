package com.qurba.android.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.network.QurbaLogger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;

    @Override
    protected String doInBackground(String... params) {

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.qurba.android")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();

            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
            else
                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                        Constants.USER_VERSION_CHECKING_FAIL, Line.LEVEL_ERROR,
                        "User attempt to check for app version ", "document is null");
        } catch (IOException e) {
            e.printStackTrace();

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_VERSION_CHECKING_FAIL, Line.LEVEL_ERROR,
                    "User failed to check for app version ", Log.getStackTraceString(e));
        }
        return newVersion;
    }
}