package net.coderbot.iris.compat.sodium.mixin.block_id;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.caffeinemc.sodium.render.chunk.compile.tasks.TerrainBuildBuffers;
import net.caffeinemc.sodium.render.chunk.passes.ChunkRenderPassManager;
import net.caffeinemc.sodium.render.terrain.format.TerrainVertexType;
import net.caffeinemc.sodium.render.vertex.VertexSink;
import net.caffeinemc.sodium.render.vertex.buffer.VertexBufferView;
import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.coderbot.iris.compat.sodium.impl.block_id.TerrainBuildBuffersExt;
import net.coderbot.iris.compat.sodium.impl.block_id.MaterialIdAwareVertexWriter;
import net.coderbot.iris.block_rendering.MaterialIdHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Associates the material ID holder with the terrain build buffers, allowing {@link MixinTerrainBuildTask} to pass
 * data to {@link MaterialIdAwareVertexWriter}.
 */
@Mixin(TerrainBuildBuffers.class)
public class MixinTerrainBuildBuffers implements TerrainBuildBuffersExt {
    @Unique
    private MaterialIdHolder idHolder;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void iris$onConstruct(TerrainVertexType vertexType, ChunkRenderPassManager renderPassManager, CallbackInfo ci) {
        Object2IntMap<BlockState> blockStateIds = BlockRenderingSettings.INSTANCE.getBlockStateIds();

        if (blockStateIds != null) {
            this.idHolder = new MaterialIdHolder(blockStateIds);
        } else {
            this.idHolder = new MaterialIdHolder();
        }
    }

    @Redirect(method = "init", remap = false, at = @At(value = "INVOKE",
            target = "net/caffeinemc/sodium/render/terrain/format/TerrainVertexType.createBufferWriter (" +
						"Lnet/caffeinemc/sodium/render/vertex/buffer/VertexBufferView;" +
					")Lnet/caffeinemc/sodium/render/vertex/VertexSink;", remap = false))
    private VertexSink iris$redirectWriterCreation(TerrainVertexType vertexType, VertexBufferView buffer) {
        VertexSink sink = vertexType.createBufferWriter(buffer);

        if (sink instanceof MaterialIdAwareVertexWriter) {
            ((MaterialIdAwareVertexWriter) sink).iris$setIdHolder(idHolder);
        }

        return sink;
    }

    @Override
    public void iris$setMaterialId(BlockState state, short renderType) {
        this.idHolder.set(state, renderType);
    }

    @Override
    public void iris$resetMaterialId() {
        this.idHolder.reset();
    }
}
