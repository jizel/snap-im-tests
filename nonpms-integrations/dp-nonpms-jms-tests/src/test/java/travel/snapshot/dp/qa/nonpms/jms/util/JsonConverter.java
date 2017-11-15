package travel.snapshot.dp.qa.nonpms.jms.util;


import java.nio.charset.Charset;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class JsonConverter {

    // TODO use message converter
    public static String convertToJson(Message message) {
        try {
            if (message instanceof TextMessage) {
                return ((TextMessage)message).getText();
            }
            if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(bytes);
                return new String(bytes, Charset.forName("utf-8"));
            }
        } catch (JMSException e) {
            throw new IllegalStateException("Cannot convert message: " + message.toString(), e);
        }

        throw new IllegalStateException("Unprocessed message: " + message.toString());
    }

}
