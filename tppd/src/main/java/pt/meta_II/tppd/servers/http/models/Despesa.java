package pt.meta_II.tppd.servers.http.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Despesa {


    String quemPagou;
    String data;
    float valor;
    String descricao;
    String[] partilhas;

    public boolean verificaData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            // Parse the date string; if it's invalid, an exception will be thrown
            LocalDate parsedDate = LocalDate.parse(this.data, formatter);
            return true; // The date is valid
        } catch (DateTimeParseException e) {
            return false; // The date is invalid
        }
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
