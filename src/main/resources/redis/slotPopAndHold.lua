-- KEYS[1] = available_slots (List)
-- KEYS[2] = hold key prefix
-- ARGV[1] = expire time

-- 1. Pop earliest slot (head of the list)
local slotId = redis.call('LPOP', KEYS[1])

-- 2. If found, set hold key
if slotId then
    -- Set a key like "KEYS[2]slotId" with value "reserved", expiring in ARGV[1] seconds
    redis.call('SET', KEYS[2] .. slotId, 'reserved', 'EX', ARGV[1])
    -- Return the slotId that was reserved
    return slotId
end
-- 3. If no slot found, return nil
return nil
