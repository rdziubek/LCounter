package pl.witampanstwa.lcounter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mDateText;
    private ArrayList<String> mHourText;

    public RecyclerViewAdapter(ArrayList<String> dateText, ArrayList<String> hourText) {
        this.mDateText = dateText;
        this.mHourText = hourText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.twNumber.setText(Integer.toString(i + 1));
        viewHolder.twDate.setText(mDateText.get(i));
        viewHolder.twHour.setText(mHourText.get(i));
    }

    @Override
    public int getItemCount() {
        return mDateText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView twNumber, twDate, twHour;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            twNumber = itemView.findViewById(R.id.twNumber);
            twDate = itemView.findViewById(R.id.twDate);
            twHour = itemView.findViewById(R.id.twHour);
        }
    }
}
