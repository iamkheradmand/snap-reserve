-- KEYS[1] = final_list (e.g., "available_slots")
-- KEYS[2] = hold_key_prefix (e.g., "slot:hold:")
-- ARGV = [slotId1, slotId2, ...]

-- Step 1: Clear the list
redis.call('DEL', KEYS[1])

-- Step 2: Rebuild it, Push all unheld slots int the list
for i = 1, #ARGV do
  local slotId = ARGV[i]
  if redis.call('EXISTS', KEYS[2] .. slotId) == 0 then
    redis.call('RPUSH', KEYS[1], slotId)
  end
end

return "OK"