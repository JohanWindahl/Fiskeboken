package com.example.johan.fiskeboken;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogFragment extends androidx.fragment.app.Fragment {

    //For saving states
    static final String STATE_OF_LIST = "listState";
    static final String WEIGHT_STATE = "weightState";
    static final String LENGTH_STATE = "heightState";
    static final String LANG_STATE = "langState";

    //Constructors
    public LogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log, container, false);
        v.setTag("RecyclerViewFragment");

        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.recView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        CustomAdapter adapter = new CustomAdapter(this.getContext());
        adapter.setCatchList(AppSettings.getCatchList());
        recycler.setAdapter(adapter);

        recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_OF_LIST, AppSettings.getCatchList());
        savedInstanceState.putSerializable(WEIGHT_STATE, AppSettings.isKg());
        savedInstanceState.putSerializable(LENGTH_STATE, AppSettings.isM());
        savedInstanceState.putSerializable(LANG_STATE, AppSettings.isSwedish());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            AppSettings.setAllCatchesList((ArrayList<Catch>) savedInstanceState.getSerializable(STATE_OF_LIST));
            AppSettings.setKg((Boolean) savedInstanceState.getSerializable(WEIGHT_STATE));
            AppSettings.setM((Boolean) savedInstanceState.getSerializable(LENGTH_STATE));
            AppSettings.setSwedish((Boolean) savedInstanceState.getSerializable(LANG_STATE));
        }
    }

    //This custom adapter class is used for the items in the cardview in the main log fragment
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        private ArrayList<Catch> catchList;
        private Context context;

        private CustomAdapter(Context context) {
            this.catchList = new ArrayList<>();
            this.context = context;
        }

        private void setCatchList(List<Catch> cList) {
            catchList.clear();
            catchList.addAll(cList);
            this.notifyItemRangeInserted(0, catchList.size() - 1);
        }

        public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.catch_item, parent,false);
            final CustomViewHolder evh = new CustomViewHolder(v);

            evh.mRemoveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeAt(evh.getLayoutPosition());
                }
            });
            return evh;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
            Catch currentCatch = catchList.get(position);
            holder.mFishType.setText(currentCatch.getFishType());
            holder.mDescription.setText(currentCatch.getDescription());

            DateTime date = currentCatch.getDate();
            String dateStr = date.getYear() + "-"  + date.getMonthOfYear() + "-" + date.getDayOfMonth();
            holder.mDate.setText(dateStr);

            String wUnit;
            String hUnit;
            if (AppSettings.isKg()) {wUnit = "kg";}
            else {wUnit = "lbs";}

            if (AppSettings.isM()) {hUnit = "cm";}
            else {hUnit = "inch";}

            String weight = new DecimalFormat("#.##").format(currentCatch.getWeight());
            String height = new DecimalFormat("#.##").format(currentCatch.getHeight());

            holder.mWeight.setText(weight + wUnit);
            holder.mHeight.setText(height + hUnit);


            try {
                String lat = new DecimalFormat("#.##").format(currentCatch.getGpsLat());
                String lng = new DecimalFormat("#.##").format(currentCatch.getGpsLong());
                holder.mGps.setText("lat/lng: (" + lat + "," + lng + ")");
            }
            catch (NullPointerException e) {
                holder.mGps.setText("Inga koordinater satta");
            }
            catch (IllegalArgumentException i) {
                holder.mGps.setText("Inga koordinater satta");
            }


            try {
                holder.mRemoveImage.setImageResource(currentCatch.getRemoveImage());

            }
            catch (NullPointerException n) {


            }
        }

        @Override
        public int getItemCount() {
            return catchList.size();
        }

        //remove item "position" from list
        public void removeAt(int position) {
            AppSettings.removeItem(position);
            DateTime date = catchList.get(position).getDate();
            String dateStr = date.getYear() + "-"  + date.getMonthOfYear() + "-" + date.getDayOfMonth();
            Toast.makeText(context, "Fisk #" + position + " - " +catchList.get(position).getFishType() + " @ " + dateStr + " borttagen", 2).show();
            catchList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, catchList.size());
        }

        //This class if for binding items to ui-elements
        public class CustomViewHolder extends RecyclerView.ViewHolder{
            private TextView mFishType;
            private TextView mDescription;
            private TextView mDate;
            private TextView mWeight;
            private TextView mHeight;
            private TextView mGps;
            private ImageView mRemoveImage;

            private CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                mFishType = itemView.findViewById(R.id.fishType);
                mDescription = itemView.findViewById(R.id.description);
                mDate  = itemView.findViewById(R.id.date);
                mWeight = itemView.findViewById(R.id.weight);
                mHeight = itemView.findViewById(R.id.height);
                mGps = itemView.findViewById(R.id.GPS);
                mRemoveImage = itemView.findViewById(R.id.removeImage);
            }
        }
    }
}