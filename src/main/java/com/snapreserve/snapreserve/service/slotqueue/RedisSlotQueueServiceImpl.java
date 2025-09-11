package com.snapreserve.snapreserve.service.slotqueue;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSlotQueueServiceImpl implements SlotQueueService {

    private static final String AVAILABLE_SLOTS_KEY = "available_slots";
    private static final String HOLD_KEY_PREFIX = "slot:hold:";

    private final RedisTemplate<String, String> redisTemplate;

    private final DefaultRedisScript<String> popAndHoldScript = new DefaultRedisScript<>();

    private final DefaultRedisScript<String> replaceSlotsScript = new DefaultRedisScript<>();

    @PostConstruct
    public void loadScript() {
        popAndHoldScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/slotPopAndHold.lua")));
        popAndHoldScript.setResultType(String.class);

        replaceSlotsScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/refreshSlots.lua")));
        replaceSlotsScript.setResultType(String.class);
    }

    @Override
    public void refreshAvailableSlots(List<AvailableSlots> slots) {
        log.info("going to update redis set : " + slots.size());

        List<String> args = new ArrayList<>(slots.size() * 2);
        for (AvailableSlots s : slots) {
            args.add(s.getId() + "|" + s.getStart_time().toString() + "|" + s.getEnd_time().toString());
        }

        String updateResult = redisTemplate.execute(
                replaceSlotsScript,
                Arrays.asList(AVAILABLE_SLOTS_KEY, HOLD_KEY_PREFIX),
                args.toArray()
        );

        log.info("Redis set update result was : {}", updateResult);
    }

    @Override
    public String popAndHoldSlot() {
        return redisTemplate.execute(
                popAndHoldScript,
                Arrays.asList(AVAILABLE_SLOTS_KEY, HOLD_KEY_PREFIX),
                String.valueOf(300));
    }
}
