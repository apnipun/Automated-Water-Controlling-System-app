package com.app.vortex.vortex;

import com.app.vortex.vortex.app.SettingsHelper;
import com.app.vortex.vortex.app.URLHelper;
import com.google.gson.Gson;

import org.junit.Test;

import okhttp3.HttpUrl;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    public void url_is_correct(){
        String url =   "maintance/main.php";
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("http").host("localhost").addPathSegments(url).query("que=main").addQueryParameter("test","hi").build();
        System.out.println(httpUrl.toString());
    }


    public void test(){
        String url = new URLHelper.Builder().setPath("http://www.fb.com/page.php")
                .addQueryParam("test","passed")
                .addQueryParam("user","kasun")
                .build()
                .toString();

        System.out.println(url);
    }

    @Test
    public void gsonTest()
    {
        String json = "{\"user_id\":\"72\",\"notifications\":\"1\",\"auto_clean\":\"0\",\"" +
                "auto_clean_interval\":\"30\",\"auto_fill_tank\":\"1\",\"notify_on_level\":\"1\",\"" +
                "tank_full_alert\":\"1\",\"well_critical_alert\":\"1\",\"cleaning_completed_alert\":\"1\",\"" +
                "tank_empty_alert\":\"1\"}";
        Gson gson = new Gson();
        SettingsHelper helper = gson.fromJson(json, SettingsHelper.class);
        System.out.println(helper.getAuto_clean());
    }
}