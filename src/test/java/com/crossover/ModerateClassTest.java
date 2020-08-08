package com.crossover;

import com.crossover.services.ExternalRatingApprovalService;
import com.crossover.services.NotificationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class ModerateClassTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ExternalRatingApprovalService externalRatingApprovalService;

    @InjectMocks
    private ModerateClass testClass;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldFail_WhenRatingIsHigherThanCeiling() {
        exceptionRule.expect(IllegalArgumentException.class);

        testClass.createRatingString(2, 1);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(testClass, "lastRating", 0);
        Mockito.when(externalRatingApprovalService.isApproved(ArgumentMatchers.anyInt())).thenReturn(true);
    }

    @Test
    public void shouldGiveTopRating_WhenRatingIsEqualToCeiling() {
        int rating = 2;
        int ratingCeiling = 2;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("TOP+" + rating, ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }

    @Test
    public void shouldGiveHighRating_WhenRatingIsGreaterThanOrEqualTo_HalfCeiling() {
        int rating = 2;
        int ratingCeiling = 4;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("HIGH=2", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }

    @Test
    public void shouldGiveLOWRating_WhenRatingIsLowerThanTo_HalfCeiling() {
        int rating = 1;
        int ratingCeiling = 4;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("LOW-1", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }

    @Test
    public void shouldGiveNotApplicable_WhenExternalRating_Disapproves() {
        int rating = 1;
        Mockito.when(externalRatingApprovalService.isApproved(ArgumentMatchers.eq(rating))).thenReturn(false);

        String ratingStr = testClass.createRatingString(rating, 2);

        Assert.assertEquals("NOT-APP", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(rating)).isApproved(rating);
    }

    @Test
    public void shouldAppendCache_WhenRatingEqualToCachedValue() {
        int rating = 1;
        int ratingCeiling = 4;

        Whitebox.setInternalState(testClass, "lastRating", rating);

        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("LOW-1-CACHED", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }
}
