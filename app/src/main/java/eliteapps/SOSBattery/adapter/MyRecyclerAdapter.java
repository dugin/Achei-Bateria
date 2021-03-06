package eliteapps.SOSBattery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    Context context;
    private List<Estabelecimentos> mList;
    private List<Estabelecimentos> mListFechados;

    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Location minhaLocalizacao;


    public MyRecyclerAdapter(Context c, List<Estabelecimentos> l, List<Estabelecimentos> listaFechados, Location minhaLocalizacao) {
        this.minhaLocalizacao = minhaLocalizacao;
        mListFechados = listaFechados;
        context = c;
        mList = l;

    }

    @Override
    public int getItemViewType(int position) {

        if (mList.size() <= position)
            return VIEW_TYPES.Fechados;

        else
            return VIEW_TYPES.Normal;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_lista_lojas, viewGroup, false);
            return new MyViewHolder(v, viewType);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {

        if (myViewHolder instanceof MyViewHolder) {
            List<Estabelecimentos> list = null;
            Estabelecimentos estabelecimentos = null;

            MyViewHolder genericViewHolder = (MyViewHolder) myViewHolder;

            if (genericViewHolder.viewType == VIEW_TYPES.Normal) {
                genericViewHolder.txtTituloFechado.setVisibility(View.GONE);
                list = mList;
                estabelecimentos = list.get(position);
            } else if (genericViewHolder.viewType == VIEW_TYPES.Fechados) {
                genericViewHolder.contentLayout.setAlpha((float) 0.6);
                list = mListFechados;
                estabelecimentos = list.get(position - mList.size());
                if (position - mList.size() == 0)
                    genericViewHolder.txtTituloFechado.setVisibility(View.VISIBLE);

                else
                    genericViewHolder.txtTituloFechado.setVisibility(View.GONE);
            }


            genericViewHolder.txtNome.setText(estabelecimentos.getNome());
            genericViewHolder.txtEnd.setText(estabelecimentos.getEnd());
            genericViewHolder.txtBairro.setText(estabelecimentos.getBairro());

            Location locationLojas = new Location("");
            locationLojas.setLatitude(Double.parseDouble(estabelecimentos.getCoordenadas()[0]));
            locationLojas.setLongitude(Double.parseDouble(estabelecimentos.getCoordenadas()[1]));


            float dist = locationLojas.distanceTo(minhaLocalizacao);
            // Log.println(Log.ASSERT, TAG, "distancia metros: " + dist);
            int minutos = calculaTempoMin(dist);

            genericViewHolder.txtDist.setText(String.format("%d min", (int) minutos));

            if (!estabelecimentos.getWifi())
                genericViewHolder.wifi.setVisibility(View.INVISIBLE);
            else
                genericViewHolder.wifi.setVisibility(View.VISIBLE);

            if (!estabelecimentos.getCabo().getAndroid())
                genericViewHolder.cabo.setVisibility(View.INVISIBLE);
            else
                genericViewHolder.cabo.setVisibility(View.VISIBLE);


            String hrFunc = CalendarUtil.HrFuncionamento(estabelecimentos);
            genericViewHolder.txtHrFunc.setText(hrFunc);
            if (hrFunc.equals("Não abre")) {


                genericViewHolder.txtHrFunc.setTextColor(Color.RED);
            } else {
                genericViewHolder.txtHrFunc.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


            if (estabelecimentos.getImgURL().length() > 2) {
                Picasso.with(context)
                        .load(estabelecimentos.getImgURL())
                        .error(R.drawable.no_image)
                        .resize(150, 150)
                        .transform(new CircleTransformation())
                        .centerCrop()
                        .into(genericViewHolder.imgLoja);
            } else {
                Picasso.with(context)
                        .load(R.drawable.no_image)
                        .resize(150, 150)

                        .into(genericViewHolder.imgLoja);

            }
        }



    }

    @Override
    public int getItemCount() {

        return mList.size() + mListFechados.size();


    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
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

        ImageView wifi, cabo, imgLoja;
        private TextView txtEnd, txtDist, txtHrFunc, txtBairro, txtNome, txtTituloFechado;
        private int viewType;
        private RelativeLayout contentLayout;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;

            // Add the title view
            contentLayout = (RelativeLayout) itemView.findViewById(R.id.content_layout);
            txtNome = (TextView) itemView.findViewById(R.id.txtNome);
            txtEnd = (TextView) itemView.findViewById(R.id.txtEnd);
            txtBairro = (TextView) itemView.findViewById(R.id.txtBairro);
            txtDist = (TextView) itemView.findViewById(R.id.txtDist);
            txtHrFunc = (TextView) itemView.findViewById(R.id.txtHrFunc);
            wifi = (ImageView) itemView.findViewById(R.id.imgWifi);
            cabo = (ImageView) itemView.findViewById(R.id.imgCabo);

            txtTituloFechado = (TextView) itemView.findViewById(R.id.txtTituloFechados);

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

    private class VIEW_TYPES {
        public static final int Normal = 1;
        public static final int Fechados = 3;
    }
}
