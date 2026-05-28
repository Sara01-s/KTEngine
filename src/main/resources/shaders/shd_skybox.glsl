#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;

out vec3 v_texCoords;

uniform mat4 _ViewMatrix;
uniform mat4 _ProjectionMatrix;

void main() {
    v_texCoords = a_position;
    mat4 staticView = mat4(mat3(_ViewMatrix));
    vec4 pos = _ProjectionMatrix * staticView * vec4(a_position, 1.0);

    gl_Position = pos.xyww;
}

#type fragment
#version 450 core

out vec4 fragColor;
in vec3 v_texCoords;

uniform samplerCube _SkyboxTex;

void main() {
    vec3 texCoords = v_texCoords;
    texCoords.y *= -1;

    fragColor = texture(_SkyboxTex, texCoords);
}