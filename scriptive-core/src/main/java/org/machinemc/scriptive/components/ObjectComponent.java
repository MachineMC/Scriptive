package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.*;
import org.machinemc.scriptive.style.TextFormat;
import org.machinemc.scriptive.util.PlayerProfileProvider;

import java.util.List;
import java.util.Objects;

/**
 * Represents an object component.
 */
public abstract sealed class ObjectComponent extends BaseComponent implements ClientComponent {

    /**
     * Creates new atlas object component.
     *
     * @param sprite sprite to display
     * @return atlas component
     */
    public static Atlas atlas(String sprite) {
        return atlas(null, sprite);
    }

    /**
     * Creates new atlas object component.
     *
     * @param atlas atlas identifier
     * @param sprite sprite to display
     * @return atlas component
     */
    public static Atlas atlas(@Nullable String atlas, String sprite) {
        return new Atlas(atlas, sprite);
    }

    /**
     * Creates new player object component.
     *
     * @param provider player profile provider
     * @param hat whether the hat should be displayed
     * @return player component
     */
    public static Player player(PlayerProfileProvider provider, boolean hat) {
        return player(ComponentProperty.properties(provider.profileProperties()), hat);
    }

    /**
     * Creates new player object component.
     *
     * @param player player profile property
     * @param hat whether the hat should be displayed
     * @return player component
     */
    public static Player player(ComponentProperty<?> player, boolean hat) {
        return new Player(player, hat);
    }

    @Override
    public final String getName() {
        return "object";
    }

    @Override
    public ObjectComponent append(String literal) {
        return (ObjectComponent) super.append(literal);
    }

    @Override
    public ObjectComponent append(String literal, TextFormat textFormat) {
        return (ObjectComponent) super.append(literal, textFormat);
    }

    @Override
    public ObjectComponent append(Component component) {
        return (ObjectComponent) super.append(component);
    }

    @Override
    public abstract ObjectComponent clone();

    @Override
    public abstract Class<? extends ObjectComponent> getType();

    @Override
    public ComponentModifier<?, ? extends ObjectComponent> modify() {
        //noinspection unchecked
        return super.modify();
    }

    /**
     * An atlas object component.
     */
    public static final class Atlas extends ObjectComponent {

        private @Nullable String atlas;
        private String sprite;

        private Atlas(@Nullable String atlas, String sprite) {
            this.atlas = atlas;
            this.sprite = Objects.requireNonNull(sprite, "Sprite can not be null");
        }

        /**
         * @return atlas identifier
         */
        public @Nullable String getAtlas() {
            return atlas;
        }

        /**
         * @param atlas new atlas identifier
         */
        public void setAtlas(@Nullable String atlas) {
            this.atlas = atlas;
        }

        /**
         * @return sprite identifier
         */
        public String getSprite() {
            return sprite;
        }

        /**
         * @param sprite new sprite identifier
         */
        public void setSprite(String sprite) {
            this.sprite = sprite;
        }

        @Override
        public List<String> getUniqueKeys() {
            return List.of("atlas", "sprite");
        }

        @Override
        public String getString() {
            return sprite;
        }

        @Override
        public Atlas append(String literal) {
            return (Atlas) super.append(literal);
        }

        @Override
        public Atlas append(String literal, TextFormat textFormat) {
            return (Atlas) super.append(literal, textFormat);
        }

        @Override
        public Atlas append(Component component) {
            return (Atlas) super.append(component);
        }

        @Override
        public void merge(Component other) {
            super.merge(other);
            if (getClass().isInstance(other)) {
                setAtlas(((Atlas) other).getAtlas());
                setSprite(((Atlas) other).getSprite());
            }
        }

        @Override
        public ComponentModifier modify() {
            return new ComponentModifier(this);
        }

        @Override
        public Atlas clone() {
            Atlas clone = new Atlas(atlas, sprite);
            clone.merge(this);
            return clone;
        }

        @Override
        public Class<Atlas> getType() {
            return Atlas.class;
        }
        @Override
        public void loadProperties(ComponentProperties properties, ComponentSerializer serializer) {
            super.loadProperties(properties, serializer);
            atlas = properties.getValue("atlas", String.class).orElse(null);
            sprite = properties.getValue("sprite", String.class).orElseThrow();
        }

        @Override
        public @UnmodifiableView ComponentProperties getProperties() {
            ComponentProperties properties = super.getProperties();
            properties.set("object", "atlas");
            properties.set("atlas", atlas);
            properties.set("sprite", sprite);
            return properties.unmodifiableView();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Atlas that)) return false;
            return Objects.equals(atlas, that.atlas)
                    && sprite.equals(that.sprite)

                    // base component
                    && getSiblings().equals(that.getSiblings())
                    && getTextFormat().equals(that.getTextFormat())
                    && Objects.equals(getInsertion(), that.getInsertion())
                    && Objects.equals(getClickEvent(), that.getClickEvent())
                    && Objects.equals(getHoverEvent(), that.getHoverEvent());
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(atlas);
            result = 31 * result + sprite.hashCode();

            // base component
            result = 31 * result + getSiblings().hashCode();
            result = 31 * result + getTextFormat().hashCode();
            result = 31 * result + Objects.hashCode(getInsertion());
            result = 31 * result + Objects.hashCode(getClickEvent());
            result = 31 * result + Objects.hashCode(getHoverEvent());

            return result;
        }

        /**
         * A component modifier for {@link Atlas} components.
         */
        public static final class ComponentModifier extends Component.ComponentModifier<ComponentModifier, Atlas> {

            private ComponentModifier(Atlas component) {
                super(component);
            }

            /**
             * Sets the atlas identifier.
             *
             * @param atlas atlas identifier
             * @return this modifier
             */
            public ComponentModifier atlas(@Nullable String atlas) {
                component.setAtlas(atlas);
                return getThis();
            }

            /**
             * Sets the sprite identifier.
             *
             * @param sprite sprite identifier
             * @return this modifier
             */
            public ComponentModifier sprite(String sprite) {
                component.setSprite(sprite);
                return getThis();
            }

        }

    }

    /**
     * A player object component.
     */
    public static final class Player extends ObjectComponent {

        private static final PropertyValidator SCHEMA = PropertyValidator.type(ComponentProperty.String.class).or(PropertyValidator.schema()
                .validator("name", PropertyValidator.type(ComponentProperty.String.class).optional())
                .array("id", PropertyValidator.type(ComponentProperty.Integer.class).optional())
                .array("properties", PropertyValidator.schema()
                        .type("name", ComponentProperty.String.class)
                        .type("value", ComponentProperty.String.class)
                        .validator("signature", PropertyValidator.type(ComponentProperty.String.class).optional())
                        .build().optional())
                .validator("texture", PropertyValidator.type(ComponentProperty.String.class).optional())
                .validator("cape", PropertyValidator.type(ComponentProperty.String.class).optional())
                .validator("model", PropertyValidator.type(ComponentProperty.String.class).optional())
                .build().and(property -> {
                    ComponentProperties properties = ((ComponentProperty.Properties) property).value();
                    return properties.contains("name") || properties.contains("id");
                }));

        private ComponentProperty<?> player;
        private boolean hat;

        private Player(ComponentProperty<?> player, boolean hat) {
            this.player = Objects.requireNonNull(player, "Player can not be null");
            if (!SCHEMA.validate(player))
                throw new IllegalArgumentException(player + " is not a valid player");
            this.hat = hat;
        }

        /**
         * @return player profile property
         */
        public ComponentProperty<?> getPlayer() {
            return player;
        }

        /**
         * @param provider new player profile provider
         */
        public void setPlayer(PlayerProfileProvider provider) {
            setPlayer(ComponentProperty.properties(provider.profileProperties()));
        }

        /**
         * @param player new player profile property
         */
        public void setPlayer(ComponentProperty<?> player) {
            if (!SCHEMA.validate(player))
                throw new IllegalArgumentException(player + " is not a valid player");
            this.player = player;
        }

        /**
         * @return whether the hat should be displayed
         */
        public boolean getHat() {
            return hat;
        }

        /**
         * @param hat whether the hat should be displayed
         */
        public void setHat(boolean hat) {
            this.hat = hat;
        }

        @Override
        public List<String> getUniqueKeys() {
            return List.of("player", "hat");
        }

        @Override
        public String getString() {
            if (player instanceof ComponentProperty.String(String string))
                return string;
            ComponentProperty.Properties properties = (ComponentProperty.Properties) player;
            return properties.value().getValue("name").or(() -> properties.value().get("id")).orElseThrow().toString();
        }

        @Override
        public Player append(String literal) {
            return (Player) super.append(literal);
        }

        @Override
        public Player append(String literal, TextFormat textFormat) {
            return (Player) super.append(literal, textFormat);
        }

        @Override
        public Player append(Component component) {
            return (Player) super.append(component);
        }

        @Override
        public void merge(Component other) {
            super.merge(other);
            if (getClass().isInstance(other)) {
                setPlayer(((Player) other).getPlayer());
                setHat(((Player) other).getHat());
            }
        }

        @Override
        public ComponentModifier modify() {
            return new ComponentModifier(this);
        }

        @Override
        public Player clone() {
            Player clone = new Player(player, hat);
            clone.merge(this);
            return clone;
        }

        @Override
        public Class<Player> getType() {
            return Player.class;
        }

        @Override
        public void loadProperties(ComponentProperties properties, ComponentSerializer serializer) {
            super.loadProperties(properties, serializer);
            setPlayer(properties.get("player", ComponentProperty.class).orElseThrow());
            hat = properties.getValue("hat", Boolean.class).orElseThrow();
        }

        @Override
        public @UnmodifiableView ComponentProperties getProperties() {
            ComponentProperties properties = super.getProperties();
            properties.set("object", "player");
            properties.set("player", player);
            properties.set("hat", hat);
            return properties.unmodifiableView();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Player that)) return false;
            return Objects.equals(player, that.player)
                    && hat == that.hat

                    // base component
                    && getSiblings().equals(that.getSiblings())
                    && getTextFormat().equals(that.getTextFormat())
                    && Objects.equals(getInsertion(), that.getInsertion())
                    && Objects.equals(getClickEvent(), that.getClickEvent())
                    && Objects.equals(getHoverEvent(), that.getHoverEvent());
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(player);
            result = 31 * result + Objects.hashCode(hat);

            // base component
            result = 31 * result + getSiblings().hashCode();
            result = 31 * result + getTextFormat().hashCode();
            result = 31 * result + Objects.hashCode(getInsertion());
            result = 31 * result + Objects.hashCode(getClickEvent());
            result = 31 * result + Objects.hashCode(getHoverEvent());

            return result;
        }

        /**
         * A component modifier for {@link Player} components.
         */
        public static final class ComponentModifier extends Component.ComponentModifier<ComponentModifier, Player> {

            private ComponentModifier(Player component) {
                super(component);
            }

            /**
             * Sets the player profile property.
             *
             * @param provider player profile provider
             * @return this modifier
             */
            public ComponentModifier player(PlayerProfileProvider provider) {
                return player(ComponentProperty.properties(provider.profileProperties()));
            }

            /**
             * Sets the player profile property.
             *
             * @param player player profile property
             * @return this modifier
             */
            public ComponentModifier player(ComponentProperty<?> player) {
                component.setPlayer(player);
                return getThis();
            }

            /**
             * Sets whether the hat should be displayed.
             *
             * @param hat whether the hat should be displayed
             * @return this modifier
             */
            public ComponentModifier hat(boolean hat) {
                component.setHat(hat);
                return getThis();
            }

        }

    }

}
