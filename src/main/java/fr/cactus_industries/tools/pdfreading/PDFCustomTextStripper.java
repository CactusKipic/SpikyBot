package fr.cactus_industries.tools.pdfreading;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.Locale;

@Slf4j
public class PDFCustomTextStripper extends PDFTextStripper {
    public PDFCustomTextStripper() throws IOException {
        super();
    }
    
    private boolean sBold = false;
    private boolean sItalic = false;
    private boolean sUnderline = false;
    private boolean isStrike = false;
    
    @Override
    protected void processTextPosition(TextPosition text) {
        log.info(text.getUnicode());
        
        // TODO Passer Bold et Italic en statique, passer à true quand vrai et placer le tag avant puis dès que faux placer encore le tag
        // TODO Idéalement mettre la fin de l'italique et bold si le caractère est en fin de ligne
        boolean bold, italic;
        bold = (text.getFont().getFontDescriptor().getFontWeight() >= 650); // Is bold ?
        String fontName = text.getFont().getFontDescriptor().getFontName();
        log.info(fontName);
        italic = fontName.toLowerCase(Locale.ROOT).contains("italic");
        if(text.getUnicode().equals("\n"))
            log.info("C'est un retour");
        String textStr = text.getUnicode().replaceAll(".{0}(?=[_*`]|~~|\\|\\|)","\\\\");
        
        // Utilisation de tag pour définir les débuts et fin de formatage du texte, qui devra ensuite être process en sortie,
        // car il est impossible de placer correctement la fin du formatage sans pouvoir connaître l'état du TextPosition
        // (Est-ce qu'il est le dernier sur la ligne ?)
    
        if(bold) {
            if(!sBold){
                // TODO add balise
                log.info("Début bold");
                sBold = true;
            }
        } else {
            if(sBold){
                // TODO add fin balise
                log.info("Fin bold");
                sBold = false;
            }
        }
        if(italic) {
            if(!sItalic){
                // TODO add balise
                log.info("Début italique");
                sItalic = true;
            }
        } else {
            if(sItalic){
                // TODO add fin balise
                log.info("Fin italique");
                sItalic = false;
            }
        }
        
        
        if(text.getUnicode().length() != textStr.length())
            super.processTextPosition(new TextPosition(text.getRotation(), text.getPageWidth(), text.getPageHeight(),
                text.getTextMatrix(), text.getEndX(), text.getEndY(), text.getHeight(), text.getIndividualWidths()[0],
                text.getWidthOfSpace(), textStr, text.getCharacterCodes(), text.getFont(),
                text.getFontSize(), (int) text.getFontSizeInPt()));
        else {
            log.info("Pas changé");
            super.processTextPosition(text);
        }
            
    }
}
