package pt.meta_II.tppd.servers.http.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Verificacoes {

    //inclui letras ou numeros,@, letras,., 2 ou 4 letras
    private Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z]+\\.[A-Za-z]{2,4}");
    //digito de 0-9, ocorre 9 vezes
    private Pattern telemovelPattern = Pattern.compile("^[0-9]{9}");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");


    public boolean verificaData(String data) {
        try {
            // Parse the date string; if it's invalid, an exception will be thrown
            LocalDate parsedDate = LocalDate.parse(data, formatter);
            return true; // The date is valid
        } catch (DateTimeParseException e) {
            return false; // The date is invalid
        }
    }

    public boolean verificaTelefone(String telefone) {
        return telemovelPattern.matcher(telefone).matches();
    }

    public boolean verificaEmail(String email) {
        return emailPattern.matcher(email).matches();
    }
}
