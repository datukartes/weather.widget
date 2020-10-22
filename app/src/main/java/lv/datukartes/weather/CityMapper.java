package lv.datukartes.weather;

import android.content.Context;

import java.util.HashMap;

public class CityMapper {

    private Context context;

    public CityMapper(Context context) {
        this.context = context;
    }

    public String reset(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(this.context.getResources().getString(R.string.ainazi), "Ainazi");
        map.put(this.context.getResources().getString(R.string.aluksne), "Aluksne");
        map.put(this.context.getResources().getString(R.string.kuldiga), "Kuldiga");
        map.put(this.context.getResources().getString(R.string.liepaja), "Liepaja");
        map.put(this.context.getResources().getString(R.string.mersrags), "Mersrags");
        map.put(this.context.getResources().getString(R.string.pavilosta), "Pavilosta");
        map.put(this.context.getResources().getString(R.string.priekuli), "Priekuli");
        map.put(this.context.getResources().getString(R.string.rezekne), "Rezekne");
        map.put(this.context.getResources().getString(R.string.riga), "Riga");
        map.put(this.context.getResources().getString(R.string.rujiena), "Rujiena");
        map.put(this.context.getResources().getString(R.string.sili), "Sili");
        map.put(this.context.getResources().getString(R.string.skriveri), "Skriveri");
        map.put(this.context.getResources().getString(R.string.zilani), "Zilani");
        map.put(this.context.getResources().getString(R.string.zoseni), "Zoseni");

        return map.getOrDefault(text, text);
    }

    public String localize(String key) {
        switch (key) {
            case "Ainazi":
                return this.context.getResources().getString(R.string.ainazi);
            case "Aluksne":
                return this.context.getResources().getString(R.string.aluksne);
            case "Kuldiga":
                return this.context.getResources().getString(R.string.kuldiga);
            case "Liepaja":
                return this.context.getResources().getString(R.string.liepaja);
            case "Mersrags":
                return this.context.getResources().getString(R.string.mersrags);
            case "Pavilosta":
                return this.context.getResources().getString(R.string.pavilosta);
            case "Priekuli":
                return this.context.getResources().getString(R.string.priekuli);
            case "Rezekne":
                return this.context.getResources().getString(R.string.rezekne);
            case "Riga":
                return this.context.getResources().getString(R.string.riga);
            case "Rujiena":
                return this.context.getResources().getString(R.string.rujiena);
            case "Sili":
                return this.context.getResources().getString(R.string.sili);
            case "Skriveri":
                return this.context.getResources().getString(R.string.skriveri);
            case "Zilani":
                return this.context.getResources().getString(R.string.zilani);
            case "Zoseni":
                return this.context.getResources().getString(R.string.zoseni);
            default:
                return key;
        }
    }
}
