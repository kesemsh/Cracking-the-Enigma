package jaxb.xml.reader;

import jaxb.generated.CTEEnigma;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLReader {
    private final static String JAXB_XML_PACKAGE_NAME = "jaxb.generated";

    public static CTEEnigma getEnigmaFromXMLFile(InputStream xmlFileStream) throws IOException, JAXBException {
        try {
            return deserializeFrom(xmlFileStream);
        }
        catch (JAXBException e) {
            throw new JAXBException("File is not a valid machine file!");
        }
    }

    private static CTEEnigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma)u.unmarshal(in);
    }
}