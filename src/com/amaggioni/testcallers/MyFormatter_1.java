package com.amaggioni.testcallers;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author magang
 */
public class MyFormatter_1 extends Formatter
{

    @Override
    public String format(LogRecord record)
    {
        // Zeichenfolgepuffer für formatierten Datensatz erstellen.
        // Mit Datum anfangen.
        StringBuffer sb = new StringBuffer();

        // Datum aus dem Protokollsatz abrufen und dem Puffer hinzufügen
        Date date = new Date(record.getMillis());
        sb.append(date.toString());
        sb.append(" ");

        // Aufrufenden
        sb.append(record.getSourceClassName());
        sb.append(" ");

        // Versionsnamen abrufen und dem Puffer hinzufügen
        sb.append(record.getLevel().getName());
        sb.append(" ");


        // Formatierte Nachricht abrufen (einschließlich Lokalisierung
        // und Substitution von Parametern) und dem Puffer hinzufügen
        sb.append(formatMessage(record));
        sb.append("\n");

        return sb.toString();


    }
}
