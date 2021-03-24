import enums.Regiao;
import models.Estatisticas;
import org.jsoup.Jsoup;

import java.text.DecimalFormat;
import java.util.HashMap;

public class EstatisticasPorRegiao {

    private        final HashMap<Regiao, Estatisticas> estatisticas;
    private static final Regiao[] REGIOES    = Regiao.values();
    private static final String   USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static       EstatisticasPorRegiao instance;

    public static EstatisticasPorRegiao getInstance() {
        if (instance == null)
            instance = new EstatisticasPorRegiao();
        return instance;
    }

    private EstatisticasPorRegiao() {
        estatisticas = new HashMap<>(REGIOES.length);
        preencherDados();
    }

    private Regiao csvStringToRegiao(String s) {
        Regiao regiao = Regiao.BRASIL;
        for (Regiao r : REGIOES)     // "TOTAL" -> BRASIL pelo valor padrão
            if (s.equals(r.name()))  // O resto "SP" -> SP etc.
                regiao = r;
        return regiao;
    }

    private void preencherDados() {
        try {
            final String url = "https://raw.githubusercontent.com/wcota/covid19br/master/cases-brazil-total.csv";
            String csv = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT)
                                           .get().body().text();
            String[] linhas = csv.split(" ");
            for (int i=1; i<linhas.length; i++) {  // Ignora 1a linha (cabeçalho)
                String[] valores = linhas[i].split(",");

                Regiao regiao = csvStringToRegiao(valores[1]);
                long casos       = Long.parseLong(valores[2]);
                long obitos      = Long.parseLong(valores[5]);
                long recuperados = Long.parseLong(valores[11]);
                long vacinados   = Long.parseLong(valores[15]);
                long segundaDose = Long.parseLong(valores[17]);

                estatisticas.put(regiao, new Estatisticas(casos, obitos, recuperados, vacinados, segundaDose));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public Estatisticas getEstatisticas(Regiao r) {
        return estatisticas.get(r);
    }

    public void printTabela() {
        DecimalFormat df = new DecimalFormat();
        df.setGroupingSize(3);

        System.out.printf("%8s%13s%13s%13s%13s%13s\n",
                "Região", "Casos", "Óbitos", "Recuperados", "Vacinados", "Segunda dose");
        for (Regiao r : estatisticas.keySet()) {
            Estatisticas e = estatisticas.get(r);
            System.out.printf("%8s%13s%13s%13s%13s%13s\n",
                    r.name(),
                    df.format(e.getCasos()),
                    df.format(e.getObitos()),
                    df.format(e.getRecuperados()),
                    df.format(e.getVacinados()),
                    df.format(e.getSegundaDose()));
        }
    }

}
