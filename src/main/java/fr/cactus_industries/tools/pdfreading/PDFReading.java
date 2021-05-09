package fr.cactus_industries.tools.pdfreading;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.user.User;

public class PDFReading {
    
    public static void sendPDFTextTo(InputStream is, Messageable target) {
        try {
            PDDocument document = PDDocument.load(is);
            
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDFTextStripper textStripper = new PDFTextStripper();
                textStripper.setParagraphStart(" ");
                
                if (target instanceof User) {
                    System.out.println("PDF's text sent to " + ((User)target).getName());
                } else {
                    System.out.println("PDF's text sent to a non user.");
                }
                
                textStripper.setSortByPosition(true);
                for (String s : PDFReading.cutdownPDF(textStripper.getText(document))) {
                    new MessageBuilder().setContent(s).send(target).join();
                }
                System.out.println("Doc translation sent !");
                document.close();
            } else {
                System.out.println("ERROR ! Document is encrypted.");
            }
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> cutdownPDF(String str) {
        ArrayList<String> LBuf = new ArrayList<>();
        ArrayList<String> LRes = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?s)(?:\\B|)([^\\v\\n\\f]{1,1999}[\\n\\- .:!?,;]|\\n)").matcher(str.replaceAll("(?<=[^\\n\\s])[ \\-]*\\n(?!( [A-Z\u00c0-\u00dd\\n\\f\\-])|\\n)", " "));
        matcher.results().forEach(matchResult -> LBuf.add(matchResult.group()));
        int i = 0;
        StringBuilder sBuild = new StringBuilder();
        for (String s : LBuf) {
            if ((i += s.length()) > 1999) {
                int y = sBuild.length();
                if (y > 1) {
                    char c1 = sBuild.charAt(y - 1);
                    char c2 = sBuild.charAt(y - 2);
                    if (!(c2 != '\n' && c2 != '\f' || c1 != '\n' && c1 != '\f')) {
                        sBuild.append("\u1cbc");
                    }
                } else if (sBuild.charAt(y - 1) == '\n') {
                    sBuild.append("\u1cbc");
                }
                LRes.add(sBuild.toString());
                sBuild = new StringBuilder(s);
                i = s.length();
                continue;
            }
            sBuild.append(s);
        }
        if (sBuild.length() > 0) {
            if (sBuild.length() == sBuild.lastIndexOf("\n") + 1) {
                sBuild.append("\u1cbc");
            }
            LRes.add(sBuild.toString());
        }
        return LRes;
    }
}
