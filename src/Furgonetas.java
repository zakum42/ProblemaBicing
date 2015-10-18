import IA.Bicing.Estaciones;

import java.util.ArrayList;
import java.util.Random;

public class Furgonetas extends ArrayList<Furgoneta> implements Cloneable{

    private int nfurg;
    private int nest;
    private long seed;
    private Estaciones estaciones;

    public Furgonetas(int nfurg, int nest, long seed, Estaciones estaciones) {

        this.nfurg = nfurg;
        this.nest = nest;
        this.seed = seed;
        this.estaciones = estaciones;

        Random myRandom = new Random(seed);

        Furgoneta f;
        for (int i = 0; i < nfurg; ++i) {
            int idEstOrigen = myRandom.nextInt(nest);
            int idPrimDestino = myRandom.nextInt(nest);
            int idSegDestino = myRandom.nextInt(nest);
            int bicisOrigen = estaciones.get(idEstOrigen).getNumBicicletasNoUsadas();
            int bicisfurg = (bicisOrigen >= 30)? 30: bicisOrigen;
            int bicis1est;
            int delta = estaciones.get(idPrimDestino).getDemanda() - estaciones.get(idPrimDestino).getNumBicicletasNext();
            if (delta > 0)
                bicis1est = (bicisfurg >= delta)? delta: bicisfurg;
            else {
                bicis1est = 0;
            }
            f = new Furgoneta(
                    estaciones.get(idEstOrigen),
                    estaciones.get(idPrimDestino),
                    estaciones.get(idSegDestino),
                    bicisfurg,
                    bicis1est
            );
            this.add(f);
        }

    }

    public Furgonetas(int nfurg, int nest, Estaciones estaciones)
    {
        this.nfurg = nfurg;
        this.nest = nest;
        this.estaciones = estaciones;
    }

    @Override
    public Furgonetas clone() {
        super.clone();
        Furgonetas nF = new Furgonetas(nfurg, nest, estaciones);
        for (Furgoneta f : this) {
            nF.add(f.clone());
        }
        return nF;
    }
}
