package parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class G1Parser extends Parser {

    protected static final String BASE_URL = "https://g1.globo.com/bemestar/coronavirus/";
    private static int MAX_PAGES;
    private static SimpleDateFormat formatter;
    private static String postUrl;

    public G1Parser() { this(10); }

    public G1Parser(int maxPages) {
        MAX_PAGES = maxPages;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("pt","BR"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));
        postUrl = this.getPostsUrl();
    }

    public ArrayList<Noticia> getNoticias() {
        ArrayList<Noticia> noticias = new ArrayList<>();

        for (int page = 1; page <= MAX_PAGES; page++) {
            getItems(getDocument(postUrl + page)).forEach(item -> {
                try {
                    if (isAggregatedPosts(item)) return;

                    noticias.add(getNoticiaFromContent(getItemContent(item), getPostDate(item)));
                } catch (Exception e) {
                    System.out.println("AVISO: " + e.toString());
                }
            });
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
        Document document = this.getDocument(BASE_URL);

        Element scriptTag = document.selectFirst(".theme").getElementsByTag("script").first();

        String[] script = scriptTag.html().split("\"");
        return script[script.length - 2] + "/page/";
    }

    private JsonArray getItems(Document document) {
        String json = document.body().html();
        JsonObject data = JsonParser.parseString(json).getAsJsonObject();
        return data.getAsJsonArray("items");
    }

    private boolean isAggregatedPosts(JsonElement item) {
        // Aggregated posts seguem um formato diferente, por isso achei melhor simplesmente ignorar
        return item.getAsJsonObject().has("aggregatedPosts");
    }

    private Date getPostDate(JsonElement item) throws ParseException {
        return formatter.parse(((JsonObject) item).get("created").getAsString());
    }

    private JsonObject getItemContent(JsonElement item) {
        return item.getAsJsonObject().get("content").getAsJsonObject();
    }

    private Noticia getNoticiaFromContent(JsonObject content, Date date) throws Exception {
        try {
            Noticia noticia = new Noticia();

            if (noticia.setUrl(content.get("url").getAsString()) &&
                    noticia.setResumo(content.get("summary").getAsString()) &&
                    noticia.setTitulo(content.get("title").getAsString()) &&
                    noticia.setData(date)
            ) {
                return noticia;
            }
        } catch (Exception ignored) { }

        throw new Exception("Não foi possível coletar uma notícia do G1.");
    }
}
