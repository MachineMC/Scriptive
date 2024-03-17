![banner](.github/assets/scriptive.png)

<h4 align="center">Java Implementation for Minecraft Java Edition Chat Component Format</h4>

[![license](https://img.shields.io/github/license/machinemc/scriptive?style=for-the-badge&color=657185)](LICENCE)
![release](https://img.shields.io/github/v/release/machinemc/scriptive?style=for-the-badge&color=edb228)

---

Since version 1.7.2, Minecraft has two separate text formatting systems.
The newer text component system is used in many specific
contexts expecting formatted text, including chat messages,
written books, death messages, window titles, and the like.

Scriptive is a library for easy creation and serialization
of such text components with support for the latest Minecraft
versions.

### Features

- *Comprehensive support for all formating options*
- Lightweight and intuitive
- Supports new NBT serialization introduced in *1.20.3* version of Minecraft
- Supports JSON serialization
- Provides [custom format](scriptive-formatify) of components suitable for *user-input*

### Modules

- [`scriptive-core`](scriptive-core) - Core of the Scriptive library
- [`scriptive-gson`](scriptive-gson) - JSON serialization of text components using GSON library
- [`scriptive-nbt`](scriptive-nbt) - NBT serialization of text components using Machine's [NBT](https://github.com/MachineMC/NBT) library
- [`scriptive-formatify`](scriptive-formatify) - Custom human-readable format for components

### Importing

#### Gradle

```kotlin
repositories {
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repo.machinemc.org/releases")
    }
}

dependencies {
    implementation("org.machinemc:scriptive-core:VERSION")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>machinemc-repository-releases</id>
        <name>MachineMC Repository</name>
        <url>https://repo.machinemc.org/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.machinemc</groupId>
        <artifactId>scriptive-core</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

Other modules can be added as another dependency.

### License
Scriptive is free software licensed under the [MIT license](LICENCE).