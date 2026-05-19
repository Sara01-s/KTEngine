#type vertex
#version 450 core

layout(location = 0) in vec2 a_position;
layout(location = 1) in vec2 a_uv;

uniform mat4 _MVP;

out vec2 uv;

void main() {
    uv = a_uv;
    gl_Position = _MVP * vec4(a_position, 0.0, 1.0);
}

#type fragment
#version 450 core

in vec2 uv;

uniform sampler2D _MainTex;
uniform vec4 _ColorTint;

out vec4 fragColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec3 msdf = texture(_MainTex, uv).rgb;
    float sd = median(msdf.r, msdf.g, msdf.b);
    float screenPxDistance = (sd - 0.5) / fwidth(sd);
    float alpha = clamp(screenPxDistance + 0.5, 0.0, 1.0);

    fragColor = vec4(_ColorTint.rgb, _ColorTint.a * alpha);
}