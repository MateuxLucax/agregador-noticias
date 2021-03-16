package models;

import java.util.Date;

public class Estatisticas
{
    private final Regiao regiao;
    private final Date   dataColeta;
    private final int    casos;
    private final int    recuperados;
    private final int    obitos;
    private final int    vacinados;
    private final int    segundaDose;

    public Contagem(Regiao regiao) {
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
