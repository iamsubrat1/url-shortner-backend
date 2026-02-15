local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

redis.call("ZREMRANGEBYSCORE", key, 0, now - window)

local current = redis.call("ZCARD", key)

if current < capacity then
    redis.call("ZADD", key, now, now)
    redis.call("EXPIRE", key, window)
    return 1
else
    return 0
end
