package revolver.headead.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import revolver.headead.R;
import revolver.headead.ui.adapters.DrugDosageUnitsAdapter2;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.TextUtils;

public class SimpleAlertDialogFragment extends DialogFragment {

    private String title, message, positiveButton, negativeButton, neutralButton;
    private OnDialogButtonClicked positiveButtonListener, negativeButtonListener, neutralButtonListener;
    private boolean dismissOnPositive, dismissOnNegative, dismissOnNeutral;
    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> listAdapter;
    private String inputHint;
    private int inputType = EditorInfo.TYPE_CLASS_TEXT;

    private View customView;
    private int[] customPaddings = { -1, -1, -1, -1 };

    private EditText editText;
    private EntryTextValidator entryTextValidator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_alert_simple, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(requireContext(), R.style.RoundedAdaptiveDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int leftPadding = customPaddings[0];
        final int topPadding = customPaddings[1];
        final int rightPadding = customPaddings[2];
        final int bottomPadding = customPaddings[3];
        view.setPadding(leftPadding >= 0 ? leftPadding : view.getPaddingLeft(),
                topPadding >= 0 ? topPadding : view.getPaddingTop(),
                    rightPadding >= 0 ? rightPadding : view.getPaddingRight(),
                        bottomPadding >= 0 ? bottomPadding : view.getPaddingBottom());

        final TextView titleView = view.findViewById(R.id.dialog_alert_simple_title);
        titleView.setText(title);
        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
        }

        final TextView messageView = view.findViewById(R.id.dialog_alert_simple_message);
        messageView.setText(message);
        if (message != null) {
            messageView.setVisibility(View.VISIBLE);
        }

        final TextView positiveButtonView = view.findViewById(R.id.dialog_alert_simple_positive);
        positiveButtonView.setText(positiveButton);
        positiveButtonView.setOnClickListener((v) -> {
            if (dismissOnPositive) {
                dismiss();
            }
            if (positiveButtonListener != null) {
                positiveButtonListener.onButtonClicked(this);
            }
        });
        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
        }

        final TextView negativeButtonView = view.findViewById(R.id.dialog_alert_simple_negative);
        negativeButtonView.setText(negativeButton);
        negativeButtonView.setOnClickListener((v) -> {
            if (dismissOnNegative) {
                dismiss();
            }
            if (negativeButtonListener != null) {
                negativeButtonListener.onButtonClicked(this);
            }
        });
        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
        }

        final TextView neutralButtonView = view.findViewById(R.id.dialog_alert_simple_neutral);
        neutralButtonView.setText(neutralButton);
        neutralButtonView.setOnClickListener((v) -> {
            if (dismissOnNeutral) {
                dismiss();
            }
            if (neutralButtonListener != null) {
                neutralButtonListener.onButtonClicked(this);
            }
        });
        if (neutralButton != null) {
            neutralButtonView.setVisibility(View.VISIBLE);
        }

        final RecyclerView listView = view.findViewById(R.id.dialog_alert_simple_list);
        listView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        if (listAdapter instanceof DrugDosageUnitsAdapter2) {
            ((DrugDosageUnitsAdapter2) listAdapter)
                    .setLayoutManager(listView.getLayoutManager());
        }
        listView.setAdapter(listAdapter);
        if (listAdapter != null) {
            listView.setVisibility(View.VISIBLE);
        }

        final TextInputLayout editTextContainer =
                view.findViewById(R.id.dialog_alert_simple_entry_container);
        editText = view.findViewById(R.id.dialog_alert_simple_entry);
        editText.setHint(inputHint);
        editText.setInputType(inputType);
        editText.addTextChangedListener(new TextUtils.SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (entryTextValidator != null) {
                    positiveButtonView.setEnabled(entryTextValidator.validate(editText));
                }
            }
        });
        if (inputHint != null) {
            editTextContainer.setVisibility(View.VISIBLE);
            positiveButtonView.setEnabled(false);
        }

        final ScrollView customViewContainer =
                view.findViewById(R.id.dialog_alert_simple_container);
        if (customView != null) {
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(customView);
        }

        if (positiveButtonView.getVisibility() != View.VISIBLE &&
                negativeButtonView.getVisibility() != View.VISIBLE &&
                    neutralButtonView.getVisibility() != View.VISIBLE) {
            view.findViewById(R.id.dialog_alert_simple_button_container).setVisibility(View.GONE);
        }
    }

    public void customPadding(int index, int padding) {
        this.customPaddings[index] = padding;
    }

    public void title(String title) {
        this.title = title;
    }

    public void message(String message) {
        this.message = message;
    }

    public void positiveButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
        this.positiveButton = text;
        this.positiveButtonListener = listener;
        this.dismissOnPositive = dismiss;
    }

    public void negativeButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
        this.negativeButton = text;
        this.negativeButtonListener = listener;
        this.dismissOnNegative = dismiss;
    }

    public void neutralButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
        this.neutralButton = text;
        this.neutralButtonListener = listener;
        this.dismissOnNeutral = dismiss;
    }

    public void list(final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        this.listAdapter = adapter;
    }

    public void editText(final String hint, int inputType, EntryTextValidator validator) {
        this.inputHint = hint;
        this.inputType = inputType;
        this.entryTextValidator = validator;
    }

    public void customView(final View view) {
        this.customView = view;
    }

    public CharSequence getEntryText() {
        return editText != null ? editText.getText() : null;
    }

    public View getPositiveButton() {
        return requireView().findViewById(R.id.dialog_alert_simple_positive);
    }

    public View getNegativeButton() {
        return requireView().findViewById(R.id.dialog_alert_simple_negative);
    }

    public View getNeutralButton() {
        return requireView().findViewById(R.id.dialog_alert_simple_neutral);
    }

    public EditText getEntryView() {
        return requireView().findViewById(R.id.dialog_alert_simple_entry);
    }

    public TextInputLayout getEntryViewContainer() {
        return requireView().findViewById(R.id.dialog_alert_simple_entry_container);
    }

    public RecyclerView getListView() {
        return requireView().findViewById(R.id.dialog_alert_simple_list);
    }

    public View getCustomView() {
        return customView;
    }

    public static class Builder {

        private final Context context;
        private final SimpleAlertDialogFragment fragment;

        public Builder(Context context) {
            this.context = context;
            this.fragment = new SimpleAlertDialogFragment();
        }

        public Builder leftPadding(int dp) {
            this.fragment.customPadding(0, M.dp(dp).intValue());
            return this;
        }

        public Builder topPadding(int dp) {
            this.fragment.customPadding(1, M.dp(dp).intValue());
            return this;
        }

        public Builder rightPadding(int dp) {
            this.fragment.customPadding(2, M.dp(dp).intValue());
            return this;
        }

        public Builder bottomPadding(int dp) {
            this.fragment.customPadding(3, M.dp(dp).intValue());
            return this;
        }

        public Builder horizontalPadding(int dp) {
            this.leftPadding(dp);
            this.rightPadding(dp);
            return this;
        }

        public Builder verticalPadding(int dp) {
            this.topPadding(dp);
            this.bottomPadding(dp);
            return this;
        }

        public Builder title(final String title) {
            this.fragment.title(title);
            return this;
        }

        public Builder title(final @StringRes int title) {
            this.fragment.title(this.context.getString(title));
            return this;
        }

        public Builder message(final String message) {
            this.fragment.message(message);
            return this;
        }

        public Builder message(final @StringRes int message) {
            this.fragment.message(this.context.getString(message));
            return this;
        }

        public Builder positiveButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.positiveButton(text, listener, dismiss);
            return this;
        }

        public Builder positiveButton(final @StringRes int text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.positiveButton(this.context.getString(text), listener, dismiss);
            return this;
        }

        public Builder negativeButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.negativeButton(text, listener, dismiss);
            return this;
        }

        public Builder negativeButton(final @StringRes int text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.negativeButton(this.context.getString(text), listener, dismiss);
            return this;
        }

        public Builder neutralButton(String text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.neutralButton(text, listener, dismiss);
            return this;
        }

        public Builder neutralButton(final @StringRes int text, OnDialogButtonClicked listener, boolean dismiss) {
            this.fragment.neutralButton(this.context.getString(text), listener, dismiss);
            return this;
        }

        public Builder list(final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
            this.fragment.list(adapter);
            return this;
        }

        public Builder editText(final String hint, int inputType, @Nullable EntryTextValidator validator) {
            this.fragment.editText(hint, inputType, validator);
            return this;
        }

        public Builder editText(final @StringRes int hint, int inputType, @Nullable EntryTextValidator validator) {
            this.fragment.editText(context.getString(hint), inputType, validator);
            return this;
        }

        public Builder customView(final View view) {
            this.fragment.customView(view);
            return this;
        }

        public SimpleAlertDialogFragment build() {
            return this.fragment;
        }
    }

    public interface EntryTextValidator {
        boolean validate(EditText editText);
    }

    public interface OnDialogButtonClicked {
        void onButtonClicked(SimpleAlertDialogFragment fragment);
    }
}
