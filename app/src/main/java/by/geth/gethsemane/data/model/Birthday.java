package by.geth.gethsemane.data.model;

import androidx.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Birthdays", id = "_id")
public class Birthday extends Model implements Comparable<Birthday> {

    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_PERSONS = "persons";

    @Column(name = COLUMN_MONTH)
    private int month;

    @Column(name = COLUMN_DAY)
    private int day;

    @Column(name = COLUMN_PERSONS)
    private String persons;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<String> getPersons() {
        return Arrays.asList(persons.split(", "));
    }

    public void setPersons(List<String> persons) {
        StringBuilder personsBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String person : persons) {
            if (!isFirst) {
                personsBuilder.append(", ");
            }
            personsBuilder.append(person);
            isFirst = false;
        }
        this.persons = personsBuilder.toString();
    }

    @Override
    public int compareTo(@NonNull Birthday other) {
        Calendar currentCalendar = Calendar.getInstance();

        Calendar thisCalendar = Calendar.getInstance();
        thisCalendar.set(Calendar.MONTH, this.getMonth() - 1);
        thisCalendar.set(Calendar.DAY_OF_MONTH, this.getDay());
        if (thisCalendar.get(Calendar.DAY_OF_YEAR) < currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            thisCalendar.add(Calendar.YEAR, 1);
        }

        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.set(Calendar.MONTH, other.getMonth() - 1);
        otherCalendar.set(Calendar.DAY_OF_MONTH, other.getDay());
        if (otherCalendar.get(Calendar.DAY_OF_YEAR) < currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            otherCalendar.add(Calendar.YEAR, 1);
        }

        return thisCalendar.compareTo(otherCalendar);
    }

    @Override
    public String toString() {
        return "Birthday{" +
                "month=" + month +
                ", day=" + day +
                ", persons='" + persons + '\'' +
                '}';
    }
}
