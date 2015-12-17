package de.interoberlin.lymbo.model.persistence.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.converters.Serializer;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;

/**
 * This class can be used to write a lymbo object into an xml file
 */
public class LymboWriter {
    public static void writeXml(Stack stack, File file) {
        // Create save path
        String LYMBO_SAVE_PATH = App.getContext().getResources().getString(R.string.lymbo_save_path);
        if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH).mkdirs())
            return;

        // Set modification date
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            DatatypeFactory datatypeFactory;
            datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now =
                    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            stack.setModificationDate(now);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(Serializer.toXml(stack));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
