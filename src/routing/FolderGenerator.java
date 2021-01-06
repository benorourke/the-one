package routing;

import java.io.File;

public class FolderGenerator
{
    private static String[] types = {"Epidemic", "Epidemic with Sprinklers", "SnW", "SnW with Sprinklers", "SnF", "SnF with Sprinklers"};
    private static int reports = 20;

    public static void main(String[] args)
    {
        String dir = "C:\\Users\\BenOR\\Dropbox\\University\\Masters\\G54ACN\\Config\\Results\\%TYPE%\\%NUM%";

        for (int i = 1; i <= reports; i ++) {
            for (int j = 0; j < types.length; j ++)
            {
                String type = types[j];
                String dir2 = dir.replace("%TYPE%", type).replace("%NUM%", String.valueOf(i));
                File file = new File(dir2);
                if (!file.exists())
                    file.mkdirs();
            }
        }
    }

}
