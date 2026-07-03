# Contributing to Ashwath.AI

## Repository Structure

This is a monorepo. Each platform directory is independent:

- **`android/`** — Open in Android Studio. Build with `./gradlew assembleDebug`.
- **`engine/`** — Go module. Build with `go build ./cmd/ashwathd`.
- **`docs/`** — Platform-level documentation.
- **`sdk/`** — Client SDKs for engine API.

## Development Flow

1. Find an EPIC or issue to work on
2. Create a feature branch from `main`
3. Make changes in the relevant directory
4. Run tests for that directory
5. Open a pull request

## Pull Request Guidelines

- Keep PRs focused on a single concern
- Update docs if changing architecture or API
- Ensure CI passes for all affected directories

## Code of Conduct

Be respectful. This is a community project.
