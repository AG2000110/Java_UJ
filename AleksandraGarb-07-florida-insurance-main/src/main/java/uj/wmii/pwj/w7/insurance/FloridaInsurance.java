package uj.wmii.pwj.w7.insurance;


import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class FloridaInsurance {

    public static void main(String[] args) {
        List<InsuranceEntry> insuranceEntries = loadAndParse();
        count(insuranceEntries);
        tiv2012(insuranceEntries);
        most_valuable(insuranceEntries);
    }

    private static List<InsuranceEntry> loadAndParse() {
        List<InsuranceEntry> insuranceEntries = new ArrayList<>();

        try (ZipFile file = new ZipFile("FL_insurance.csv.zip")) {
            ZipEntry entry = file.getEntry("FL_insurance.csv");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    if (lineNumber++ < 1) continue;  //naglowek
                    String[] tokens = line.split(",");
                    try {
                        InsuranceEntry data = new InsuranceEntry(
                                parseLong(tokens[0]),
                                tokens[1],
                                tokens[2],  //country
                                new BigDecimal(tokens[3]),
                                new BigDecimal(tokens[4]),
                                new BigDecimal(tokens[5]),
                                new BigDecimal(tokens[6]),
                                new BigDecimal(tokens[7]),  //tiv2011
                                new BigDecimal(tokens[8]),  //tiv2012
                                new BigDecimal(tokens[9]),
                                new BigDecimal(tokens[10]),
                                new BigDecimal(tokens[11]),
                                new BigDecimal(tokens[12]),
                                parseDouble(tokens[13]),
                                parseDouble(tokens[14]),
                                tokens[15],
                                tokens[16],
                                parseInt(tokens[17])
                        );
                        insuranceEntries.add(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return insuranceEntries;
    }

    private static void count(List<InsuranceEntry> insuranceEntries) {
        File file = new File("count.txt");
        int counter = 0;
        List<String> countryList = new ArrayList<>();
        for (InsuranceEntry data : insuranceEntries) {
            if (!countryList.contains(data.county())) {
                countryList.add(data.county());
                counter++;
            }
        }
        try (FileWriter writer = new FileWriter(file.getName())) {
            writer.write(Integer.toString((counter)));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void tiv2012(List<InsuranceEntry> insuranceEntries) {
        File file = new File("tiv2012.txt");
        BigDecimal sum = new BigDecimal(0);
        for (InsuranceEntry data : insuranceEntries) {
            sum = sum.add(data.tiv_2012());
        }
        try(FileWriter writer = new FileWriter(file.getName())){
            writer.write(sum.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void most_valuable(List<InsuranceEntry> insuranceEntries) {
        File file = new File("most_valuable.txt");
        Map<String, BigDecimal> countryAndValues = getStringBigDecimalMap(insuranceEntries);
        List<Map.Entry<String,BigDecimal>> sortedEntries = countryAndValues.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .toList();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName()))) {
            writer.write("country,value\n");
            for(var entry : sortedEntries) {
                writer.write(entry.getKey() + ',' + entry.getValue() + "\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static Map<String, BigDecimal> getStringBigDecimalMap(List<InsuranceEntry> insuranceEntries) {
        Map<String, BigDecimal> countryAndValues = new HashMap<>();
        for (InsuranceEntry data : insuranceEntries) {
            String country = data.county();
            BigDecimal tiv2011 = data.tiv_2011();
            BigDecimal tiv2012 = data.tiv_2012();
            BigDecimal diff = tiv2012.subtract(tiv2011);
            if(countryAndValues.containsKey(country)) {
                BigDecimal value = countryAndValues.get(country);
                countryAndValues.put(country, value.add(diff));
            } else {
                countryAndValues.put(country, diff);
            }
        }
        return countryAndValues;
    }
}
