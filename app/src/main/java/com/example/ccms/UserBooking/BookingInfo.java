package com.example.ccms.UserBooking;

import com.google.firebase.firestore.Exclude;

public class BookingInfo {
private String email;private String name;
private String mobile;private String additional_mobile;
private String address;private String booked_date;
private String booking_Id; private String shift;
private String event_type;private String date_of_booking_request;
private String booking_time;private String number_of_guests;
private String hall_rental_price;
private String payment_status;private String booking_status;
private String booked_by;
private String key;

public BookingInfo(){}

public BookingInfo(String email,String name,String mobile,String additional_mobile,String address,String booked_date,
                   String booking_Id,String shift,String event_type,String date_of_booking_request,String booking_time,
                   String number_of_guests,String hall_rental_price,String payment_status,String booking_status,String booked_by) {

    this.email=email;this.name=name;this.mobile=mobile;this.additional_mobile=additional_mobile;this.address=address;
    this.booked_date=booked_date;this.booking_Id=booking_Id;this.shift=shift;this.event_type=event_type;
    this.date_of_booking_request=date_of_booking_request;this.booking_time=booking_time;this.number_of_guests=number_of_guests;
    this.hall_rental_price=hall_rental_price;this.payment_status=payment_status;this.booking_status=booking_status;this.booked_by=booked_by;
}

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAdditional_mobile() {
        return additional_mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getBooked_date() {
        return booked_date;
    }

    public String getBooking_Id() {
        return booking_Id;
    }

    public String getShift() {
        return shift;
    }

    public String getEvent_type() {
        return event_type;
    }

    public String getDate_of_booking_request() {
        return date_of_booking_request;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public String getNumber_of_guests() {
        return number_of_guests;
    }

    public String getHall_rental_price() {
        return hall_rental_price;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public String getBooked_by() {
        return booked_by;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
