package com.snapreserve.snapreserve.service.reservationpersistance;

import com.snapreserve.snapreserve.repository.reservation.Reservation;
import com.snapreserve.snapreserve.repository.reservation.ReservationRepository;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import com.snapreserve.snapreserve.service.reservationpersistance.model.PersistReserveModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationPersistenceServiceImpl implements ReservationPersistenceService {

	private final AvailableSlotsRepository slotsRepository;

	private final UserRepository userRepository;

	private final ReservationRepository reservationRepository;

	@Transactional
	@Override
	public void persistReserve(PersistReserveModel model) {
		Users user = userRepository.findByUsername(model.userName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		AvailableSlots slot = slotsRepository.findByIdForUpdate(model.slotId())
				.orElseThrow(() -> new RuntimeException("Slot not found"));

		if (slot.is_reserved()) {
			throw new RuntimeException("Slot already reserved");
		}

		slot.set_reserved(true);
		slotsRepository.save(slot);

		Reservation res = new Reservation();
		res.setSlot(slot);
		res.setUser(user);
		reservationRepository.save(res);

		log.info("[ReservationPersistenceServiceImpl] - Persistence finished successfully for {}", model);
	}

}
