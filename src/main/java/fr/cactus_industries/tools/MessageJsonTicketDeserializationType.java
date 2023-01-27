package fr.cactus_industries.tools;

import com.google.gson.*;
import fr.cactus_industries.tools.tickets.MessageJsonTicket;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
