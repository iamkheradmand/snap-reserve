-- KEYS[1] = new temporary ZSET key (e.g. "available_slots:new")
-- KEYS[2] = final ZSET key (e.g. "available_slots")
-- KEYS[3] = hold prefix (e.g. "slot:hold:")
-- ARGV = slotId1, score1, slotId2, score2, ...

-- Step 1: start with a clean temporary ZSET
redis.call('DEL', KEYS[1])

-- Step 2: add slots, skipping held ones
for i = 1, #ARGV, 2 do
  local slotId = ARGV[i]
  local score = tonumber(ARGV[i+1])
  if redis.call('EXISTS', KEYS[3] .. slotId) == 0 then
    redis.call('ZADD', KEYS[1], score, slotId)
  end
end

-- Step 3: atomically rename temp → final (overwrite old)
redis.call('RENAME', KEYS[1], KEYS[2])

return "OK"
