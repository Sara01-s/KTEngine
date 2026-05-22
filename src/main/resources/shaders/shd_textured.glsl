#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_uv;

uniform mat4 _MVP;
const vec3 _RotationAngles = vec3(45.0, 45.0, 45.0);

out vec2 uv;

vec3 rotateAllAxes(vec3 position, vec3 angles) {
    vec3 rad = radians(angles);

    vec3 c = cos(rad);
    vec3 s = sin(rad);

    // Matriz de rotación en X
    mat3 rotX = mat3(
        1.0, 0.0, 0.0,
        0.0, c.x, s.x,
        0.0, -s.x, c.x
    );

    // Matriz de rotación en Y
    mat3 rotY = mat3(
        c.y, 0.0, -s.y,
        0.0, 1.0, 0.0,
        s.y, 0.0, c.y
    );

    // Matriz de rotación en Z
    mat3 rotZ = mat3(
        c.z, s.z, 0.0,
        -s.z, c.z, 0.0,
        0.0, 0.0, 1.0
    );

    // Combinamos las rotaciones multiplicándolas.
    // El orden de multiplicación importa (Z * Y * X aplicará primero X, luego Y, luego Z)
    mat3 combinedRotation = rotZ * rotY * rotX;

    return combinedRotation * position;
}

void main() {
    uv = a_uv;

    vec3 r = rotateAllAxes(a_position, _RotationAngles);

    gl_Position = _MVP * vec4(a_position, 1.0);
}

#type fragment
#version 450 core

out vec4 fragColor;

uniform sampler2D _MainTex;
uniform vec4 _ColorTint;

in vec2 uv;

void main() {
    fragColor = texture(_MainTex, uv) * _ColorTint;
}