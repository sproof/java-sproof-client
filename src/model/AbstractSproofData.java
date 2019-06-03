package at.ac.fhsalzburg.sproof.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Basisklasse fuer Daten-Klassen
 */
public abstract class AbstractSproofData implements SproofData {

    private JSONArray getArray(Field field) {
        List list = null;
        try {
            list = (List) field.get(this);
        } catch (IllegalAccessException e) {
            field.setAccessible(true);
            try {
                list = (List) field.get(this);
            } catch (IllegalAccessException e1) {
            }
            field.setAccessible(false);
        }
        JSONArray array = new JSONArray();
        for(Object o : list) {
            array.add(o);
        }
        return array;
    }

    /**
     * liefert ein JSONObject zurueck, welches alle Namen und Werte der Felder der aktuellen Klasse beinhaltet
     * @return ein JSONObject mit key, value-Paaren der Felder der aktuellen Klasse
     */
    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        Class<?> clazz = getClass();

        for(var field : clazz.getDeclaredFields()) {
            if(List.class.isAssignableFrom(field.getType())) {
                JSONArray array = getArray(field);
                if(array != null)
                    json.put(field.getName(), array);
            } else {
                Object fieldValue = null;
                try {
                    fieldValue = field.get(this);
                } catch (IllegalAccessException e) {
                    field.setAccessible(true);
                    try {
                        fieldValue = field.get(this);
                    } catch (IllegalAccessException e1) {
                        e.printStackTrace();
                    }
                    field.setAccessible(false);
                }
                if (fieldValue != null) {
                    json.put(field.getName(), fieldValue);
                }
            }
        }
        return json;
    }
}
