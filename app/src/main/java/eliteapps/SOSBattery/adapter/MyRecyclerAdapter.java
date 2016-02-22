package eliteapps.SOSBattery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.extras.CircleTransformation;
import eliteapps.SOSBattery.extras.RecyclerViewOnClickListenerHack;
import eliteapps.SOSBattery.util.CalendarUtil;

/**
 * Created by Rodrigo on 16/02/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    Context context;
    private List<Estabelecimentos> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Location minhaLocalizacao;


    public MyRecyclerAdapter(Context c, List<Estabelecimentos> l, Location minhaLocalizacao) {
        this.minhaLocalizacao = minhaLocalizacao;
        context = c;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("LOG", "onCreateViewHolder()");
        View v = mLayoutInflater.inflate(R.layout.content_lista_lojas, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Log.i("LOG", "onBindViewHolder()");
        Estabelecimentos estabelecimentos = mList.get(position);

        myViewHolder.txtNome.setText(estabelecimentos.getNome());
        myViewHolder.txtEnd.setText(estabelecimentos.getEnd());
        myViewHolder.txtBairro.setText(estabelecimentos.getBairro());


        Location locationLojas = new Location("");
        locationLojas.setLatitude(Double.parseDouble(estabelecimentos.getL()[0]));
        locationLojas.setLongitude(Double.parseDouble(estabelecimentos.getL()[1]));

        float dist = locationLojas.distanceTo(minhaLocalizacao);
        Log.println(Log.ASSERT, TAG, "distancia km: " + dist);
        int minutos = calculaTempoMin(dist);

        myViewHolder.txtDist.setText(String.format("%d min", (int) minutos));

        if (estabelecimentos.getWifi().equals("false"))
            myViewHolder.wifi.setVisibility(View.GONE);


        String hrFunc = CalendarUtil.HrFuncionamento(estabelecimentos);
        myViewHolder.txtHrFunc.setText(hrFunc);
        if (hrFunc.equals("Fechado"))
            myViewHolder.txtHrFunc.setTextColor(Color.RED);

        Picasso.with(context)
                .load(estabelecimentos.getImg().getUrl())
                .resize(50, 50)
                .transform(new CircleTransformation())
                .centerCrop()
                .into(myViewHolder.imgLoja);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }


    public void addListItem(Estabelecimentos e, int position) {
        mList.add(e);
        notifyItemInserted(position);
    }


    public void removeListItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    private int calculaTempoMin(double distanciaM) {
        double segundos = distanciaM / 1.3;
        double minutos = segundos / 60;
        if ((int) minutos == 0)
            minutos = 1;
        return (int) minutos;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final String TAG = this.getClass().getSimpleName();

        ImageView wifi, imgLoja;
        private TextView txtEnd, txtDist, txtHrFunc, txtBairro, txtNome;

        public MyViewHolder(View itemView) {
            super(itemView);


            // Add the title view
            txtNome = (TextView) itemView.findViewById(R.id.txtNome);
            txtEnd = (TextView) itemView.findViewById(R.id.txtEnd);
            txtBairro = (TextView) itemView.findViewById(R.id.txtBairro);
            txtDist = (TextView) itemView.findViewById(R.id.txtDist);
            txtHrFunc = (TextView) itemView.findViewById(R.id.txtHrFunc);
            wifi = (ImageView) itemView.findViewById(R.id.imgWifi);

            imgLoja = (ImageView) itemView.findViewById(R.id.imgLoja);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }
    }
}
