package fr.cactus_industries.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class CustomColor {
    
    private int value;
    
    public Color toColor() {
        return new Color(value);
    }
    
    public static CustomColor fromColor(Color color) {
        return new CustomColor(color.getRGB());
    }
}
