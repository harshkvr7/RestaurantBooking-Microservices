package com.rms.reservation_service.config;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rms.reservation_service.model.ReservationSlot;
import com.rms.reservation_service.repository.ReservationSlotRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ReservationSlotsSeeder implements CommandLineRunner{
    
    private final ReservationSlotRepository reservationSlotRepository;

    @Override
    public void run(String... args) {
        if (reservationSlotRepository.count() == 0) {
            System.out.println("--- Seeding 30 min Reservation Slots");
            List<ReservationSlot> slots = new ArrayList<>();

            LocalTime currentSlotTime = LocalTime.MIDNIGHT; 

            for (int i = 0; i < 48; i++) { 
                ReservationSlot slot = new ReservationSlot();
                slot.setStartTime(currentSlotTime);
                slot.setEndTime(currentSlotTime.plusMinutes(30));
                slots.add(slot);
                
                currentSlotTime = currentSlotTime.plusMinutes(30);
            }

            reservationSlotRepository.saveAll(slots);
            System.out.println("--- " + slots.size() + " reservation slots have been created. ---");
        }
    }
}
