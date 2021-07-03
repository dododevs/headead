package revolver.headead.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revolver.headead.R;
import revolver.headead.aifa.Aifa;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.ui.activities.DrugLookupActivity;
import revolver.headead.ui.adapters.DrugDosageUnitsAdapter;
import revolver.headead.ui.adapters.DrugDosageUnitsAdapter2;
import revolver.headead.util.ui.TextUtils;

public class DrugPackagingOverviewFragment extends BottomSheetDialogFragment {

    private DrugPackaging drugPackaging;
    private DrugDosageUnit selectedUnit = DrugDosageUnit.UNKNOWN;
    private float quantity = Float.MIN_VALUE;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), R.style.RoundedBottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_drug_packaging_overview, container, false);
        final TextView valueView =
                rootView.findViewById(R.id.fragment_drug_packaging_overview_dosage_unit);
        valueView.setOnClickListener((v) -> {
            final DrugDosageUnitsAdapter2 adapter;
//            new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
//                    .setAdapter(adapter = new DrugDosageUnitsAdapter(
//                            requireContext(), (int) quantity, selectedUnit), null)
//                    .setPositiveButton(R.string.dialog_drug_dosage_units_positive, (dialog, which) -> {
//                        selectedUnit = adapter.getSelectedItem();
//                        valueView.setText(getResources()
//                                .getQuantityString(selectedUnit.getNameResource(), (int) quantity));
//                    }).create().show();
            new SimpleAlertDialogFragment.Builder(requireContext())
                    .title(R.string.dialog_drug_unit_title)
                    .list(adapter = new DrugDosageUnitsAdapter2((int) quantity, selectedUnit))
                    .positiveButton(R.string.dialog_drug_unit_positive, (dialog) -> {
                        selectedUnit = adapter.getSelectedItem();
                        valueView.setText(getResources()
                                .getQuantityText(selectedUnit.getNameResource(), (int) quantity));
                    }, true).build().show(getChildFragmentManager(), null);
        });

        final EditText dosageValueView =
                rootView.findViewById(R.id.fragment_drug_packaging_overview_dosage_value);
        dosageValueView.addTextChangedListener(new TextUtils.SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    quantity = Float.parseFloat(s.toString());
                    if (quantity <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    quantity = Float.MIN_VALUE;
                }
                valueView.setText(getResources()
                        .getQuantityString(selectedUnit.getNameResource(), (int) quantity));
            }
        });

        final Spinner defaultValuesSpinnerView =
                rootView.findViewById(R.id.fragment_drug_packaging_overview_dosage_default_values);
        defaultValuesSpinnerView.setAdapter(new SimpleAdapter(requireContext(), Arrays.asList(
                Collections.singletonMap("fraction", getString(R.string.fraction12)),
                Collections.singletonMap("fraction", getString(R.string.fraction13)),
                Collections.singletonMap("fraction", getString(R.string.fraction14)),
                Collections.singletonMap("fraction", getString(R.string.fraction23)),
                Collections.singletonMap("fraction", getString(R.string.fraction34)),
                Collections.singletonMap("fraction",
                        getString(R.string.fragment_drug_packaging_overview_set_value))
        ), R.layout.item_drug_dosage_default_value, new String[] {
                "fraction"
        }, new int[] {
                R.id.item_drug_dosage_default_value
        }));
        defaultValuesSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 5) {
                    onNothingSelected(parent);
                }
                switch (position) {
                    case 0: /* 1/2 */
                        quantity = -12;
                        break;
                    case 1: /* 1/3 */
                        quantity = -13;
                        break;
                    case 2: /* 1/4 */
                        quantity = -14;
                        break;
                    case 3: /* 2/3 */
                        quantity = -23;
                        break;
                    case 4: /* 3/4 */
                        quantity = -34;
                        break;
                    case 5: /* other */
                        quantity = Float.MIN_VALUE;
                        dosageValueView.setEnabled(true);
                        dosageValueView.setSelection(0);
                        dosageValueView.requestFocus();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dosageValueView.getText().clear();
                dosageValueView.setEnabled(false);
            }
        });

        rootView.findViewById(R.id.fragment_drug_packaging_overview_confirm).setOnClickListener((v) -> {
            if (quantity != Float.MIN_VALUE) {
                ((DrugLookupActivity) requireActivity()).onDrugPackagingAndDosageConfirmed(
                        new DrugIntake(drugPackaging, (int) quantity,
                                selectedUnit.name(), null, null, null)
                );
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Aifa.getDrugLookupService().findPackagingFormatById(
                Aifa.buildDrugPackagingByIdQueryString(
                        requireArguments().getString("id")))
                .enqueue(new DrugPackagingLookupByIdCallback());
    }

    private void showDrugPackagingInfo(final DrugPackaging drugPackaging) {
        final TextView drugNameView = requireView()
                .findViewById(R.id.fragment_drug_packaging_overview_drug_name);
        final TextView drugMakerView = requireView()
                .findViewById(R.id.fragment_drug_packaging_overview_drug_maker);
        final TextView drugPackagingNameView = requireView()
                .findViewById(R.id.fragment_drug_packaging_overview_packaging_name);
        final TextView drugPackagingActivePrincipleView = requireView()
                .findViewById(R.id.fragment_drug_packaging_overview_packaging_active_principle);
        drugNameView.setText(normalizeText(drugPackaging.getDrugDescription()));
        drugMakerView.setText(normalizeText(drugPackaging.getDrugMaker()));
        drugPackagingNameView.setText(normalizeText(drugPackaging.getPackagingDescription()));
        drugPackagingActivePrincipleView.setText(normalizeText(drugPackaging.getActivePrinciple()));
    }

    private static Spanned normalizeText(final String info) {
        if (info != null && !info.isEmpty()) {
            return Html.fromHtml(info);
        }
        return new SpannableString("-");
    }

    private class DrugPackagingLookupByIdCallback implements Callback<List<DrugPackaging>> {
        @Override
        public void onResponse(@NonNull Call<List<DrugPackaging>> call, @NonNull Response<List<DrugPackaging>> response) {
            if (response.isSuccessful()) {
                final List<DrugPackaging> drugPackagingList = response.body();
                if (drugPackagingList != null && !drugPackagingList.isEmpty()) {
                    showDrugPackagingInfo(drugPackaging = drugPackagingList.get(0));
                } else {
                    Toast.makeText(requireContext(),
                            R.string.fragment_drug_packaging_overview_error,
                                Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            } else {
                Toast.makeText(requireContext(),
                        R.string.fragment_drug_packaging_overview_error,
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<DrugPackaging>> call, @NonNull Throwable t) {
            Toast.makeText(requireContext(),
                    R.string.fragment_drug_packaging_overview_error,
                    Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    public static DrugPackagingOverviewFragment of(final String id) {
        final DrugPackagingOverviewFragment fragment = new DrugPackagingOverviewFragment();
        final Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }
}
