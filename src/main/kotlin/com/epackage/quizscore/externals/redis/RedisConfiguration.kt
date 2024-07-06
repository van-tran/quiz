package com.epackage.quizscore.externals.redis

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfiguration {

    @Autowired
    lateinit var objectMapper: ObjectMapper


    @Value("\${spring.data.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.data.redis.port}")
    var redisPort: Int = 6379

    @Value("\${spring.data.redis.password}")
    lateinit var redisPass: String

    @Bean
    public fun jedisConnectionFactory()  : JedisConnectionFactory
    {
        val configuration = RedisStandaloneConfiguration(redisHost, redisPort)
            .apply { setPassword(redisPass) };

        val jedisClientConfiguration = JedisClientConfiguration.builder()
            .useSsl()
            .build();
        return JedisConnectionFactory(configuration, jedisClientConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer(getRedisObjectMapper()))
        template.connectionFactory = jedisConnectionFactory()
        return template
    }

    private fun getRedisObjectMapper(): ObjectMapper {
        val objectMapper = objectMapper.copy()
        var defaultTypeResolver: StdTypeResolverBuilder = ObjectMapper.DefaultTypeResolverBuilder(
            ObjectMapper.DefaultTyping.EVERYTHING,
            objectMapper.polymorphicTypeValidator
        )
        defaultTypeResolver = defaultTypeResolver.init(JsonTypeInfo.Id.CLASS, null)
        defaultTypeResolver = defaultTypeResolver.inclusion(JsonTypeInfo.As.PROPERTY)
        objectMapper.setDefaultTyping(defaultTypeResolver)
        return objectMapper
    }




}