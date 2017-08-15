package com.gautamastudios.whatweather;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.gautamastudios.whatweather.ui.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Test
    public void testThatForecastResponseIsSuccess() {
        Intents.init();
        activityTestRule.launchActivity(createIntent(MainActivity.class));
        intended(hasComponent(MainActivity.class.getName()));

        // then
        //        onView(withText("Hello World!")).check(matches(isDisplayed()));
    }

    private Intent createIntent(Class intentClass) {
        return new Intent(getContext(), intentClass);
    }

    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
