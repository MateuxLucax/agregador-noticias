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
import java.util.concurrent.atomic.AtomicReference;

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
    public Noticia getNoticia(String url) {
        AtomicReference<Noticia> noticia = new AtomicReference<>(new Noticia());
        int page = 1;

        while (page <= MAX_PAGES) {
            Objects.requireNonNull(getNewsElements(BASE_URL + page)).forEach(item -> {
                try {
                    if(isSearchedUrl(item, url)) {
                        noticia.set(getNoticiaFromContent(item, getNewsDate(item)));
                    }
                } catch (Exception e) {
                    System.out.println("AVISO: " + e.toString());
                }
            });
            page++;
        }

        return noticia.get();
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

    @Override
    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        Instant pesquisaInstant = dataPesquisa.toInstant().truncatedTo(ChronoUnit.DAYS);
        ArrayList<Noticia> noticias = new ArrayList<>();
        int page = 1;

        while (page <= MAX_PAGES) {
            Objects.requireNonNull(getNewsElements(BASE_URL + page)).forEach(item -> {
                try {
                    Date date = getNewsDate(item);

                    if (!pesquisaInstant.equals(date.toInstant().truncatedTo(ChronoUnit.DAYS))) return;

                    noticias.add(getNoticiaFromContent(item, date));
                } catch (Exception e) {
                    System.out.println("AVISO: " + e.toString());
                }
            });
            page++;
        }

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        int page = 1;

        while (page <= MAX_PAGES) {
            Objects.requireNonNull(getNewsElements(BASE_URL + page)).forEach(item -> {
                try {
                    Date date = getNewsDate(item);

                    if (date.before(dataInicial) || date.after(dataFinal)) return;

                    noticias.add(getNoticiaFromContent(item, date));
                } catch (Exception e) {
                    System.out.println("AVISO: " + e.toString());
                }
            });
            page++;
        }

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticiasRecentes() {
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        ArrayList<Noticia> noticias = new ArrayList<>();
        int page = 1;

        while (page <= MAX_PAGES) {
            Objects.requireNonNull(getNewsElements(BASE_URL + page)).forEach(item -> {
                try {
                    Date date = getNewsDate(item);

                    if (date.before(Date.from(yesterday))) {
                        return;
                    }

                    noticias.add(getNoticiaFromContent(item, date));
                } catch (Exception e) {
                    System.out.println("something went wrong while parsing news: " + e.getMessage());
                }
            });
            page++;
        }

        return noticias;
    }

    private Elements getNewsElements(String url) {
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT).get();

            Element body = document.body();
            return body.select("ol.lx-stream__feed").first().getElementsByTag("li");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    private Date getNewsDate(Element item) throws ParseException {
        return formatter.parse(item.getElementsByTag("time").first().getElementsByTag("span").last().text());
    }

    private Noticia getNoticiaFromContent(Element item, Date date) throws Exception {
        Noticia noticia = new Noticia();

        if (noticia.setUrl("https://www.bbc.com" + item.select("a.qa-heading-link").attr("href")) &&
            noticia.setResumo(item.select("p.qa-story-summary").first().text()) &&
            noticia.setTitulo(item.getElementsByTag("h3").first().text()) &&
            noticia.setData(date)
        ) {
            return noticia;
        }

        throw new Exception("something went wrong while getting news BBC.");
    }

    private boolean isSearchedUrl(Element item, String search) {
        return ("https://www.bbc.com/" + item.select("a.qa-heading-link").attr("href")).equalsIgnoreCase(search);
    }
}
