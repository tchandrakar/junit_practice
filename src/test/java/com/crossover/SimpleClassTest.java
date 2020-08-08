package com.crossover;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class SimpleClassTest {

    private SimpleClass testClass = new SimpleClass();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldFail_WhenRatingIsHigherThanCeiling() {
        exceptionRule.expect(IllegalArgumentException.class);

        testClass.createRatingString(2, 1);
    }

    @Test
    public void shouldGiveTopRating_WhenRatingIsEqualToCeiling() {
        String rating = testClass.createRatingString(2, 2);

        Assert.assertEquals("TOP+2", rating);
    }

    @Test
    public void shouldGiveHighRating_WhenRatingIsGreaterThanOrEqualTo_HalfCeiling() {
        String rating = testClass.createRatingString(2, 4);

        Assert.assertEquals("HIGH=2", rating);
    }

    @Test
    public void shouldGiveLOWRating_WhenRatingIsLowerThanTo_HalfCeiling() {
        String rating = testClass.createRatingString(1, 4);

        Assert.assertEquals("LOW-1", rating);
    }
}
