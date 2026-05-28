#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_uv;

#ifdef HAS_TANGENTS
layout(location = 3) in vec3 a_tangent;
layout(location = 4) in vec3 a_bitangent;
#endif

uniform mat4 _MVP;
uniform mat4 _ModelMatrix;

out vec3 v_worldPos;
out vec3 v_normal;
out vec2 v_uv;

#ifdef HAS_TANGENTS
out mat3 v_tbn;
#endif

void main() {
    vec4 worldPosCalculated = _ModelMatrix * vec4(a_position, 1.0);
    v_worldPos = worldPosCalculated.xyz;
    v_uv = a_uv;

    mat3 normalMatrix = transpose(inverse(mat3(_ModelMatrix)));
    vec3 N = normalize(normalMatrix * a_normal);
    v_normal = N;

    #ifdef HAS_TANGENTS
    vec3 T = normalize(normalMatrix * a_tangent);
    vec3 B = normalize(normalMatrix * a_bitangent);
    v_tbn = mat3(T, B, N);
    #endif

    gl_Position = _MVP * vec4(a_position, 1.0);
}

#type fragment
#version 450 core

layout(location = 0) out vec4 fragColor;

in vec3 v_worldPos;
in vec3 v_normal;
in vec2 v_uv;

#ifdef HAS_TANGENTS
in mat3 v_tbn;
#endif

uniform vec3 _CameraPosition;
uniform vec3 _LightColor;
uniform vec4 _Color;

const vec3 _LightDirection = vec3(0.5, -0.8, 0.3); // TODO: make uniform.
const float _LightIntensity = 1.0; // TODO: make uniform.

#ifdef HAS_DIFFUSE
uniform sampler2D _DiffuseTexture;
#endif
#ifdef HAS_SPECULAR
uniform sampler2D _SpecularTexture;
#endif
#ifdef HAS_TANGENTS
uniform sampler2D _NormalTexture;
#endif
#ifdef HAS_OPACITY
uniform sampler2D _OpacityTexture;
#endif

const float ambientIntensity = 0.45;
const float shininess = 32.0;
const float specularIntensity = 0.4;

void main() {
    vec3 albedo = _Color.rgb;
    float alpha = _Color.a;

    #ifdef HAS_DIFFUSE
        vec4 diffuseSample = texture(_DiffuseTexture, v_uv);
        albedo *= diffuseSample.rgb;

        #ifndef HAS_OPACITY
            alpha *= diffuseSample.a;
        #endif
    #endif

    #ifdef HAS_OPACITY
    alpha *= texture(_OpacityTexture, v_uv).r;
    #endif

    vec3 normal = normalize(v_normal);
    #ifdef HAS_TANGENTS
    vec3 tangentNormal = texture(_NormalTexture, v_uv).xyz * 2.0 - 1.0;
    normal = normalize(v_tbn * tangentNormal);
    #endif

    vec3 lightDir = normalize(-_LightDirection);
    float NdotL = max(dot(normal, lightDir), 0.0);

    vec3 viewDir = normalize(_CameraPosition - v_worldPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    float currentSpecularIntensity = specularIntensity;
    #ifdef HAS_SPECULAR
    currentSpecularIntensity *= texture(_SpecularTexture, v_uv).r;
    #endif

    vec3 ambient = albedo * ambientIntensity;
    vec3 diffuse = albedo * _LightColor * NdotL * _LightIntensity;
    vec3 specular = _LightColor * spec * currentSpecularIntensity * _LightIntensity;

    fragColor = vec4(ambient + diffuse + specular, alpha);
}