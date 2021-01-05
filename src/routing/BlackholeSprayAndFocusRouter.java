package routing;

import core.Message;
import core.Settings;

import java.util.ArrayList;
import java.util.Random;

public class BlackholeSprayAndFocusRouter extends SprayAndFocusRouter
{
    public static String BLACKHOLE_SNF_NS = "BlackholeSprayAndFocusRouter";
    public static String DROP_PROBABILITY_SETTING = "dropProbability";
    private double dropProbability = 1.0;
    private Random random;

    public BlackholeSprayAndFocusRouter(Settings settings)
    {
        super(settings);

        Settings snfSettings = new Settings(BLACKHOLE_SNF_NS);
        dropProbability = snfSettings.getDouble(DROP_PROBABILITY_SETTING);
        random = new Random();
    }

    public BlackholeSprayAndFocusRouter(BlackholeSprayAndFocusRouter router)
    {
        super(router);

        this.dropProbability = router.dropProbability;
        random = new Random();
    }

    @Override
    public SprayAndFocusRouter replicate()
    {
        return new BlackholeSprayAndFocusRouter(this);
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
