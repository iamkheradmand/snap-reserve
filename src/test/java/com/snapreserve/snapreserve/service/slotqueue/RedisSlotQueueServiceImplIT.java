package com.snapreserve.snapreserve.service.slotqueue;

import com.snapreserve.snapreserve.BaseIT;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class RedisSlotQueueServiceImplIT extends BaseIT {

    private static final String AVAILABLE_SLOTS_KEY = "available_slots";
    private static final String HOLD_KEY_PREFIX = "slot:hold:";

    @Autowired
    private SlotQueueService slotQueueService;

    @Autowired
    private AvailableSlotsRepository repository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    @DisplayName("""
            Given available slots in database
            When refreshAvailableSlots is called
            Then slots should be loaded into Redis list excluding held slots
            """)
    void refreshAvailableSlots_WithValidSlots_ShouldLoadIntoRedis() {
        List<AvailableSlots> slots = Arrays.asList(
                createAvailableSlot(1L, "09:00", "10:00"),
                createAvailableSlot(2L, "10:00", "11:00"),
                createAvailableSlot(3L, "11:00", "12:00")
        );
        long expectedListSize = 3L;

        slotQueueService.refreshAvailableSlots(slots);

        Long listSize = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).size();
        assertThat(listSize).isEqualTo(expectedListSize);
    }

    @Test
    @DisplayName("""
            Given some slots are held
            When refreshAvailableSlots is called
            Then held slots should be excluded from available list
            """)
    void refreshAvailableSlots_WithHeldSlots_ShouldExcludeHeldSlots() {
        String heldSlotId = "2|2025-09-11T10:00|2025-09-11T11:00";
        redisTemplate.opsForValue().set(HOLD_KEY_PREFIX + heldSlotId, "reserved", Duration.ofSeconds(300));

        List<AvailableSlots> slots = Arrays.asList(
                createAvailableSlot(1L, "09:00", "10:00"),
                createAvailableSlot(2L, "10:00", "11:00"), // This should be excluded
                createAvailableSlot(3L, "11:00", "12:00")
        );

        long expectedListSize = 2L;

        slotQueueService.refreshAvailableSlots(slots);

        Long listSize = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).size();
        assertThat(listSize).isEqualTo(expectedListSize);

        List<String> slotsInRedis = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).range(0, -1);
        assertThat(slotsInRedis).doesNotContain(heldSlotId);

    }

    @Test
    @DisplayName("""
            Given slots exist in Redis
            When popAndHoldSlot is called
            Then should return the earliest slot and set hold key
            """)
    void popAndHoldSlot_WithAvailableSlots_ShouldReturnAndHoldSlot() {
        String expectedFirstSlot = "1|2025-09-11T09:00|2025-09-11T10:00";
        String expectedHoldKey = HOLD_KEY_PREFIX + expectedFirstSlot;
        String expectedHoldValue = "reserved";
        long expectedListSizeAfterPop = 2L;

        List<String> initialSlots = Arrays.asList(
                "1|2025-09-11T09:00|2025-09-11T10:00",
                "2|2025-09-11T10:00|2025-09-11T11:00",
                "3|2025-09-11T11:00|2025-09-11T12:00"
        );
        redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).rightPushAll(initialSlots.toArray(new String[0]));

        String result = slotQueueService.popAndHoldSlot();

        assertThat(result).isEqualTo(expectedFirstSlot);

        Long listSize = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).size();
        assertThat(listSize).isEqualTo(expectedListSizeAfterPop);

        Boolean holdKeyExists = redisTemplate.hasKey(expectedHoldKey);
        assertThat(holdKeyExists).isTrue();

        String holdValue = redisTemplate.opsForValue().get(expectedHoldKey);
        assertThat(holdValue).isEqualTo(expectedHoldValue);
    }

    @Test
    @DisplayName("""
            Given multiple calls to popAndHoldSlot
            When slots are available
            Then should return slots in FIFO order
            """)
    void popAndHoldSlot_MultipleCalls_ShouldReturnFIFO() {
        String expectedFirstSlot = "1|2025-09-11T09:00|2025-09-11T10:00";
        String expectedSecondSlot = "2|2025-09-11T10:00|2025-09-11T11:00";
        String expectedThirdSlot = "3|2025-09-11T11:00|2025-09-11T12:00";

        List<String> initialSlots = Arrays.asList(
                expectedFirstSlot,
                expectedSecondSlot,
                expectedThirdSlot
        );
        redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).rightPushAll(initialSlots.toArray(new String[0]));

        String firstSlot = slotQueueService.popAndHoldSlot();
        assertThat(firstSlot).isEqualTo(expectedFirstSlot);

        String secondSlot = slotQueueService.popAndHoldSlot();
        assertThat(secondSlot).isEqualTo(expectedSecondSlot);

        String thirdSlot = slotQueueService.popAndHoldSlot();
        assertThat(thirdSlot).isEqualTo(expectedThirdSlot);

        String fourthSlot = slotQueueService.popAndHoldSlot();
        assertThat(fourthSlot).isNull();
    }

    @Test
    @DisplayName("""
            Given no slots available
            When popAndHoldSlot is called
            Then should return null
            """)
    void popAndHoldSlot_WithNoSlots_ShouldReturnNull() {
        String result = slotQueueService.popAndHoldSlot();

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("""
            Given refreshAvailableSlots is called multiple times
            When slots data changes
            Then Redis should be updated correctly
            """)
    void refreshAvailableSlots_MultipleCalls_ShouldUpdateRedis() {
        long expectedListSize1 = 2L;
        long expectedListSize2 = 3L;
        List<AvailableSlots> slots1 = Arrays.asList(
                createAvailableSlot(1L, "2025-09-11T09:00", "2025-09-11T10:00"),
                createAvailableSlot(2L, "2025-09-11T10:00", "2025-09-11T11:00")
        );
        slotQueueService.refreshAvailableSlots(slots1);

        Long listSize1 = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).size();
        assertThat(listSize1).isEqualTo(expectedListSize1);

        List<AvailableSlots> slots2 = Arrays.asList(
                createAvailableSlot(3L, "11:00", "12:00"),
                createAvailableSlot(4L, "12:00", "13:00"),
                createAvailableSlot(5L, "13:00", "14:00")
        );
        slotQueueService.refreshAvailableSlots(slots2);

        Long listSize2 = redisTemplate.boundListOps(AVAILABLE_SLOTS_KEY).size();
        assertThat(listSize2).isEqualTo(expectedListSize2);

    }

    private AvailableSlots createAvailableSlot(Long id, String startTime, String endTime) {
        AvailableSlots slot = new AvailableSlots();
        slot.setId(id);
        slot.setStart_time(LocalTime.parse(startTime).atDate(LocalDate.now()));
        slot.setEnd_time(LocalTime.parse(endTime).atDate(LocalDate.now()));
        return slot;
    }
}
