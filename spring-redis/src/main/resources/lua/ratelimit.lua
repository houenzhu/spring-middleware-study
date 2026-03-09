local key = KEYS[1]
local now = tonumber(ARGV[1])
local windowStart = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local windowSizeMs = tonumber(ARGV[4])
local member= ARGV[5]

-- 删除窗口开始前的数据
redis.call('ZREMRANGEBYSCORE', key, 0, windowStart)
---查看当前窗口请求是否超过阈值
local current = redis.call('zcard', key)
if current >= limit then
     return 0
end

-- 添加当前请求
redis.call('zadd', key, now, member)
redis.call('expire', key, math.ceil(windowSizeMs / 1000) + 1)
return 1
