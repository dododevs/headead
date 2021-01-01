package revolver.headead.ui.adapters;

import android.location.Address;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import revolver.headead.R;
import revolver.headead.util.ui.ViewUtils;

public class GeocoderResultsAdapter extends RecyclerView.Adapter<GeocoderResultsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView placeNameView;
        final TextView placeLocationView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(8.f).get());

            placeNameView = v.findViewById(R.id.item_place_name);
            placeLocationView = v.findViewById(R.id.item_place_location);
        }
    }

    private List<Address> addresses;
    private OnGeocoderResultClickedListener clickListener;

    public GeocoderResultsAdapter(final List<Address> addresses) {
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_place, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Address address = addresses.get(position);
        if (address.getFeatureName() != null) {
            holder.placeNameView.setText(address.getFeatureName());
        } else if (address.getAddressLine(0) != null) {
            holder.placeNameView.setText(address.getAddressLine(0));
        } else {
            holder.placeNameView.setText(R.string.item_place_name_unknown);
        }
        final StringBuilder sb = new StringBuilder();
        if (address.getThoroughfare() != null) {
            sb.append(address.getThoroughfare()).append(" ");
        }
        if (address.getLocality() != null) {
            sb.append(address.getLocality()).append(" ");
        }
        if (address.getPostalCode() != null) {
            sb.append(address.getPostalCode()).append(" ");
        }
        holder.placeLocationView.setText(sb.toString().trim());
        holder.itemView.setOnClickListener((v) -> {
            if (clickListener != null) {
                clickListener.onResultClicked(addresses.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses != null ? addresses.size() : 0;
    }

    public void updateAddressesList(final List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    public void setOnGeocoderResultClickedListener(final OnGeocoderResultClickedListener listener) {
        clickListener = listener;
    }

    public interface OnGeocoderResultClickedListener {
        void onResultClicked(final Address address);
    }
}
