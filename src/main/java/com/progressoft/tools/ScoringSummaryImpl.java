package com.progressoft.tools;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoringSummaryImpl implements ScoringSummary {
    List<BigDecimal> numbers;

    public ScoringSummaryImpl(List<BigDecimal> numbers) {
        this.numbers = numbers;
    }

    @Override
    public BigDecimal mean() {
        // stream numbers and and find sum it , scale 2 through rounding
        //2 return sum / (m.length -> scale 2 );
        return numbers.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(numbers.size()),RoundingMode.HALF_EVEN).setScale(2 , RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal standardDeviation() {
       //same variance , bith then claculate must extract sqrt ,
        BigDecimal standardDeviation = getStandardDeviation();
        return sqrt(standardDeviation.divide(new BigDecimal(numbers.size()),  RoundingMode.HALF_EVEN)).setScale(2 , RoundingMode.HALF_EVEN);
    }

    private BigDecimal getStandardDeviation() {

        // variance equal every num subtract mean then  power 2 , then divide count of numbers .
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal standardDeviation = BigDecimal.ZERO;
        long length = numbers.size();

        for (BigDecimal num : numbers) {
            sum = sum.add(num);
        }

        BigDecimal mean = sum.divide(new BigDecimal(length), RoundingMode.HALF_EVEN);

        for (BigDecimal num : numbers) {
            standardDeviation = standardDeviation.add(num.subtract(mean).pow(2));
        }
        return standardDeviation;
    }

    private BigDecimal sqrt(BigDecimal value) {
        // to extract  standardDeviation
        BigDecimal x = BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
        return x.add(BigDecimal.valueOf(value.subtract(x.multiply(x)).doubleValue() / (x.doubleValue() * 2.0)));
    }

    @Override
    public BigDecimal variance() {
        // variance equal every num subtract mean then  power 2 , then divide count of numbers .
        BigDecimal standardDeviation = getStandardDeviation();

        return standardDeviation.divide(new BigDecimal(numbers.size()),  RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal median() {
//        If n is odd then Median (M) = value of ((n + 1)/2)th item term.
//        If n is even then Median (M) = value of [((n)/2)th item term + ((n)/2 - 1)th item term ]/2
        // sort Collection
        Collections.sort(numbers);
        BigDecimal median;
        // get count of scores
        int totalElements = numbers.size();
        // check if total number of scores is even
        if (totalElements % 2 == 0) {
            median = numbers.get(numbers.size() / 2).add(numbers.get(numbers.size() / 2 - 1)).divide(BigDecimal.valueOf(2),  RoundingMode.HALF_EVEN).setScale(2 , RoundingMode.HALF_EVEN);
        } else {
            // get the middle element
            median = numbers.get(numbers.size() / 2).setScale(2 , RoundingMode.HALF_EVEN);
        }
        return median;

    }

    @Override
    public BigDecimal min() {
        // use stream sort natural numbers 1,2,3  and found max ,if empty return 0 but scale 2  00 ;
        return numbers.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2 , RoundingMode.HALF_EVEN);
    }

    ///_______________________________
    @Override
    public BigDecimal max() {
        return numbers.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2 , RoundingMode.HALF_EVEN);
    }


}
