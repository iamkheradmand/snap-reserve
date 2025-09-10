-- 1. Get the SMALLEST SCORED member (earliest time) from the ZSET
local slotId = redis.call('ZRANGE', KEYS[1], 0, 0)[1]
-- 2. If found...
if slotId then
    -- Remove it from the ZSET
    redis.call('ZREM', KEYS[1], slotId)
    -- Set a key like "KEYS[2]slotId" with value "reserved", expiring in ARGV[1] seconds
    redis.call('SET', KEYS[2] .. slotId, 'reserved', 'EX', ARGV[1])
    -- Return the slotId that was reserved
    return slotId
end
-- 3. If no slot found, return nil
return nil
