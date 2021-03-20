package models;

import enums.Regiao;
import org.jsoup.Jsoup;

import java.util.HashMap;

import java.util.Date;

public class EstatisticasPorRegiao {

    private       Date                          dataColeta;
    private final HashMap<Regiao, Estatisticas> estatisticas;

    private static final Regiao[] regioes    = Regiao.values();
    private static final String   USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";


    public EstatisticasPorRegiao() {
        estatisticas = new HashMap<>(regioes.length);
        preencherDados();
    }

    private void preencherDados() {
        try {
            final String url = "https://raw.githubusercontent.com/wcota/covid19br/master/cases-brazil-total.csv";
            String csv = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT)
                                           .get().body().text();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

}
