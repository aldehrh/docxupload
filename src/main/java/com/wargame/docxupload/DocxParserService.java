package com.wargame.docxupload;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.web.multipart.MultipartFile;

@Service
public class DocxParserService {

    private static final Logger logger = Logger.getLogger("XXE");

    public String parseDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xml")) {
                    byte[] xmlBytes = zis.readAllBytes();

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
                    dbf.setFeature("http://xml.org/sax/features/external-general-entities", true);
                    dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
                    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
                    dbf.setExpandEntityReferences(true);
                    dbf.setNamespaceAware(true);

                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
                    Document doc = db.parse(source);

                    String textContent = doc.getDocumentElement().getTextContent();
                    return textContent;
                }
            }
        }
        return "No XML with XXE payload found.";
    }

}
