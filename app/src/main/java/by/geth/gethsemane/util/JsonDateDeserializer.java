package by.geth.gethsemane.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonDateDeserializer implements JsonDeserializer<Date> {
    private static final String[] DATE_PATTERNS_ARRAY = new String[] {
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String str = json.getAsString();
        for (String datePattern : DATE_PATTERNS_ARRAY) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
            try {
                return dateFormat.parse(str);
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
