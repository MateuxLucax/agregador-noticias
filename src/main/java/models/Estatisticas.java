package models;

public class Estatisticas {

    private final long casos;
    private final long recuperados;
    private final long obitos;
    private final long vacinados;
    private final long segundaDose;

    public Estatisticas(long casos, long recuperados, long obitos, long vacinados, long segundaDose) {
        this.casos       = casos;
        this.recuperados = recuperados;
        this.obitos      = obitos;
        this.vacinados   = vacinados;
        this.segundaDose = segundaDose;
    }

    public long getCasos()       { return casos; }
    public long getRecuperados() { return recuperados; }
    public long getObitos()      { return obitos; }
    public long getVacinados()   { return vacinados; }
    public long getSegundaDose() { return segundaDose; }

    @Override
    public String toString() {
        return "Estatisticas{" +
                "casos=" + casos +
                ", recuperados=" + recuperados +
                ", obitos=" + obitos +
                ", vacinados=" + vacinados +
                ", segundaDose=" + segundaDose +
                '}';
    }
}
