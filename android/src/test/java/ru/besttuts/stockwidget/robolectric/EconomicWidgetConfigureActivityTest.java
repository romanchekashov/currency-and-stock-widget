package ru.besttuts.stockwidget.robolectric;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.fakes.RoboMenuItem;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.ui.ConfigPreferenceFragment;
import ru.besttuts.stockwidget.ui.EconomicWidgetConfigureActivity;
import ru.besttuts.stockwidget.ui.SlidingTabsFragment;
import ru.besttuts.stockwidget.ui.TrackingQuotesFragment;
import ru.besttuts.stockwidget.util.Utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 16.01.2015.
 */
//@Config(manifest = "./android/src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class EconomicWidgetConfigureActivityTest {

    private static final String TAG = makeLogTag(EconomicWidgetConfigureActivityTest.class);

    EconomicWidgetConfigureActivity activity;

    private static void println(String s) {
        System.out.println(TAG + ": " + s);
    }

    @Before
    public void setUp() throws Exception { // TODO: Does not work with ActionBarActivity! Need to fix!
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);

        activity = Robolectric.buildActivity(EconomicWidgetConfigureActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }

    @After
    public void tearDown() throws Exception {
        shadowOf(activity).finish();
    }

    @Test
    public void testPreconditions() {
        assertThat(activity).isNotNull();
        // Должно быть 3 фрагмента
        assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(3);
    }

    @Test
    public void hasToolbar() {
        assertThat(activity.findViewById(R.id.toolbar)).isNotNull();
    }

    @Test
    public void hasSlidingTabsFragment() throws Exception {
        Fragment slidingTabsFragement = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_place);
        assertNotNull(slidingTabsFragement);
        assertTrue(slidingTabsFragement instanceof SlidingTabsFragment);
    }

    @Test
    public void hasTrackingQuotesFragment() throws Exception {
        SlidingTabsFragment slidingTabsFragement = (SlidingTabsFragment)
                activity.getSupportFragmentManager().findFragmentById(R.id.fragment_place);
        ViewPager viewPager = (ViewPager) slidingTabsFragement.getView().findViewById(R.id.viewpager);
        assertNotNull(viewPager);

        Fragment trackingQuotesFragment = ((FragmentPagerAdapter) viewPager.getAdapter()).getItem(0);
        assertNotNull(trackingQuotesFragment);
        assertTrue(trackingQuotesFragment instanceof TrackingQuotesFragment);
    }

    @Test
    public void hasConfigPreferenceFragment() throws Exception {
        SlidingTabsFragment slidingTabsFragement = (SlidingTabsFragment)
                activity.getSupportFragmentManager().findFragmentById(R.id.fragment_place);
        ViewPager viewPager = (ViewPager) slidingTabsFragement.getView().findViewById(R.id.viewpager);
        assertNotNull(viewPager);

        Fragment configPreferenceFragment = ((FragmentPagerAdapter) viewPager.getAdapter()).getItem(1);
        assertNotNull(configPreferenceFragment);
        assertTrue(configPreferenceFragment instanceof ConfigPreferenceFragment);
    }

    @Test
    public void viewPagerShoudChangeCurrentItem() throws Exception {

        SlidingTabsFragment slidingTabsFragement = (SlidingTabsFragment)
                activity.getSupportFragmentManager().findFragmentById(R.id.fragment_place);

        ViewPager viewPager = (ViewPager) slidingTabsFragement.getView().findViewById(R.id.viewpager);
        assertNotNull(viewPager);

        assertThat(viewPager.getCurrentItem()).isEqualTo(0);

        viewPager.setCurrentItem(1);

        assertThat(viewPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void menuItemClickShouldShowQuoteTypeDialog() throws Exception {

        final Menu menu = shadowOf(activity).getOptionsMenu();

        assertThat(menu.findItem(R.id.action_add_quote).getTitle()).isEqualTo("+ Add quote");
//
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
//        assertNotNull(toolbar);
//        assertNotNull(toolbar.findViewById(R.id.action_add_quote));

        MenuItem item = new RoboMenuItem() {
            public int getItemId() {
                return R.id.action_add_quote;
            }
        };
//
//        activity.onOptionsItemSelected(item);

//        System.out.println(toolbar.findViewById(R.id.action_add_quote));
//        toolbar.findViewById(R.id.action_add_quote).performClick();
//        ShadowView.clickOn(toolbar.findViewById(R.id.action_add_quote));
//        shadowOf(activity).clickMenuItem(R.id.action_add_quote);

//        assertTrue(ShadowDialog.getLatestDialog().isShowing());
    }

    @Test
    public void testGetModelNameFromResourcesBySymbol() throws Exception {
        assertEquals("EUR/RUB", Utils.getModelNameFromResourcesBySymbol(activity,
                QuoteType.CURRENCY, "EURRUB"));
        assertEquals(activity.getString(R.string.gcf15_cmx),
                Utils.getModelNameFromResourcesBySymbol(activity,
                        QuoteType.GOODS, "GCF15.CMX"));
    }

}
