package revolver.headead;

import android.app.Application;
import android.graphics.Typeface;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import revolver.headead.core.model.Trigger;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;

public class App extends Application {

    private static int colorPrimary, colorPrimaryDark, colorAccent;
    private static final List<Trigger> allTriggers = new ArrayList<>();
    private static Realm defaultRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        M.setDisplayMetrics(getResources().getDisplayMetrics());

        Realm.init(this);
        defaultRealm = Realm.getInstance(
                new RealmConfiguration.Builder().name("userdata.5.realm").build());

        colorPrimary = ColorUtils.get(this, R.color.colorPrimary);
        colorPrimaryDark = ColorUtils.get(this, R.color.colorPrimaryDark);
        colorAccent = ColorUtils.get(this, R.color.colorAccent);

        allTriggers.add(new Trigger(getString(R.string.trigger_allergy), getString(R.string.trigger_description_allergy), R.drawable.ic_trigger_allergy));
        allTriggers.add(new Trigger(getString(R.string.trigger_anger), getString(R.string.trigger_description_anger), R.drawable.ic_trigger_anger));
        allTriggers.add(new Trigger(getString(R.string.trigger_claustrophobia), getString(R.string.trigger_description_claustrophobia), R.drawable.ic_trigger_claustrophobia));
        allTriggers.add(new Trigger(getString(R.string.trigger_cold), getString(R.string.trigger_description_cold), R.drawable.ic_trigger_cold));
        allTriggers.add(new Trigger(getString(R.string.trigger_effort), getString(R.string.trigger_description_effort), R.drawable.ic_trigger_effort));
        allTriggers.add(new Trigger(getString(R.string.trigger_fear), getString(R.string.trigger_description_fear), R.drawable.ic_trigger_fear));
        allTriggers.add(new Trigger(getString(R.string.trigger_food), getString(R.string.trigger_description_food), R.drawable.ic_trigger_food));
        allTriggers.add(new Trigger(getString(R.string.trigger_habit_change), getString(R.string.trigger_description_habit_change), R.drawable.ic_trigger_habit_change));
        allTriggers.add(new Trigger(getString(R.string.trigger_heat), getString(R.string.trigger_description_heat), R.drawable.ic_trigger_heat));
        allTriggers.add(new Trigger(getString(R.string.trigger_hunger), getString(R.string.trigger_description_hunger), R.drawable.ic_trigger_hunger));
        allTriggers.add(new Trigger(getString(R.string.trigger_light), getString(R.string.trigger_description_light), R.drawable.ic_trigger_light));
        allTriggers.add(new Trigger(getString(R.string.trigger_no_light), getString(R.string.trigger_description_no_light), R.drawable.ic_trigger_no_light));
        allTriggers.add(new Trigger(getString(R.string.trigger_no_sleep), getString(R.string.trigger_description_no_sleep), R.drawable.ic_trigger_no_sleep));
        allTriggers.add(new Trigger(getString(R.string.trigger_noise), getString(R.string.trigger_description_noise), R.drawable.ic_trigger_noise));
        allTriggers.add(new Trigger(getString(R.string.trigger_pain), getString(R.string.trigger_description_pain), R.drawable.ic_trigger_pain));
        allTriggers.add(new Trigger(getString(R.string.trigger_period), getString(R.string.trigger_description_period), R.drawable.ic_trigger_period));
        allTriggers.add(new Trigger(getString(R.string.trigger_screen), getString(R.string.trigger_description_screen), R.drawable.ic_trigger_screen));
        allTriggers.add(new Trigger(getString(R.string.trigger_sleep), getString(R.string.trigger_description_sleep), R.drawable.ic_trigger_sleep));
        allTriggers.add(new Trigger(getString(R.string.trigger_stress), getString(R.string.trigger_description_stress), R.drawable.ic_trigger_stress));
        allTriggers.add(new Trigger(getString(R.string.trigger_substance), getString(R.string.trigger_description_substance), R.drawable.ic_trigger_substance));
        allTriggers.add(new Trigger(getString(R.string.trigger_travel), getString(R.string.trigger_description_travel), R.drawable.ic_trigger_travel));
        allTriggers.add(new Trigger(getString(R.string.trigger_weather), getString(R.string.trigger_description_weather), R.drawable.ic_trigger_weather));

        Fonts.Montserrat.Black = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Black.ttf");
        Fonts.Montserrat.Bold = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Bold.ttf");
        Fonts.Montserrat.ExtraBold = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-ExtraBold.ttf");
        Fonts.Montserrat.Light = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Light.ttf");
        Fonts.Montserrat.Medium = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Medium.ttf");
        Fonts.Montserrat.Regular = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Regular.ttf");
        Fonts.Montserrat.SemiBold = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-SemiBold.ttf");
        Fonts.Montserrat.Thin = Typeface.createFromAsset(
                getAssets(), "fonts/montserrat/Montserrat-Thin.ttf");
        Fonts.Lekton.Bold = Typeface.createFromAsset(
                getAssets(), "fonts/lekton/Lekton-Bold.ttf");
        Fonts.Lekton.Italic = Typeface.createFromAsset(
                getAssets(), "fonts/lekton/Lekton-Italic.ttf");
        Fonts.Lekton.Regular = Typeface.createFromAsset(
                getAssets(), "fonts/lekton/Lekton-Regular.ttf");
        Fonts.Roboto.Regular = ResourcesCompat.getFont(this, R.font.roboto_regular);
        Fonts.Manrope.Bold = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-Bold.ttf");
        Fonts.Manrope.ExtraBold = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-ExtraBold.ttf");
        Fonts.Manrope.ExtraLight = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-ExtraLight.ttf");
        Fonts.Manrope.Light = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-Light.ttf");
        Fonts.Manrope.Medium = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-Medium.ttf");
        Fonts.Manrope.Regular = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-Regular.ttf");
        Fonts.Manrope.SemiBold = Typeface.createFromAsset(
                getAssets(), "fonts/manrope/Manrope-SemiBold.ttf");
    }

    public static List<Trigger> getAllTriggers() {
        return allTriggers;
    }

    public static @ColorInt int colorPrimary() {
        return colorPrimary;
    }

    public static @ColorInt int colorPrimaryDark() {
        return colorPrimaryDark;
    }

    public static @ColorInt int colorAccent() {
        return colorAccent;
    }

    public static Realm getDefaultRealm() {
        return defaultRealm;
    }

    public static class Fonts {
        public static class Montserrat {
            public static Typeface Black, Bold, ExtraBold, Light, Medium, Regular, SemiBold, Thin;
        }

        public static class Lekton {
            public static Typeface Bold, Italic, Regular;
        }

        public static class Roboto {
            public static Typeface Regular;
        }

        public static class Manrope {
            public static Typeface Bold, ExtraBold, ExtraLight, Light, Medium, Regular, SemiBold;
        }
    }
}
