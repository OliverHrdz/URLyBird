package suncertify.application;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class representing a hotel room.
 * 
 * @author Oliver Hernandez
 */
public class Room implements Serializable {

    private static final long serialVersionUID = -2759754373439767554L;

    private long id;

    private String hotelName;

    private String city;

    private int maxOccupancy;

    private boolean smoking;

    private String smokingText = "N";

    private String rate;

    private String date;

    private String owner;

    /**
     * Construct a new <code>Room</code> object.
     */
    public Room() {
    }

    /**
     * Get the room ID.
     * 
     * @return the room ID number.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Set the room's ID.
     * 
     * @param newId
     *            the new room ID number.
     */
    public void setId(long newId) {
        this.id = newId;
    }

    /**
     * Get the hotel name.
     * 
     * @return a hotel name.
     */
    public String getHotelName() {
        return this.hotelName;
    }

    /**
     * Set the hotel name.
     * 
     * @param newName
     *            the new hotel name.
     */
    public void setHotelName(String newName) {
        this.hotelName = newName.trim();
    }

    /**
     * Get the city.
     * 
     * @return a city.
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Set the city.
     * 
     * @param newCity
     *            the new city.
     */
    public void setCity(String newCity) {
        this.city = newCity.trim();
    }

    /**
     * Get the maximum occupancy.
     * 
     * @return the maximum occupancy.
     */
    public int getMaxOccupancy() {
        return this.maxOccupancy;
    }

    /**
     * Set the maximum occupancy.
     * 
     * @param newMax
     *            the new maximum occupancy.
     */
    public void setMaxOccupancy(int newMax) {
        this.maxOccupancy = newMax;
    }

    /**
     * Set the maximum occupancy to the number in the specified
     * <code>String</code>.
     * 
     * @param newMax
     *            the new maximum occupancy.
     */
    public void setMaxOccupancy(String newMax) {
        try {
            this.maxOccupancy = Integer.parseInt(newMax.trim());
        } catch (NumberFormatException e) {
            this.maxOccupancy = 0;
        }
    }

    /**
     * Get whether or not the room is a smoking room.
     * 
     * @return <code>true</code> if the room is smoking, <code>false</code>
     *         otherwise.
     */
    public boolean getSmoking() {
        return this.smoking;
    }

    /**
     * Get whether or not the room is a smoking room as text.
     * 
     * @return "Y" if the room is smoking, "N" otherwise.
     */
    public String getSmokingText() {
        return this.smokingText;
    }

    /**
     * Set whether or not the room is smoking.
     * 
     * @param newSmoking
     *            <code>boolean</code> value if room is smoking.
     */
    public void setSmoking(boolean newSmoking) {
        this.smoking = newSmoking;

        if (newSmoking) {
            this.smokingText = "Y";
        } else {
            this.smokingText = "N";
        }
    }

    /**
     * Set whether or not the room is smoking. Valid values are either "Y" or
     * "N".
     * 
     * @param newSmoking
     *            <code>String</code> value if room is smoking.
     */
    public void setSmoking(String newSmoking) {
        if (newSmoking.trim().equalsIgnoreCase("Y")) {
            this.smokingText = "Y";
            this.smoking = true;
        } else if (newSmoking.trim().equalsIgnoreCase("N")) {
            this.smokingText = "N";
            this.smoking = false;
        } else {
            this.smokingText = "N";
            this.smoking = false;
        }
    }

    /**
     * Get the room rate.
     * 
     * @return the room rate.
     */
    public String getRate() {
        return this.rate;
    }

    /**
     * Set the room rate.
     * 
     * @param newRate
     *            the new rate.
     */
    public void setRate(String newRate) {
        this.rate = newRate.trim();
    }

    /**
     * The date the room is available.
     * 
     * @return date of availability.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Set the date the room is available.
     * 
     * @param newDate
     *            the new date
     */
    public void setDate(Date newDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        this.date = formatter.format(newDate);
    }

    /**
     * Set the date the room is available.
     * 
     * @param newDate
     *            the new date as a <code>String</code>.
     */
    public void setDate(String newDate) {
        this.date = newDate.trim();
    }

    /**
     * Get the customer who has the room booked.
     * 
     * @return the customer id.
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Set the customer who has the room booked.
     * 
     * @param newOwner
     *            the new customer id.
     */
    public void setOwner(String newOwner) {
        this.owner = newOwner.trim();
    }

}
