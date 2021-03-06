package net.minecraft.world.level.levelgen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import net.minecraft.SystemUtils;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.feature.NoiseEffect;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructureJigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;

public class Beardifier implements NoiseChunk.c {

    public static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = 24;
    private static final float[] BEARD_KERNEL = (float[]) SystemUtils.make(new float[13824], (afloat) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    afloat[i * 24 * 24 + j * 24 + k] = (float) computeBeardContribution(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private final ObjectList<StructurePiece> rigids;
    private final ObjectList<WorldGenFeatureDefinedStructureJigsawJunction> junctions;
    private final ObjectListIterator<StructurePiece> pieceIterator;
    private final ObjectListIterator<WorldGenFeatureDefinedStructureJigsawJunction> junctionIterator;

    protected Beardifier(StructureManager structuremanager, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.getMinBlockX();
        int j = chunkcoordintpair.getMinBlockZ();

        this.junctions = new ObjectArrayList(32);
        this.rigids = new ObjectArrayList(10);
        Iterator iterator = StructureGenerator.NOISE_AFFECTING_FEATURES.iterator();

        while (iterator.hasNext()) {
            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

            structuremanager.startsForFeature(SectionPosition.bottomOf(ichunkaccess), structuregenerator).forEach((structurestart) -> {
                Iterator iterator1 = structurestart.getPieces().iterator();

                while (iterator1.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator1.next();

                    if (structurepiece.isCloseToChunk(chunkcoordintpair, 12)) {
                        if (structurepiece instanceof WorldGenFeaturePillagerOutpostPoolPiece) {
                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = (WorldGenFeaturePillagerOutpostPoolPiece) structurepiece;
                            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturepillageroutpostpoolpiece.getElement().getProjection();

                            if (worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID) {
                                this.rigids.add(worldgenfeaturepillageroutpostpoolpiece);
                            }

                            Iterator iterator2 = worldgenfeaturepillageroutpostpoolpiece.getJunctions().iterator();

                            while (iterator2.hasNext()) {
                                WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) iterator2.next();
                                int k = worldgenfeaturedefinedstructurejigsawjunction.getSourceX();
                                int l = worldgenfeaturedefinedstructurejigsawjunction.getSourceZ();

                                if (k > i - 12 && l > j - 12 && k < i + 15 + 12 && l < j + 15 + 12) {
                                    this.junctions.add(worldgenfeaturedefinedstructurejigsawjunction);
                                }
                            }
                        } else {
                            this.rigids.add(structurepiece);
                        }
                    }
                }

            });
        }

        this.pieceIterator = this.rigids.iterator();
        this.junctionIterator = this.junctions.iterator();
    }

    @Override
    public double calculateNoise(int i, int j, int k) {
        double d0 = 0.0D;

        int l;
        int i1;

        while (this.pieceIterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) this.pieceIterator.next();
            StructureBoundingBox structureboundingbox = structurepiece.getBoundingBox();

            l = Math.max(0, Math.max(structureboundingbox.minX() - i, i - structureboundingbox.maxX()));
            i1 = j - (structureboundingbox.minY() + (structurepiece instanceof WorldGenFeaturePillagerOutpostPoolPiece ? ((WorldGenFeaturePillagerOutpostPoolPiece) structurepiece).getGroundLevelDelta() : 0));
            int j1 = Math.max(0, Math.max(structureboundingbox.minZ() - k, k - structureboundingbox.maxZ()));
            NoiseEffect noiseeffect = structurepiece.getNoiseEffect();

            if (noiseeffect == NoiseEffect.BURY) {
                d0 += getBuryContribution(l, i1, j1);
            } else if (noiseeffect == NoiseEffect.BEARD) {
                d0 += getBeardContribution(l, i1, j1) * 0.8D;
            }
        }

        this.pieceIterator.back(this.rigids.size());

        while (this.junctionIterator.hasNext()) {
            WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) this.junctionIterator.next();
            int k1 = i - worldgenfeaturedefinedstructurejigsawjunction.getSourceX();

            l = j - worldgenfeaturedefinedstructurejigsawjunction.getSourceGroundY();
            i1 = k - worldgenfeaturedefinedstructurejigsawjunction.getSourceZ();
            d0 += getBeardContribution(k1, l, i1) * 0.4D;
        }

        this.junctionIterator.back(this.junctions.size());
        return d0;
    }

    private static double getBuryContribution(int i, int j, int k) {
        double d0 = MathHelper.length((double) i, (double) j / 2.0D, (double) k);

        return MathHelper.clampedMap(d0, 0.0D, 6.0D, 1.0D, 0.0D);
    }

    private static double getBeardContribution(int i, int j, int k) {
        int l = i + 12;
        int i1 = j + 12;
        int j1 = k + 12;

        return l >= 0 && l < 24 ? (i1 >= 0 && i1 < 24 ? (j1 >= 0 && j1 < 24 ? (double) Beardifier.BEARD_KERNEL[j1 * 24 * 24 + l * 24 + i1] : 0.0D) : 0.0D) : 0.0D;
    }

    private static double computeBeardContribution(int i, int j, int k) {
        double d0 = (double) (i * i + k * k);
        double d1 = (double) j + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(2.718281828459045D, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;

        return d4 * d3;
    }
}
