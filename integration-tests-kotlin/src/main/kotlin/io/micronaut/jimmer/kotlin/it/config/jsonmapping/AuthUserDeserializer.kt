package io.micronaut.jimmer.kotlin.it.config.jsonmapping

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.MissingNode

class AuthUserDeserializer : JsonDeserializer<AuthUser>() {
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext?,
    ): AuthUser {
        val mapper = jsonParser.codec as ObjectMapper
        val jsonNode = mapper.readTree<JsonNode>(jsonParser)
        val authorities: String = readJsonNode(jsonNode, "authorities").asText()
        val passwordNode: JsonNode = readJsonNode(jsonNode, "password")
        val username: String = readJsonNode(jsonNode, "username").asText()
        val id: String = readJsonNode(jsonNode, "id").asText()
        val password = passwordNode.asText("")
        val enabled: Boolean = readJsonNode(jsonNode, "enabled").asBoolean()
        val accountNonExpired: Boolean = readJsonNode(jsonNode, "accountNonExpired").asBoolean()
        val credentialsNonExpired: Boolean = readJsonNode(jsonNode, "credentialsNonExpired").asBoolean()
        val accountNonLocked: Boolean = readJsonNode(jsonNode, "accountNonLocked").asBoolean()
        val tenantId: String = readJsonNode(jsonNode, "tenantId").asText()
        val deleteFlag: Boolean = readJsonNode(jsonNode, "deleteFlag").asBoolean()
        return AuthUser(
            id,
            username,
            password,
            authorities,
            accountNonExpired,
            accountNonLocked,
            credentialsNonExpired,
            enabled,
            tenantId,
            deleteFlag,
        )
    }

    private fun readJsonNode(
        jsonNode: JsonNode,
        field: String,
    ): JsonNode = if (jsonNode.has(field)) jsonNode[field] else MissingNode.getInstance()
}
