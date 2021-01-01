package revolver.headead.ui.fragments.record2.pickers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;

import java.util.Objects;

import revolver.headead.R;
import revolver.headead.core.model.PainIntensity;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.views.AnimatedSlider;
import revolver.headead.ui.views.PainIntensityScaleDrawable;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.IconUtils;

public class PainIntensityPickerFragment extends Fragment {

    private PainIntensityScaleDrawable scaleDrawable;
    private PainIntensity painIntensity;

    /*@NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), R.style.RoundedBottomSheetDialog);
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pain_intensity_picker, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final AnimatedSlider intensitySliderView =
                view.findViewById(R.id.fragment_pain_intensity_picker_slider);
        intensitySliderView.addOnChangeListener((slider, value, fromUser) ->
                onSliderValueChanged(value / 30.f));
        intensitySliderView.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                final float value = slider.getValue();
                if (value > 0.0f && value <= 5.f) {
                    slider.setValue(0.f);
                    painIntensity = null;
                } else if ((value > 5.f && value <= 10.f) || (value > 10.f && value <= 15.f)) {
                    slider.setValue(10.f);
                    painIntensity = PainIntensity.LOW;
                } else if ((value > 15.f && value <= 20.f) || (value > 20.f && value <= 25.f)) {
                    slider.setValue(20.f);
                    painIntensity = PainIntensity.MEDIUM;
                } else if (value > 25.f && value <= 30.f) {
                    slider.setValue(30.f);
                    painIntensity = PainIntensity.HIGH;
                }
            }
        });
        final ImageView painIntensityScale =
                view.findViewById(R.id.fragment_pain_intensity_picker_scale);
        painIntensityScale.setImageDrawable(scaleDrawable =
                new PainIntensityScaleDrawable(requireContext()));
        painIntensityScale.setOnTouchListener((v, event) -> intensitySliderView.onTouchEvent(event));

        view.findViewById(R.id.fragment_pain_intensity_picker_confirm).setOnClickListener((v) -> {
            if (painIntensity != null) {
                requireRecordHeadacheActivity().animatePainIntensityChange(painIntensity);
                requireRecordHeadacheActivity().resetBottomPane();
            }
        });

        final PainIntensity currentPainIntensity =
                requireRecordHeadacheActivity().getPainIntensity();
        if (currentPainIntensity != null) {
            switch (currentPainIntensity) {
                case LOW:
                    intensitySliderView.setValue(10.f);
                    break;
                case MEDIUM:
                    intensitySliderView.setValue(20.f);
                    break;
                case HIGH:
                    intensitySliderView.setValue(30.f);
                    break;
            }
            painIntensity = currentPainIntensity;
        } else {
            painIntensity = null;
        }
    }

    private void onSliderValueChanged(final float value) {
        scaleDrawable.setValue(value);
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) Objects.requireNonNull(getActivity());
    }
}
