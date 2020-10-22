package lv.datukartes.weather;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class WarningsRetriever {

    private static final String url = "https://videscentrs.lvgmc.lv/data/warnings";
    private static final String textLv = "teksts";
    private static final String textEn = "teksts_en";

    RequestQueue requestQueue;

    enum locales {
        lv,
        en
    }

    interface ResponseHandler {
        void onSuccess(ArrayList<String> data);
        void onError();
    }

    public WarningsRetriever(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void withData(WarningsRetriever.locales locale, final ResponseHandler handler) {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, WarningsRetriever.url, null, response -> {
                    try {
                        handler.onSuccess(normalizeResponse(locale, response));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> handler.onError());

        requestQueue.add(request);
    }

    public ArrayList<String> normalizeResponse(WarningsRetriever.locales locale, JSONArray response) throws JSONException {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.getJSONObject(i);
            if (locales.lv == locale) {
                list.add(item.getString(WarningsRetriever.textLv));
            } else {
                list.add(item.getString(WarningsRetriever.textEn));
            }

        }

        return list;
    }
}
