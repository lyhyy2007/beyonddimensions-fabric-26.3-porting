package com.wintercogs.beyonddimensions.api.fluid;

/**
 * 移植垫片：NeoForge 的 {@code FluidType}（流体物理/渲染属性）。
 * <p>
 * Fabric 没有流体类型注册表，这里只承载上游代码用到的属性子集。
 */
public class FluidType
{
    public static final int BUCKET_VOLUME = 1000;

    private final Properties properties;

    public FluidType(Properties properties)
    {
        this.properties = properties == null ? Properties.create() : properties;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public int getLightLevel()
    {
        return properties.lightLevel;
    }

    public int getDensity()
    {
        return properties.density;
    }

    public int getViscosity()
    {
        return properties.viscosity;
    }

    public static class Properties
    {
        private int lightLevel = 0;
        private int density = 1000;
        private int viscosity = 1000;
        private int temperature = 300;
        private boolean canPushEntity = true;
        private boolean canSwim = true;
        private boolean canDrown = true;
        private boolean supportsBoating = false;

        public static Properties create()
        {
            return new Properties();
        }

        public Properties lightLevel(int value)
        {
            this.lightLevel = value;
            return this;
        }

        public Properties density(int value)
        {
            this.density = value;
            return this;
        }

        public Properties viscosity(int value)
        {
            this.viscosity = value;
            return this;
        }

        public Properties temperature(int value)
        {
            this.temperature = value;
            return this;
        }

        public Properties canPushEntity(boolean value)
        {
            this.canPushEntity = value;
            return this;
        }

        public Properties canSwim(boolean value)
        {
            this.canSwim = value;
            return this;
        }

        public Properties canDrown(boolean value)
        {
            this.canDrown = value;
            return this;
        }

        public Properties supportsBoating(boolean value)
        {
            this.supportsBoating = value;
            return this;
        }
    }
}