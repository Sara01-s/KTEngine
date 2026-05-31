#type vertex
#version 450 core

layout(location = 0) in vec3 a_position;

uniform mat4 _MVP;
uniform mat4 _ModelMatrix;

out vec3 v_worldPos;

void main() {
    vec4 worldPos = _ModelMatrix * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;
    gl_Position = _MVP * vec4(a_position, 1.0);
}

#type fragment
#version 450 core

layout(location = 0) out vec4 fragColor;

in vec3 v_worldPos;

// Parámetros de configuración
const float GRID_SIZE      = 1.0;
const float LINE_WIDTH     = 0.012;
const float GRID_OPACITY   = 0.19;
const float MAJOR_OPACITY  = 0.1;

const vec3  GRID_COLOR     = vec3(0.6, 0.6, 0.6);
const vec3  X_AXIS_COLOR   = vec3(0.8, 0.15, 0.15);
const vec3  Z_AXIS_COLOR   = vec3(0.15, 0.45, 0.8);

float getGridLine(float value, float width) {
    float fw = fwidth(value);
    float dist = abs(fract(value - 0.5) - 0.5);
    return 1.0 - smoothstep(width - fw, width + fw, dist);
}

void main() {
    float x = v_worldPos.x;
    float z = v_worldPos.z;

    float thinX = getGridLine(x / GRID_SIZE, LINE_WIDTH);
    float thinZ = getGridLine(z / GRID_SIZE, LINE_WIDTH);
    float thinGrid = max(thinX, thinZ);

    float majorX = getGridLine(x / (GRID_SIZE * 10.0), LINE_WIDTH * 1.5);
    float majorZ = getGridLine(z / (GRID_SIZE * 10.0), LINE_WIDTH * 1.5);
    float majorGrid = max(majorX, majorZ);

    float axisX = 1.0 - smoothstep(0.02, 0.04, abs(z));
    float axisZ = 1.0 - smoothstep(0.02, 0.04, abs(x));

    vec4 finalColor = vec4(0.0);
    finalColor = mix(finalColor, vec4(GRID_COLOR, GRID_OPACITY), thinGrid);
    finalColor = mix(finalColor, vec4(GRID_COLOR, MAJOR_OPACITY), majorGrid);

    finalColor = mix(finalColor, vec4(X_AXIS_COLOR, 1.0), axisX * 0.7);
    finalColor = mix(finalColor, vec4(Z_AXIS_COLOR, 1.0), axisZ * 0.7);

    float dist = length(v_worldPos.xz);
    float fog = 1.0 - smoothstep(50.0, 200.0, dist);
    finalColor.a *= fog;

    if (finalColor.a <= 0.01) {
        discard;
    }

    fragColor = finalColor;
}