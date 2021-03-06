# kotlin-migration
A Kotlin library to enable easier program migrations, inspired by AndroidX Room

## Usage

### Add the dependency

```kotlin
dependencies {
  implementation("io.github.boswelja.migration:migration-core:$version")
}
```

### Set up a `Migrator`

The basis of this library is a `Migrator`, that will run any `Migration` you give it. To create a `Migrator`, you should create your own class extending `Migrator`.

```kotlin
class MigrationManager : Migrator(
  currentVersion = 1,
  migrations = listOf(
    // Your migrations here
  )
) {
  override suspend fun getOldVersion(): Int {
    // You should fetch your previous version here
    return 1
  }
}
```

That's it! You can then call `migrate()` on an instance of your `Migrator` implementation.

```kotlin
val migrationManager = MigrationManager()

coroutineScope.launch {
  migrationManager.migrate()
}
```

### Creating Migrations

Currently, this library provides 2 types of migrations, `VersionMigration` and `ConditionalMigration`. Both should be passed into your `Migrator` constructor.

#### `VersionMigration`

```kotlin
val migration1_2 = object : VersionMigration(fromVersion = 1, toVersion = 2) {
  override suspend fun migrate(): Boolean {
    // Do your migration here
    return true
  }
}
```

#### `ConditionalMigration`

```kotlin
val migration1_2 = object : ConditionalMigration() {
  override suspend fun shouldMigrate(fromVersion: Int): Boolean {
    // Check whether this migration should be run, optionally taking a version into account
    var shouldMigrate = ...
    return shouldMigrate
  }

  override suspend fun migrate(): Boolean {
    // Do your migration here
    return true
  }
}
```

#### Creating your own migration type

You can implement the `Migration` interface on a class to build your own migration with more complex logic.

```kotlin
abstract class VersionMigration(
  val fromVersion: Int,
  final override val toVersion: Int
) : Migration {
  final override suspend fun shouldMigrate(fromVersion: Int): Boolean {
    // Run this migration if fromVersion is the same as the version provided by the user
    return fromVersion == this.fromVersion
  }
}
```
