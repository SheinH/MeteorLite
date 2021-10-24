package meteor.plugins.api.kotlin;

import meteor.chat.ChatMessageManager;
import meteor.chat.QueuedMessage;
import net.runelite.api.ChatMessageType;

public class KotlinUtils {

    public static void sendChatMessage(ChatMessageManager chatMessageManager, String message ){
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }
}
