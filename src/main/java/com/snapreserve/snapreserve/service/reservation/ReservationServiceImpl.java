package com.snapreserve.snapreserve.service.reservation;

import com.snapreserve.snapreserve.exception.ReservationDefaultException;
import com.snapreserve.snapreserve.exception.ReservationSlotException;
import com.snapreserve.snapreserve.repository.reservation.Reservation;
import com.snapreserve.snapreserve.repository.reservation.ReservationRepository;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import com.snapreserve.snapreserve.service.reservation.model.DeleteReserveModel;
import com.snapreserve.snapreserve.service.reservation.model.PersistReserveModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final AvailableSlotsRepository slotsRepository;

    private final UserRepository userRepository;

    private final ReservationRepository reservationRepository;

    @Transactional
    @Override
    public void persistReservation(PersistReserveModel model) {
        Users user = userRepository.findByUsername(model.userName())
                .orElseThrow(() -> new ReservationDefaultException("User not found"));

        AvailableSlots slot = slotsRepository.findByIdForUpdate(model.slotId())
                .orElseThrow(() -> new ReservationSlotException("Slot not found:" + model.slotId()));

        if (slot.is_reserved()) {
            throw new ReservationSlotException("Slot already reserved");
        }

        slot.set_reserved(true);
        slotsRepository.save(slot);

        Reservation res = new Reservation();
        res.setReservationId(model.reservationId());
        res.setSlot(slot);
        res.setUser(user);
        reservationRepository.save(res);

        log.info("Persistence finished for {}", model);
    }

    @Transactional
    @Override
    public void deleteReservation(DeleteReserveModel model) {
        reservationRepository.deleteByReservationId(model.reservationId());
    }

}
