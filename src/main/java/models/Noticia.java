package models;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Noticia {
  
  private String titulo;
  private Date   data;
  private String url;
  private String resumo;

  public Noticia() {

  }

  public Noticia(String titulo, Date data, String url, String resumo) {
    this.titulo = titulo;
    this.data = data;
    this.url = url;
    this.resumo = resumo;
  }
  
  public String getTitulo() { return titulo; }
  public Date   getData()   { return data; }
  public String getUrl()    { return url; }
  public String getResumo() { return resumo; }

  public boolean setTitulo(String titulo) {
    if (titulo.isBlank()) return false;
    this.titulo = titulo;
    return true;
  }

  public boolean setData(Date data) {
    if (this.data != null && this.data.after(data)) return false;
    this.data = data;
    return true;
  }

  public boolean setUrl(String url) {
    if (url.isBlank() || !isValidURL(url)) return false;
    this.url = url;
    return true;
  }


  public boolean setResumo(String resumo) {
    if (resumo.isBlank()) return false;  
    this.resumo = resumo;
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Noticia noticia = (Noticia) o;
    return url.equals(noticia.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public String toString() {
    return "Noticia{" +
            "titulo='" + titulo + '\'' +
            ", data=" + data +
            ", url='" + url + '\'' +
            ", resumo='" + resumo + '\'' +
            '}';
  }

  public static boolean isValidURL(String url) {
    String regex = "((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)";

    Pattern pattern = Pattern.compile(regex);

    if (url == null) return false;

    Matcher matcher = pattern.matcher(url);

    return matcher.matches();
  }
}
