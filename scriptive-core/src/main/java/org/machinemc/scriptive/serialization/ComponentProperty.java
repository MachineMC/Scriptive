package org.machinemc.scriptive.serialization;

import java.util.Objects;

/**
 * Represents an object that can represent value of {@link ComponentProperties}.
 * <p>
 * Each serializer has to support all property implementations.
 */
public sealed interface ComponentProperty<Value> {

    /**
     * @param value string value
     * @return string property
     */
    static String string(java.lang.String value) { return new String(value); }

    /**
     * @param value boolean value
     * @return boolean property
     */
    static Boolean bool(java.lang.Boolean value) { return new Boolean(value); }

    /**
     * @param value integer value
     * @return integer property
     */
    static Integer integer(java.lang.Integer value) { return new Integer(value); }

    /**
     * @param value properties value
     * @return properties property
     */
    static Properties properties(ComponentProperties value) { return new Properties(value); }

    /**
     * @param value properties array value
     * @return properties array property
     */
    static Array array(ComponentProperties[] value) { return new Array(value); }

    /**
     * Components are represented as {@link Properties} property type by default, but
     * can also be represented by {@link String} or {@link Array} property types instead.
     * <p>
     * This method converts components represented by string or array to
     * properties.
     * <p>
     * This can be used for deserialization.
     *
     * @param property component property to convert
     * @return converted property
     */
    static Properties convertToProperties(ComponentProperty<?> property) {
        return switch (property) {
            case Properties properties -> properties;

            case String string -> {
                ComponentProperties properties = new ComponentProperties();
                properties.set("text", string.value);
                yield new Properties(properties);
            }

            case Array array -> {
                ComponentProperties properties = new ComponentProperties();
                ComponentProperties[] propertiesArray = array.value;
                if (propertiesArray.length == 0) yield convertToProperties(string("")); // empty component
                properties.copyAll(propertiesArray[0]);
                ComponentProperties[] extra = new ComponentProperties[propertiesArray.length - 1];
                System.arraycopy(propertiesArray, 1, extra, 0, extra.length);
                if (extra.length != 0) properties.set("extra", extra);
                yield new Properties(properties);
            }

            default -> throw new UnsupportedOperationException();
        };
    }

    /**
     * Creates component property out of given object or none if there is no
     * available wrapper for it.
     *
     * @param object object to convert
     * @return its component property representation
     * @param <Type> object type
     * @param <Property> property type
     */
    static <Type, Property extends ComponentProperty<Type>> Property of(Type object) {
        return (Property) switch (object) {
            case java.lang.String s -> new String(s);
            case java.lang.Boolean b -> new Boolean(b);
            case java.lang.Integer i -> new Integer(i);
            case ComponentProperties properties -> new Properties(properties);
            case ComponentProperties[] properties -> new Array(properties);
            default -> throw new UnsupportedOperationException("Unsupported value: " + object);
        };
    }

    /**
     * Returns wrapped value of this property.
     *
     * @return value of this property
     */
    Value value();

    ComponentProperty<Value> clone();

    /**
     * String component property.
     */
    record String(java.lang.String value) implements ComponentProperty<java.lang.String> {

        public String {
            Objects.requireNonNull(value, "Value can not be null");
        }

        @Override
        public String clone() {
            return new String(value);
        }

    }

    /**
     * Boolean component property.
     */
    record Boolean(java.lang.Boolean value) implements ComponentProperty<java.lang.Boolean> {

        public Boolean {
            Objects.requireNonNull(value, "Value can not be null");
        }

        @Override
        public Boolean clone() {
            return new Boolean(value);
        }

    }

    /**
     * Integer component property.
     */
    record Integer(java.lang.Integer value) implements ComponentProperty<java.lang.Integer> {

        public Integer {
            Objects.requireNonNull(value, "Value can not be null");
        }

        @Override
        public Integer clone() {
            return new Integer(value);
        }

    }

    /**
     * Component properties component property.
     */
    record Properties(ComponentProperties value) implements ComponentProperty<ComponentProperties> {

        public Properties {
            Objects.requireNonNull(value, "Value can not be null");
        }

        @Override
        public Properties clone() {
            return new Properties(value.clone());
        }

    }

    /**
     * Component properties array component property.
     */
    record Array(ComponentProperties[] value) implements ComponentProperty<ComponentProperties[]> {

        public Array {
            Objects.requireNonNull(value, "Value can not be null");
        }

        @Override
        public Array clone() {
            ComponentProperties[] next = new ComponentProperties[value.length];
            for (int i = 0; i < value.length; i++) next[i] = value[i].clone();
            return new Array(next);
        }

    }

}
