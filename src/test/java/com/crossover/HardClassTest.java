package com.crossover;

import com.crossover.services.ExternalRatingApprovalService;
import com.crossover.services.NotificationService;
import com.crossover.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * This class will not run with piTest!
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.crossover.*")
@SuppressStaticInitializationFor("com.HardClass")
public class HardClassTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ExternalRatingApprovalService externalRatingApprovalService;

    @Mock
    private Utils utils;

    @InjectMocks
    private HardClass testClass;

    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(HardClass.class, "HARD_CACHE", 0);
        Whitebox.setInternalState(HardClass.class, "UTILS", utils);
        Mockito.when(utils.getRatingDecoration()).thenReturn("MOCK");
        Mockito.when(externalRatingApprovalService.isApproved(ArgumentMatchers.anyInt())).thenReturn(true);
    }

    @Test
    public void shouldFail_WhenRatingIsHigherThanCeiling() {
        exceptionRule.expect(IllegalArgumentException.class);

        testClass.createRatingString(2, 1);
    }


    @Test
    public void shouldGiveTopRating_WhenRatingIsEqualToCeiling() {
        int rating = 2;
        int ratingCeiling = 2;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("TOP+" + rating + "MOCK", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }

    @Test
    public void shouldGiveHighRating_WhenRatingIsGreaterThanOrEqualTo_HalfCeiling() {
        int rating = 2;
        int ratingCeiling = 4;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("HIGH=" + rating + "MOCK", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }

    @Test
    public void shouldGiveLOWRating_WhenRatingIsLowerThanTo_HalfCeiling() {
        int rating = 1;
        int ratingCeiling = 4;
        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("LOW-" + rating + "MOCK", ratingStr);

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

        Whitebox.setInternalState(HardClass.class, "HARD_CACHE", rating);

        String ratingStr = testClass.createRatingString(rating, ratingCeiling);

        Assert.assertEquals("LOW-1-CACHEDMOCK", ratingStr);

        Mockito.verify(externalRatingApprovalService, Mockito.atLeast(1)).isApproved(rating);
        Mockito.verify(notificationService, Mockito.atLeast(1)).notify(rating);
    }
}
