package routing;

import core.Message;
import core.NetworkInterface;
import core.Settings;

import java.util.ArrayList;
import java.util.Random;

public class BlackholeSprayAndWaitRouter extends SprayAndWaitRouter
{
    public static String BLACKHOLE_SNW_NS = "BlackholeSprayAndWaitRouter";
    public static String DROP_PROBABILITY_SETTING = "dropProbability";
    private double dropProbability;
    private Random random;

    public BlackholeSprayAndWaitRouter(Settings settings)
    {
        super(settings);

        Settings snwSettings = new Settings(BLACKHOLE_SNW_NS);
        dropProbability = snwSettings.getDouble(DROP_PROBABILITY_SETTING);
        random = new Random();
    }

    public BlackholeSprayAndWaitRouter(BlackholeSprayAndWaitRouter router)
    {
        super(router);

        this.dropProbability = router.dropProbability;
        random = new Random();
    }

    @Override
    public SprayAndWaitRouter replicate()
    {
        return new BlackholeSprayAndWaitRouter(this);
    }

    @Override
    public void update()
    {

        // Remove all cached messages
        int total = 0;
        for (Message message : new ArrayList<>(getMessageCollection()))
        {
            if (random.nextDouble() <= dropProbability)
                removeFromMessages(message.getId());
                total ++;
        }

        if(total > 0) System.out.println(getHost().getAddress() + ": Removed " + total + " messages");

        // Call the derivative functions, although there will be no messages to perform spraying on!
        super.update();
    }

}
