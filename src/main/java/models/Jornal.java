package models;

import org.w3c.dom.CDATASection;
import parsers.Parser;
import utils.DateUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Jornal {

    private final String             nome;
    private final String             url;
    private       Parser             parser;
    private boolean                  seguido = true;
    private final ArrayList<Noticia> noticias;

    public Jornal(String nome, String url) {
        this.nome     = nome;
        this.url      = url;
        this.noticias = new ArrayList<>();
    }

    public Jornal(String nome, String url, Parser parser) {
        this(nome, url);
        this.parser   = parser;
        noticias.addAll(parser.getNoticias());
    }

    public void setSeguido(boolean seguido) { this.seguido = seguido; }

    public String  getNome()   { return nome; }
    public String  getUrl()    { return url; }
    public boolean isSeguido() { return seguido; }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    private ArrayList<Noticia> filtrarNoticias(Predicate<Noticia> predicate) {
        return noticias
                .stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Noticia> getNoticiasRecentes() {
        Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS).atZone(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()).toInstant());
        return filtrarNoticias(noticia -> noticia.getData().after(yesterday));
    }

    public ArrayList<Noticia> getNoticias(String titulo) {
        return filtrarNoticias(noticia -> noticia.getTitulo().toLowerCase().contains(titulo.toLowerCase()));
    }

    public ArrayList<Noticia> getNoticias(Date dataPesquisa) {
        return filtrarNoticias(noticia -> {
            LocalDate pesquisa = DateUtil.dateToLocalDate(dataPesquisa);
            LocalDate dataNoticia = DateUtil.dateToLocalDate(noticia.getData());
            return dataNoticia.isEqual(pesquisa);
        });
    }

    public ArrayList<Noticia> getNoticias(Date dataInicial, Date dataFinal) {
        LocalDate dataInicialLocal = DateUtil.dateToLocalDate(dataInicial);
        LocalDate dataFinalLocal = DateUtil.dateToLocalDate(dataFinal);

        return filtrarNoticias(noticia -> {
            LocalDate dataNoticia = DateUtil.dateToLocalDate(noticia.getData());
            return (dataNoticia.isAfter(dataInicialLocal) || dataNoticia.isEqual(dataInicialLocal)) && (dataNoticia.isBefore(dataFinalLocal) || dataNoticia.isEqual(dataFinalLocal));
        });
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
