package mod.bespectacled.modernbetaforge.api.world.biome.climate;

public class Clime {
    private final float temp;
    private final float rain;
    
    /**
     * Constructs a container to hold a climate temperature and rainfall value.
     * 
     * @param temp A temperature value.
     * @param rain A rainfall value.
     */
    public Clime(double temp, double rain) {
        this.temp = (float)temp;
        this.rain = (float)rain;
    }
    
    /**
     * @return The clime's temperature value.
     */
    public double temp() {
        return this.temp;
    }
    
    /**
     * @return The clime's rainfall value.
     */
    public double rain() {
        return this.rain;
    }
}