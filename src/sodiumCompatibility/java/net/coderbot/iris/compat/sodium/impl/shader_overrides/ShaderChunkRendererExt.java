package net.coderbot.iris.compat.sodium.impl.shader_overrides;

import net.caffeinemc.gfx.api.shader.Program;

public interface ShaderChunkRendererExt {
	Program<IrisChunkShaderInterface> iris$getOverride();
}
