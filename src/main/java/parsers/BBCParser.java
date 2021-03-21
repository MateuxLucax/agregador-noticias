package parsers;

import models.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BBCParser extends Parser {
    protected static final String BASE_URL = "https://www.bbc.com/portuguese/topics/clmq8rgyyvjt/page/";
    private static int MAX_PAGES;
    private static SimpleDateFormat formatter;

    public BBCParser() {
        this(10);
    }

    public BBCParser(int maxPages) {
        MAX_PAGES = maxPages;
        formatter = new SimpleDateFormat("HH:mm dd MMMM yyyy", new Locale("pt","BR"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));
    }

    @Override
    public ArrayList<Noticia> getNoticias() {
        ArrayList<Noticia> noticias = new ArrayList<>();

        for (int page = 1; page <= MAX_PAGES; page++) {
            Objects.requireNonNull(getNewsElements(BASE_URL + page)).forEach(item -> {
                try {
                    noticias.add(getNoticiaFromContent(item, getNewsDate(item)));
                } catch (Exception e) {
                    System.out.println("AVISO: " + e.toString());
                }
            });
        }

        return noticias;
    }

    private Elements getNewsElements(String url) {
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT).get();

            Element body = document.body();
            return body.select("ol.lx-stream__feed").first().select("li.lx-stream__post-container");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    private Date getNewsDate(Element item) throws ParseException {
        try {
            return formatter.parse(item.getElementsByTag("time").first().getElementsByTag("span").last().text());
        } catch (ParseException e) {
            Date today = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS));
            SimpleDateFormat todayFormatter = new SimpleDateFormat( " dd MMMM yyyy", new Locale("pt","BR"));
            return formatter.parse(item.getElementsByTag("time").first().getElementsByTag("span").last().text() + todayFormatter.format(today));
        }
    }

    private Noticia getNoticiaFromContent(Element item, Date date) throws Exception {
        Noticia noticia = new Noticia();

        if (noticia.setUrl("https://www.bbc.com" + item.select("a.qa-heading-link").attr("href")) &&
            noticia.setResumo(getNewsSummary(item)) &&
            noticia.setTitulo(item.getElementsByTag("h3").first().text()) &&
            noticia.setData(date)
        ) {
            return noticia;
        }

        throw new Exception("Não foi possível coletar uma notícia da BBC.");
    }

    private String getNewsSummary(Element item) {
        Elements summaryElements = item.select("p.qa-story-summary");
        if (summaryElements.isEmpty()) {
            return "Não foi possível obter o resumo dessa notícia.";
        }

        return summaryElements.first().text();
    }
}
