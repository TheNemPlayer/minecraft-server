package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class DataConverterEntityRavagerRename extends DataConverterEntityRenameAbstract {

    public static final Map<String, String> RENAMED_IDS = ImmutableMap.builder().put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg").build();

    public DataConverterEntityRavagerRename(Schema schema, boolean flag) {
        super("EntityRavagerRenameFix", schema, flag);
    }

    @Override
    protected String rename(String s) {
        return Objects.equals("minecraft:illager_beast", s) ? "minecraft:ravager" : s;
    }
}
