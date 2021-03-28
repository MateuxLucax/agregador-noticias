package models;

import parsers.Parser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Jornal {

    private final String             nome;
    private final String             url;
    private       Parser             parser;
    private       boolean            seguido;
    private final ArrayList<Noticia> noticias;

    public Jornal(String nome, String url) {
        this.nome     = nome;
        this.url      = url;
        this.seguido  = true;
        this.noticias = new ArrayList<>();
    }

    public Jornal(String nome, String url, Parser parser) {
        this.nome     = nome;
        this.url      = url;
        this.parser   = parser;
        this.seguido  = true;
        this.noticias = new ArrayList<>();
        noticias.addAll(parser.getNoticias());
    }

    public void setSeguido(boolean seguido) { this.seguido = seguido; }

    public String  getNome()   { return nome; }
    public String  getUrl()    { return url; }
    public boolean isSeguido() { return seguido; }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    public boolean addNoticia(Noticia noticia) {
        if (this.noticias.contains(noticia)) return false;
        this.noticias.add(noticia);
        return true;
    }

    private ArrayList<Noticia> filtrarNoticias(Predicate<Noticia> predicate) {
        return noticias
                .stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Noticia> getNoticiasRecentes() {
        Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        return filtrarNoticias(noticia -> noticia.getData().after(yesterday));
    }

    public ArrayList<Noticia> getNoticias(String titulo) {
        return filtrarNoticias(noticia -> noticia.getTitulo().contains(titulo)) ;
    }

    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        Instant pesquisaInstant = dataPesquisa.toInstant().truncatedTo(ChronoUnit.DAYS);
        return filtrarNoticias(noticia -> pesquisaInstant.equals(noticia.getData().toInstant().truncatedTo(ChronoUnit.DAYS)));
    }

    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        dataInicial.toInstant().truncatedTo(ChronoUnit.DAYS);
        dataFinal.toInstant().truncatedTo(ChronoUnit.DAYS);
        return filtrarNoticias(noticia -> (noticia.getData().after(dataInicial) && noticia.getData().before(dataFinal)));
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
