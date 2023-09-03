package fr.cactus_industries.tools;

import com.google.gson.Gson;
import fr.cactus_industries.tools.tickets.MessageJsonTicket;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MessageJsonTicketDeserializationType implements AttributeConverter<MessageJsonTicket, String> {
    
    private final Gson gson = new Gson();
    
    @Override
    public String convertToDatabaseColumn(MessageJsonTicket messageJsonTicket) {
        return gson.toJson(messageJsonTicket);
    }
    
    @Override
    public MessageJsonTicket convertToEntityAttribute(String s) {
        return gson.fromJson(s, MessageJsonTicket.class);
    }
}
