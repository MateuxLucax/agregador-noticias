package models;

import parsers.Parser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Jornal {

    private final String             nome;
    private final String             url;
    private final Parser             parser;
    private       boolean            seguido;
    private final ArrayList<Noticia> noticias = new ArrayList<>();

    public Jornal(String nome, String url, Parser parser) {
        this.nome    = nome;
        this.url     = url;
        this.parser  = parser;
        this.seguido = true;
        noticias.addAll(parser.getNoticias());
    }

    public void seguir()    { seguido = true; }
    public void naoSeguir() { seguido = false; }

    public String  getNome()   { return nome; }
    public String  getUrl()    { return url; }
    public boolean seguido()   { return seguido; }

    public ArrayList<Noticia> getNoticiasRecentes() {
        Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));

        return noticias
                .stream()
                .filter(noticia -> noticia.getData().after(yesterday))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    public ArrayList<Noticia> getNoticias(String titulo) {
        return noticias
                .stream()
                .filter(noticia -> noticia.getTitulo().contains(titulo))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        Instant pesquisaInstant = dataPesquisa.toInstant().truncatedTo(ChronoUnit.DAYS);

        return noticias
                .stream()
                .filter(noticia -> pesquisaInstant.equals(noticia.getData().toInstant().truncatedTo(ChronoUnit.DAYS)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        dataInicial.toInstant().truncatedTo(ChronoUnit.DAYS);
        dataFinal.toInstant().truncatedTo(ChronoUnit.DAYS);

        return noticias
                .stream()
                .filter(noticia -> (noticia.getData().after(dataInicial) && noticia.getData().before(dataFinal)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jornal jornal = (Jornal) o;
        return url.equals(jornal.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Jornal{" +
                "nome='" + nome + '\'' +
                ", url='" + url + '\'' +
                ", seguido=" + seguido +
                ", noticias=" + noticias +
                '}';
    }
}
