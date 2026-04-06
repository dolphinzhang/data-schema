# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**db-tools** (`org.dtools:db-tools:1.0.5-SNAPSHOT`) — A Java library that loads relational database schema metadata via JDBC and provides code generation support (Java/C# type mappings, naming conventions) and DDL script generation (CREATE TABLE, ALTER TABLE diffs).

## Build Commands

```bash
mvn compile          # Compile
mvn test             # Run all tests (requires live MySQL databases configured in test class)
mvn package          # Build JAR
mvn deploy           # Deploy to Aliyun Maven repository
```

Tests connect to real databases — they are not isolated. See `DatabaseSchemaLoaderTest` for connection details.

## Architecture

### Schema Model (`org.dol.database.schema`)

- **DatabaseSchemaLoader** — Entry point. Connects via JDBC, reads `DatabaseMetaData` and `information_schema` to populate the schema model. Supports MySQL-specific features (character set, collation, `SHOW CREATE TABLE` for comments). Static `load()` methods accept either a `Connection` or driver/URL/credentials.
- **DatabaseSchema** — Top-level container. Holds a map of `TableSchema` objects. Provides `fieldNames()` to aggregate columns across tables and `getColumnMap()` for hierarchical lookup by property/model name.
- **TableSchema** — Represents a table or view. Converts table names to CamelCase model names stripping a configurable prefix (e.g., `t_user_info` → `UserInfo`). Lazily caches references to special columns (primary, status, createTime, updateTime, deleted, remarks).
- **ColumnSchema** — Richly decorated column metadata. Beyond database attributes (type, size, nullable), it computes Java/C# types, property names, getter/setter names, UI widget hints (EasyUI), query eligibility (equal/in/like/range/keyword), and test value generation.
- **DataTypeEnum** — Maps 40+ database type names to Java types, C# types, and JDBC type codes. Lookup via `DataTypeEnum.get(dataTypeName)`.
- **SchemaConstraints** — Static lists of column name conventions for detecting special-purpose columns (create/update timestamps, soft delete flags, version, status, company ID). This is how the library recognizes columns like `UPDATE_TIME`, `IS_DELETED`, etc.
- **KeySchema / IndexSchema** — Primary key and index representations with member column lists.

### Utilities (`org.dol.database.utils`)

- **ScriptGenerator** — Generates DDL: `generate(DatabaseSchema)` for full CREATE TABLE scripts; `generateModifySQL(fromDB, toDB, ...)` for ALTER TABLE migration scripts comparing two schemas. Handles column adds/modifies, primary key changes, and index changes. Does not generate DROP COLUMN (conservative).
- **Utils** — String manipulation (camelCase splitting, capitalize, padding), collection helpers, random data generators (email, mobile, URL, IPv4), and reflection utilities.

## Key Conventions

- **Column naming detection**: Special columns are identified purely by name matching against lists in `SchemaConstraints`. When adding new conventions, update those lists.
- **Lombok**: Uses `@Data`, `@Slf4j`, `@AllArgsConstructor`, `@SneakyThrows` throughout. Lombok 1.18.12.
- **Java 8**: Source/target level is 1.8.
- **Type mapping**: All database-to-language type conversions go through `DataTypeEnum`. To support a new database type, add an enum entry there.
- **Table prefix**: The `tablePrefix` parameter (e.g., `"t_"`) is stripped when generating model names and property names.
