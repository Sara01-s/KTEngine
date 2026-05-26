#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_uv;

uniform mat4 _MVP;
uniform mat4 _ModelMatrix;

out vec2 worldXZ;

void main() {
    vec4 worldPos = _ModelMatrix * vec4(a_position, 1.0);

    worldXZ = worldPos.xz;

    gl_Position = _MVP * vec4(a_position, 1.0);
}

#type fragment
#version 450 core

out vec4 fragColor;

in vec2 worldXZ;

const float GRID_SIZE     = 1.0;
const float LINE_WIDTH    = 0.03;
const float AXIS_WIDTH    = 0.06;

const float GRID_OPACITY  = 0.15;
const float MAJOR_OPACITY = 0.35;

const vec3  GRID_COLOR    = vec3(0.6, 0.6, 0.6);
const vec3  X_AXIS_COLOR  = vec3(0.8, 0.15, 0.15);
const vec3  Z_AXIS_COLOR  = vec3(0.15, 0.45, 0.8);

// Fog
const float FOG_START = 30.0;
const float FOG_END   = 60.0;

float gridLine(float value, float period, float halfWidth) {

    float v = mod(value, period);

    if (v > period * 0.5) {
        v = period - v;
    }

    return 1.0 - smoothstep(halfWidth * 0.8, halfWidth, v);
}

void main() {

    float x = worldXZ.x;
    float z = worldXZ.y;

    // Thin grid
    float thin = max(
        gridLine(x, GRID_SIZE, LINE_WIDTH),
        gridLine(z, GRID_SIZE, LINE_WIDTH)
    );

    // Major grid (every 10 tiles)
    float major = max(
        gridLine(x, GRID_SIZE * 10.0, LINE_WIDTH * 1.5),
        gridLine(z, GRID_SIZE * 10.0, LINE_WIDTH * 1.5)
    );

    // Axes
    float axisX = 1.0 - smoothstep(
        AXIS_WIDTH * 0.8,
        AXIS_WIDTH,
        abs(z)
    );

    float axisZ = 1.0 - smoothstep(
        AXIS_WIDTH * 0.8,
        AXIS_WIDTH,
        abs(x)
    );

    vec4 color = vec4(0.0);

    color = mix(color, vec4(GRID_COLOR, GRID_OPACITY), thin);
    color = mix(color, vec4(GRID_COLOR, MAJOR_OPACITY), major);
    color = mix(color, vec4(X_AXIS_COLOR, 1.0), axisX);
    color = mix(color, vec4(Z_AXIS_COLOR, 1.0), axisZ);

    float dist = length(worldXZ);

    float fog = 1.0 - smoothstep(
        FOG_START,
        FOG_END,
        dist
    );

    color.a *= fog;

    if (color.a < 0.01) {
        discard;
    }

    fragColor = color;
}