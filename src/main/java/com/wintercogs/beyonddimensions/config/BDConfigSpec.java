package com.wintercogs.beyonddimensions.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 移植垫片：NeoForge 的 {@code BDConfigSpec}。
 * <p>
 * Fabric 没有内置配置系统，这里实现一个最小可用版本：
 * 定义项注册 -> 以 JSON 落到 {@code config/<modid>-<type>.json} -> 读写与范围/枚举校验。
 * 语法面覆盖上游用到的 {@code comment / define / defineInRange / defineEnum / build}。
 */
public class BDConfigSpec
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /** 配置文件种类（对应 NeoForge 的 ModConfig.Type，现为模组自有枚举）。 */
    public enum Type
    {
        COMMON,
        CLIENT,
        SERVER,
        STARTUP
    }

    private static Path configFile(String modId, Type type)
    {
        return Path.of("config", modId + "-" + type.name().toLowerCase(java.util.Locale.ROOT) + ".json")
                .toAbsolutePath();
    }
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<ConfigValue<?>> values;
    private final Map<String, ConfigValue<?>> byPath;
    private final String modId;
    private volatile BDConfigSpec.Type activeType = BDConfigSpec.Type.COMMON;

    private BDConfigSpec(List<ConfigValue<?>> values, Map<String, ConfigValue<?>> byPath, String modId)
    {
        this.values = values;
        this.byPath = byPath;
        this.modId = modId;
    }

    public List<ConfigValue<?>> getValues()
    {
        return values;
    }

    public void load(BDConfigSpec.Type type)
    {
        Path file = configFile(modId, type);
        JsonObject json = new JsonObject();
        if (Files.isRegularFile(file))
        {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
            {
                JsonObject read = GSON.fromJson(reader, JsonObject.class);
                if (read != null)
                {
                    json = read;
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("读取配置文件 {} 失败，使用默认值", file, e);
            }
        }

        boolean changed = false;
        for (ConfigValue<?> value : values)
        {
            if (json.has(value.path) && json.get(value.path).isJsonPrimitive())
            {
                if (value.read(json.get(value.path).getAsJsonPrimitive()))
                {
                    changed = true;
                }
            }
            else
            {
                changed = true;
            }
        }

        if (changed || !Files.isRegularFile(file))
        {
            save(type);
        }
    }

    public void save(BDConfigSpec.Type type)
    {
        Path file = configFile(modId, type);
        JsonObject json = new JsonObject();
        for (ConfigValue<?> value : values)
        {
            json.add(value.path, value.write());
        }
        try
        {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
            {
                GSON.toJson(json, writer);
            }
        }
        catch (IOException e)
        {
            LOGGER.warn("写入配置文件 {} 失败", file, e);
        }
    }

    public static class Builder
    {
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private final Map<String, ConfigValue<?>> byPath = new LinkedHashMap<>();
        private String comment;
        private final String modId;
        private final List<String> section = new ArrayList<>();

        public Builder(String modId)
        {
            this.modId = (modId == null || modId.isEmpty()) ? "beyonddimensions" : modId;
        }

        public Builder comment(String comment)
        {
            this.comment = comment;
            return this;
        }

        public Builder push(String name)
        {
            section.add(name);
            return this;
        }

        public Builder pop()
        {
            if (!section.isEmpty())
            {
                section.remove(section.size() - 1);
            }
            return this;
        }

        private String qualify(String path)
        {
            return section.isEmpty() ? path : String.join(".", section) + "." + path;
        }

        private <V extends ConfigValue<?>> V add(V value)
        {
            if (byPath.putIfAbsent(value.path, value) != null)
            {
                throw new IllegalStateException("重复的配置项：" + value.path);
            }
            values.add(value);
            return value;
        }

        public ConfigValue<String> define(String path, String defaultValue)
        {
            return add(new ConfigValue<>(qualify(path), defaultValue, comment, String.class, null));
        }

        public BooleanValue define(String path, boolean defaultValue)
        {
            return add(new BooleanValue(qualify(path), defaultValue, comment));
        }

        public IntValue defineInRange(String path, int defaultValue, int min, int max)
        {
            return add(new IntValue(qualify(path), defaultValue, comment, min, max));
        }

        public LongValue defineInRange(String path, long defaultValue, long min, long max)
        {
            return add(new LongValue(qualify(path), defaultValue, comment, min, max));
        }

        public <E extends Enum<E>> EnumValue<E> defineEnum(String path, E defaultValue)
        {
            return add(new EnumValue<>(qualify(path), defaultValue, comment));
        }

        public BDConfigSpec build()
        {
            return new BDConfigSpec(List.copyOf(values), Map.copyOf(byPath), modId);
        }
    }

    /** 配置值基类。 */
    public static class ConfigValue<T>
    {
        protected final String path;
        protected final String comment;
        protected final Class<T> type;
        protected final T defaultValue;
        protected volatile T value;
        private BDConfigSpec owner;

        ConfigValue(String path, T defaultValue, String comment, Class<T> type, Object ignored)
        {
            this.path = path;
            this.defaultValue = defaultValue;
            this.comment = comment;
            this.type = type;
            this.value = defaultValue;
        }

        void attach(BDConfigSpec spec)
        {
            this.owner = spec;
        }

        /** 上游用法：把当前值写回磁盘。 */
        public void save()
        {
            if (owner != null)
            {
                owner.save(owner.activeType);
            }
        }

        public String getPath()
        {
            return path;
        }

        public String getComment()
        {
            return comment;
        }

        @SuppressWarnings("unchecked")
        public T get()
        {
            return value;
        }

        public void set(T newValue)
        {
            this.value = newValue;
        }

        JsonPrimitive write()
        {
            return new JsonPrimitive(String.valueOf(value));
        }

        @SuppressWarnings("unchecked")
        boolean read(JsonPrimitive primitive)
        {
            try
            {
                String raw = primitive.getAsString();
                if (type == String.class)
                {
                    value = (T) raw;
                }
                return false;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }

    public static class BooleanValue extends ConfigValue<Boolean>
    {
        BooleanValue(String path, boolean defaultValue, String comment)
        {
            super(path, defaultValue, comment, Boolean.class, null);
        }

        @Override
        boolean read(JsonPrimitive primitive)
        {
            try
            {
                value = primitive.getAsBoolean();
                return false;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }

    public static class IntValue extends ConfigValue<Integer>
    {
        private final int min;
        private final int max;

        IntValue(String path, int defaultValue, String comment, int min, int max)
        {
            super(path, defaultValue, comment, Integer.class, null);
            this.min = min;
            this.max = max;
        }

        @Override
        boolean read(JsonPrimitive primitive)
        {
            try
            {
                value = Math.max(min, Math.min(max, primitive.getAsInt()));
                return false;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }

    public static class LongValue extends ConfigValue<Long>
    {
        private final long min;
        private final long max;

        LongValue(String path, long defaultValue, String comment, long min, long max)
        {
            super(path, defaultValue, comment, Long.class, null);
            this.min = min;
            this.max = max;
        }

        @Override
        boolean read(JsonPrimitive primitive)
        {
            try
            {
                value = Math.max(min, Math.min(max, primitive.getAsLong()));
                return false;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }

    public static class EnumValue<E extends Enum<E>> extends ConfigValue<E>
    {
        EnumValue(String path, E defaultValue, String comment)
        {
            super(path, defaultValue, comment, null, null);
        }

        @Override
        @SuppressWarnings("unchecked")
        boolean read(JsonPrimitive primitive)
        {
            try
            {
                String name = primitive.getAsString();
                for (E candidate : defaultValue.getDeclaringClass().getEnumConstants())
                {
                    if (candidate.name().equals(name))
                    {
                        value = candidate;
                        return false;
                    }
                }
                return true;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }
}