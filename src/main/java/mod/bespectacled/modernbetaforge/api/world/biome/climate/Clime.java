package mod.bespectacled.modernbetaforge.api.world.biome.climate;

public class Clime {
    private final double temp;
    private final double rain;
    
    /**
     * Constructs a container to hold a climate temperature and rainfall value.
     * 
     * @param temp A temperature value.
     * @param rain A rainfall value.
     */
    public Clime(double temp, double rain) {
        this.temp = temp;
        this.rain = rain;
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