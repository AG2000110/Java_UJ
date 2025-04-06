package uj.wmii.pwj.w7.insurance;

import java.math.BigDecimal;

public record InsuranceEntry(
        long policyID,
        String statecode,
        String county,
        BigDecimal eq_site_limit,
        BigDecimal hu_site_limit,
        BigDecimal fl_site_limit,
        BigDecimal fr_site_limit,
        BigDecimal tiv_2011,
        BigDecimal tiv_2012,
        BigDecimal eq_site_deductible,
        BigDecimal hu_site_deductible,
        BigDecimal fl_site_deductible,
        BigDecimal fr_site_deductible,
        double point_latitude,
        double point_longitude,
        String line,
        String construction,
        int point_granularity
) {
}
