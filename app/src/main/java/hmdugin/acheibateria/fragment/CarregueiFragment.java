package hmdugin.acheibateria.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hmdugin.acheibateria.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarregueiFragment extends Fragment {


    public CarregueiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_carreguei, container, false);

        return v;
    }


}
