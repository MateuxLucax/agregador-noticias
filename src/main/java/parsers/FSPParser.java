package parsers;

import models.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FSPParser extends Parser {

    protected static final String BASE_URL = "https://www1.folha.uol.com.br/cotidiano/coronavirus/";
    private static SimpleDateFormat formatter;


    public FSPParser() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("pt","BR"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));
    };

    @Override
    public ArrayList<Noticia> getNoticias() {
        ArrayList<Noticia> noticias = new ArrayList<>();

        Objects.requireNonNull(getNewsElements()).forEach(news -> {
            try {
                if (hasOutBrain(news)) return;

                Element headlineContent = getHeadlineContent(news);

                noticias.add(getNoticiaFromContent(headlineContent, getNewsDate(headlineContent)));
            } catch (Exception e) {
                String errorMessage = e.toString();
                if (hasSubscriberOnlyContent(news)) errorMessage = "Conteúdo apenas para assinantes";

                System.out.println("AVISO [FSP]: " + errorMessage);
            }
        });

        return noticias;
    }

    private Elements getNewsElements() {
        try {
            Document document = Jsoup.connect(BASE_URL).ignoreContentType(true).userAgent(USER_AGENT).get();

            Element body = document.body();
            return body.select(".u-list-unstyled").get(1).getElementsByTag("li");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    private Noticia getNoticiaFromContent(Element headlineContent, Date date) throws Exception {
        Noticia noticia = new Noticia();

        if (noticia.setUrl(headlineContent.getElementsByTag("a").first().attr("href")) &&
            noticia.setResumo(headlineContent.getElementsByTag("p").first().text()) &&
            noticia.setTitulo(headlineContent.getElementsByTag("h2").first().text()) &&
            noticia.setData(date)
        ) {
            return noticia;
        }

        throw new Exception("Não foi possível coletar uma notícia da Folha de São Paulo.");
    }

    private Date getNewsDate(Element headlineContent) throws ParseException {
        return formatter.parse(headlineContent.getElementsByTag("time").first().attr("datetime"));
    }

    private Element getHeadlineContent(Element news) {
        return news.selectFirst(".c-headline__content");
    }

    private boolean hasOutBrain(Element news) {
        return !news.select(".OUTBRAIN").isEmpty();
    }

    private boolean hasSubscriberOnlyContent(Element news) {
        return news.text().contains("Recurso exclusivo para assinantes");
    }
}
