package mg.itu.prom16;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validator {

    public static boolean validateNumber(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    public static boolean validateDate(java.sql.Date dateStr, String format) {
        if (dateStr == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            String dateString = sdf.format(dateStr);
            Date parsedDate = new Date(sdf.parse(dateString).getTime());
            return dateStr.equals(parsedDate); 
        } catch (ParseException e) {
            return false;
        }
    }
    
    public static boolean validatePrecisionAndScale(double value, int precision, int scale) {
        String[] parts = String.valueOf(value).split("\\."); 
    
        int intPartLength = parts[0].replace("-", "").length(); 
        int decimalPartLength = (parts.length > 1) ? parts[1].length() : 0; 
    
        return (intPartLength + decimalPartLength) <= precision && decimalPartLength <= scale;
    }
    
    public static boolean validateBoolean(Boolean bool) {
        if (bool == null) {
            return false;
        }
        return bool.equals(Boolean.TRUE) || bool.equals(Boolean.FALSE);
    }
    
    public static boolean validateDateHeure(Timestamp dateHeure, String format) {
        if (dateHeure == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            String dateHeureStr = sdf.format(dateHeure);
            sdf.parse(dateHeureStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean validateTemps(Time temps, String format) {
        if (temps == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            String tempsstr = sdf.format(temps);
            sdf.parse(tempsstr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
