local stock = redis.call('get', KEYS[1])
local exists = redis.call('sismember', KEYS[2], ARGV[1])
if tonumber(stock) <= 0 then
    return -1 -- 库存不足
end
if exists == 1 then
    return -2 -- 用户已经抢购过了
end

redis.call('decr', KEYS[1]) -- 库存扣减
redis.call('sadd', KEYS[2], ARGV[1]) -- 将用户添加到已抢购的集合中
return 1;
