package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) throws IllegalArgumentException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm VV");
        Duration diff = Duration.between(
                ZonedDateTime.parse(start, dtf),
                ZonedDateTime.parse(end, dtf)
        );
        long days = diff.toDays();
        long hours = diff.minusDays(days).toHours();
        long minutes = diff.minusDays(days).minusHours(hours).toMinutes();
        BigDecimal salary = dailyRate.multiply(BigDecimal.valueOf(days));
        return salary.add(
                (hours > 12) ? dailyRate :
                        (hours > 8) ? dailyRate.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP) :
                                (hours > 0 || minutes > 0) ? dailyRate.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP) :
                                        BigDecimal.valueOf(0)
        );
    }
}
