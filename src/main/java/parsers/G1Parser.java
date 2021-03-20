package parsers;

import com.google.gson.*;
import models.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public class G1Parser extends Parser {

    private static final String PAGE_URL = "https://g1.globo.com/bemestar/coronavirus/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static final int MAX_PAGES = 10;

    @Override
    public Noticia getNoticia(String url) {
        return null;
    }

    public ArrayList<Noticia> getNoticias() {
        ArrayList<Noticia> noticias = new ArrayList<>();
        String postUrl = this.getPostsUrl();

        for (int page = 1; noticias.size() < MAX_PAGES; page++) {

            String url = postUrl + "/page/" + page;

            Document document = this.getDocument(url);

            String json = document.body().html();
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = data.getAsJsonArray("items");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            items.forEach(item -> {
                try {
                    if (item.getAsJsonObject().has("aggregatedPosts")) {
                        // Essa cara aqui tem um esquema diferente para pegar os posts, ignorei para ter menos trabalho
                        return;
                    }
                    JsonObject content = item.getAsJsonObject().get("content").getAsJsonObject();

                    Noticia noticia = new Noticia();

                    Date modifiedAt = formatter.parse(((JsonObject) item).get("modified").getAsString());

                    if (noticia.setUrl(content.get("url").getAsString()) &&
                        noticia.setResumo(content.get("summary").getAsString()) &&
                        noticia.setTitulo(content.get("title").getAsString()) &&
                        noticia.setData(modifiedAt)
                    ) {
                        noticias.add(noticia);
                    }
                } catch (Exception ignored) {
                    System.out.println("something went wrong while parsing item: " + item.toString());
                }
            });
        }

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        Instant pesquisaInstant = dataPesquisa.toInstant()
                .truncatedTo(ChronoUnit.DAYS);

        ArrayList<Noticia> noticias = new ArrayList<>();
        String postUrl = this.getPostsUrl();
        int page = 1;

        while (noticias.size() < MAX_PAGES) {
            String url = postUrl + "/page/" + page;

            Document document = this.getDocument(url);

            String json = document.body().html();
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = data.getAsJsonArray("items");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            items.forEach(item -> {
                try {
                    Date modifiedAt = formatter.parse(((JsonObject) item).get("modified").getAsString());

                    if (!pesquisaInstant.equals(modifiedAt.toInstant().truncatedTo(ChronoUnit.DAYS))) {
                        return;
                    }

                    if (item.getAsJsonObject().has("aggregatedPosts")) {
                        // Essa cara aqui tem um esquema diferente para pegar os posts, ignorei para ter menos trabalho
                        return;
                    }

                    JsonObject content = item.getAsJsonObject().get("content").getAsJsonObject();

                    Noticia noticia = new Noticia();

                    if (noticia.setUrl(content.get("url").getAsString()) &&
                            noticia.setResumo(content.get("summary").getAsString()) &&
                            noticia.setTitulo(content.get("title").getAsString()) &&
                            noticia.setData(modifiedAt)
                    ) {
                        noticias.add(noticia);
                    }
                } catch (Exception ignored) {
                    System.out.println("something went wrong while parsing item: " + item.toString());
                }
            });
            page++;
        }

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        String postUrl = this.getPostsUrl();
        int page = 1;

        while (noticias.size() < MAX_PAGES) {
            String url = postUrl + "/page/" + page;

            Document document = this.getDocument(url);

            String json = document.body().html();
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = data.getAsJsonArray("items");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            items.forEach(item -> {
                try {
                    Date modifiedAt = formatter.parse(((JsonObject) item).get("modified").getAsString());

                    if (modifiedAt.before(dataInicial) || modifiedAt.after(dataFinal)) {
                        return;
                    }

                    if (item.getAsJsonObject().has("aggregatedPosts")) {
                        // Essa cara aqui tem um esquema diferente para pegar os posts, ignorei para ter menos trabalho
                        return;
                    }

                    JsonObject content = item.getAsJsonObject().get("content").getAsJsonObject();

                    Noticia noticia = new Noticia();

                    if (noticia.setUrl(content.get("url").getAsString()) &&
                            noticia.setResumo(content.get("summary").getAsString()) &&
                            noticia.setTitulo(content.get("title").getAsString()) &&
                            noticia.setData(modifiedAt)
                    ) {
                        noticias.add(noticia);
                    }
                } catch (Exception ignored) {
                    System.out.println("something went wrong while parsing item: " + item.toString());
                }
            });
            page++;
        }

        return noticias;
    }

    @Override
    public ArrayList<Noticia> getNoticiasRecentes() {
        Instant now = Instant.now();
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        ArrayList<Noticia> noticias = new ArrayList<>();
        String postUrl = this.getPostsUrl();
        AtomicBoolean shouldContinue = new AtomicBoolean(true);
        int page = 1;

        while (noticias.size() < MAX_PAGES || shouldContinue.get()) {
            String url = postUrl + "/page/" + page;

            Document document = this.getDocument(url);

            String json = document.body().html();
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = data.getAsJsonArray("items");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            items.forEach(item -> {
                try {
                    Date modifiedAt = formatter.parse(((JsonObject) item).get("modified").getAsString());

                    if (modifiedAt.before(Date.from(yesterday))) {
                        shouldContinue.set(false);
                        return;
                    }

                    if (item.getAsJsonObject().has("aggregatedPosts")) {
                        // Essa cara aqui tem um esquema diferente para pegar os posts, ignorei para ter menos trabalho
                        return;
                    }

                    JsonObject content = item.getAsJsonObject().get("content").getAsJsonObject();

                    Noticia noticia = new Noticia();

                    if (noticia.setUrl(content.get("url").getAsString()) &&
                            noticia.setResumo(content.get("summary").getAsString()) &&
                            noticia.setTitulo(content.get("title").getAsString()) &&
                            noticia.setData(modifiedAt)
                    ) {
                        noticias.add(noticia);
                    }
                } catch (Exception e) {
                    System.out.println("something went wrong while parsing news: " + e.getMessage());
                }
            });
            page++;
        }

        return noticias;
    }

    private Document getDocument(String url) {
        Document document = null;

        try {
            document = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT).get();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return document;
    }

    private String getPostsUrl() {
        Document document = this.getDocument(PAGE_URL);

        Element scriptTag = document.selectFirst(".theme").getElementsByTag("script").first();

        String[] script = scriptTag.html().split("\"");
        return script[script.length - 2];
    }
}
