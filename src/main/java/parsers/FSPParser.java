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

public class FSPParser extends Parser {

    protected static final String BASE_URL = "https://www1.folha.uol.com.br/cotidiano/coronavirus/";
    private static SimpleDateFormat formatter;


    public FSPParser() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("pt","BR"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));
    }

    @Override
    public Noticia getNoticia(String url) {
        Noticia noticia = new Noticia();

        for (Element news: Objects.requireNonNull(getNewsElements())) {
            try {
                Element headlineContent = getHeadlineContent(news);

                if (isSearchedUrl(headlineContent, url)) {
                    noticia = getNoticiaFromContent(headlineContent, getNewsDate(headlineContent));
                    break;
                }
            } catch (Exception e) {
                System.out.println("AVISO: " + e.toString());
            }
        }

        return noticia;
    }

    @Override
    public ArrayList<Noticia> getNoticias() {
        ArrayList<Noticia> noticias = new ArrayList<>();

        Objects.requireNonNull(getNewsElements()).forEach(news -> {
            try {
                Element headlineContent = getHeadlineContent(news);

                noticias.add(getNoticiaFromContent(headlineContent, getNewsDate(headlineContent)));
            } catch (Exception e) {
                System.out.println("AVISO: " + e.toString());
            }
        });

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        Instant pesquisaInstant = dataPesquisa.toInstant().truncatedTo(ChronoUnit.DAYS);
        ArrayList<Noticia> noticias = new ArrayList<>();

        for (Element news: Objects.requireNonNull(getNewsElements())) {
            try {
                Element headlineContent = getHeadlineContent(news);

                Date date = getNewsDate(headlineContent);

                if (pesquisaInstant.equals(date.toInstant().truncatedTo(ChronoUnit.DAYS))) {
                    noticias.add(getNoticiaFromContent(headlineContent, date));

                }
            } catch (Exception e) {
                System.out.println("AVISO: " + e.toString());
            }
        };

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        ArrayList<Noticia> noticias = new ArrayList<>();

        for (Element news: Objects.requireNonNull(getNewsElements())) {
            try {
                Element headlineContent = getHeadlineContent(news);

                Date date = getNewsDate(headlineContent);

                if (!date.before(dataInicial) || !date.after(dataFinal)) {
                    noticias.add(getNoticiaFromContent(headlineContent, date));
                }
            } catch (Exception e) {
                System.out.println("AVISO: " + e.toString());
            }
        };

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticiasRecentes() {
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);

        ArrayList<Noticia> noticias = new ArrayList<>();

        for (Element news: Objects.requireNonNull(getNewsElements())) {
            try {
                Element headlineContent = getHeadlineContent(news);

                Date date = getNewsDate(headlineContent);

                if (date.after(Date.from(yesterday))) {
                    noticias.add(getNoticiaFromContent(headlineContent, date));

                }
            } catch (Exception e) {
                System.out.println("AVISO: " + e.toString());
            }
        };

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

        throw new Exception("algo deu errado ao tentar coletar uma notícia do G1.");
    }

    private Date getNewsDate(Element headlineContent) throws ParseException {
        return formatter.parse(headlineContent.getElementsByTag("time").first().attr("datetime"));
    }

    private Element getHeadlineContent(Element news) {
        return news.selectFirst(".c-headline__content");
    }

    private boolean isSearchedUrl(Element headlineContent, String search) {
        return headlineContent.getElementsByTag("a").first().attr("href").equalsIgnoreCase(search);
    }
}
