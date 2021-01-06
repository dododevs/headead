package revolver.headead.ui.fragments.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.display.criteria.ByDuration;
import revolver.headead.core.display.criteria.ByMonth;
import revolver.headead.core.display.criteria.ByPainIntensity;
import revolver.headead.core.display.criteria.ByPainLocation;
import revolver.headead.core.display.criteria.ByPainType;
import revolver.headead.core.display.criteria.OrderingCriterion;
import revolver.headead.core.display.filters.ByDrugPackaging;
import revolver.headead.core.display.filters.FilteringCriterion;
import revolver.headead.core.display.filters.NoFilter;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.Headache;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.adapters.RecordedHeadachesAdapter;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class ListHeadachesFragment extends Fragment {

    private List<Headache> headaches;
    private RecordedHeadachesAdapter adapter;

    private RecyclerView listView;
    private View noHeadachesView;

    private static OrderingCriterion orderingCriterion;
    private static FilteringCriterion filteringCriterion;
    private static int scroll;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireRecordHeadacheActivity().getToolbar()
                .setTitle(R.string.fragment_list_headaches_title);
        requireRecordHeadacheActivity().getToolbar()
                .inflateMenu(R.menu.toolbar_fragment_list_headaches);
        requireRecordHeadacheActivity().getToolbar().setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.filter) {
                buildOrderingAndFilteringDialog().show(getChildFragmentManager(), null);
            }
            return true;
        });
        requireRecordHeadacheActivity().getToolbar()
                .setNavigationIcon(R.drawable.ic_view_list);
        requireRecordHeadacheActivity().getToolbar().setNavigationOnClickListener((v) -> {
            // TODO: show menu with view options
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requireRecordHeadacheActivity().resetToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_headaches, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list);

        final LinearLayoutManager layoutManager;
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false));

        headaches = App.getDefaultRealm().copyFromRealm(
                App.getDefaultRealm().where(Headache.class).findAll());
        listView.setAdapter(adapter = new RecordedHeadachesAdapter(
                requireContext(), headaches,
                    filteringCriterion, orderingCriterion != null ?
                        orderingCriterion : (orderingCriterion = new ByMonth())));
        if (scroll != -1 && scroll < adapter.getItemCount()) {
            layoutManager.scrollToPosition(scroll);
        }
        adapter.setOnHeadacheSelectedListener((headache, holder) -> {
            scroll = layoutManager.findFirstCompletelyVisibleItemPosition();
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    HeadacheDetailFrontFragment.of(headache), "detailFront",
                        M.screenHeight() - ViewUtils.getStatusBarHeight() - M.dp(128.f + 16.f).intValue(),
                            false, false);
            requireRecordHeadacheActivity().replaceBackdropFragment(
                    HeadacheDetailBackdropFragment.of(headache));
        });
        adapter.setOnHeadacheLongClickedListener((headache, holder) ->
                buildRemovalDialog(headache, holder).show(getChildFragmentManager(), null));

        noHeadachesView = view.findViewById(R.id.no_results);
        if (headaches.isEmpty()) {
            listView.setVisibility(View.GONE);
            noHeadachesView.setVisibility(View.VISIBLE);
        } else {
            noHeadachesView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.fab).setOnClickListener((v) -> {
            requireRecordHeadacheActivity().clearAllFields();
            requireRecordHeadacheActivity().resetBottomPane();
        });
    }

    private SimpleAlertDialogFragment buildOrderingAndFilteringDialog() {
        final View view = View.inflate(requireContext(), R.layout.dialog_main_list_view, null);

        final Spinner drugSpinner =
                view.findViewById(R.id.dialog_main_list_view_filter_by_drug_spinner);
        final List<DrugPackaging> distinctDrugPackagings = getDrugsFromIntakes();
        drugSpinner.setAdapter(new FilterByDrugSpinnerAdapter(
                requireContext(), distinctDrugPackagings));
        final MaterialCheckBox filterByDrugCheckbox =
                view.findViewById(R.id.dialog_main_list_view_filter_by_drug);
        filterByDrugCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                drugSpinner.setVisibility(View.VISIBLE);
            } else {
                drugSpinner.setVisibility(View.GONE);
            }
        });

        return new SimpleAlertDialogFragment.Builder(requireContext())
                .title(R.string.dialog_main_list_view_title)
                .customView(view)
                .positiveButton(R.string.dialog_main_list_view_positive, fragment -> {
                    final RadioGroup groupingSelector = fragment
                            .requireView().findViewById(R.id.dialog_main_list_view_grouping);
                    final int id = groupingSelector.getCheckedRadioButtonId();
                    final OrderingCriterion oldOrderingCriterion = orderingCriterion;
                    boolean shouldApplyOrdering = true;

                    if (id == R.id.dialog_main_list_view_grouping_by_month) {
                        orderingCriterion = new ByMonth();
                    } else if (id == R.id.dialog_main_list_view_grouping_by_pain_location) {
                        orderingCriterion = new ByPainLocation();
                    } else if (id == R.id.dialog_main_list_view_grouping_by_pain_intensity) {
                        orderingCriterion = new ByPainIntensity();
                    } else if (id == R.id.dialog_main_list_view_grouping_by_pain_type) {
                        orderingCriterion = new ByPainType();
                    } else if (id == R.id.dialog_main_list_view_grouping_by_duration) {
                        orderingCriterion = new ByDuration();
                    } else {
                        shouldApplyOrdering = false;
                    }
                    if (oldOrderingCriterion.equals(orderingCriterion)) {
                        shouldApplyOrdering = false;
                    }

                    if (shouldApplyOrdering) {
                        adapter.applyOrderingCriterion(orderingCriterion);
                        adapter.notifyDataSetChanged();
                    }

                    if (filterByDrugCheckbox.isChecked()) {
                        int drugPosition = drugSpinner.getSelectedItemPosition();
                        if (drugPosition == 0) {
                            return;
                        } else {
                            drugPosition--;
                        }
                        final DrugPackaging selectedDrugPackaging = distinctDrugPackagings.get(drugPosition);
                        final FilteringCriterion oldFilteringCriterion = filteringCriterion;
                        filteringCriterion = new ByDrugPackaging(selectedDrugPackaging);
                        if (oldFilteringCriterion == null || !oldFilteringCriterion.equals(filteringCriterion)) {
                            adapter.applyFilteringCriterion(filteringCriterion);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        filteringCriterion = new NoFilter();
                        adapter.applyFilteringCriterion(filteringCriterion);
                        adapter.notifyDataSetChanged();
                    }
                }, true).negativeButton(R.string.dialog_main_list_view_negative, null, true).build();
    }

    private SimpleAlertDialogFragment buildRemovalDialog(final Headache headache, final RecordedHeadachesAdapter.ViewHolder holder) {
        return new SimpleAlertDialogFragment.Builder(requireContext())
                .title(R.string.dialog_recorded_headache_removal_title)
                .message(R.string.dialog_recorded_headache_removal_message)
                .positiveButton(R.string.dialog_recorded_headache_removal_positive, fragment -> {
                    App.getDefaultRealm().executeTransaction(realm -> {
                        realm.where(Headache.class)
                                .equalTo("uuid", headache.getUuid())
                                .findAll()
                                .deleteAllFromRealm();
                    });
                    adapter.setHeadaches(headaches = App.getDefaultRealm().copyFromRealm(
                            App.getDefaultRealm().where(Headache.class).findAll()));
                    adapter.notifyDataSetChanged();
                    if (headaches.isEmpty()) {
                        listView.setVisibility(View.GONE);
                        noHeadachesView.setVisibility(View.VISIBLE);
                    } else {
                        noHeadachesView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }, true).negativeButton(R.string.dialog_recorded_headache_removal_negative, null, true).build();
    }

    private List<DrugPackaging> getDrugsFromIntakes() {
        final List<DrugPackaging> drugs = new ArrayList<>();
        for (final Headache headache : headaches) {
            for (final DrugIntake drugIntake : headache.getDrugIntakes()) {
                if (!drugs.contains(drugIntake.getDrugPackaging())) {
                    drugs.add(drugIntake.getDrugPackaging());
                }
            }
        }
        return drugs;
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    private static class FilterByDrugSpinnerAdapter extends SimpleAdapter {
        FilterByDrugSpinnerAdapter(final Context context, final List<DrugPackaging> drugPackagings) {
            super(context, generateSpinnerAdapterMaps(context, drugPackagings), R.layout.item_drug_filter, new String[] {
                    "name", "packaging", "id"
            }, new int[] {
                    R.id.item_drug_filter_name,
                    R.id.item_drug_filter_packaging_name,
                    R.id.item_drug_filter_packaging_active_principle
            });
        }

        private static List<Map<String, Spanned>> generateSpinnerAdapterMaps(final Context context, final List<DrugPackaging> drugPackagings) {
            final List<Map<String, Spanned>> maps = new ArrayList<>();

            final SpannableString emptySpannable = new SpannableString("");
            final ArrayMap<String, Spanned> defaultMap = new ArrayMap<>();
            defaultMap.put("name", emptySpannable);
            defaultMap.put("packaging", new SpannableStringBuilder(
                    context.getString(R.string.item_drug_filter_name_default)));
            defaultMap.put("id", emptySpannable);
            maps.add(defaultMap);

            for (final DrugPackaging drugPackaging : drugPackagings) {
                final ArrayMap<String, Spanned> map = new ArrayMap<>();
                map.put("name", Html.fromHtml(drugPackaging.getDrugDescription()));
                map.put("packaging", Html.fromHtml(drugPackaging.getPackagingDescription()));
                map.put("id", Html.fromHtml(drugPackaging.getActivePrinciple()));
                maps.add(map);
            }
            return maps;
        }
    }
}
