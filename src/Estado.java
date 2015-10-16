import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Estado {
    private Furgonetas furgonetas;
    private static Estaciones estaciones;
    private ArrayList<Integer> bicisE;
    private static Map<Estacion, Integer> m;


    public static String SUSTITUIR_ESTACION = "sustituir_estacion";
    public static String DEJAR_BICIS = "dejar_bicis";
    public static String RECOGER_BICIS = "recoger_bicis";
    public static String CAMBIAR_ESTACION_ORIGIEN = "cambiar_estacion_origen";
    public static String QUITAR_ESTACION = "quitar_estacion";

    public Estado(int nf, Estaciones est) {
        estaciones = est;
        furgonetas = new Furgonetas(nf, estaciones.size(), System.currentTimeMillis(), estaciones);
        bicisE = new ArrayList<>();
        m = new HashMap<>();
        for (int i = 0; i < estaciones.size(); ++i) {
            m.put(estaciones.get(i), i);
        }
        bicisE.addAll(m.values());

        for (Furgoneta f : this.furgonetas)
        {
            Estacion e1 = f.getPrimerDestino();
            if(e1 != null)
            {
                int index = m.get(e1);
                Integer bic = this.bicisE.get(index);
                Integer llev = f.getBicisPrimeraEstacion();
                bic = (bic == null) ? llev : bic + llev;
                this.bicisE.set(index, bic);
            }

            Estacion e2  = f.getSegundoDestino();
            if(e2 != null)
            {
                int i2 = m.get(e2);
                Integer bic2 = this.bicisE.get(i2);
                Integer llev2 = f.getBicisEstacionOrigen()-f.getBicisPrimeraEstacion();
                bic2 = (bic2 == null)? llev2 : bic2+llev2;
                this.bicisE.set(i2,bic2);
            }
        }
    }

    public Estado(Estado estado) {
        //TODO: falta implementar la copia de bicisE, en principio un loop por el Array es suficiente. Also, nunca añadimos nada a bicisE
        this.furgonetas = estado.furgonetas.clone();
        estaciones = estado.getEstaciones();
        this.bicisE = new ArrayList<>();
        for (Integer i : estado.getBicisE()) {
            this.bicisE.add(i);
        }
        for (Furgoneta f : this.furgonetas)
        {
            Estacion e1 = f.getPrimerDestino();
            if(e1 != null)
            {
                int index = m.get(e1);
                Integer bic = this.bicisE.get(index);
                Integer llev = f.getBicisPrimeraEstacion();
                bic = (bic == null) ? llev : bic + llev;
                this.bicisE.set(index, bic);
            }

            Estacion e2  = f.getSegundoDestino();
            if(e2 != null)
            {
                int i2 = m.get(e2);
                Integer bic2 = this.bicisE.get(i2);
                Integer llev2 = f.getBicisEstacionOrigen()-f.getBicisPrimeraEstacion();
                bic2 = (bic2 == null)? llev2 : bic2+llev2;
                this.bicisE.set(i2,bic2);
            }
        }

    }

    public static Map<Estacion, Integer> getM() {
        return m;
    }

    public static void setM(Map<Estacion, Integer> map) {
        m = map;
    }

    public Furgonetas getFurgonetas() {
        return furgonetas;
    }

    public void setFurgonetas(Furgonetas furgonetas) {
        this.furgonetas = furgonetas;
    }

    public Estaciones getEstaciones() {
        return estaciones;
    }

    public void setEstaciones(Estaciones est) {
        estaciones = est;
    }

    public ArrayList<Integer> getbicisE() {return bicisE;}

    public void setbicisE(ArrayList<Integer> bicisE) {this.bicisE = bicisE;}

    public boolean puedeSustituirEstacion(Estacion vieja, Estacion nueva, Furgoneta f) {
        return vieja != null && nueva != null &&!nueva.equals(vieja) && coincideEstacionDestino(vieja, f);
    }

    public void sustituirEstacion(Estacion vieja, Estacion nueva, Furgoneta f) {
        if (f.getPrimerDestino().equals(vieja)) f.setPrimerDestino(nueva);
        else f.setSegundoDestino(nueva);
    }

    public boolean puedeDejarBicis(Furgoneta f, int n) {
        return n > 0 && n <= f.getBicisEstacionOrigen() && noNulo(f.getPrimerDestino());
    }

    public void dejarBicis(Furgoneta f, int n) {
        f.setBicisPrimeraEstacion(n);
        int ip = m.get(f.getPrimerDestino());
        int bp = bicisE.get(ip) + n;
        bicisE.set(ip, bp);
        if (noNulo(f.getSegundoDestino())) {
            int is = m.get(f.getSegundoDestino());
            int bs = f.getBicisEstacionOrigen() - bp;
            bicisE.set(is, bs);
        }
    }

    public boolean puedeRecogerBicis(Furgoneta f, int n) {
        return n >= 0 && n <= 30 &&
                noNulo(f.getOrigen());
    }

    public void recogerBicis(Furgoneta f, int n) {
        f.setBicisEstacionOrigen(n);
        int i = m.get(f.getOrigen());
        bicisE.set(i, bicisE.get(i) - n);
    }

    public boolean puedeCambiarEstacionOrigen(Estacion e, Furgoneta f) {
        return true;
    }

    public void cambiarEstacionOrigen(Estacion e, Furgoneta f) {
        f.setOrigen(e);
    }

    public boolean puedeQuitarEstacion(Estacion e, Furgoneta f) {
        return existeEstacionFurgoneta(e, f);
    }

    public void quitarEstacion(Estacion e, Furgoneta f) {
        if (f.getPrimerDestino().equals(e)) {
            int i = m.get(f.getPrimerDestino());
            bicisE.set(i, bicisE.get(i) - f.getBicisPrimeraEstacion());
            if (f.getSegundoDestino() != null)
            {
                f.setPrimerDestino(f.getSegundoDestino());
                f.setSegundoDestino(null);
                f.setBicisPrimeraEstacion(f.getBicisEstacionOrigen());
            }
            /*else {
                f.setPrimerDestino(null);
                f.setBicisPrimeraEstacion(0);
                f.setOrigen(null);
                f.setBicisEstacionOrigen(0);
            }*/
        }
        else {
            int i = m.get(f.getSegundoDestino());
            bicisE.set(i, bicisE.get(i) - (f.getBicisEstacionOrigen() - f.getBicisPrimeraEstacion()));
            f.setSegundoDestino(null);
            f.setBicisPrimeraEstacion(f.getBicisEstacionOrigen());
        }
    }

    private boolean existeEstacionFurgoneta(Estacion e, Furgoneta f) {
        return (noNulo(f.getPrimerDestino()) && f.getPrimerDestino().equals(e)) ||
        (noNulo(f.getSegundoDestino()) && f.getSegundoDestino().equals(e));
    }

    private boolean coincideEstacionDestino(Estacion e, Furgoneta f) {
        return (noNulo(f.getPrimerDestino()) && f.getPrimerDestino().equals(e)) || (noNulo(f.getSegundoDestino()) && f.getSegundoDestino().equals(e));
    }

    private boolean noNulo(Estacion e) {return e != null;}

    public ArrayList<Integer> getBicisE() {
        return bicisE;
    }

    public void setBicisE(ArrayList<Integer> bicisE) {
        this.bicisE = bicisE;
    }
}
