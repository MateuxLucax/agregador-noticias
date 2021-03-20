package models;

import enums.Regiao;

import java.util.Date;

public class Estatisticas
{
    private final Regiao regiao;
    private Date   dataColeta;
    private int    casos;
    private int    recuperados;
    private int    obitos;
    private int    vacinados;
    private int    segundaDose;

    public Estatisticas(Regiao regiao) {
        this.regiao = regiao;
        preencherDados();
    }

    private void preencherDados() {
        // a partir da regiao,
        // busca em algum site o número de casos, recuperados e óbitos,
        // assim como número de vacinados e pessoas que receberam a segunda dose da vacina
        // e carrega os números nos atributos correspondentes
        // dataColeta é a data informada na fonte em que os dados foram atualizados pela última vez
    }

    public Regiao getRegiao()      { return regiao; }
    public Date   getDataColeta()  { return dataColeta; }
    public int    getCasos()       { return casos; }
    public int    getRecuperados() { return recuperados; }
    public int    getObitos()      { return obitos; }
    public int    getVacinados()   { return vacinados; }
    public int    getSegundaDose() { return segundaDose; }
}
