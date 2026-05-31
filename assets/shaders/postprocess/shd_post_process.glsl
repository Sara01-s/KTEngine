#type vertex
#version 430 core
layout (location = 0) in vec3 a_position;
layout (location = 2) in vec2 a_uv;

out vec2 v_uv;

void main() {
    v_uv = a_uv;
    gl_Position = vec4(a_position.xy, 0.0, 1.0);
}

#type fragment
#version 430 core

#include "include/shd_post_processing_effects.glsl"

out vec4 fragColor;
in vec2 v_uv;

uniform sampler2D _ScreenTexture;
uniform sampler2D _BloomTexture;

const float _BloomIntensity = 0.5;
const float _VignetteIntensity = 1.4;
const float _Gamma = 1.2;
const float _GrainStrength = 0.01;

void main() {
    vec3 sceneColor = texture(_ScreenTexture, v_uv).rgb;
    vec3 bloomColor = texture(_BloomTexture, v_uv).rgb;

    vec3 color = sceneColor + (bloomColor * _BloomIntensity);

    float vignette = applyVignette(v_uv, _VignetteIntensity);
    color *= vignette;

    color = applyFilmGrain(color, v_uv, _GrainStrength);
    color = applyGammaCorrection(color, _Gamma);

    fragColor = vec4(sceneColor, 1.0);
}