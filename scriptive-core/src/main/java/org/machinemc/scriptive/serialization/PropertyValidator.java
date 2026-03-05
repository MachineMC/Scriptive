package org.machinemc.scriptive.serialization;

import java.util.*;

/**
 * A property validator used to validate component properties.
 */
public interface PropertyValidator {

    /**
     * A validator that ignores all properties and always returns true.
     */
    PropertyValidator IGNORE = property -> true;

    /**
     * Validates a property.
     *
     * @param property property to validate
     * @return true if the property is valid
     */
    boolean validate(ComponentProperty<?> property);

    /**
     * Returns a new validator that is a logical OR of this and the other validator.
     *
     * @param other other validator
     * @return new validator
     */
    default PropertyValidator or(PropertyValidator other) {
        return property -> validate(property) || other.validate(property);
    }

    /**
     * Returns a new validator that is a logical AND of this and the other validator.
     *
     * @param other other validator
     * @return new validator
     */
    default PropertyValidator and(PropertyValidator other) {
        return property -> validate(property) && other.validate(property);
    }

    /**
     * Returns a new validator that makes this validator optional (returns true if property is null).
     *
     * @return new validator
     */
    default PropertyValidator optional() {
        return property -> property == null || validate(property);
    }

    /**
     * Returns a validator that validates the type of the property.
     *
     * @param type property type
     * @return new validator
     */
    static PropertyValidator type(java.lang.Class<? extends ComponentProperty<?>> type) {
        return new Class(Collections.singleton(type));
    }

    /**
     * Returns a validator that validates the type of the property.
     *
     * @param types property types
     * @return new validator
     */
    @SafeVarargs
    static PropertyValidator types(java.lang.Class<? extends ComponentProperty<?>>... types) {
        return new Class(Set.of(types));
    }

    /**
     * Returns a validator that validates an array of properties.
     *
     * @param validator validator for array elements
     * @return new validator
     */
    static PropertyValidator array(PropertyValidator validator) {
        return new Array(validator);
    }

    /**
     * Returns a new schema builder.
     *
     * @return builder
     */
    static Builder schema() {
        return new Builder();
    }

    /**
     * A validator for property types.
     *
     * @param validClasses valid property classes
     */
    record Class(Set<java.lang.Class<? extends ComponentProperty<?>>> validClasses) implements PropertyValidator {

        @Override
        public boolean validate(ComponentProperty<?> property) {
            if (property == null)
                return false;

            for (java.lang.Class<?> validClass : validClasses) {
                if (validClass.isInstance(property))
                    return true;
            }
            return false;
        }

    }

    /**
     * A validator for arrays of properties.
     *
     * @param validator validator for array elements
     */
    record Array(PropertyValidator validator) implements PropertyValidator {

        @Override
        public boolean validate(ComponentProperty<?> property) {
            if (!(property instanceof ComponentProperty.Array(ComponentProperty<?>[] array)))
                return false;
            for (ComponentProperty<?> child : array) {
                if (!validator.validate(ComponentProperty.of(child)))
                    return false;
            }
            return true;
        }

    }

    /**
     * A validator for properties objects.
     *
     * @param schema schema of the properties object
     */
    record Properties(Map<String, PropertyValidator> schema) implements PropertyValidator {

        @Override
        public boolean validate(ComponentProperty<?> property) {
            if (!(property instanceof ComponentProperty.Properties(ComponentProperties properties)))
                return false;
            
            Set<String> unhandled = new HashSet<>(properties.getKeys());
            for (Map.Entry<String, PropertyValidator> entry : schema.entrySet()) {
                String key = entry.getKey();
                PropertyValidator validator = entry.getValue();

                if (!validator.validate(properties.get(key).orElse(null)))
                    return false;
            }

            return unhandled.isEmpty();
        }

    }

    /**
     * A builder for property schemas.
     */
    final class Builder {

        private final Map<String, PropertyValidator> schema = new HashMap<>();

        private Builder() {}

        /**
         * Adds a type validator for a key.
         *
         * @param key key
         * @param type property type
         * @return this builder
         */
        public Builder type(String key, java.lang.Class<? extends ComponentProperty<?>> type) {
            return validator(key, PropertyValidator.type(type));
        }

        /**
         * Adds a type validator for a key.
         *
         * @param key key
         * @param types property types
         * @return this builder
         */
        @SafeVarargs
        public final Builder types(String key, java.lang.Class<? extends ComponentProperty<?>>... types) {
            return validator(key, PropertyValidator.types(types));
        }

        /**
         * Adds an array validator for a key.
         *
         * @param key key
         * @param validator validator for array elements
         * @return this builder
         */
        public Builder array(String key, PropertyValidator validator) {
            return validator(key, PropertyValidator.array(validator));
        }

        /**
         * Adds a validator for a key.
         *
         * @param key key
         * @param validator validator
         * @return this builder
         */
        public Builder validator(String key, PropertyValidator validator) {
            schema.put(key, validator);
            return this;
        }

        /**
         * Builds the validator.
         *
         * @return validator
         */
        public PropertyValidator build() {
            return new Properties(schema);
        }

    }

}
