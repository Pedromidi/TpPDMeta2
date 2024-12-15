package pt.meta_II.tppd.clienteHTTP;

public class Despesa {

    String quemPagou;
    float valor;
    String data;
    String descricao;
    String[] partilhas;

    public Despesa( String quemPagou, String data, float valor, String descricao, String[] partilhas) {
        this.quemPagou = quemPagou;
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
        this.partilhas = partilhas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuemPagou() {
        return quemPagou;
    }

    public void setQuemPagou(String quemPagou) {
        this.quemPagou = quemPagou;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String[] getPartilhas() {
        return partilhas;
    }

    public void setPartilhas(String[] partilhas) {
        this.partilhas = partilhas;
    }

}
