package routing;

import core.Message;
import core.Settings;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SnWInformationSprinklerRouter extends ActiveRouter
{
    private static Map<String, Message> backboneCache;
    private static Field messagesField;

    static
    {
        backboneCache = new HashMap<>();
        try {
            messagesField = MessageRouter.class.getDeclaredField("messages");
            messagesField.setAccessible(true);
            System.out.println("Reflected Messages Field for Future Injection");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            messagesField = null;
        }
    }

    public SnWInformationSprinklerRouter(Settings settings) {
        super(settings);
    }

    protected SnWInformationSprinklerRouter(SnWInformationSprinklerRouter r) {
        super(r);
    }

    @Override
    public boolean createNewMessage(Message msg) {
        makeRoomForNewMessage(msg.getSize());

        msg.setTtl(this.msgTtl);
        // Only one forwarding token because it can only ever be given to the destination
        msg.addProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY, 1);
        addToMessages(msg, true);
        return true;
    }

    @Override
    public MessageRouter replicate() {
        return new SnWInformationSprinklerRouter(this);
    }

    @Override
    public void update()
    {
        super.update();

        if (isTransferring() || !canStartTransfer()) {
            return; // transferring, don't try other connections yet
        }

        // Synchronise the Information Sprinkler with the backbone network
        synchroniseWithBackbone();

        // Try the messages that can be delivered to final recipient
        exchangeDeliverableMessages();
    }

    @Override
    protected boolean makeRoomForMessage(int size)
    {
        // We assume an infinite amount of space with these buffers
        return true;
    }

    private void purgeExpiredMessages() {
        for (Map.Entry<String, Message> entry : backboneCache.entrySet()) {
            if (entry.getValue().getTtl() <= 0)
                backboneCache.remove(entry.getKey());
        }
    }

    public void synchroniseWithBackbone() {
        // Add any messages the backbone network is missing
        for (Message message : getMessageCollection()) {
            // The backbone network already contains this message
            if (backboneCache.containsKey(message.getId()))
                continue;

            // The message has expired
            if (message.getTtl() <= 0)
                continue;

            backboneCache.put(message.getId(), message);
        }

        // Purge any expired messages from the cache
        purgeExpiredMessages();

        // Inject the contents of the backbone network cache into this router
        try
        {
            Map<String, Message> internalCache = (Map<String, Message>) messagesField.get(this);
            internalCache.clear();
            internalCache.putAll(backboneCache);

            System.out.println("Cache size: " + backboneCache.size());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

}
