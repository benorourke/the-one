package routing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataLoader
{
    private static String[] scenarioNames = {"SnW"/*, "SnW with Sprinklers", "SnF", "SnF with Sprinklers"*/};
    private static int reports = 20;
    private static String dir = "C:\\Users\\BenOR\\Dropbox\\University\\Masters\\G54ACN\\Config\\Results\\%TYPE%\\%NUM%";
    private static String fileName = "Scenario1_MessageStatsReport.txt";

    public static void main(String[] args)
    {

        for (String scenarioName : scenarioNames)
        {
            Scenario scenario = new Scenario(scenarioName);
            String[] lookingFor = {"delivery_prob: "};
            scenario.loadAll(lookingFor, reports);
            scenario.print();
        }

    }

    private static class Scenario {
        private String scenarioName;
        private Map<Integer, Entry[]> entries;

        private Scenario(String scenarioName)
        {
            this.scenarioName = scenarioName;
            this.entries = new LinkedHashMap<>();
        }

        public void loadAll(String[] lookFor, int reports)
        {
            for (int i = 1; i <= reports; i ++)
            {
                String path = dir.replace("%TYPE%", scenarioName).replace("%NUM%", String.valueOf(i)) + "\\" + fileName;

                Path filePath = new File(path).toPath();
                Charset charset = Charset.defaultCharset();
                try
                {
                    List<String> stringList = Files.readAllLines(filePath, charset);

                    Entry[] entries = new Entry[lookFor.length];
                    int lookForCount = 0;
                    for (String lookForPrefix : lookFor)
                    {

                        inner: for (String line : stringList)
                        {
                            if (line.startsWith(lookForPrefix))
                            {
                                entries[lookForCount ++] = new Entry(lookForPrefix,
                                                                     Double.valueOf(line.replace(lookForPrefix, "")));
                                break inner;
                            }
                        }
                    }

                    this.entries.put(i, entries);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void print()
        {
            System.out.println(scenarioName + ":");

            for (Map.Entry<Integer, Entry[]> entry : entries.entrySet())
            {
                System.out.println("    " + entry.getKey() + ":");
                for (Entry entry2 : entry.getValue())
                {
                    System.out.println("        " + entry2.getKey() + entry2.getValue());
                }
            }
        }

        private class Entry {
            private String key;
            private double value;

            public Entry(String key, double value)
            {
                this.key = key;
                this.value = value;
            }

            public String getKey()
            {
                return key;
            }

            public void setKey(String key)
            {
                this.key = key;
            }

            public double getValue()
            {
                return value;
            }

            public void setValue(double value)
            {
                this.value = value;
            }
        }
    }

}
