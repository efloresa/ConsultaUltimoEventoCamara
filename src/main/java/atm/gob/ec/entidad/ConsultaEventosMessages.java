package atm.gob.ec.entidad;

public class ConsultaEventosMessages {

    private String LOCATION_ID;
    private String CAMARA_SERIAL;
    private String DIRECCION;
    private String GEOREFERENCIA;
    private Long CANTIDAD_EVENTOS;
    
    public ConsultaEventosMessages(){
        
    }

    public String getLOCATION_ID() {
        return LOCATION_ID;
    }

    public void setLOCATION_ID(String LOCATION_ID) {
        this.LOCATION_ID = LOCATION_ID;
    }

    public String getCAMARA_SERIAL() {
        return CAMARA_SERIAL;
    }

    public void setCAMARA_SERIAL(String CAMARA_SERIAL) {
        this.CAMARA_SERIAL = CAMARA_SERIAL;
    }

    public String getDIRECCION() {
        return DIRECCION;
    }

    public void setDIRECCION(String DIRECCION) {
        this.DIRECCION = DIRECCION;
    }

    public String getGEOREFERENCIA() {
        return GEOREFERENCIA;
    }

    public void setGEOREFERENCIA(String GEOREFERENCIA) {
        this.GEOREFERENCIA = GEOREFERENCIA;
    }

    public Long getCANTIDAD_EVENTOS() {
        return CANTIDAD_EVENTOS;
    }

    public void setCANTIDAD_EVENTOS(Long CANTIDAD_EVENTOS) {
        this.CANTIDAD_EVENTOS = CANTIDAD_EVENTOS;
    }

    @Override
    public String toString() {
        return "ConsultaEventosMessages{" + "LOCATION_ID=" + LOCATION_ID + ", CAMARA_SERIAL=" + CAMARA_SERIAL + ", DIRECCION=" + DIRECCION + ", GEOREFERENCIA=" + GEOREFERENCIA + ", CANTIDAD_EVENTOS=" + CANTIDAD_EVENTOS + '}';
    }
    
        
}
