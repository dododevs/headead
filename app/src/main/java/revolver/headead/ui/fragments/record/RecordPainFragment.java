package revolver.headead.ui.fragments.record;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import revolver.headead.R;
import revolver.headead.core.model.PainIntensity;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.PainType;
import revolver.headead.util.ui.ColorUtils;

public class RecordPainFragment extends BaseRecordFragment {

    private enum PainLocationVisualization {
        NONE, SIDE, BACK;
    }

    private PainLocationVisualization sxHeadVisualization = PainLocationVisualization.NONE;
    private PainLocationVisualization dxHeadVisualization = PainLocationVisualization.NONE;

    private PainLocation painLocation;
    private PainIntensity painIntensity = PainIntensity.LOW;
    private PainType painType;

    private ImageView sxHeadView, dxHeadView;
    private Slider painIntensitySlider;
    private ValueAnimator painLocationSwitcherAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_record_pain, container, false);

        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        painIntensitySlider = rootView.findViewById(R.id.fragment_record_pain_intensity_slider);
        painIntensitySlider.addOnChangeListener((slider, value, fromUser) -> {
            final int startColor = painIntensitySlider.getTrackTintList().getDefaultColor(), endColor;
            switch ((int) value) {
                case 0:
                    endColor = ColorUtils.get(getContext(), R.color.pastelGreen);
                    painIntensity = PainIntensity.LOW;
                    break;
                case 1:
                    endColor = ColorUtils.get(getContext(), R.color.mikadoYellow);
                    painIntensity = PainIntensity.MEDIUM;
                    break;
                case 2:
                    endColor = ColorUtils.get(getContext(), R.color.bittersweetRed);
                    painIntensity = PainIntensity.HIGH;
                    break;
                default:
                    endColor = ColorUtils.get(getContext(), R.color.iconGray);
                    break;
            }
            if (painLocationSwitcherAnimator != null && painLocationSwitcherAnimator.isRunning()) {
                painLocationSwitcherAnimator.cancel();
            }
            painLocationSwitcherAnimator = ValueAnimator.ofArgb(startColor, endColor);
            painLocationSwitcherAnimator.setDuration(250L);
            painLocationSwitcherAnimator.setInterpolator(new LinearInterpolator());
            painLocationSwitcherAnimator.addUpdateListener(animation -> painIntensitySlider.setTrackTintList(
                    ColorStateList.valueOf((Integer) painLocationSwitcherAnimator.getAnimatedValue())));
            painLocationSwitcherAnimator.start();
        });
        painIntensitySlider.setLabelFormatter(value -> {
            final String label;
            switch ((int) value) {
                case 0:
                    label = getString(PainIntensity.LOW.getLongStringLabel());
                    break;
                case 1:
                    label = getString(PainIntensity.MEDIUM.getLongStringLabel());
                    break;
                case 2:
                    label = getString(PainIntensity.HIGH.getLongStringLabel());
                    break;
                default:
                    return "";
            }
            return label;
        });

        rootView.findViewById(R.id.fragment_record_pain_location_sx).setOnClickListener((v) ->
                animatePainLocationChange(PainLocation.SX, false));
        rootView.findViewById(R.id.fragment_record_pain_location_dx).setOnClickListener((v) ->
                animatePainLocationChange(PainLocation.DX, false));
        rootView.findViewById(R.id.fragment_record_pain_location_front).setOnClickListener((v) ->
                animatePainLocationChange(PainLocation.BILATERAL, false));
        rootView.findViewById(R.id.fragment_record_pain_location_back).setOnClickListener((v) ->
                animatePainLocationChange(PainLocation.BACK, false));

        rootView.findViewById(R.id.fragment_record_pain_type_a).setOnClickListener(
                (v) -> painType = PainType.A);
        rootView.findViewById(R.id.fragment_record_pain_type_b).setOnClickListener(
                (v) -> painType = PainType.B);
        rootView.findViewById(R.id.fragment_record_pain_type_c).setOnClickListener(
                (v) -> painType = PainType.C);

        dxHeadView = rootView.findViewById(R.id.fragment_record_pain_location_head_1);
        sxHeadView = rootView.findViewById(R.id.fragment_record_pain_location_head_2);

        rootView.findViewById(R.id.fragment_record_pain_type_info)
                .setOnClickListener((v) -> createPainTypeInfoDialog().show());
        rootView.findViewById(R.id.fab).setOnClickListener((v) -> {
            if (painLocation == null) {
                createPainLocationMissingDialog().show();
            } else if (painType == null) {
                createPainTypeMissingDialog().show();
            } else {
                final Bundle data = getRecordHeadacheActivity().getChainDataBundle();
                data.putString("painLocation", painLocation.name());
                data.putString("painIntensity", painIntensity.name());
                data.putString("painType", painType.name());

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.fragment_record_scenario_slide_in,
                                R.anim.fragment_record_scenario_slide_out,
                                R.anim.fragment_record_scenario_slide_in,
                                R.anim.fragment_record_scenario_slide_out)
                        .addToBackStack(null)
                        .replace(R.id.frame, new RecordScenarioFragment())
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        painLocation = PainLocation.fromString(getRecordHeadacheActivity()
                .getChainDataBundle()
                .getString("painLocation", null));
        if (painLocation != null) {
            animatePainLocationChange(painLocation, true);
        }
        painIntensity = PainIntensity.fromString(getRecordHeadacheActivity()
                .getChainDataBundle()
                .getString("painIntensity", PainIntensity.LOW.name()));
        painIntensitySlider.setValue(painIntensity.ordinal());
        painType = PainType.fromString(getRecordHeadacheActivity()
                .getChainDataBundle()
                .getString("painType", null));
    }

    private void animatePainLocationChange(final PainLocation newPainLocation, final boolean forceAnimation) {
        if (forceAnimation) {
            sxHeadVisualization = dxHeadVisualization = PainLocationVisualization.NONE;
        }
        boolean changed = forceAnimation;
        switch (newPainLocation) {
            case SX:
                if (sxHeadVisualization == PainLocationVisualization.NONE) {
                    sxHeadView.setImageResource(R.drawable.animated_head_none_to_side);
                    changed = true;
                } else if (sxHeadVisualization == PainLocationVisualization.BACK) {
                    sxHeadView.setImageResource(R.drawable.animated_head_back_to_side);
                    changed = true;
                }
                if (sxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) sxHeadView.getDrawable()).start();
                }

                changed = false;
                if (dxHeadVisualization == PainLocationVisualization.SIDE) {
                    dxHeadView.setImageResource(R.drawable.animated_head_side_to_none);
                    changed = true;
                } else if (dxHeadVisualization == PainLocationVisualization.BACK) {
                    dxHeadView.setImageResource(R.drawable.animated_head_back_to_none);
                    changed = true;
                }
                if (dxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) dxHeadView.getDrawable()).start();
                }

                sxHeadVisualization = PainLocationVisualization.SIDE;
                dxHeadVisualization = PainLocationVisualization.NONE;
                setSelectedPainLocation(R.id.fragment_record_pain_location_sx_selected);
                break;
            case DX:
                if (dxHeadVisualization == PainLocationVisualization.NONE) {
                    dxHeadView.setImageResource(R.drawable.animated_head_none_to_side);
                    changed = true;
                } else if (dxHeadVisualization == PainLocationVisualization.BACK) {
                    dxHeadView.setImageResource(R.drawable.animated_head_back_to_side);
                    changed = true;
                }
                if (dxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) dxHeadView.getDrawable()).start();
                }

                changed = false;
                if (sxHeadVisualization == PainLocationVisualization.SIDE) {
                    sxHeadView.setImageResource(R.drawable.animated_head_side_to_none);
                    changed = true;
                } else if (sxHeadVisualization == PainLocationVisualization.BACK) {
                    sxHeadView.setImageResource(R.drawable.animated_head_back_to_none);
                    changed = true;
                }
                if (sxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) sxHeadView.getDrawable()).start();
                }

                sxHeadVisualization = PainLocationVisualization.NONE;
                dxHeadVisualization = PainLocationVisualization.SIDE;
                setSelectedPainLocation(R.id.fragment_record_pain_location_dx_selected);
                break;
            case BILATERAL:
                if (dxHeadVisualization == PainLocationVisualization.NONE) {
                    dxHeadView.setImageResource(R.drawable.animated_head_none_to_side);
                    changed = true;
                } else if (dxHeadVisualization == PainLocationVisualization.BACK) {
                    dxHeadView.setImageResource(R.drawable.animated_head_back_to_side);
                    changed = true;
                }
                if (dxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) dxHeadView.getDrawable()).start();
                }

                changed = false;
                if (sxHeadVisualization == PainLocationVisualization.NONE) {
                    sxHeadView.setImageResource(R.drawable.animated_head_none_to_side);
                    changed = true;
                } else if (sxHeadVisualization == PainLocationVisualization.BACK) {
                    sxHeadView.setImageResource(R.drawable.animated_head_back_to_side);
                    changed = true;
                }
                if (sxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) sxHeadView.getDrawable()).start();
                }

                sxHeadVisualization = PainLocationVisualization.SIDE;
                dxHeadVisualization = PainLocationVisualization.SIDE;
                setSelectedPainLocation(R.id.fragment_record_pain_location_front_selected);
                break;
            case BACK:
                if (dxHeadVisualization == PainLocationVisualization.NONE) {
                    dxHeadView.setImageResource(R.drawable.animated_head_none_to_back);
                    changed = true;
                } else if (dxHeadVisualization == PainLocationVisualization.SIDE) {
                    dxHeadView.setImageResource(R.drawable.animated_head_side_to_back);
                    changed = true;
                }
                if (dxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) dxHeadView.getDrawable()).start();
                }

                changed = false;
                if (sxHeadVisualization == PainLocationVisualization.NONE) {
                    sxHeadView.setImageResource(R.drawable.animated_head_none_to_back);
                    changed = true;
                } else if (sxHeadVisualization == PainLocationVisualization.SIDE) {
                    sxHeadView.setImageResource(R.drawable.animated_head_side_to_back);
                    changed = true;
                }
                if (sxHeadView.getDrawable() instanceof AnimatedVectorDrawable && changed) {
                    ((AnimatedVectorDrawable) sxHeadView.getDrawable()).start();
                }

                sxHeadVisualization = PainLocationVisualization.BACK;
                dxHeadVisualization = PainLocationVisualization.BACK;
                setSelectedPainLocation(R.id.fragment_record_pain_location_back_selected);
                break;
        }
        painLocation = newPainLocation;
    }

    private void setSelectedPainLocation(@IdRes int tickViewRes) {
        requireView().findViewById(R.id.fragment_record_pain_location_sx_selected)
                .setVisibility(View.INVISIBLE);
        requireView().findViewById(R.id.fragment_record_pain_location_dx_selected)
                .setVisibility(View.INVISIBLE);
        requireView().findViewById(R.id.fragment_record_pain_location_front_selected)
                .setVisibility(View.INVISIBLE);
        requireView().findViewById(R.id.fragment_record_pain_location_back_selected)
                .setVisibility(View.INVISIBLE);
        requireView().findViewById(tickViewRes).setVisibility(View.VISIBLE);
    }

    private AlertDialog createPainTypeInfoDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_pain_type_info_title)
                .setView(R.layout.dialog_pain_type_info)
                .setPositiveButton(R.string.dialog_pain_type_info_positive, null)
                .create();
    }

    private AlertDialog createPainLocationMissingDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_pain_location_missing_title)
                .setMessage(R.string.dialog_pain_location_missing_message)
                .setPositiveButton(R.string.dialog_pain_location_missing_positive, null)
                .create();
    }

    private AlertDialog createPainTypeMissingDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_pain_type_missing_title)
                .setMessage(R.string.dialog_pain_type_missing_message)
                .setPositiveButton(R.string.dialog_pain_type_missing_positive, null)
                .create();
    }
}
