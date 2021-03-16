package models;

import java.util.Date;

public class Noticia {
  
  private String titulo;
  private Date   data;
  private String url;
  private String resumo;

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
    if (data != this.data) return false;
    this.data = data;
    return true;
  }


  public boolean setUrl(String url) {
    // TODO usar regex pra detectar se é URL válida
    if (url.isBlank()) return false; 
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
}
