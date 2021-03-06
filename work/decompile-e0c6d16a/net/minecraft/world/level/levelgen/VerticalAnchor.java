package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionManager;

public abstract class VerticalAnchor {

    public static final Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(VerticalAnchor.b.CODEC, ExtraCodecs.xor(VerticalAnchor.a.CODEC, VerticalAnchor.c.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
    private static final VerticalAnchor BOTTOM = aboveBottom(0);
    private static final VerticalAnchor TOP = belowTop(0);
    private final int value;

    protected VerticalAnchor(int i) {
        this.value = i;
    }

    public static VerticalAnchor absolute(int i) {
        return new VerticalAnchor.b(i);
    }

    public static VerticalAnchor aboveBottom(int i) {
        return new VerticalAnchor.a(i);
    }

    public static VerticalAnchor belowTop(int i) {
        return new VerticalAnchor.c(i);
    }

    public static VerticalAnchor bottom() {
        return VerticalAnchor.BOTTOM;
    }

    public static VerticalAnchor top() {
        return VerticalAnchor.TOP;
    }

    private static VerticalAnchor merge(Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> either) {
        return (VerticalAnchor) either.map(Function.identity(), (either1) -> {
            return (VerticalAnchor) either1.map(Function.identity(), Function.identity());
        });
    }

    private static Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> split(VerticalAnchor verticalanchor) {
        return verticalanchor instanceof VerticalAnchor.b ? Either.left((VerticalAnchor.b) verticalanchor) : Either.right(verticalanchor instanceof VerticalAnchor.a ? Either.left((VerticalAnchor.a) verticalanchor) : Either.right((VerticalAnchor.c) verticalanchor));
    }

    protected int value() {
        return this.value;
    }

    public abstract int resolveY(WorldGenerationContext worldgenerationcontext);

    private static final class b extends VerticalAnchor {

        public static final Codec<VerticalAnchor.b> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.b::new, VerticalAnchor::value).codec();

        protected b(int i) {
            super(i);
        }

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return this.value();
        }

        public String toString() {
            return this.value() + " absolute";
        }
    }

    private static final class a extends VerticalAnchor {

        public static final Codec<VerticalAnchor.a> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.a::new, VerticalAnchor::value).codec();

        protected a(int i) {
            super(i);
        }

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.getMinGenY() + this.value();
        }

        public String toString() {
            return this.value() + " above bottom";
        }
    }

    private static final class c extends VerticalAnchor {

        public static final Codec<VerticalAnchor.c> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.c::new, VerticalAnchor::value).codec();

        protected c(int i) {
            super(i);
        }

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.getGenDepth() - 1 + worldgenerationcontext.getMinGenY() - this.value();
        }

        public String toString() {
            return this.value() + " below top";
        }
    }
}
