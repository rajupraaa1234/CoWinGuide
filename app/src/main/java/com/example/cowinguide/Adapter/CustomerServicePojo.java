package com.example.cowinguide.Adapter;

public class CustomerServicePojo {
    /**
     * Calltype
     * "OUTGOING"
     * ServiceType
     * "medicine"
     * date
     * "Thu May 06 15:29:18 GMT+05:30 2021"
     * location
     * "8, Kautilya Ln, Vikash Nagar, Balapur, Patna, Bihar 800010, India"
     * name
     * "RajuKumar"
     * number
     * "+916200332633"
     **/

    String Calltype;
    String ServiceType;
    String date;
    String location;
    String name;
    String number;

    public String getCalltype() {
        return Calltype;
    }

    public void setCalltype(String calltype) {
        Calltype = calltype;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
