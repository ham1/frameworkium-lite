package com.frameworkium.integration.restfulbooker.api.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.frameworkium.integration.restfulbooker.api.dto.booking.Booking;
import com.frameworkium.integration.restfulbooker.api.dto.booking.BookingID;
import com.frameworkium.integration.restfulbooker.api.dto.booking.search.SearchParamsMapper;
import com.frameworkium.integration.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.restfulbooker.api.service.ping.PingService;
import com.frameworkium.lite.api.tests.BaseAPITest;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

// app resets every 10m, so could happen in the middle of this test
@Disabled("Service is down")
public class SearchBookerTest extends BaseAPITest {

    @BeforeAll
    public static void ensure_site_is_up_by_using_ping_service() {
        assertThat(new PingService().ping()).isEqualTo("Created");
    }

    @Test
    public void search_for_existing_records_by_name() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(1);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(SearchParamsMapper.namesOfBooking(booking));

        assertThat(bookingIDs).contains(existingID);
    }

    @Test
    public void search_for_existing_records_by_date() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(3);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(SearchParamsMapper.datesOfBooking(booking));

        Assumptions.assumeTrue(
                bookingIDs.contains(existingID), "Known bug in service, dates not inclusive");
        assertThat(bookingIDs).contains(existingID);
    }
}
